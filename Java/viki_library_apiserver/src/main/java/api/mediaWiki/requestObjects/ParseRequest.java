package api.mediaWiki.requestObjects;

import api.ApiObject;
import com.google.gson.annotations.SerializedName;

public class ParseRequest extends ApiObject {

    @SerializedName(value = "parse")
    private ParsedPage parsedPage;

    public ParsedPage getParsedPage() {
        return this.parsedPage;
    }

}
