package api.mediaWiki.requestObjects;

import api.ApiObject;
import com.google.gson.annotations.SerializedName;


/**
 * This is an actual article in wikipedia with the parsed html text
 */
public class ParsedPage extends ApiObject {

    private String title;
    @SerializedName(value = "pageid")
    private int pageId;
    @SerializedName(value = "revid")
    private int revisionId;
    @SerializedName(value = "wikitext")
    private String wikiText;

    public String getWikiText() {
        return wikiText;
    }

    public int getRevisionId() {
        return this.revisionId;
    }

    public void setRevisionId(int revisionId){
        this.revisionId = revisionId;
    }

}
