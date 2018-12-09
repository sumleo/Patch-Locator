package Utils;

import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.LinkedList;

public class PathMatcherTest {
    @Before
    public void before() throws Exception {
        Config.initProperties();
    }

//    @Test
//    public void testPathMather() throws Exception {
//        GetModifiedClasses getModifiedClasses = new GetModifiedClasses();
//        getModifiedClasses.processModifiedClasses();
//        HashMap<String, LinkedList<String>> modifiedClasses = getModifiedClasses.getModifiedClasses();
//        LinkedList<String> classes = modifiedClasses.get("1");
//        for (String classFile : classes) {
//            System.out.println(PathMatcher.match("/Users/liuyi/Desktop/Math_1b", classFile));
//        }
//    }
}
