/**
 *
 */
package d4j_analysis.stacktrace;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import d4j_analysis.fixedmethod_locator.Beans.Patch;
import util.tuple.Triple;

/**
 * @author mijung
 */
public class StackTraceInfo {
    private String testNum;
    private String testFile;
    private String errorMsg;
    private List<Triple<String, String, Integer>> traceInfo;
    private List<Triple<String, String, Integer>> traceInfoAfterTest; //trace after crash method in the test is invoked
    private List<Triple<String, String, Integer>> traceInfoAfterTestNoLib; //trace after crash method in the test is invoked, and removes lib stacks on top
    private Set<Patch> patches=new HashSet<>();

    public Set<Patch> getPatches() {
        return patches;
    }

    public void setPatches(Set<Patch> patches) {
        this.patches = patches;
    }

    /**
     * @return the testNum
     */
    public String getTestNum() {
        return testNum;
    }

    /**
     * @param testNum the testNum to set
     */
    public void setTestNum(String testNum) {
        this.testNum = testNum;
    }

    /**
     * @return the testFile
     */
    public String getTestFile() {
        return testFile;
    }

    /**
     * @param testFile the testFile to set
     */
    public void setTestFile(String testFile) {
        this.testFile = testFile;
    }

    /**
     * @return the errorMsg
     */
    public String getErrorMsg() {
        return errorMsg;
    }

    /**
     * @param errorMsg the errorMsg to set
     */
    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    /**
     * @return the traceInfo
     */
    public List<Triple<String, String, Integer>> getTraceInfo() {
        return traceInfo;
    }

    /**
     * @param traceInfo the traceInfo to set
     */
    public void setTraceInfo(List<Triple<String, String, Integer>> traceInfo) {
        this.traceInfo = traceInfo;
    }

    /**
     * @return the traceInfoAfterTestNoLib
     */
    public List<Triple<String, String, Integer>> getTraceInfoAfterTestNoLib() {
        return traceInfoAfterTestNoLib;
    }

    /**
     * @return the traceInfoAfterTest
     */
    public List<Triple<String, String, Integer>> getTraceInfoAfterTest() {
        return traceInfoAfterTest;
    }

    /**
     * @param traceInfoAfterTest the traceInfoAfterTest to set
     */
    public void setTraceInfoAfterTest(List<Triple<String, String, Integer>> traceInfoAfterTest) {
        this.traceInfoAfterTest = traceInfoAfterTest;
    }
    /**
     * @param traceInfoAfterTestNoLib the traceInfoAfterTest to set
     */
    public void setTraceInfoAfterTestNoLib(List<Triple<String, String, Integer>> traceInfoAfterTestNoLib) {
        this.traceInfoAfterTestNoLib = traceInfoAfterTestNoLib;
    }


    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append(testFile + "." + testNum + "(" + errorMsg + ")\n");
        for (Triple<String, String, Integer> frame : traceInfoAfterTest) {
            int lastindex = frame.getFirst().lastIndexOf(".");
            String classOnly = null;
            if (lastindex >= 0)
                classOnly = frame.getFirst().substring(lastindex + 1);
            else
                classOnly = frame.getFirst();
            result.append("\tat " + frame.getFirst() + "." + frame.getSecond() + "("
                    + classOnly +
                    ":" + frame.getThird() + ")\n");
        }
        result.append("\t======= NO LIB ===========\n");
        for (Triple<String, String, Integer> frame : traceInfoAfterTestNoLib) {
            int lastindex = frame.getFirst().lastIndexOf(".");
            String classOnly = null;
            if (lastindex >= 0)
                classOnly = frame.getFirst().substring(lastindex + 1);
            else
                classOnly = frame.getFirst();
            result.append("\tat " + frame.getFirst() + "." + frame.getSecond() + "("
                    + classOnly +
                    ":" + frame.getThird() + ")\n");
        }
        result.append("\t=============================\n");
        return result.toString();

    }

    @Override
    public boolean equals(Object o) {
        StackTraceInfo other = (StackTraceInfo) o;

        if (traceInfoAfterTest.size() != other.traceInfoAfterTest.size())
            return false;

        if (traceInfoAfterTest.equals(other.traceInfoAfterTest))
            return true;

        return false;
    }

}
