package nyp.fypj.irarphotodiary.dto;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by L33533 on 11/5/2014.
 */
public class Tag implements Parcelable {

    private String tag;
    private double confidence;

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public double getConfidence() {
        return confidence;
    }

    public void setConfidence(double confidence) {
        this.confidence = confidence;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(tag);
        parcel.writeDouble(confidence);
    }

    public static final Creator<Tag> CREATOR = new Creator<Tag>() {
        @Override
        public Tag createFromParcel(Parcel parcel) {
            return new Tag(parcel);
        }

        @Override
        public Tag[] newArray(int i) {
            return new Tag[i];
        }
    };

    private Tag(Parcel parcel){
        this.tag = parcel.readString();
        this.confidence = parcel.readDouble();
    }

}
