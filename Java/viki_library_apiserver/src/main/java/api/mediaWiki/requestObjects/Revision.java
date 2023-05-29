package api.mediaWiki.requestObjects;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

/**
 * Class that holds the revision data of a wikipedia page
 */
public class Revision {

    @SerializedName(value = "revid")
    private int revisionId;
    @SerializedName(value = "parentid")
    private int parentId;
    private String user;
    private Date timestamp;
    private String comment;

    public int getRevisionId() {
        return this.revisionId;
    }
}
