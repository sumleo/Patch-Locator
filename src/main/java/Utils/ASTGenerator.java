package Utils;

import org.eclipse.jdt.core.dom.*;

import java.io.*;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.List;

/**
 * This is generate AST for a single .java file
 */
public class ASTGenerator {
    /**
     * Entry of The class
     *
     * @param fileName
     * @return
     */
    public static HashMap getMethodsAndBody(String fileName) throws Exception {
        String code = loadFile(fileName);
        return genResult(code);
    }

    /**
     * Generate hashmap <MethodName,Method BodyHash(MD5)>
     *
     * @param code
     * @return
     */
    protected static HashMap genResult(String code) throws Exception {
        //Init md5 encrypt
        MessageDigest messageDigest=MessageDigest.getInstance("MD5");
        ASTParser astParser = ASTParser.newParser(AST.JLS3);
        astParser.setSource(code.toCharArray());
        HashMap<String, String> results = new HashMap<String, String>();
        //This will generate the AST for Java File
        CompilationUnit compilationUnit = (CompilationUnit) astParser.createAST(null);
        List typesDeclared = compilationUnit.types();
        //Get Delared Method
        TypeDeclaration typeDeclaration = (TypeDeclaration) typesDeclared.get(0);
        MethodDeclaration[] methodDeclarations = typeDeclaration.getMethods();
        for (MethodDeclaration methodDeclaration : methodDeclarations) {
            try {
                messageDigest.update(methodDeclaration.getBody().toString().getBytes());
                results.put(methodDeclaration.getName().toString(), new BigInteger(1, messageDigest.digest()).toString(16));
            }catch (Exception e){
                
            }
        }
        return results;
    }

    /**
     * Load file to generate the code String
     *
     * @param fileName
     * @return
     */
    protected static String loadFile(String fileName) {
        String code = "";
        try {
            File file = new File(fileName);
            if (!file.exists()) {
                throw new RuntimeException("File " + fileName + " doesn't exist.");
            }
            if (!file.canRead()) {
                throw new RuntimeException("File " + fileName + "cann't be read");
            }
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            String line = "";
            while ((line = bufferedReader.readLine()) != null) {
                code += line + "\n";
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Load file failed!");
        }
        return code;
    }

}
