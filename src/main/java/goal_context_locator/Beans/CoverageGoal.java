package d4j_analysis.goal_context_locator.Beans;

public class CoverageGoal {
    private String branch;
    private String rawContext;
    private String[] context;
    private int CUTDist;

    public int getCUTDist() {
        return CUTDist;
    }

    public void setCUTDist(int CUTDist) {
        this.CUTDist = CUTDist;
    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public String getRawContext() {
        return rawContext;
    }

    public void setRawContext(String rawContext) {
        this.rawContext = rawContext;
    }

    public String[] getContext() {
        return context;
    }

    public void setContext(String[] context) {
        this.context = context;
    }

    @Override
    public String toString() {
        String ctx = "";
        for (String context : this.context) {
            ctx += context + " ";
        }
        return branch + " " + ctx + "CUT dist : " + this.CUTDist;
    }

    @Override
    public boolean equals(Object obj) {
        return this.toString().equals(obj.toString());
    }
}
