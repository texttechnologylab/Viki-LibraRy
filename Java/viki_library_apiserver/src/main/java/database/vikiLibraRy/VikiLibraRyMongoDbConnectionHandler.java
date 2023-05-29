package database.vikiLibraRy;

import database.MongoDbConnectionHandlerBase;
import database.vikiLibraRy.databaseObjects.WikiPage;
import helper.PropertiesHelper;
import org.bson.Document;

import java.util.Arrays;
import java.util.Properties;

public class VikiLibraRyMongoDbConnectionHandler extends MongoDbConnectionHandlerBase {

    private final String pagesCollectionName = "wiki_pages";

    @Override
    protected Properties getConnectionCredentials() {
        return PropertiesHelper.fetchConfigByName("vikiLibraRyDbConfig.properties");
    }

    @Override
    /**
     * Inits the db. Creates collections if needed.
     */
    public void init() {
        if (!collectionExists(pagesCollectionName)) {
            db.createCollection(pagesCollectionName);
        }
    }

    /**
     * Gets the wikiPage by a pageId and revId and returns null if not found.
     *
     * @param pageId
     * @param revId
     * @return
     */
    public WikiPage getWikiPageByRevAndPageId(int pageId, int revId) {
        var query = new Document();

        query.append("$and", Arrays.asList(
                        new Document()
                                .append("revid", new Document()
                                        .append("$eq", revId)
                                ),
                        new Document()
                                .append("pageid", new Document()
                                        .append("$eq", pageId)
                                )
                )
        );

        return getSingleMongoDbObject(query, pagesCollectionName, WikiPage.class);
    }

    /**
     * Gets the wiki page by its page id
     *
     * @param pageId
     * @return
     */
    public WikiPage getWikiPageByPageId(int pageId) {
        var query = new Document();

        query.append("pageid", new Document()
                .append("$eq", pageId)
        );

        return getSingleMongoDbObject(query, pagesCollectionName, WikiPage.class);
    }

    /**
     * Inserts the given page into the mongo database
     *
     * @return
     */
    public boolean insertWikiPage(WikiPage page) {
        return insertMongoDbObject(page, pagesCollectionName);
    }
}
