package d4j_analysis.goal_context_locator;

import d4j_analysis.goal_context_locator.Beans.CoverageGoal;
import d4j_analysis.goal_context_locator.Utils.Config;
import d4j_analysis.goal_context_locator.Utils.DifferService;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

public class Locator {
    public static void main(String[] args) {
        if (args.length < 3) {
            throw new IllegalArgumentException("The usage should be <path to triggered stacktrace> <path to ccbranch result> ccdepth");
        }
        parserCommandLine(args);
        run();
        analyze();
    }

    private static void parserCommandLine(String[] args) {
        Config.EXCEPTION_HOME = args[0];
        Config.GOALS_HOME = args[1];
        Config.CCDEPTH = Integer.parseInt(args[2]) + 1;
    }

    private static void run() {
        DifferService.initService();
        DifferService.run();
    }

    private static void analyze() {
        Map<String, ArrayList<CoverageGoal>> allGoals = DifferService.getMatchedAllGoals();
        Map<String, ArrayList<CoverageGoal>> coveredGoals = DifferService.getMatchedCoveredGoals();
        ArrayList<String> results = new ArrayList<>();
        Set<String> versions = allGoals.keySet();
        String header = String.format("Version,Covered Goals,All Goals");
        System.out.println(header);
        for (String version : versions) {
            ArrayList<CoverageGoal> allGoal = allGoals.get(version);
            ArrayList<CoverageGoal> coveredGoal = coveredGoals.get(version);
            String line = String.format("%s,%d,%d", version, coveredGoal.size(), allGoal.size());
            System.out.println(line);
        }
    }
}
