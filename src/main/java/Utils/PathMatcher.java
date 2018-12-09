package Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.Queue;

public class PathMatcher {
    /**
     * Match the Target Path And Get the Abs path
     * @param baseDir
     * @param target
     * @return
     * @throws Exception
     */
    public static String match(String baseDir, String target) throws Exception {
        String result = null;
        File dir = new File(baseDir);
        if (!dir.exists()) {
            throw new RuntimeException("The base dir does not exist.");
        }
        if (!dir.isDirectory()) {
            throw new RuntimeException("The base dir is not a folder.");
        }
        if (!dir.canRead()) {
            throw new RuntimeException("The base dir can not be read.");
        }
        if (target.length() < 1) {
            throw new RuntimeException("The target length cann't less than 1.");
        }
        ArrayList<File> files = new ArrayList<File>();
        files.add(dir);
        while (!files.isEmpty()) {
            File file = files.get(0);
            files.remove(file);
            if (file.isDirectory()) {
                for (String subDir : file.list()) {
                    files.add(new File(file.getAbsolutePath() + File.separator + subDir));
                }
            } else {
                if (file.getAbsolutePath().contains(target)) {
                    result = file.getAbsolutePath();
                    break;
                }
            }
        }
        return result;
    }
}
