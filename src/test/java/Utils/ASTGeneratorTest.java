package Utils;

import org.eclipse.jdt.core.dom.ASTParser;
import org.junit.Test;

import java.net.URL;
import java.util.HashMap;
import java.util.List;


public class ASTGeneratorTest {
    @Test
    public void testAnalysis() throws Exception {
        String code = "package Utils;\n" +
                "\n" +
                "import org.eclipse.jdt.core.dom.AST;\n" +
                "import org.eclipse.jdt.core.dom.ASTParser;\n" +
                "\n" +
                "/**\n" +
                " * This is generate AST for a single .java file\n" +
                " */\n" +
                "public class ASTGenerator {\n" +
                "    public static void analyze(){\n" +
                "        ASTParser astParser=ASTParser.newParser(AST.JLS3);\n" +
                "        astParser.setSource(\"public class ASTGenerator {}\".toCharArray());\n" +
                "    }\n" +
                "}\n";
        ASTGenerator a = new ASTGenerator();
        a.genResult(code);
    }

    @Test
    public void testReadFile() {
        ASTGenerator a = new ASTGenerator();
        System.out.println(a.loadFile("/Users/liuyi/IdeaProjects/patchlocator/src/main/java/Utils/ASTGenerator.java"));
    }

    @Test
    public void testDifferentClass() throws Exception {
        String testA = "/Users/liuyi/IdeaProjects/patchlocator/src/test/java/Utils/TestA.java";
        String testB = "/Users/liuyi/IdeaProjects/patchlocator/src/test/java/Utils/TestB.java";
        ASTGenerator a = new ASTGenerator();
        ASTGenerator b = new ASTGenerator();
        HashMap<String, String> hashA = a.getMethodsAndBody(testA);
        HashMap<String, String> hashB = b.getMethodsAndBody(testB);
        for (String name : hashA.keySet()) {
            System.out.printf("%s %s \n", name, hashA.get(name));
        }
        System.out.println("After");
        for (String name : hashB.keySet()) {
            System.out.printf("%s %s \n", name, hashB.get(name));
        }
    }

    @Test
    public void testASTDiffer() throws Exception {
        String testA = "/Users/liuyi/IdeaProjects/patchlocator/src/test/java/Utils/TestA.java";
        String testB = "/Users/liuyi/IdeaProjects/patchlocator/src/test/java/Utils/TestB.java";
        ASTDiffer astDiffer = new ASTDiffer(testA, testB);
        ASTGenerator a = new ASTGenerator();
        ASTGenerator b = new ASTGenerator();
        HashMap<String, String> hashA = a.getMethodsAndBody(testA);
        HashMap<String, String> hashB = b.getMethodsAndBody(testB);  astDiffer.process(hashA, hashB);
        List<String> modifiedMethods = astDiffer.getModifiedMethods();
        List<String> newAddedMethods = astDiffer.getNewAddedMethods();
        List<String> removedMethods = astDiffer.getRemovedMethods();
        List<String> unmodifiedMethods = astDiffer.getUnmodifiedMethods();
        System.out.println("ModifiedMethods");
        for (String method : modifiedMethods) {
            System.out.println(method);
        }
        System.out.println("newAddedMethods");
        for (String method : newAddedMethods) {
            System.out.println(method);
        }
        System.out.println("removedMethods");
        for (String method : removedMethods) {
            System.out.println(method);
        }
        System.out.println("unmodifiedMethods");
        for (String method : unmodifiedMethods) {
            System.out.println(method);
        }
    }

    @Test
    public void testASTDifferConstructor() throws Exception {
        String testA = "/Users/liuyi/IdeaProjects/patchlocator/src/test/java/Utils/TestA.java";
        String testB = "/Users/liuyi/IdeaProjects/patchlocator/src/test/java/testDifferenName/TestA.java";
        ASTDiffer astDiffer = new ASTDiffer(testA, testB);
        ASTGenerator a = new ASTGenerator();
        ASTGenerator b = new ASTGenerator();
        HashMap<String, String> hashA = a.getMethodsAndBody(testA);
        HashMap<String, String> hashB = b.getMethodsAndBody(testB);
        astDiffer.process(hashA, hashB);
        List<String> modifiedMethods = astDiffer.getModifiedMethods();
        List<String> newAddedMethods = astDiffer.getNewAddedMethods();
        List<String> removedMethods = astDiffer.getRemovedMethods();
        List<String> unmodifiedMethods = astDiffer.getUnmodifiedMethods();
        System.out.println("ModifiedMethods");
        for (String method : modifiedMethods) {
            System.out.println(method);
        }
        System.out.println("newAddedMethods");
        for (String method : newAddedMethods) {
            System.out.println(method);
        }
        System.out.println("removedMethods");
        for (String method : removedMethods) {
            System.out.println(method);
        }
        System.out.println("unmodifiedMethods");
        for (String method : unmodifiedMethods) {
            System.out.println(method);
        }
    }
}
