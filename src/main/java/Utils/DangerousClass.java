package Utils;

public class DangerousClass {
    public void m1(boolean bool) throws Exception {
        if (bool) {
            System.out.println();
        } else {
            System.out.println(1);
        }
        throw new Exception("");
    }

    public void m2(boolean bool) {
        System.out.println("");
        if (bool) {
            System.out.println();
        } else {
            System.out.println(1);
        }
    }
}
