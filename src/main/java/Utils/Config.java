package Utils;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

public class Config {
    public static Properties prop;

    /**
     * Load properties
     *
     * @throws Exception
     */
    public static void initProperties() throws Exception {
        if (prop != null) {
            return;
        }
        prop = new Properties();
        InputStream in = new BufferedInputStream(new
                FileInputStream("config.properties"));
        prop.load(in);
    }
}
