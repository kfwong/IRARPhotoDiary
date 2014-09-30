package nyp.fypj.irarphotodiary.dto;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by L33533 on 9/19/2014.
 */
public class ImageProfile implements Parcelable {
    private String filename;
    private String extension;
    private String url;
    private List rgbColors;
    private List labColors;
    private String title;
    private String description;
    private transient String cachedUri;
    private transient String actualUri;

    public ImageProfile(){//required
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public List getRgbColors() {
        return rgbColors;
    }

    public void setRgbColors(List rgbColors) {
        this.rgbColors = rgbColors;
    }

    public List getLabColors() {
        return labColors;
    }

    public void setLabColors(List labColors) {
        this.labColors = labColors;
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

    public String getCachedUri() {
        return cachedUri;
    }

    public void setCachedUri(String cachedUri) {
        this.cachedUri = cachedUri;
    }

    public String getActualUri() {
        return actualUri;
    }

    public void setActualUri(String actualUri) {
        this.actualUri = actualUri;
    }

    // implementation of Parcelable interface to allow this object to pass within intents
    // you can use serializable interface or gson to avoid all these implementations but they are generally slower/ bad performace, see: http://stackoverflow.com/questions/5550670/benefit-of-using-parcelable-instead-of-serializing-object
    // http://stackoverflow.com/questions/15543033/how-to-write-list-into-parcel
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(filename);
        parcel.writeString(extension);
        parcel.writeString(url);
        parcel.writeList(rgbColors);
        parcel.writeList(labColors);
        parcel.writeString(title);
        parcel.writeString(description);
        parcel.writeString(cachedUri);
        parcel.writeString(actualUri);
    }

    public static final Creator<ImageProfile> CREATOR = new Creator<ImageProfile>(){
        @Override
        public ImageProfile createFromParcel(Parcel parcel) {
            return new ImageProfile(parcel);
        }

        @Override
        public ImageProfile[] newArray(int i) {
            return new ImageProfile[i];
        }
    };

    private ImageProfile(Parcel parcel){
        this.filename = parcel.readString();
        this.extension = parcel.readString();
        this.url = parcel.readString();
        parcel.readList(rgbColors, List.class.getClassLoader());
        parcel.readList(labColors, List.class.getClassLoader());
        this.title = parcel.readString();
        this.description = parcel.readString();
        this.cachedUri = parcel.readString();
        this.actualUri = parcel.readString();
    }
}
