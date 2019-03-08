package d4j_analysis.fixedmethod_locator.Utils;

import d4j_analysis.fixedmethod_locator.Beans.Patch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

/**
 * Generate the map for <Version(String),ClassName(String),Patch(Patch)>
 */
public class ResultFomatter {
    private ArrayList<Patch> patches;
    private ResultParser resultParser;

    public ResultFomatter(String filePath) {
        this.resultParser = new ResultParser(filePath);
    }


    public HashMap getResult() {
        this.run();
        if (this.patches == null) {
            System.out.println("Get patches error.");
            return null;
        }
        HashMap<String, HashMap<String, LinkedList<Patch>>> results = new HashMap<>();
        for (Patch patch : patches) {
            String version = patch.getVersion();
            if (results.containsKey(version)) {
                HashMap<String, LinkedList<Patch>> classAndPatch = results.get(version);
                if (classAndPatch.containsKey(patch.getChangedFullClassName())) {
                    LinkedList<Patch> sameClassPatches = classAndPatch.get(patch.getChangedFullClassName());
                    sameClassPatches.add(patch);
                } else {
                    LinkedList<Patch> sameClassPatches = new LinkedList<>();
                    sameClassPatches.add(patch);
                    classAndPatch.put(patch.getChangedFullClassName(), sameClassPatches);
                }
            } else {
                HashMap<String, LinkedList<Patch>> classAndPatch = new HashMap<>();
                LinkedList<Patch> patchLinkedList = new LinkedList<>();
                patchLinkedList.add(patch);
                classAndPatch.put(patch.getChangedFullClassName(), patchLinkedList);
                results.put(patch.getVersion(), classAndPatch);
            }
        }
        return results;
    }

    /**
     * Get patches list
     */
    private void run() {
        this.resultParser.parse();
        this.patches = resultParser.getPatches();
    }

}
