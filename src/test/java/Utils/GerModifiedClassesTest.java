package Utils;

import org.junit.Before;
import org.junit.Test;

public class GerModifiedClassesTest {
    @Before
    public void before() throws Exception{
        Config.initProperties();
    }
    @Test
    public void testGetClasses() throws Exception{
        GetModifiedClasses getModifiedClasses=new GetModifiedClasses();
        getModifiedClasses.processModifiedClasses();
    }
}
