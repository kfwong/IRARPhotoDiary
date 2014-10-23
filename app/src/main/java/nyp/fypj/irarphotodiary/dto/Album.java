package nyp.fypj.irarphotodiary.dto;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by L33533 on 10/21/2014.
 */
public class Album {
    private String title;
    private String description;
    private Date dateUploaded;
    private ArrayList<ImageProfile> imageProfiles;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getDateUploaded() {
        return dateUploaded;
    }

    public void setDateUploaded(Date dateUploaded) {
        this.dateUploaded = dateUploaded;
    }

    public ArrayList<ImageProfile> getImageProfiles() {
        return imageProfiles;
    }

    public void setImageProfiles(ArrayList<ImageProfile> imageProfiles) {
        this.imageProfiles = imageProfiles;
    }


}
