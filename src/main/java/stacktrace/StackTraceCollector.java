/**
 *
 */
package d4j_analysis.stacktrace;

import d4j_analysis.properties.ExceptionNames;
import util.CollectionUtil;
import util.PrintHelper;
import util.tuple.Triple;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author mijung
 *
 */
public class StackTraceCollector {

	private LinkedHashMap<Integer, StackTraceInfo> stInfos; //Map<TestId, StackTraceInfo>
	private Set<Triple<String,String, Integer>> topTraceSet; //Set<Triple<Class name, Method name, Line number>>
	private HashMap<Triple<String,String, Integer>, Set<Integer>> topTraceToTestIdMap; //Map<topTrace, TestId in stInfos that have such topTraces>
	private LinkedHashMap<Integer, String> failIdToTestNameMap; //Map<TestId, TestName(e.g., RandoopTest1.test100)>
	private LinkedHashMap<String, Integer> testNameToFailIdMap; //Map<TestName(e.g., RandoopTest1.test100), TestId>
	private HashMap<String, String> testNameToCUTMap; //Map<TestName,CUT class name>

	private HashMap<String, Integer> exceptionNumsMap; //Map<exception type, # of such type occurred>
	private Collection<String> crashMethodSigs;
	private HashMap<String, String> assertionCrashMethodSig; //Map<TestName, crashMethodSig> only for assertionFailures
	private HashMap<String, String> crashMethodSigsMap; //Map<TestName,crashMathodSig>
	private Collection<String> callStackClassSigs; //Set<classes shown in call stack>
	private Collection<String> crashClassSigs; //Set<classes of crash methods>

	private Set<Triple<String,String, Integer>> allNonBottomFrameSet; //set of all stack frames except the bottom one (method under test). For JCrasher comparison

	public Collection<String> getCrashClassSigs() {
		return crashClassSigs;
	}

	/**
	 * @return the testNameToCUTMap
	 */

	public Collection<String> getCallStackClassSigs() {
		return callStackClassSigs;
	}
	public HashMap<String, String> getTestNameToCUTMap() {
		return testNameToCUTMap;
	}

	public HashMap<String, String> getCrashMethodSigsMap() {
		return crashMethodSigsMap;
	}

	public Collection<String> getCrashMethodSigs() {
		return crashMethodSigs;
	}

	public LinkedHashMap<Integer, StackTraceInfo> getStInfos() {
		return stInfos;
	}

	public Set<Triple<String, String, Integer>> getTopTraceSet() {
		return topTraceSet;
	}

	public HashMap<Triple<String, String, Integer>, Set<Integer>> getTopTraceToTestIdMap() {
		return topTraceToTestIdMap;
	}

	public Set<Triple<String, String, Integer>> getAllNonBottomFrameSet() {
		return allNonBottomFrameSet;
	}

	/**
	 * @return the testIdToTestNameMap
	 */
	public LinkedHashMap<Integer, String> getFailIdToTestNameMap() {
		return failIdToTestNameMap;
	}

	/**
	 * @return the testNameToFailIdMap
	 */
	public LinkedHashMap<String, Integer> getTestNameToFailIdMap() {
		return testNameToFailIdMap;
	}

	/**
	 * @return the exceptionNumsMap
	 */
	public HashMap<String, Integer> getExceptionNumsMap() {
		return exceptionNumsMap;
	}

