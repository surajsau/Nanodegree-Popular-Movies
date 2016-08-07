package in.surajsau.popularmovies;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by MacboolBro on 08/04/16.
 */
public interface IConstants {

    String BASE_URL = "https://api.themoviedb.org/3/";
    String API_KEY_PARAM = "api_key";
    String API_KEY = "19eeb63c818d1da25b4cb4b46625da90";

    String MOVIE_TITLE = "movie_title";
    String MOVIE_ID = "movie_id";

    String MOVIE_LIST_FRAGMENT = "movie_list_fragment";
    String MOVIE_DETAILS_FRAGMENT = "movie_details_fragment";

    String BASE_YOUTUBE_URL = "https://www.youtube.com/watch?v=";
    String BASE_YOUTUBE_APP_URL = "vnd.youtube:";

    String CURRENT_MOVIES_LIST = "current_movies_list";
    String CURRENT_MOVIE_DETAILS = "current_movie_details";

}
