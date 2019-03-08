package d4j_analysis.goal_context_locator.Utils;

import d4j_analysis.goal_context_locator.Beans.CCBranchCoverageGoal;
import d4j_analysis.goal_context_locator.Beans.CoverageGoal;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.*;

public class GoalUtil {
    /**
     * Covered goals
     */
    private static Map<String, ArrayList<CoverageGoal>> coveredGoals = new HashMap<>();
    /**
     * All goals
     */
    private static Map<String, ArrayList<CoverageGoal>> allGoals = new HashMap<>();

    /**
     * read goal file and get lines
     *
     * @param path
     * @return
     */
    private static ArrayList<String> readGoalFile(String path) {
        File file = new File(path);
        ArrayList<String> lines = new ArrayList();
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line = null;
            while ((line = br.readLine()) != null) {
                assert line.length() != 0;
                lines.add(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lines;
    }

    /**
     * list dir and get the version and path
     *
     * @param path
     * @return
     */
    private static ArrayList getAllVersionPath(String path) {
        ArrayList<String> result = new ArrayList<>();
        File dir = new File(path);
        if (!dir.isDirectory() || !dir.exists()) {
            System.err.println(String.format("The %s is not exist", path));
        }
        try {
            for (File version : dir.listFiles()) {
                result.add(version.getAbsolutePath());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * get version like "2f"->"2"
     *
     * @param path
     * @return
     */
    private static String getVersion(String path) {
        return path.lastIndexOf(File.separator) != -1 ? path.substring(path.lastIndexOf(File.separator) + 1, path.length() - 1) : path.substring(0, path.length() - 1);
    }

    /**
     * get ccbranch all goals for specific version
     *
     * @param path
     * @return
     */
    public static ArrayList getCCBranchAllGoals(String path) {
        if (!path.endsWith(File.separator)) {
            path += File.separator;
        }
        path += Config.ALL_GOALS;
        ArrayList<CoverageGoal> ccBranchCoverageGoals = new ArrayList<>();
        ArrayList<String> rawGoals = readGoalFile(path);
        Set<String> unique = new HashSet<>();
        for (String goal : rawGoals) {
            /**
             * Branch org.jfree.chart.JFreeChart.fireChartChanged()V: root-Branch in context: org.jfree.chart.JFreeChart:setBackgroundPaint(Ljava/awt/Paint;)V org.jfree.chart.JFreeChart:fireChartChanged()V at distance to CUT : 1
             */
            if (unique.contains(goal)) {
                continue;
            } else {
                unique.add(goal);
            }
            String[] rawGoal = goal.split(Config.IN_CONTEXT);
            if (rawGoal.length != 2) {
                continue;
            }
            assert rawGoal.length == 2;
            CoverageGoal coverageGoal = new CCBranchCoverageGoal();
            coverageGoal.setBranch(rawGoal[0].trim());
            String[] contextAndDist = rawGoal[1].split(Config.CUT_DIST);
            assert contextAndDist.length == 2;
            coverageGoal.setCUTDist(Integer.parseInt(contextAndDist[1]));
            String[] context = contextAndDist[0].trim().split(" ");
            coverageGoal.setContext(context);
            coverageGoal.setRawContext(contextAndDist[0]);
            ccBranchCoverageGoals.add(coverageGoal);
        }
        return ccBranchCoverageGoals;
    }

    /**
     * get ccbranch covered goals for specific version
     *
     * @param path
     * @return
     */
    public static ArrayList getCCBranchCoveredGoals(String path) {
        if (!path.endsWith(File.separator)) {
            path += File.separator;
        }
        path += Config.COVERED_GOALS;
        ArrayList<CoverageGoal> ccBranchCoverageGoals = new ArrayList<>();
        ArrayList<String> rawGoals = readGoalFile(path);
        Set<String> unique = new HashSet<>();
        for (String goal : rawGoals) {
            /**
             * test000,Branch org.jfree.chart.JFreeChart.fireChartChanged()V: root-Branch in context: org.jfree.chart.JFreeChart:setBackgroundPaint(Ljava/awt/Paint;)V org.jfree.chart.JFreeChart:fireChartChanged()V at distance to CUT : 1
             */
            String[] cutHeader = goal.split(",");
            assert cutHeader.length == 2;
            goal = cutHeader[1];
            if (unique.contains(goal)) {
                continue;
            } else {
                unique.add(goal);
            }
            String[] rawGoal = goal.split(Config.IN_CONTEXT);
            if (rawGoal.length != 2) {
                continue;
            }
            assert rawGoal.length == 2;
            CoverageGoal coverageGoal = new CCBranchCoverageGoal();
            coverageGoal.setBranch(rawGoal[0].trim());
            String[] contextAndDist = rawGoal[1].split(Config.CUT_DIST);
            assert contextAndDist.length == 2;
            coverageGoal.setCUTDist(Integer.parseInt(contextAndDist[1]));
            String[] context = contextAndDist[0].trim().split(" ");
            coverageGoal.setContext(context);
            coverageGoal.setRawContext(contextAndDist[0]);
            ccBranchCoverageGoals.add(coverageGoal);
        }
        return ccBranchCoverageGoals;
    }

    /**
     * initial the all goals and covered goals
     *
     * @param path
     */
    public static void init(String path) {
        ArrayList<String> versionPath = getAllVersionPath(path);
        for (String verisionPath : versionPath) {
            String version = getVersion(verisionPath);
            ArrayList<CoverageGoal> allCovereageGoals = getCCBranchAllGoals(verisionPath);
            ArrayList<CoverageGoal> coverageGoals = getCCBranchCoveredGoals(verisionPath);
            coveredGoals.put(version, coverageGoals);
            allGoals.put(version, allCovereageGoals);
        }
    }

    /**
     * singleton for allGoals
     *
     * @return
     */
    public static Map<String, ArrayList<CoverageGoal>> getAllGoalsInstance() {
        return allGoals;
    }

    /**
     * singleton for coveredGoals
     *
     * @return
     */
    public static Map<String, ArrayList<CoverageGoal>> getCoveredGoalsInstance() {
        return coveredGoals;
    }
}
