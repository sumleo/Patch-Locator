package d4j_analysis.fixedmethod_locator.Beans;

import d4j_analysis.stacktrace.StackTraceInfo;

import java.util.HashMap;
import java.util.LinkedList;

public class Patch {
    private String projectName;
    private String version;
    private String changedFullClassName;
    private String methodName;
    private String parameters;
    private Integer startLine;
    private Integer endLine;
    private String status;
    private HashMap<StackTraceInfo,Integer> stackTraceInfos = new HashMap<>();

    public HashMap<StackTraceInfo, Integer> getStackTraceInfos() {
        return stackTraceInfos;
    }

    public void setStackTraceInfos(HashMap<StackTraceInfo, Integer> stackTraceInfos) {
        this.stackTraceInfos = stackTraceInfos;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getChangedFullClassName() {
        return changedFullClassName;
    }

    public void setChangedFullClassName(String changedFullClassName) {
        this.changedFullClassName = changedFullClassName;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public String getParameters() {
        return parameters;
    }

    public void setParameters(String parameters) {
        this.parameters = parameters;
    }

    public Integer getStartLine() {
        return startLine;
    }

    public void setStartLine(Integer startLine) {
        this.startLine = startLine;
    }

    public Integer getEndLine() {
        return endLine;
    }

    public void setEndLine(Integer endLine) {
        this.endLine = endLine;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return String.format("ClassName:%s,MethodName:%s,StartLine:%d,EndLine:%d", this.changedFullClassName, this.methodName, this.startLine, this.endLine);
    }
}
