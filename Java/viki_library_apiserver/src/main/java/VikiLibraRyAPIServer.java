import services.WikiTextToUnityParserService;
import sparks.VikiLibraRySpark;

import java.nio.file.Files;
import java.nio.file.Paths;


public class VikiLibraRyAPIServer {

    /***
     * Entry point for the server application
     * @param args
     */
    public static void main(String[] args) {
        System.out.println("Viki LibraRy API Server starting...");

        // Setup the sparks: ================================================================
        // See also: http://sparkjava.com/news.html#spark-25-released
        // https://stackoverflow.com/questions/34976459/sparkjava-do-routes-have-to-be-in-main-method
        // http://sparkjava.com/documentation.html#response
        // Viki Library
        var vikiLibrarySpark = new VikiLibraRySpark();
        vikiLibrarySpark.initSpark();
    }
}
