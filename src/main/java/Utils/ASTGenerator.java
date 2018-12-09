package Utils;

import org.eclipse.jdt.core.dom.*;
import org.w3c.dom.NodeList;

import java.io.*;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.List;

/**
 * This is generate AST for a single .java file
 */
public class ASTGenerator {
    private HashMap<String, String> positions = new HashMap<String, String>();

    public HashMap<String, String> getPositions() {
        return positions;
    }

    public void setPositions(HashMap<String, String> positions) {
        this.positions = positions;
    }

    /**
     * Entry of The class
     *
     * @param fileName
     * @return
     */
    public HashMap getMethodsAndBody(String fileName) throws Exception {
        String code = loadFile(fileName);
        return genResult(code);
    }

    /**
     * Generate hashmap <MethodName,Method BodyHash(MD5)>
     *
     * @param code
     * @return
     */
    protected HashMap genResult(String code) throws Exception {
        //Init md5 encrypt
        MessageDigest messageDigest = MessageDigest.getInstance("MD5");
        ASTParser astParser = ASTParser.newParser(AST.JLS3);
        astParser.setKind(ASTParser.K_COMPILATION_UNIT);
        astParser.setResolveBindings(true);
        astParser.setSource(code.toCharArray());
        HashMap<String, String> results = new HashMap<String, String>();
        //This will generate the AST for Java File
        CompilationUnit compilationUnit = (CompilationUnit) astParser.createAST(null);
        List typesDeclared = compilationUnit.types();
        //Get Delared Method
        TypeDeclaration typeDeclaration = (TypeDeclaration) typesDeclared.get(0);
        MethodDeclaration[] methodDeclarations = typeDeclaration.getMethods();
        for (MethodDeclaration methodDeclaration : methodDeclarations) {
            String reloadPar = "";
            for (Object parm : methodDeclaration.parameters()) {
                reloadPar += parm.toString() + ",";
            }
            String positionLine = String.format("StartLineNum:%d, ENDLINE:%d, Length:%d",
                    compilationUnit.getLineNumber(methodDeclaration.getStartPosition()) - 1,
                    compilationUnit.getLineNumber(methodDeclaration.getStartPosition() + methodDeclaration.getLength()) - 1,
                    methodDeclaration.getLength()
            );
            if (methodDeclaration.getBody() == null) {
                messageDigest.update((methodDeclaration.getName() + ":" + reloadPar).getBytes());
                results.put(methodDeclaration.getName() + ":" + reloadPar, new BigInteger(1, messageDigest.digest()).toString(16));
            } else {
                messageDigest.update((methodDeclaration.getBody().toString()).getBytes());
                results.put(methodDeclaration.getName() + ":" + reloadPar, new BigInteger(1, messageDigest.digest()).toString(16));
            }
            this.positions.put(methodDeclaration.getName() + ":" + reloadPar, positionLine);
        }

        return results;
    }

    /**
     * Load file to generate the code String
     *
     * @param fileName
     * @return
     */
    protected String loadFile(String fileName) {
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
