package Service;

import Utils.ASTDiffer;
import Utils.Config;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Bootstrap {
    public static ArrayList<ASTDiffer> astDiffers = new ArrayList<ASTDiffer>();

    public static void init() throws Exception {
        InitService.init();
        String sourceBaseDir = Config.prop.getProperty("sourceBaseDir");
        DifferService.differ(sourceBaseDir);
        astDiffers = DifferService.astDiffers;
    }

    public static void printResult() {
        for (ASTDiffer astDiffer : astDiffers
        ) {
            String version = astDiffer.getVersion();
            String projectID = Config.prop.getProperty("projectID");
            List<String> modifiedMethods = astDiffer.getModifiedMethods();
            List<String> removedMethods = astDiffer.getRemovedMethods();
            String className = astDiffer.getOriginalClassPath().substring(astDiffer.getOriginalClassPath().lastIndexOf(File.separator) + 1);
            for (String modified : modifiedMethods
            ) {
                System.out.printf("%s %s %s %s CHANGED\n", projectID, version, className, modified);
            }
            for (String remove : removedMethods
            ) {
                System.out.printf("%s %s %s %s REMOVED\n", projectID, version, className, remove);
            }
        }
    }

}
