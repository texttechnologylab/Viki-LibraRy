package database;

import api.ApiObject;
import com.google.gson.Gson;
import org.bson.Document;

import java.lang.reflect.Type;

/**
 * Base class all mongodb entities have to extend
 */
public class MongoDbObject {

    /**
     * Serializes this object to a mongodb document
     * @return
     */
    public Document toMongoDbDocument() {
        var gson = new Gson();
        return Document.parse(gson.toJson(this));
    }

    /**
     * Turns the given document into this MongoDbo of the given type
     * @param document The document the model will be built from
     * @param type The type this object should be parsed into.
     * @return
     */
    public static <T extends MongoDbObject> T fromMongoDbDocument(Document document, Class<T> type) {
        var gson = new Gson();
        var asJson = document.toJson();
        return (T)gson.fromJson(asJson, (Type) type);
    }
}
