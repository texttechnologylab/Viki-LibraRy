package helper;

import scala.App;

import java.io.InputStream;
import java.util.Properties;

public class PropertiesHelper {

    /**
     * Fetches a properties config file from the ressources folder by its full name
     * @param fileName
     * @return
     */
    public static Properties fetchConfigByName(String fileName){
        try (InputStream input = App.class.getClassLoader().getResourceAsStream(fileName)) {
            Properties prop = new Properties();
            // load a properties file
            prop.load(input);
            return prop;
        } catch (Exception ex) {
            System.out.println("Couldn't open the properties file, error: " + ex.getMessage());
            System.out.println("Using internal connection then.");
            return null;
        }
    }


}
