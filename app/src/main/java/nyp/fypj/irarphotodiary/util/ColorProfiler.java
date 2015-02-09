package nyp.fypj.irarphotodiary.util;

/**
 * Created by L33533 on 9/17/2014.
 * http://www.colorwiki.com/wiki/Delta_E:_The_Color_Difference
 * http://stackoverflow.com/questions/492211/is-there-an-easy-way-to-compare-how-close-two-colors-are-to-each-other
 * http://colormine.org/delta-e-calculator
 */
public class ColorProfiler {

    private static final int COLOR_PALETTE_COUNT = 5;

    /**
     * sRGB to XYZ conversion matrix
     */
    private final static double[][] M = {{0.4124, 0.3576, 0.1805},
            {0.2126, 0.7152, 0.0722},
            {0.0193, 0.1192, 0.9505}};

    /**
     * XYZ to sRGB conversion matrix
     */
    private final static double[][] Mi = {{3.2406, -1.5372, -0.4986},
            {-0.9689, 1.8758, 0.0415},
            {0.0557, -0.2040, 1.0570}};

    private final static double[] whitePoint = {95.0429, 100.0, 108.8900}; //D50

    private ColorProfiler() {
        // not allow to instantiate this helper class!
    }

    public static double computeDistance(double[] labColor1, double[] labColor2) {
        return Math.sqrt(Math.pow(labColor2[0] - labColor1[0], 2) + Math.pow(labColor2[1] - labColor1[1], 2) + Math.pow(labColor2[2] - labColor1[2], 2));

    }


    public static double computeDistancePercentage(double[] labColor1, double[] labColor2) {
        return Math.sqrt(Math.pow(labColor2[0] - labColor1[0], 2) + Math.pow(labColor2[1] - labColor1[1], 2) + Math.pow(labColor2[2] - labColor1[2], 2)) / Math.sqrt(Math.pow(100, 2) + Math.pow(184.439, 2) + Math.pow(202.345, 2));
    }

    // get dominant color and color palette, flatten as json and return
    // NO LONGER USED
    /*
    public static String generateJsonProfile(Bitmap bitmap, String filename, String extension, String url) {
        String jsonProfile = "{}";
        try {
            List<int[]> bitmapRGBs = ColorThief.compute(bitmap, COLOR_PALETTE_COUNT);
            List<double[]> bitmapLabs = new ArrayList<double[]>(COLOR_PALETTE_COUNT);
            //convert rgb values to lab equivalent
            for(int i =0; i<bitmapRGBs.size(); i++){
                bitmapLabs.add(ColorProfiler.RGBtoLAB(bitmapRGBs.get(i)));
            }

            ImageProfile imageProfile = new ImageProfile();
            imageProfile.setRgbColors(bitmapRGBs);
            imageProfile.setLabColors(bitmapLabs);

            Gson gson = new Gson();
            jsonProfile = gson.toJson(imageProfile);

        } catch (IOException ex) {
            ex.printStackTrace();
            return "{}";
        }

        return jsonProfile;
    }
    */

    /**
     * Convert LAB to RGB.
     *
     * @param L
     * @param a
     * @param b
     * @return RGB values
     */
    public static int[] LABtoRGB(double L, double a, double b) {
        return XYZtoRGB(LABtoXYZ(L, a, b));
    }

    /**
     * @param Lab
     * @return RGB values
     */
    public static int[] LABtoRGB(double[] Lab) {
        return XYZtoRGB(LABtoXYZ(Lab));
    }

    /**
     * Convert LAB to XYZ.
     *
     * @param L
     * @param a
     * @param b
     * @return XYZ values
     */
    public static double[] LABtoXYZ(double L, double a, double b) {
        double[] result = new double[3];

        double y = (L + 16.0) / 116.0;
        double y3 = Math.pow(y, 3.0);
        double x = (a / 500.0) + y;
        double x3 = Math.pow(x, 3.0);
        double z = y - (b / 200.0);
        double z3 = Math.pow(z, 3.0);

        if (y3 > 0.008856) {
            y = y3;
        } else {
            y = (y - (16.0 / 116.0)) / 7.787;
        }
        if (x3 > 0.008856) {
            x = x3;
        } else {
            x = (x - (16.0 / 116.0)) / 7.787;
        }
        if (z3 > 0.008856) {
            z = z3;
        } else {
            z = (z - (16.0 / 116.0)) / 7.787;
        }

        result[0] = x * whitePoint[0];
        result[1] = y * whitePoint[1];
        result[2] = z * whitePoint[2];

        return result;
    }

    /**
     * Convert LAB to XYZ.
     *
     * @param Lab
     * @return XYZ values
     */
    public static double[] LABtoXYZ(double[] Lab) {
        return LABtoXYZ(Lab[0], Lab[1], Lab[2]);
    }

    /**
     * @param R
     * @param G
     * @param B
     * @return Lab values
     */
    public static double[] RGBtoLAB(int R, int G, int B) {
        return XYZtoLAB(RGBtoXYZ(R, G, B));
    }

