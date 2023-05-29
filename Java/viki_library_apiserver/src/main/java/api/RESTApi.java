package api;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

/***
 * Abstract API class that holds default methods for performing REST Api calls.
 */
public abstract class RESTApi {
    protected String baseUrl;

    public RESTApi(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    /**
     * Performs a REST call with the given parameters and returns the parsed json as a model of type T
     *
     * @param type           The type the returned json should be parsed to
     * @param urlParameters  The parameters appended unto the base url
     * @param requestMethod  GET, POST, PUT, UPDATE, DELETE
     * @param requestHeaders A hashmap of potential headers. Key being the name, and value the value of the header.
     * @return
     */
    protected  <T extends ApiObject> T doRestCall(String urlParameters,
                            String requestMethod,
                            HashMap<String, String> requestHeaders,
                            Class<T> type) {

        HttpURLConnection connection = null;
        try {
            // Create the http connection
            var fullUrl = baseUrl + urlParameters;
            var url = new URL(fullUrl);
            connection = (HttpURLConnection) url.openConnection();
            // Set the type
            connection.setRequestMethod(requestMethod);
            // Set the headers
            requestHeaders.forEach(connection::setRequestProperty);

            // Execute the call
            var status = connection.getResponseCode();

            // Welp, something went wrong
            if(status > 299){
                System.out.println("API call failed to " + fullUrl + "\nStatuscode: " + status);
                return null;
            }

            // get the response stream
            var inputStream = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            // And then the response as a string
            var line = "";
            var stringResponse = new StringBuilder();
            while ((line = inputStream.readLine()) != null) {
                stringResponse.append(line);
            }
            connection.disconnect();

            if(requestHeaders.get("content-type") == "application/json"){
                // Parse the content to the given model T and return it.
                // Parse the response to the model type given into the method. Return it after parsing.
                return (T)T.fromJson(stringResponse.toString(), type);
            }

            // Else, the caller of this method has to handle the format which is not json
            return type.cast(stringResponse.toString());
        } catch (Exception ex) {
            System.out.println("Error while trying to perform a REST call:");
            ex.printStackTrace();
        } finally {
            // Close the connection finally.
            if (connection != null)
                connection.disconnect();
        }

        return null;
    }

}
