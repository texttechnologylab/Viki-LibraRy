package api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import database.MongoDbObject;
import helper.PostProcessingEnabler;

import java.lang.reflect.Type;

/**
 * Base class for all Api objects
 */
public class ApiObject extends MongoDbObject {

    /**
     * Serializes this object to json
     * @return
     */
    public String toJson() {
        var gson = new GsonBuilder().registerTypeAdapterFactory(new PostProcessingEnabler()).create();
        return gson.toJson(this);
    }

    /**
     * Turns the given string into this ApiObject of the given type
     * @param json The json the model will be built from
     * @param type The type this object should be parsed into.
     * @return
     */
    public static <T extends ApiObject> T fromJson(String json, Class<T> type) {
        var gson = new GsonBuilder().registerTypeAdapterFactory(new PostProcessingEnabler()).create();
        return gson.fromJson(json, (Type) type);
    }
}
