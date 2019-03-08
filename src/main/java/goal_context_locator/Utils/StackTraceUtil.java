package d4j_analysis.goal_context_locator.Utils;

import d4j_analysis.stacktrace.StackTraceInfo;
import d4j_analysis.stacktrace.StackTraceParser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class StackTraceUtil {
    /**
     * stacktraces
     */
    private static Map<String, Collection<StackTraceInfo>> stackTraces;

    /**
     * The input should be the
     *
     * @param home
     */
    public static void parseStackTrace(String home) {
        ArrayList<String> paths = getExceptionVersion(home);
        StackTraceParser.parseExceptionBugs(home, paths);
        stackTraces = StackTraceParser.getExceptionST();
    }

    /**
     * read exception version from the exception file
     *
     * @param home
     * @return
     */
    private static ArrayList<String> getExceptionVersion(String home) {
        ArrayList<String> results = new ArrayList<>();
        String formatedHome = home.endsWith(File.separator) ? home : home + File.separator;
        File excepetionFile = new File(formatedHome + Config.EXCEPTION_FILE);
        if (!excepetionFile.exists()) {
            System.err.println(String.format("The %s is not exist.", excepetionFile.getAbsolutePath()));
        }
        try {
            BufferedReader bf = new BufferedReader(new FileReader(excepetionFile));
            String line = "";
            while ((line = bf.readLine()) != null) {
                results.add(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return results;
    }

    /**
     * singleton for stacktraces
     *
     * @return
     */
    public static Map<String, Collection<StackTraceInfo>> getStackTracesInstance() {
        return stackTraces;
    }
}
