package Utils;

import Service.Bootstrap;
import org.junit.Test;

import java.math.BigInteger;
import java.security.MessageDigest;

public class BootstrapTest {
    @Test
    public void bootStrapTest() throws Exception{
        Bootstrap.init();
        Bootstrap.printResult();
    }
    @Test
    public void testNull() throws Exception{
        MessageDigest messageDigest=MessageDigest.getInstance("MD5");
        messageDigest.update("".getBytes());
        System.out.println(new BigInteger(1,messageDigest.digest()).toString(16));
    }
}