    /**
     * @param RGB
     * @return Lab values
     */
    public static double[] RGBtoLAB(int[] RGB) {
        return XYZtoLAB(RGBtoXYZ(RGB));
    }

    /**
     * Convert RGB to XYZ
     *
     * @param R
     * @param G
     * @param B
     * @return XYZ in double array.
     */
    public static double[] RGBtoXYZ(int R, int G, int B) {
        double[] result = new double[3];

        // convert 0..255 into 0..1
        double r = R / 255.0;
        double g = G / 255.0;
        double b = B / 255.0;

        // assume sRGB
        if (r <= 0.04045) {
            r = r / 12.92;
        } else {
            r = Math.pow(((r + 0.055) / 1.055), 2.4);
        }
        if (g <= 0.04045) {
            g = g / 12.92;
        } else {
            g = Math.pow(((g + 0.055) / 1.055), 2.4);
        }
        if (b <= 0.04045) {
            b = b / 12.92;
        } else {
            b = Math.pow(((b + 0.055) / 1.055), 2.4);
        }

        r *= 100.0;
        g *= 100.0;
        b *= 100.0;

        // [X Y Z] = [r g b][M]
        result[0] = (r * M[0][0]) + (g * M[0][1]) + (b * M[0][2]);
        result[1] = (r * M[1][0]) + (g * M[1][1]) + (b * M[1][2]);
        result[2] = (r * M[2][0]) + (g * M[2][1]) + (b * M[2][2]);

        return result;
    }

    /**
     * Convert RGB to XYZ
     *
     * @param RGB
     * @return XYZ in double array.
     */
    public static double[] RGBtoXYZ(int[] RGB) {
        return RGBtoXYZ(RGB[0], RGB[1], RGB[2]);
    }

    /**
     * Convert XYZ to LAB.
     *
     * @param X
     * @param Y
     * @param Z
     * @return Lab values
     */
    public static double[] XYZtoLAB(double X, double Y, double Z) {

        double x = X / whitePoint[0];
        double y = Y / whitePoint[1];
        double z = Z / whitePoint[2];

        if (x > 0.008856) {
            x = Math.pow(x, 1.0 / 3.0);
        } else {
            x = (7.787 * x) + (16.0 / 116.0);
        }
        if (y > 0.008856) {
            y = Math.pow(y, 1.0 / 3.0);
        } else {
            y = (7.787 * y) + (16.0 / 116.0);
        }
        if (z > 0.008856) {
            z = Math.pow(z, 1.0 / 3.0);
        } else {
            z = (7.787 * z) + (16.0 / 116.0);
        }

        double[] result = new double[3];

        result[0] = (116.0 * y) - 16.0;
        result[1] = 500.0 * (x - y);
        result[2] = 200.0 * (y - z);

        return result;
    }

    /**
     * Convert XYZ to LAB.
     *
     * @param XYZ
     * @return Lab values
     */
    public static double[] XYZtoLAB(double[] XYZ) {
        return XYZtoLAB(XYZ[0], XYZ[1], XYZ[2]);
    }

    /**
     * Convert XYZ to RGB.
     *
     * @param X
     * @param Y
     * @param Z
     * @return RGB in int array.
     */
    public static int[] XYZtoRGB(double X, double Y, double Z) {
        int[] result = new int[3];

        double x = X / 100.0;
        double y = Y / 100.0;
        double z = Z / 100.0;

        // [r g b] = [X Y Z][Mi]
        double r = (x * Mi[0][0]) + (y * Mi[0][1]) + (z * Mi[0][2]);
        double g = (x * Mi[1][0]) + (y * Mi[1][1]) + (z * Mi[1][2]);
        double b = (x * Mi[2][0]) + (y * Mi[2][1]) + (z * Mi[2][2]);

        // assume sRGB
        if (r > 0.0031308) {
            r = ((1.055 * Math.pow(r, 1.0 / 2.4)) - 0.055);
        } else {
            r = (r * 12.92);
        }
        if (g > 0.0031308) {
            g = ((1.055 * Math.pow(g, 1.0 / 2.4)) - 0.055);
        } else {
            g = (g * 12.92);
        }
        if (b > 0.0031308) {
            b = ((1.055 * Math.pow(b, 1.0 / 2.4)) - 0.055);
        } else {
            b = (b * 12.92);
        }

        r = (r < 0) ? 0 : r;
        g = (g < 0) ? 0 : g;
        b = (b < 0) ? 0 : b;

        // convert 0..1 into 0..255
        result[0] = (int) Math.round(r * 255);
        result[1] = (int) Math.round(g * 255);
        result[2] = (int) Math.round(b * 255);

        return result;
    }

    /**
     * Convert XYZ to RGB
     *
     * @param XYZ in a double array.
     * @return RGB in int array.
     */
    public static int[] XYZtoRGB(double[] XYZ) {
        return XYZtoRGB(XYZ[0], XYZ[1], XYZ[2]);
    }
}
