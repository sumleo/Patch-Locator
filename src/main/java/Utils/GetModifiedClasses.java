package Utils;

import jdk.nashorn.internal.runtime.Version;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.LinkedList;

public class GetModifiedClasses {
    private HashMap<String, LinkedList<String>> modifiedClasses = new HashMap<String, LinkedList<String>>();

    public HashMap<String, LinkedList<String>> getModifiedClasses() {
        return modifiedClasses;
    }

    /**
     * Get Modified Classes All
     * @throws Exception
     */

    public void processModifiedClasses() throws Exception {
        String folderPath = Config.prop.getProperty("modifiedClassesPath");
        File dir = new File(folderPath);
        if (!dir.exists()) {
            throw new RuntimeException("The modifiled class folder does not exist.");
        }

        if (!dir.isDirectory()) {
            throw new RuntimeException("The input modified class folder is not directory.");
        }
        String[] raw_srcs = dir.list();
        LinkedList<String> srcs = new LinkedList<String>();
        for (String src : raw_srcs) {
            srcs.add(folderPath + File.separator + src);
        }
        for (String path : srcs) {
            File src = new File(path);
            BufferedReader bufferedReader = new BufferedReader(new FileReader(src));
            String line = "";
            String version = path.substring(path.lastIndexOf(File.separator) + 1).replace(CONSTANT.SRC_CHANGE, "");
            LinkedList<String> paths = new LinkedList<String>();
            while ((line = bufferedReader.readLine()) != null) {
                line = line.trim();
                paths.add(line.replace(".", File.separator)+CONSTANT.JAVA_SUFFIX);
            }
            this.modifiedClasses.put(version, paths);
        }
    }
}