	/**
	 * This method takes as input a txt file containing test report {@code testReportFile},
	 * collects the stack traces for EVERY failing test,
	 * and put the stack trace information collected into a HashMap {@code result}
	 * @param testReportFile
	 */
	public void collectStackTrace(String testReportFile) {
		List<String> stackTraces = listStackTraces(testReportFile);
//		PrintHelper.printCollection(stackTraces, "stacktraces collected");
		stInfos = putStackTraceInfo(stackTraces);
//		PrintHelper.printStackTraceMap(stInfos);
	}
	/**
	 * This method collects the signature of crash methods.
	 * If the error type is assertions, the crash method is the one on the top frame
	 * Otherwise, the crash method is the method linked to the the stack frame corresponding to the test case.
	 */
	public void collectCrashMethod() {
		if(stInfos == null)
			throw new NullPointerException("stInfo should not be null at this point. please call collectStackTrace() first");
		crashMethodSigs = new HashSet<String>();
		crashMethodSigsMap = new HashMap<String,String>();
		callStackClassSigs = new HashSet<>();
		crashClassSigs = new HashSet<>();
		//1. For each collected stack trace
		for( Integer testId : stInfos.keySet()){
			StackTraceInfo contents = stInfos.get(testId);
			String test = contents.getTestFile() + "." + contents.getTestNum();

			//Using new function getTraceInfoAfterTest for same functionality
			List<Triple<String, String, Integer>> frames = contents.getTraceInfoAfterTest();
			if(CollectionUtil.isCollectionEmpty(frames))
				continue;
			Triple<String, String, Integer> crashPoint = frames.get(frames.size()-1); //crash method

			String crashline = crashPoint.getFirst() + ":" + crashPoint.getSecond() + ":" + crashPoint.getThird();
			String crashMethod = crashPoint.getFirst() + ":" + crashPoint.getSecond();
			String crashClass = crashPoint.getFirst();
			if(crashClass.contains("$")){
				crashClass = crashClass.substring(0, crashClass.indexOf("$"));
			}
			crashClassSigs.add(crashClass);

			for(Triple t : frames) {
				String clsSig = t.getFirst().toString().trim();
				if(clsSig.contains("$"))
					clsSig = clsSig.substring(0, clsSig.indexOf("$"));
				if(!clsSig.contains("java.") &&
						!clsSig.contains("javax.") &&
						!clsSig.contains("sun.")
						&& !clsSig.contains("junit.")) {
					callStackClassSigs.add(clsSig);
				}
			}
			crashMethodSigs.add(crashMethod);
			crashMethodSigsMap.put(test, crashline);
		} //end of for( Integer testId : stInfos.keySet())
	}


