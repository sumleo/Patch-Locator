package Utils;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class ASTDiffer {
    private String originalClassPath;
    private String modifiedClassPath;
    private String version;
    private List<String> unmodifiedMethods = new LinkedList<String>();
    private List<String> modifiedMethods = new LinkedList<String>();
    private List<String> removedMethods = new LinkedList<String>();
    private List<String> newAddedMethods = new LinkedList<String>();
    private HashMap<String, String> originalClassMethods = new HashMap<String, String>();
    private HashMap<String, String> modifiedClassMethods = new HashMap<String, String>();
    private HashMap<String, String> positions = new HashMap<String, String>();


    /**
     * Init the AST Differ
     *
     * @param originalClassPath
     * @param modifiedClassPath
     */
    public ASTDiffer(String originalClassPath, String modifiedClassPath) {
        this.originalClassPath = originalClassPath;
        this.modifiedClassPath = modifiedClassPath;
    }


    public HashMap<String, String> getPositions() {
        return positions;
    }

    public void setPositions(HashMap<String, String> positions) {
        this.positions = positions;
    }

    public String getOriginalClassPath() {
        return originalClassPath;
    }

    public String getModifiedClassPath() {
        return modifiedClassPath;
    }

    /**
     * Process the differ Content
     */
    public void process(HashMap<String, String> originalMethods, HashMap<String, String> modifiedMethodsPath) {
        try {
            HashMap<String, String> originalClassMethods = originalMethods;
            HashMap<String, String> modifiedClassPath = modifiedMethodsPath;
            for (String method : originalClassMethods.keySet()) {
                if (modifiedClassPath.containsKey(method)) {
                    if (!modifiedClassPath.get(method).equals(originalClassMethods.get(method))) {
                        modifiedMethods.add(method);
                    } else {
                        unmodifiedMethods.add(method);
                    }
                } else {
                    removedMethods.add(method);
                }
            }
            for (String method : modifiedClassPath.keySet()) {
                if (!originalClassMethods.containsKey(method)) {
                    newAddedMethods.add(method);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<String> getUnmodifiedMethods() {
        return unmodifiedMethods;
    }


    public List<String> getModifiedMethods() {
        return modifiedMethods;
    }


    public List<String> getRemovedMethods() {
        return removedMethods;
    }

    public List<String> getNewAddedMethods() {
        return newAddedMethods;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
