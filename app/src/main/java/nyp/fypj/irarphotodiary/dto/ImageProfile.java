package nyp.fypj.irarphotodiary.dto;

import java.util.List;

/**
 * Created by L33533 on 9/19/2014.
 */
public class ImageProfile {
    private String filename;
    private String extension;
    private String url;
    private List rgbColors;
    private List labColors;

    public ImageProfile(String filename, String extension, String url){
        this.filename = filename;
        this.extension = extension;
        this.url = url;
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
}
