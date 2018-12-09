import Service.Bootstrap;

public class PatchLocator {
    public static void main(String[] args) {
        try {
            Bootstrap.init();
            Bootstrap.printResult();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
