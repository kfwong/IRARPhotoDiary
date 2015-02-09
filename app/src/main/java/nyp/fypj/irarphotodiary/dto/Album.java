package nyp.fypj.irarphotodiary.dto;

import java.util.ArrayList;

/**
 * Created by L33533 on 10/21/2014.
 */
public class Album {
    private String _id;
    private String title;
    private String description;
    private String dateUploaded;
    private ArrayList<ImageProfile> imageProfiles;

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

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

    public String getDateUploaded() {
        return dateUploaded;
    }

    public void setDateUploaded(String dateUploaded) {
        this.dateUploaded = dateUploaded;
    }

    public ArrayList<ImageProfile> getImageProfiles() {
        return imageProfiles;
    }

    public void setImageProfiles(ArrayList<ImageProfile> imageProfiles) {
        this.imageProfiles = imageProfiles;
    }


}
