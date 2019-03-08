package d4j_analysis.fixedmethod_locator.Utils;

import d4j_analysis.fixedmethod_locator.Beans.Patch;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;

/**
 * (e.g. line format)Chart,17,org.jfree.data.time.TimeSeries,clone,,840,859,CHANGED
 */
public class ResultParser {
    private String filePath;
    private File resultFile;
    private ArrayList<Patch> patches = new ArrayList<Patch>();

    public ResultParser(String filePath) {
        this.filePath = filePath;
        this.resultFile = new File(filePath);
    }

    public ArrayList<Patch> getPatches() {
        return patches;
    }

    public void parse() {
        if (!resultFile.exists()) {
            System.out.println(String.format("%s does not exist.", this.filePath));
            return;
        }
        if (!resultFile.canRead()) {
            System.out.println(String.format("%s can not be read.", this.filePath));
            return;
        }
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(resultFile));
            String line = "";
            while ((line = bufferedReader.readLine()) != null) {
                if (line.length() == 0) {
                    continue;
                }
                String[] args = line.split(",");
                if (args.length != 8) {
                    System.out.println(line);
                    throw new Exception("The file format is not correct.");
                }
                Patch patch = new Patch();
                patch.setProjectName(args[0]);
                patch.setVersion(args[1]);
                patch.setChangedFullClassName(args[2]);
                patch.setMethodName(args[3]);
                patch.setParameters(args[4]);
                patch.setStartLine(Integer.parseInt(args[5]));
                patch.setEndLine(Integer.parseInt(args[6]));
                patch.setStatus(args[7]);
                patches.add(patch);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

}
