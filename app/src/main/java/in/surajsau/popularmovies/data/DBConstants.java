package in.surajsau.popularmovies.data;

import android.provider.BaseColumns;

/**
 * Created by surajkumarsau on 09/07/16.
 */
public interface DBConstants extends BaseColumns {
    String FAVOURITES_TABLE = "fav_table";
    String MOVIE_ID = "movie_id";
    String POSTER_PATH = "poster_path";
    String MOVIE_TITLE = "movie_title";
    String POPULARITY = "popularity";
    String VOTES_AVG = "votes_avg";
}
