package api.mediaWiki;

import api.RESTApi;
import api.mediaWiki.requestObjects.ParseRequest;
import api.mediaWiki.requestObjects.QueryRequest;

import java.util.HashMap;

/**
 * API class for performing request to the media wikipedia api.
 */
public class MediaWikiAPI extends RESTApi {

    private String defaultCategoryQueryParameters;
    private String defaultPageTextParseParameters;
    private HashMap<String, String> defaultHeaders;

    public MediaWikiAPI(String baseUrl,
                        String defaultQueryParameters,
                        String defaultPageTextParseParameters,
                        HashMap<String, String> defaultHeaders) {
        super(baseUrl);
        this.defaultCategoryQueryParameters = defaultQueryParameters;
        this.defaultPageTextParseParameters = defaultPageTextParseParameters;
        this.defaultHeaders = defaultHeaders;
    }

    /**
     * Fetches pages of all kind by a given category name from the mediawiki api.
     *
     * @param categoryName
     * @return A QueryRequest that holds the information fetched from the API
     */
    public QueryRequest getPagesByCategory(String categoryName) {
        return doRestCall(defaultCategoryQueryParameters.replace("{CATEGORY}", categoryName),
                "GET",
                defaultHeaders,
                QueryRequest.class);
    }

    /**
     * Fetches the text of a single page by its pageid
     *
     * @return A QueryRequest that holds the information fetched from the API
     */
    public ParseRequest getParsedPageByPageId(String pageId) {
        return doRestCall(defaultPageTextParseParameters.replace("{PAGEID}", pageId),
                "GET",
                defaultHeaders,
                ParseRequest.class);
    }

}