	/**
	 * @param stackTraces
	 * @return
	 */
	private LinkedHashMap<Integer, StackTraceInfo> putStackTraceInfo(List<String> stackTraces) {
		LinkedHashMap<Integer, StackTraceInfo> result = new LinkedHashMap<Integer, StackTraceInfo>();
		exceptionNumsMap = new HashMap<String, Integer>();
		failIdToTestNameMap = new LinkedHashMap<Integer, String>();
		testNameToFailIdMap = new LinkedHashMap<String, Integer>();
		testNameToCUTMap = new HashMap<String, String>();
		int failNum = 0;

		Collection<List<Triple<String, String, Integer>>> stackTraceAfterTestSet = new ArrayList<List<Triple<String, String, Integer>>>();
		for (String trace : stackTraces) {
			StackTraceInfo sti = new StackTraceInfo();

			//test1(randoopFailures.RandoopTest_failure_1)java.lang.NullPointerException
//			Pattern headLinePattern = Pattern.compile("(.+)\\((.+)\\)(.+)");
//			Pattern headLinePattern = Pattern.compile("(.+?)\\((.+?)\\)(.*)"); //evosuite
//			Pattern headLinePattern = Pattern.compile("(^(?!at).+)\\((.+)\\)(.*)"); //evosuite
//			Pattern headLinePattern = Pattern.compile("(^(?!at).+)\\((.+?)\\)(.*)"); //evosuite
			Pattern headLinePattern = Pattern.compile("(^(?!at).+?)\\((.+?)\\)(.*)"); //randoop, hopefully for evosuite as well
		    Matcher headLineMatcher = headLinePattern.matcher(trace);

		    while (!trace.trim().startsWith("at ") && headLineMatcher.find()) {
//		    	System.out.println(trace);
		    	sti.setTestNum(headLineMatcher.group(1));
		    	sti.setTestFile(headLineMatcher.group(2));
		    	sti.setErrorMsg(headLineMatcher.group(3));

		    	updateExceptionInfo(sti.getErrorMsg());
		    }
		    if(sti.getTestNum()==null || sti.getErrorMsg()==null){
//		    	System.out.println("num: " + sti.getTestNum() + " , msg: " + sti.getErrorMsg());
		    	continue;
		    }
		    if(sti.getTestNum().equals("initializationError") && sti.getErrorMsg().contains("No runnable methods")) // evosuite (don't include these cases)
		    	continue;
	        // "at package.class.method(source.java:123)"
		    // "at package.class.method(Native Method)"
	        Pattern tracePattern = Pattern
//	                .compile("\\s*at\\s+([\\w\\.$_]+)\\.([\\<*\\w$_\\>*]+)(\\(.+java)?:(\\d+)\\)(\\n|\\r\\n)");
	        		.compile("\\s*at\\s+([\\w\\.$_]+)\\.([\\<*\\w$_\\>*]+)\\((.+)\\)(\\n|\\r\\n)");

	        Matcher traceMatcher = tracePattern.matcher(trace);

	        List<Triple<String, String, Integer>> stackTrace = new ArrayList<Triple<String,String,Integer>>();
	        List<Triple<String, String, Integer>> stackTraceAfterTest = new ArrayList<Triple<String,String,Integer>>();
			List<Triple<String, String, Integer>> stackTraceAfterTestNoLib = new ArrayList<Triple<String,String,Integer>>();
	        boolean traceAfterTest = true;
	        while (traceMatcher.find()) {
//				System.out.println("---- " + trace);
	            String className = traceMatcher.group(1);
	            String methodName = traceMatcher.group(2);
	            String sourceFile = traceMatcher.group(3);
	            int lineNum = -1;
	            if(!sourceFile.trim().equals("Native Method") &&
	            		!sourceFile.trim().equals("Unknown Source") &&
	            		!sourceFile.trim().equals("Jasmin")){
	            	Pattern sourceFilePattern = Pattern.compile("(.+java):(\\d+)");
	            	Matcher sourceFileMatcher = sourceFilePattern.matcher(sourceFile);
	            	if(sourceFileMatcher.find())
	            		lineNum = Integer.parseInt(sourceFileMatcher.group(2));
	            	else{
	            		System.err.println("S K I P during collecting stack traces. Line number cannot be collected for source file name for: " + sti.getTestFile() + "." + sti.getTestNum());
//	            		continue; //just skip instead of aborting (for stack over flow exception, there are cases where source code number is empty)
	            	}
	            }
	            //Triple<Class name, Method name, Line number>
	            Triple<String, String, Integer> triple = new Triple<String, String, Integer>(className, methodName, lineNum);
            	stackTrace.add(triple);

		        if(traceAfterTest) {
					stackTraceAfterTest.add(triple);
				}
				if(className.endsWith("Test") || className.endsWith("Tests")
						|| ( isLib(className) && sourceFile.startsWith("Test"))) {
					traceAfterTest = false;
				}
	        }
	        sti.setTraceInfo(stackTrace);
	        sti.setTraceInfoAfterTest(stackTraceAfterTest);
	        boolean foundNonLib = false;
	        for( Triple<String, String, Integer> i : stackTraceAfterTest){
	        	if(!isLib(i.getFirst())){
	        		foundNonLib = true;
				}
				if(foundNonLib){
//	        		System.out.println(i);
					stackTraceAfterTestNoLib.add(i);
				}

			}
			sti.setTraceInfoAfterTestNoLib(stackTraceAfterTestNoLib);

	        //adding only unique staceTrace to reduce test set for non-assertion errors
//	        if(sti.getErrorMsg().contains(ExceptionsNames.ASSERTIONE) ||
//	        		!stackTraceAfterTestSet.contains(stackTraceAfterTest)){
	        	stackTraceAfterTestSet.add(stackTraceAfterTest);
	        	result.put(failNum, sti);
	        	String testName = sti.getTestFile().trim()+"."+sti.getTestNum().trim();
		        failIdToTestNameMap.put(failNum, testName);
		        testNameToFailIdMap.put(sti.getTestFile().trim()+"."+sti.getTestNum().trim(), failNum);
		        if (!CollectionUtil.isCollectionEmpty(sti.getTraceInfoAfterTest())){
		        	Triple<String, String, Integer> cut = sti.getTraceInfoAfterTest().get(sti.getTraceInfoAfterTest().size()-1);
		        	testNameToCUTMap.put(testName, cut.getFirst());
		        }
//	        }
	        failNum++;
		} //end of for
		return result;
	}

	private boolean isLib(String className) {
		return 	className.startsWith("junit") ||
				className.startsWith("sun") ||
				className.startsWith("java") ||
                className.startsWith("$junit") ||
                className.startsWith("$sun") ||
                className.startsWith("$java");
		}

	/**
	 * @param msg
	 */
	private void updateExceptionInfo(String msg) {
		String errorMsg = trimErrorMsg(msg);
		if(exceptionNumsMap.containsKey(errorMsg)){
			int num = exceptionNumsMap.get(errorMsg).intValue() + 1;
			exceptionNumsMap.put(errorMsg, num);
		} else{
			exceptionNumsMap.put(errorMsg, 1);
		}
//		System.out.println(errorMsg + "(" + exceptionNumsMap.get(errorMsg) + ")");
	}

	/**
	 * @param str
	 */
	private String trimErrorMsg(String str) {
		String result = null;
		StringTokenizer st = new StringTokenizer(str, ":");
		if(st.hasMoreElements()){
			result = st.nextElement().toString();
//			System.out.println("\ttoken: " + result);
//			System.out.println("\tmsg: " + str);
		} else {
			result = str;
		}
		return result;
	}

