package Service;

import Utils.ASTDiffer;
import Utils.Config;
import jdk.nashorn.internal.runtime.regexp.RegExp;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
            HashMap<String, String> fixedPositions = astDiffer.getPositions();
            for (String modified : modifiedMethods
            ) {

                String[] methodNameAndParamters = new String[2];
                if (modified.split(":").length == 2) {
                    methodNameAndParamters = modified.split(":");
                } else {
                    methodNameAndParamters[0] = modified.split(":")[0];
                    methodNameAndParamters[1] = "";
                }
                System.out.printf("%s,%s,%s,%s,%s,%s,CHANGED\n", projectID, version, astDiffer.getOriginalClassPath(), methodNameAndParamters[0], methodNameAndParamters[1].replace(",", ""), fixedPositions.get(modified));
            }
            for (String remove : removedMethods
            ) {
                String[] methodNameAndParamters = new String[2];
                if (remove.split(":").length == 2) {
                    methodNameAndParamters = remove.split(":");
                } else {
                    methodNameAndParamters[0] = remove.split(":")[0];
                    methodNameAndParamters[1] = "";
                }
                System.out.printf("%s,%s,%s,%s,%s,REMOVED\n", projectID, version, astDiffer.getOriginalClassPath(), methodNameAndParamters[0], methodNameAndParamters[1], fixedPositions.get(remove));
            }
        }
    }

}
