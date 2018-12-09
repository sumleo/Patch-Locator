package Service;

import Utils.*;

import java.io.File;
import java.util.*;

public class DifferService {
    public static ArrayList<ASTDiffer> astDiffers = new ArrayList<ASTDiffer>();
    public static Set<String> nonExistDirs = new HashSet<String>();

    public static void differ(String basePath) throws Exception {
        //Init for set up
        InitService.init();
        GetModifiedClasses getModifiedClasses = new GetModifiedClasses();
        getModifiedClasses.processModifiedClasses();
        HashMap<String, LinkedList<String>> modifiedClassesWithVersion = getModifiedClasses.getModifiedClasses();
        //Version for source code
        Set<String> versions = modifiedClassesWithVersion.keySet();
        //Get project ID
        String projectID = Config.prop.getProperty("projectID");
        //Traverse the basePath
        for (String version : versions) {
            String bugVersionDir = basePath + File.separator + projectID + CONSTANT.PROJECT_DIR_UNDERSCORE + version + CONSTANT.BUG_SUFFIX;
            String fixedVersionDir = basePath + File.separator + projectID + CONSTANT.PROJECT_DIR_UNDERSCORE + version + CONSTANT.FIXED_SUFFIX;
            File bugDir = new File(bugVersionDir);
            File fixedDir = new File(fixedVersionDir);
            LinkedList<String> modifiedClasses = modifiedClassesWithVersion.get(version);
            for (String modifiedClass : modifiedClasses) {
                if (!bugDir.exists() || !fixedDir.exists()) {
                    nonExistDirs.add(bugVersionDir);
                    nonExistDirs.add(fixedVersionDir);
                    continue;
                }
                String bugAbsPath = PathMatcher.match(bugVersionDir, modifiedClass);
                String fixedAbsPath = PathMatcher.match(fixedVersionDir, modifiedClass);
                HashMap<String, String> bugMethods = ASTGenerator.getMethodsAndBody(bugAbsPath);
                HashMap<String, String> fixedMethods = ASTGenerator.getMethodsAndBody(fixedAbsPath);
                ASTDiffer astDiffer = new ASTDiffer(bugAbsPath, fixedAbsPath);
                astDiffer.process(bugMethods, fixedMethods);
                astDiffer.setVersion(version);
                astDiffers.add(astDiffer);
            }
        }
    }
}