	/**
	 * @param testReportFile
	 * @return
	 */
	private List<String> listStackTraces(String testReportFile) {
		List<String> result = new ArrayList<String>();
		try {
			BufferedReader reader = new BufferedReader(new FileReader(testReportFile));
			String line = null, toAdd = null;
			StringBuffer sb = null;
			String lastErrorMsg = null;
			String startLine = null;
			while ((line = reader.readLine()) != null) {
				Pattern headLinePattern = Pattern.compile("(^[0-9]*\\)\\s)"); //1286) test5(RandoopTest0)
				Matcher headLineMatcher = headLinePattern.matcher(line);
				Pattern headLinePattern_d4j = Pattern.compile("^\\-\\-\\- (.+)::(.+)"); //--- org.jfree.data.general.junit.DatasetUtilitiesTests::testBug2849731_2
				Matcher headLineMatcher_d4j = headLinePattern_d4j.matcher(line);
				if (headLineMatcher.find()) {
					if(sb != null) {
						result.add(sb.toString());
					}
					startLine = line.substring(headLineMatcher.group(1).length());
					sb = new StringBuffer(startLine);
//					System.out.println("Headline: " + startLine);
//					System.out.println(line);
				} else {
					if(headLineMatcher_d4j.find()){
						if(sb != null) {
							result.add(sb.toString());
						}
						startLine = headLineMatcher_d4j.group(2)+ "(" + headLineMatcher_d4j.group(1) + ")"; // testBug2849731_2(org.jfree.data.general.junit.DatasetUtilitiesTests)
						sb = new StringBuffer(startLine);
//						System.out.println("Headline: " + startLine);
//						System.out.println(line);

					}
				}
//				if(testReportFile.contains("53b"))
//				System.out.println(line);
				if(sb != null &&
						(!line.trim().startsWith("at ") || line.trim().startsWith("Caused by: "))&&
						(((line.contains("java.")) && line.contains("Error")) ||
				line.contains("Exception") || line.contains("Assertion"))){ //java.lang.NullPointerException: blah
//					System.out.println("\tFOUND");
					lastErrorMsg = line;
//					sb.append(line);
				}
				if(sb != null && line.trim().startsWith("at ")){
                    if(lastErrorMsg!=null){
						if(lastErrorMsg.startsWith("Caused by: ")) {
							sb = new StringBuffer(startLine);
						}
//						System.out.println("lastErrorMsg: " + lastErrorMsg);
						sb.append(lastErrorMsg);
						lastErrorMsg = null;
                    }
					sb.append("\n" + line);
				}
			}
			if(sb != null)
				result.add(sb.toString());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * collects the top traces into a hash set first not to insert duplicate probes
	 * This method collects only NPE with non java library top stack trace at this point.
	 */
	public void collectTopStackTrace() {
		if(stInfos == null)
			throw new NullPointerException("stInfo should not be null at this point. please call collectStackTrace() first");
		topTraceSet = new HashSet<Triple<String,String,Integer>>();
		topTraceToTestIdMap = new HashMap<Triple<String,String,Integer>, Set<Integer>>();
		for( Integer testId : stInfos.keySet()){
			StackTraceInfo contents = stInfos.get(testId);
			//TODO Handles NPE only for now. Need to handle other types of errors in the future
			if(contents.getErrorMsg().contains(ExceptionNames.NPE)
//					|| contents.getErrorMsg().contains(ExceptionNames.AOBE)
//					|| contents.getErrorMsg().contains(ExceptionNames.IAE)
//					|| contents.getErrorMsg().contains(ExceptionNames.ISE)
					|| contents.getErrorMsg().contains(ExceptionNames.ASSERTIONE)) {
				List<Triple<String, String, Integer>> st = contents.getTraceInfo();
				if(st.size()==0)
					continue;
				Triple<String, String, Integer> topTrace = st.get(0);
				if(!topTrace.getFirst().trim().startsWith("java.") &&
//						!topTrace.getFirst().trim().startsWith("javax.") &&
						!topTrace.getFirst().trim().startsWith("sun.")) {
//						&& !topTrace.getFirst().trim().startsWith("org.junit.")){
					topTraceSet.add(topTrace);
					Set<Integer> value = null;
					if(topTraceToTestIdMap.containsKey(topTrace)){
						value = topTraceToTestIdMap.get(topTrace);
					} else {
						value = new HashSet<Integer>();
					}
					value.add(testId);
					topTraceToTestIdMap.put(topTrace, value);
				} else {
					System.err.println("W A R N I N G : top stack trace occurred in java library. Skipping " + topTrace.getFirst() +
							" for " + contents.getTestFile() + "." + contents.getTestNum());
				}

			}
		}
	}



}
