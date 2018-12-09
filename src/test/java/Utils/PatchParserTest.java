//package Utils;
//
//import org.junit.Test;
//
//import java.util.HashMap;
//import java.util.HashSet;
//
//public class PatchParserTest {
//    @Test
//    public void testPatchParser() throws Exception{
//        VersionGetter versionGetter=new VersionGetter();
//        versionGetter.processFolder();
//        HashMap<String,String> versions=versionGetter.getVersions();
//        System.out.println(versions.get("1"));
//        PatchParser patchParser=new PatchParser(versions.get("1"));
//        patchParser.processPatch();
//        for (String key:patchParser.getFilesPath()){
//            System.out.println(key);
//        }
//    }
//}
