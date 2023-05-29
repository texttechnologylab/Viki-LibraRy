package api.mediaWiki.requestObjects;

import api.ApiObject;
import com.google.gson.annotations.SerializedName;

public class QueryRequest extends ApiObject {

    @SerializedName(value = "query")
    private QueryResult queryResult;

}
