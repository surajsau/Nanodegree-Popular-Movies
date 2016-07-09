package in.surajsau.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by surajkumarsau on 09/07/16.
 */
public class FavouritesDBHelper extends SQLiteOpenHelper {

    private static final String FAVOURITES_DB = "favourites.db";
    private static final int DB_VERSION = 1;

    private static final String CREATE_FAVOURITES_TABLE = "CREATE TABLE " + DBConstants.FAVOURITES_TABLE + "(" +
            DBConstants._ID + " INTEGER PRIMARY KEY, " +
            DBConstants.MOVIE_ID + " INTEGER NON NULL, " +
            DBConstants.MOVIE_TITLE + " STRING NON NULL, " +
            DBConstants.POSTER_PATH + " STRING, " +
            DBConstants.POPULARITY + " FLOAT, " +
            DBConstants.VOTES_AVG + " FLOAT);";

    private static final String DELETE_FAVOURITES_TABLE = "DELETE TABLE IF EXISTS " + DBConstants.FAVOURITES_TABLE;

    public FavouritesDBHelper(Context context) {
        super(context, FAVOURITES_DB, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_FAVOURITES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DELETE_FAVOURITES_TABLE);
        onCreate(db);
    }
}
