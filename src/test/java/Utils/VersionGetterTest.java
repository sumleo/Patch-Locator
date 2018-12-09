package Utils;

import org.junit.Test;

public class VersionGetterTest {
    @Test
    public void testConfig() throws Exception{
        Config.initProperties();
        System.out.println(Config.prop.getProperty("patchesPath"));
    }
//    @Test
//    public void testVersionGetter() throws Exception{
//        VersionGetter versionGetter=new VersionGetter();
//        versionGetter.processFolder();
//        for (String version:versionGetter.getVersions().keySet()){
//            System.out.printf("%s %s\n",version,versionGetter.getVersions().get(version));
//        }
//    }
}
