package d4j_analysis.goal_context_locator.Utils;

import d4j_analysis.goal_context_locator.Beans.CoverageGoal;
import d4j_analysis.stacktrace.StackTraceInfo;
import util.tuple.Triple;

import java.io.Serializable;
import java.util.*;

public class DifferService {

    /**
     * Generate from the goalutil and stacktrace util
     */
    private static Map<String, ArrayList<CoverageGoal>> allGoals;
    private static Map<String, ArrayList<CoverageGoal>> coveredGoals;
    private static Map<String, Collection<StackTraceInfo>> exceptions;
    /**
     * result of matched goals
     */
    private static Map<String, ArrayList<CoverageGoal>> matchedCoveredGoals = new HashMap<>();
    private static Map<String, ArrayList<CoverageGoal>> matchedAllGoals = new HashMap<>();


    /**
     * singleton for matched all goals
     *
     * @return
     */
    public static Map<String, ArrayList<CoverageGoal>> getMatchedAllGoals() {
        return matchedAllGoals;
    }

    /**
     * singleton for matched covered goals
     *
     * @return
     */
    public static Map<String, ArrayList<CoverageGoal>> getMatchedCoveredGoals() {
        return matchedCoveredGoals;
    }

    /**
     * Initial set up environment
     */
    public static void initService() {
        GoalUtil.init(Config.GOALS_HOME);
        StackTraceUtil.parseStackTrace(Config.EXCEPTION_HOME);
        allGoals = GoalUtil.getAllGoalsInstance();
        coveredGoals = GoalUtil.getCoveredGoalsInstance();
        exceptions = StackTraceUtil.getStackTracesInstance();
    }

    /**
     * run the analysis process
     */
    public static void run() {
        Set<String> versions = exceptions.keySet();
        for (String version : versions) {
            Collection<StackTraceInfo> stacktraces = exceptions.get(version);
            ArrayList<CoverageGoal> allGoal = allGoals.get(version);
            ArrayList<CoverageGoal> coveredGoal = coveredGoals.get(version);
            ArrayList<CoverageGoal> matchesCoveredGoal = getMatchedGoals(coveredGoal, stacktraces);
            ArrayList<CoverageGoal> matchesAllGoal = getMatchedGoals(allGoal, stacktraces);
            matchedAllGoals.put(version, matchesAllGoal);
            matchedCoveredGoals.put(version, matchesCoveredGoal);
        }
    }

    /**
     * get matched goals for specific version
     *
     * @param goals
     * @param traces
     * @return
     */
    private static ArrayList<CoverageGoal> getMatchedGoals(ArrayList<CoverageGoal> goals, Collection<StackTraceInfo> traces) {
        ArrayList<CoverageGoal> results = new ArrayList<>();
        if (goals == null || traces == null)
            return results;
        for (CoverageGoal coverageGoal : goals) {
            for (StackTraceInfo stackTraceInfo : traces) {
                boolean match = matches(coverageGoal, stackTraceInfo);
                if (match) {
                    results.add(coverageGoal);
                }
            }
        }
        return results;
    }

    /**
     * judge the goal matches the stacktrace or not
     *
     * @param goal
     * @param stackTraceInfo
     * @return
     */
    private static boolean matches(CoverageGoal goal, StackTraceInfo stackTraceInfo) {
        if (goal == null || stackTraceInfo == null)
            return false;
        List<Triple<String, String, Integer>> traces = stackTraceInfo.getTraceInfoAfterTestNoLib();
        String[] contexts = goal.getContext();
        if (traces.size() == 0) {
            return false;
        }
        int count = 0;
        for (String context : contexts) {
            for (Triple frame : traces) {
                String method = frame.getFirst() + ":" + frame.getSecond();
                if (context.startsWith(method)) {
                    count++;
                    break;
                }
            }
        }
        return count == Config.CCDEPTH;
    }
}
