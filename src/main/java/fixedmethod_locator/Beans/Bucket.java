package d4j_analysis.fixedmethod_locator.Beans;

import d4j_analysis.stacktrace.StackTraceInfo;

import java.util.LinkedList;

public class Bucket {
    private StackTraceInfo stackTraceInfo;
    private LinkedList<Patch> patches=new LinkedList<>();

    public StackTraceInfo getStackTraceInfo() {
        return stackTraceInfo;
    }

    public void setStackTraceInfo(StackTraceInfo stackTraceInfo) {
        this.stackTraceInfo = stackTraceInfo;
    }

    public LinkedList<Patch> getPatches() {
        if(this.patches==null)
            this.patches=new LinkedList<Patch>();
        return patches;
    }

    public void setPatches(LinkedList<Patch> patches) {
        this.patches = patches;
    }
}
