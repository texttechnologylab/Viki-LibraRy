package database;

import java.util.*;

import com.mongodb.MongoClientSettings;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.*;
import org.bson.Document;

import java.util.Properties;
import java.util.stream.StreamSupport;

/**
 * Base class for all mongodb connections
 */
public abstract class MongoDbConnectionHandlerBase {

    protected MongoDatabase db;
    protected MongoClient client;

    /**
     * Gets the current connection properties used to connect to the database
     *
     * @return
     */
    protected abstract Properties getConnectionCredentials();

    /**
     * Init the db. Performs all operations needed to work with the db instance.
     */
    public abstract void init();

    /**
     * Closes the current db connection.
     */
    public void closeConnection() {
        db = null;
        client.close();
    }

    /**
     * Inserts the given mongodbobject into the given collection
     *
     * @param obj
     * @param collectionName
     * @return
     */
    protected boolean insertMongoDbObject(MongoDbObject obj, String collectionName) {
        try{
            db.getCollection(collectionName).insertOne(obj.toMongoDbDocument());
            return true;
        } catch (Exception ex){
            System.out.println("Couldn't store page in db, Error:");
            ex.printStackTrace();
            return false;
        }
    }

    /**
     * Gets many mongodb objects by a given filter and parses them into the given ype
     * @param <T>
     * @param filter
     * @param collectionName
     * @param type
     * @return
     */
    protected <T extends MongoDbObject> List<T> getManyMongoDbObjects(Document filter, String collectionName, Class<T> type){
        try{
            return StreamSupport
                    .stream(db.getCollection(collectionName)
                            .find(filter)
                            .map(d -> T.fromMongoDbDocument(d, type)).spliterator(), false)
                    .toList();
        } catch (Exception ex){
            System.out.println("Couldn't find many documents by given filter: " + filter.toJson());
            return null;
        }
    }

    /**
     * Gets a single mongodb object by the passed in filter and parses it into the given type
     * @return
     */
    protected <T extends MongoDbObject> T getSingleMongoDbObject(Document filter, String collectionName, Class<T> type){
        try{
            return getManyMongoDbObjects(filter, collectionName, type).get(0);
        } catch(Exception ex){
            System.out.println("Couldn't find single document by given filter: " + filter.toJson());
            return null;
        }
    }

    /**
     * Checks if a collection exists in the current db
     */
    protected boolean collectionExists(String collectionName) {
        var collection = db.listCollectionNames();
        for (String s : collection) {
            if (s.equals(collectionName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Opens a connection to the mongodb provided in the config file.
     *
     * @return
     */
    public boolean openConnection() {
        try {
            // Get the connection properties
            var properties = getConnectionCredentials();

            // Open the connection
            String user = properties.getProperty("remote_user");
            String database = properties.getProperty("remote_database");
            char[] pw = properties.getProperty("remote_password").toCharArray();
            String host = properties.getProperty("remote_host");
            String port = properties.getProperty("remote_port");

            MongoCredential credential = MongoCredential.createCredential(
                    user,
                    database,
                    pw);

            MongoClientSettings settings = MongoClientSettings.builder()
                    .credential(credential)
                    .applyToSslSettings(builder -> builder.enabled(false))
                    .applyToClusterSettings(builder ->
                            builder.hosts(Arrays.asList(
                                    new ServerAddress(host,
                                            Integer.parseInt(port)))))
                    .build();

            client = MongoClients.create(settings);
            // Accessing the database
            db = client.getDatabase(properties.getProperty("remote_database"));
            return true;
        } catch (Exception ex) {
            System.out.println("Error while opening the connection to a mongo db.");
            ex.printStackTrace();
        }

        return false;
    }
}
