package api.mediaWiki.requestObjects;

import api.ApiObject;
import com.google.gson.annotations.SerializedName;

public class CategoryInfo extends ApiObject {

    private int size;
    private int pages;
    private int files;
    @SerializedName(value = "subcats")
    private int subCategories;

}
