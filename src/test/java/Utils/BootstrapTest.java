package Utils;

import Service.Bootstrap;
import org.junit.Test;

public class BootstrapTest {
    @Test
    public void bootStrapTest() throws Exception{
        Bootstrap.init();
        Bootstrap.printResult();
    }
}
