package Utils;

import Service.DifferService;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DifferServiceTest {
    @Test
    public void testDifferService() throws Exception {
        DifferService.differ("/Users/liuyi/Desktop/");
        ArrayList<ASTDiffer> astDiffers=DifferService.astDiffers;
        for(ASTDiffer astDiffer:astDiffers){
            System.out.println(astDiffer.getVersion());
            System.out.println(astDiffer.getOriginalClassPath().substring(astDiffer.getOriginalClassPath().lastIndexOf(File.separator)+1));
            List<String> modifiedMethods=astDiffer.getModifiedMethods();
            for(String method:modifiedMethods){
                System.out.println(method);
            }
        }
    }
}
