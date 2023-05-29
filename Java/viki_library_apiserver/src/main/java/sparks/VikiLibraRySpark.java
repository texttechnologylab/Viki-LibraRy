package sparks;

import api.mediaWiki.MediaWikiAPI;
import api.mediaWiki.requestObjects.QueryRequest;
import api.mediaWiki.requestObjects.QueryResult;
import database.vikiLibraRy.VikiLibraRyMongoDbConnectionHandler;
import database.vikiLibraRy.databaseObjects.WikiPage;
import helper.PropertiesHelper;
import io.github.manusant.ss.SparkSwagger;
import io.github.manusant.ss.conf.Options;
import io.github.manusant.ss.demo.endpoint.HammerEndpoint;
import io.github.manusant.ss.demo.endpoint.ShieldEndpoint;
import static io.github.manusant.ss.descriptor.EndpointDescriptor.endpointPath;
import static io.github.manusant.ss.descriptor.MethodDescriptor.path;
import static io.github.manusant.ss.rest.RestResponse.badRequest;
import static io.github.manusant.ss.rest.RestResponse.ok;

import io.github.manusant.ss.demo.model.Network;
import io.github.manusant.ss.descriptor.EndpointDescriptor;
import io.github.manusant.ss.route.Route;
import org.apache.commons.text.StringEscapeUtils;
import services.WikiTextToUnityParserService;
import spark.Request;
import spark.Response;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Properties;

import static spark.Spark.get;

/**
 * Spark instance the Viki LibraRy communicates with.
 */
public class VikiLibraRySpark extends SparkInstance {

    private final String baseEndpoint = "/wiki";
    private final int port = 8080;
    private final int threadPool = 20;

    private static MediaWikiAPI wikiApi;
    private static VikiLibraRyMongoDbConnectionHandler db;

    @Override
    protected Properties getConfigurationProperties() {
        return PropertiesHelper.fetchConfigByName("wikiApiConfig.properties");
    }

    @Override
    public void initSpark() {
        // Setup some configs.
        http.port(port).threadPool(threadPool);
        System.out.println("Igniting spark under port " + port + " with threadpool " + threadPool);

        // Enable swagger for the spark
        try{
            SparkSwagger.of(http, Options.defaultOptions().build())
                    .endpoints(() -> Arrays.asList(new VikiLibraRySpark()))
                    .generateDoc();
        } catch (Exception ex){
            System.out.println("Couldn't init swagger");
            ex.printStackTrace();
        }

        // Setup the wiki api handler to communicate with it.
        // Get default headers
        var defaultHeaders = new HashMap<String, String>();
        defaultHeaders.put("content-type", "application/json");

        // Get the configurations
        var properties = getConfigurationProperties();
        var defaultQueryParameters = properties.getProperty("default_query_parameters");
        var defaultPageTextParseParameters = properties.getProperty("default_page_text_parse_parameters");
        var baseUrl = properties.getProperty("base_url");

        // Create the API
        wikiApi = new MediaWikiAPI(baseUrl, defaultQueryParameters, defaultPageTextParseParameters, defaultHeaders);
        System.out.println("Created Wiki API communication");

        // Create the viki Library database
        db = new VikiLibraRyMongoDbConnectionHandler();
        // If the db cannot connect, we cannot start the spark service
        if (!db.openConnection()) return;
        db.init();
        System.out.println("Opened connection to MongoDB.");

        // Create the endpoints for the api here. ============================================================================
        System.out.println("VikiLibraRy Spark ignited.");

        // Get wikipedia pages by a given categoryName.
        // eg.: http://localhost:8080/wiki/pages?categoryname=Mathematicians
        http.get(baseEndpoint + "/pages", (req, res) -> {
            try{
                var categoryName = req.queryParams("categoryname");
                return getPages(categoryName).toJson();
            } catch(Exception ex){
                System.out.println("Error: Couldn't fetch wiki pages of the given category: " + req.url());
                ex.printStackTrace();
                res.status(500);
                return "A correct and existing categoryname has to be provided. Example parameters: /pages?categoryname=Mathematicians";
            }
        });

        // Get the full content of a page
        // eg.: http://localhost:8080/wiki/page?pageid=18902&revid=1125210758
        http.get(baseEndpoint + "/page", (req, res) -> {
            try {
                var pageId = Integer.parseInt(req.queryParams("pageid"));
                var revId = Integer.parseInt(req.queryParams("revid"));
                return getPage(pageId, revId).toJson();
            } catch (Exception ex) {
                System.out.println("Error: Couldn't fetch wiki page with the given url: " + req.url());
                ex.printStackTrace();
                res.status(500);
                return "A correct and existing revision id and page id have to be provided. Example parameters: /page?pageid=18902&revid=1125210758";
            }
        });
    }

    /**
     * Executes the logic to get the pages by a given category
     * @param categoryName
     * @return
     */
    private QueryRequest getPages(String categoryName){
        // We have to gap any spaces
        categoryName = categoryName.replace(" ", "%20");
        return wikiApi.getPagesByCategory(categoryName);
    }

    /**
     * Executes the logic to get the page
     * @return
     */
    private WikiPage getPage(int pageId, int revId){
        // Let's check if we have this page with the given revid in our database already before asking the mediaAPI.
        var page = db.getWikiPageByRevAndPageId(pageId, revId);
        if (page != null) return page;

        // Else, we need to fetch the page and store it in the database
        var result = wikiApi.getParsedPageByPageId(Integer.toString(pageId));
        // If the page is new, we need to parse the wikitext
        var wikiPage = WikiPage.fromJson((result.getParsedPage().toJson()), WikiPage.class);
        wikiPage.setParsedUIText(WikiTextToUnityParserService.ParseWikiText(wikiPage.getWikiText()));
        // Store it
        if (!db.insertWikiPage(wikiPage)) {
            System.out.println("Error: Couldn't cache a wikiPage.");
        }

        // Return it
        return wikiPage;
    }

    /**
     * Add the description for swagger here
     * @param sparkSwagger
     */
    @Override
    public void bind(final SparkSwagger sparkSwagger) {
        System.out.println("Binding swagger");
        sparkSwagger.endpoint(endpointPath(baseEndpoint)
                        .withDescription("VikiLibraRy REST API Service "), (q, a) -> System.out.println("Received request for Rest API"))

                // Describe the /wiki/page/ endpoint
                .get(path("/page")
                        .withDescription("Gets a single page")
                        .withQueryParam().withName("pageid").withDescription("Id of the page you want to fetch").withRequired(true)
                        .and()
                        .withQueryParam().withName("revid").withDescription("The exact revision you want to fetch").withRequired(true)
                        .and()
                        .withResponseType(WikiPage.class), new Route() {
                    @Override
                    public Object onRequest(Request request, Response response) {
                        var pageId = Integer.parseInt(request.queryParams("pageid"));
                        var revId = Integer.parseInt(request.queryParams("revid"));
                        return ok(response, getPage(pageId, revId));
                    }
                })
                // Describe the /wiki/pages/ endpoint
                .get(path("/pages")
                        .withDescription("Gets all pages of a category")
                        .withQueryParam().withName("categoryname").withDescription("Name of the category you want to fetch").withRequired(true)
                        .and()
                        .withResponseType(QueryRequest.class), new Route() {
                    @Override
                    public Object onRequest(Request request, Response response) {
                        var categoryName = request.queryParams("categoryname");
                        return ok(response, getPages(categoryName));
                    }
                });
    }
}
