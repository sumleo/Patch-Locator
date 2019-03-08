package d4j_analysis.fixedmethod_locator;

import d4j_analysis.fixedmethod_locator.Beans.Bucket;
import d4j_analysis.fixedmethod_locator.Beans.Patch;
import d4j_analysis.fixedmethod_locator.Utils.ResultFomatter;
import d4j_analysis.fixedmethod_locator.Utils.ResultParser;
import d4j_analysis.stacktrace.StackTraceInfo;
import d4j_analysis.stacktrace.StackTraceParser;
import util.PrintHelper;
import util.tuple.Triple;

import java.io.File;
import java.util.*;
import java.util.regex.Matcher;

/**
 * Created by mijung on 12/9/18.
 */
public class AnalysisMain {
    private static final String EXCEPTIONS_LIST = "EXCEPTIONS";
    private static String TRACE_HOME;
    private static String RESULT_PATH;
    /**
     * The patch which hits by the stack trace
     */
    private static LinkedList<Patch> HITED_PATCHES;
    /**
     * Buckets by the stackTraces
     */
    private static Set<Bucket> BUCKETS;
    /**
     * Version,ClassName,Patches
     */

    private static Map<String, Collection<StackTraceInfo>> exceptionST; //<d4j4evocontext/framework/projects/Chart/trigger_tests/2, its STInfo>

    public static void main(String[] args) {
        if (args.length < 2) {
            System.err.println("Arguments should be given as follows:");
            System.err.println("\t- Path to d4j trigger_tests (e.g., d4j4evocontext/framework/projects/Chart/trigger_tests) resultPath (e.g., /a/b/result.log)");
            System.err.println("\t- Path to result of Patch-locator jar file (e.g., /a/b/result.log)");
        }
        TRACE_HOME = args[0];
        RESULT_PATH = args[1];

        if (!TRACE_HOME.endsWith(File.pathSeparator))
            TRACE_HOME += File.separator;

        parseCallStacks(); //This fills exceptionST map.
//        PrintHelper.printCollectionMap(exceptionST, "exceptions");
        runAnalysis();
        showAnalyzeResult();
    }

    /**
     * print all the information of analysis
     */
    private static void showAnalyzeResult() {
        for (String filePath :
                exceptionST.keySet()) {
            for (StackTraceInfo stackTraceInfo : exceptionST.get(filePath)) {
                Set<Patch> patches = stackTraceInfo.getPatches();
                Iterator<Patch> iterator = patches.iterator();
                if (patches.size() == 0) {
                    String version = filePath.substring(filePath.lastIndexOf(File.separator) + 1);
                    String output = String.format("%s,%s,%s,%s,%s,%s,%s,%s",
                            HITED_PATCHES.get(0).getProjectName(), version, "N/A",
                            "N/A","N/A",
                            "N/A","N/A",
                            stackTraceInfo.getTestNum()
                    );
                    System.out.println(output);
                    continue;
                }
                Patch maxPatch = null;
                Patch minPatch = null;
                int max = Integer.MIN_VALUE;
                int min = Integer.MAX_VALUE;
                while (iterator.hasNext()) {
                    Patch tmp = iterator.next();
                    int depth = tmp.getStackTraceInfos().get(stackTraceInfo).intValue();
                    if (max < depth) {
                        max = depth;
                        maxPatch = tmp;
                    }
                    if (depth < min) {
                        min = depth;
                        minPatch = tmp;
                    }
                }
                String CUT = minPatch.getChangedFullClassName();
                Stack<Triple> CUTStack = new Stack<>();
                for (Triple trace : stackTraceInfo.getTraceInfoAfterTestNoLib()) {
                    String className = (String) trace.getFirst();
                    if (className.equals(CUT)) {
                        CUTStack.push(trace);
                    }
                }
                int depth = maxPatch.getStackTraceInfos().get(stackTraceInfo).intValue() - 1;
                int CUTDepth = stackTraceInfo.getPatches().size() - 1;
                int contextDepth = CUTStack.size() - 1;
                /**
                 * The output will be <project name,version,class name,method name,depth,cut depth,context depth, testNum>
                 */
                String output = String.format("%s,%s,%s,%s,%d,%d,%d,%s",
                        maxPatch.getProjectName(), maxPatch.getVersion(), maxPatch.getChangedFullClassName(),
                        maxPatch.getMethodName(), depth,
                        CUTDepth, contextDepth,
                        stackTraceInfo.getTestNum()
                );
                System.out.println(output);
            }
        }
    }


    private static void runAnalysis() {
        /**
         * Parse the result file and get result
         */
        ResultFomatter resultFomatter = new ResultFomatter(RESULT_PATH);
        HashMap<String, HashMap<String, LinkedList<Patch>>> versionClassPatch = resultFomatter.getResult();
        LinkedList<Patch> resultPathes = new LinkedList<>();
        /**
         * Go traverse the Stack Trace
         */
        for (String filePath : exceptionST.keySet()) {
            String version = filePath.substring(filePath.lastIndexOf(File.separator) + 1);
            HashMap<String, LinkedList<Patch>> classPatches = versionClassPatch.get(version);
            if (classPatches == null) {
                continue;
            }
            /**
             * Traver the stack trace
             */

            for (StackTraceInfo stackTraceInfo : exceptionST.get(filePath)) {
                List<Triple<String, String, Integer>> buggyClassMethodLines = stackTraceInfo.getTraceInfoAfterTestNoLib();

                /**
                 * Get stack infomation detail reversely
                 */
                for (int i = 0; i < buggyClassMethodLines.size();
                     i++) {
                    String className = (String) buggyClassMethodLines.get(i).getFirst();
                    /**
                     * Judge whether in the CUT
                     */
                    Set<Patch> flagToCheckWhetherContains = new HashSet<>();
                    if (classPatches.containsKey(className)) {
                        LinkedList<Patch> patchesInTheClass = classPatches.get(className);
                        for (Patch patch :
                                patchesInTheClass) {
                            if (patch.getMethodName().equals(buggyClassMethodLines.get(i).getSecond())) {
                                int crashedLine = Integer.parseInt(buggyClassMethodLines.get(i).getThird().toString());
                                int fixedStartLine = patch.getStartLine().intValue();
                                int fixedEndLine = patch.getEndLine().intValue();
                                /**
                                 * Whether hit in the trace
                                 */
                                if (version.equals(patch.getVersion()) && crashedLine <= fixedEndLine && crashedLine >= fixedStartLine && !flagToCheckWhetherContains.contains(patch)) {
                                    int depth = 1 + i;
                                    patch.getStackTraceInfos().put(
                                            stackTraceInfo, Integer.valueOf(depth));
                                    resultPathes.add(patch);
                                    stackTraceInfo.getPatches().add(patch);
                                    flagToCheckWhetherContains.add(patch);
//                                    if (version.equals("14"))
//                                        System.out.printf("%d,%d,%d,%d,%d\n", patch.getStartLine(), patch.getEndLine(),depth,i,stackTraceInfo.getTraceInfoAfterTest().size());
                                }
                            }
                        }
                    }
                }
            }
        }
        HITED_PATCHES = resultPathes;
    }


    private static void parseCallStacks() {
        StackTraceParser parser = new StackTraceParser();
        parser.parseExceptionBugs(TRACE_HOME, EXCEPTIONS_LIST);
        exceptionST = parser.getExceptionST();
    }
}
