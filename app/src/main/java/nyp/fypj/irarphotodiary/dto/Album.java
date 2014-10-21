package nyp.fypj.irarphotodiary.dto;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by L33533 on 10/21/2014.
 */
public class Album {
    @SerializedName("_id")
    private String albumId;

    @SerializedName("data")
    private ArrayList<ImageProfile> imageProfiles;

    public String getAlbumId() {
        return albumId;
    }

    public void setAlbumId(String albumId) {
        this.albumId = albumId;
    }

    public ArrayList<ImageProfile> getImageProfiles() {
        return imageProfiles;
    }

    public void setImageProfiles(ArrayList<ImageProfile> imageProfiles) {
        this.imageProfiles = imageProfiles;
    }


}
