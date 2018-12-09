package Utils;

import java.io.File;
import java.util.HashMap;

public class VersionGetter {
    private String patchsFolder;
    private HashMap<String,String> versions=new HashMap<String, String>();

    /**
     * Load the patchesPath
     *
     * @throws Exception
     */
    public VersionGetter() throws Exception {
        Config.initProperties();
        this.patchsFolder = Config.prop.getProperty("patchesPath");
    }

    /**
     * Traverse the patch folder
     */
    protected void processFolder() {
        try {
            File dir = new File(this.patchsFolder);

            if (!dir.exists()) {
                throw new RuntimeException("The patch folder does not exist.");
            }

            if (!dir.isDirectory()) {
                throw new RuntimeException("The input patch folder is not directory.");
            }
            String[] patches = dir.list();
            //Filter the patches
            for (String patch:patches){
                if(patch.endsWith(CONSTANT.PATCH_SUFFIX)){
                    //check whether the file is end with /
                    if(this.patchsFolder.endsWith(File.separator)){
                        this.versions.put(patch.replace(CONSTANT.PATCH_SUFFIX,"").trim(),this.patchsFolder+patch);
                    }else {
                        this.versions.put(patch.replace(CONSTANT.PATCH_SUFFIX,"").trim(),this.patchsFolder+File.separator+patch);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public HashMap<String, String> getVersions() {
        return versions;
    }
}
