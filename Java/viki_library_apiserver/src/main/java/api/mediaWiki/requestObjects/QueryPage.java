package api.mediaWiki.requestObjects;

import api.ApiObject;
import com.google.gson.annotations.SerializedName;
import helper.PostProcessingEnabler;

import java.util.List;

public class QueryPage extends ApiObject implements PostProcessingEnabler.PostProcessable {

    @SerializedName(value = "pageid")
    private int pageId;
    private String ns;
    private String title;
    private List<Category> categories;
    @SerializedName(value = "categoryinfo")
    private CategoryInfo categoryInfo;
    private List<Revision> revisions;
    private int currentRevisionId;

    /**
     * Gets the newest revision id. This is an additonal field to handle the revision id easier, since the api
     * always returns a list of revision ids. But we mostly need the newest.
     *
     * @return
     */
    public int getRevisionId() {
        return this.currentRevisionId;
    }

    /**
     * After deserialization of gson, we set the current newest rev id by hand.
     */
    @Override
    public void postProcess() {
        if (revisions.size() > 0)
            currentRevisionId = this.revisions
                    .stream()
                    .sorted((r1, r2) -> Integer.compare(r2.getRevisionId(), r1.getRevisionId()))
                    .toList()
                    .get(0)
                    .getRevisionId();
    }
}
