package in.surajsau.popularmovies.mainscreen.utils;

/**
 * Created by MacboolBro on 09/04/16.
 */
public class Util {

    public static String getPosterImageUrl(String posterImagePath) {
        return "http://image.tmdb.org/t/p/w154/" + posterImagePath;
    }

    public static String getBackdropImageUrl(String backdropImagePath) {
        return "http://image.tmdb.org/t/p/w1280/" + backdropImagePath;
    }

    public static String getValueOrNull(String str) {
        if(null ==  str)
            return "..";
        else {
            if(str.length() == 0)
                return "..";
            else
                return str;
        }
    }
}
