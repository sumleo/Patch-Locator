package d4j_analysis.stacktrace;

import util.PrintHelper;
import util.tuple.Triple;

import java.io.*;
import java.util.*;

/**
 * Created by mijung on 12/8/18.
 */
public class StackTraceParser {

    private static Map<String, Collection<StackTraceInfo>> exceptionST = new HashMap<>(); //<d4j4evocontext/framework/projects/Chart/trigger_tests/2, its STInfo>

    public static Map<String, Collection<StackTraceInfo>> getExceptionST() {
        return exceptionST;
    }

    public void printCallStack() {
        for (String bug : exceptionST.keySet()) {
            PrintHelper.printLine(bug);
            Collection<StackTraceInfo> infos = exceptionST.get(bug);

            System.out.println();
            for (StackTraceInfo i : infos) {
                List<Triple<String, String, Integer>> callStack = i.getTraceInfoAfterTest();
                PrintHelper.printCollection(callStack, "call stack size");
            }
            System.out.println();
        }
    }


    public static void parseExceptionBugs(String home, String file) {

        List<String> exceptionPaths = collectExceptionBugs(home, file);

        for (String p : exceptionPaths) {
//            System.out.println("path: " + p);
            exceptionST.put(p, new ArrayList<>());
            parseST(p);

        }


    }

    public static void parseExceptionBugs(String home, ArrayList<String> paths) {

        List<String> exceptionPaths = collectExceptionBugs(home, paths);

        for (String p : exceptionPaths) {
//            System.out.println("path: " + p);
            exceptionST.put(p, new ArrayList<>());
            parseST(p);
        }
        postHandle();
    }

    /**
     * Rename the name of stacktrace key
     */
    private static void postHandle() {
        Map result = new HashMap();
        for (String key : exceptionST.keySet()) {
            String version = "";
            if (key.lastIndexOf(File.separator) != -1) {
                version = key.substring(key.lastIndexOf(File.separator) + 1);
            } else {
                version = key;
            }
            result.put(version, exceptionST.get(key));
        }
        exceptionST = result;
    }

    private static void parseST(String file) {
        StackTraceCollector c = new StackTraceCollector();
        c.collectStackTrace(file);
        LinkedHashMap<Integer, StackTraceInfo> sti = c.getStInfos();
        for (Integer i : sti.keySet()) {
            StackTraceInfo st = sti.get(i);
            String errMsg = st.getErrorMsg();

            if (getTestName(st) != null) {
                exceptionST.get(file).add(st);

            }
        }
    }

    private static List<String> collectExceptionBugs(String home, String file) {
        BufferedReader br = null;
        List<String> result = new ArrayList<>();
        try {
            br = new BufferedReader(new FileReader(home + file));
            String line = br.readLine();
            while (line != null) {
                result.add(home + line); //d4j4evocontext/framework/projects/Chart/trigger_tests/2
                line = br.readLine();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    private static List<String> collectExceptionBugs(String home, ArrayList<String> paths) {
        BufferedReader br = null;
        List<String> result = new ArrayList<>();
        try {
            File dir = new File(home);
            if (!dir.isDirectory()) {
                System.err.println(String.format("The %s is not a folder", home));
            }
            for (File trace :
                    dir.listFiles()) {
                if (paths.contains(trace.getName()))
                    result.add(trace.getAbsolutePath());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;

    }

    private static String getTestName(StackTraceInfo st) {
        if (st.getTestFile() == null || st.getTestNum() == null
                || st.getTestFile().isEmpty() || st.getTestNum().isEmpty())
            return null;
        return st.getTestFile() + "." + st.getTestNum();
    }
}