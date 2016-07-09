package in.surajsau.popularmovies.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;

import in.surajsau.popularmovies.network.models.PopularMoviesResponse;

/**
 * Created by surajkumarsau on 09/07/16.
 */
public class FavouritesDAO {

    private static final String TAG = FavouritesDAO.class.getSimpleName();

    private FavouritesDBHelper mHelper;
    private SQLiteDatabase mDb;

    private String[] allColumns = {DBConstants._ID, DBConstants.MOVIE_ID, DBConstants.MOVIE_TITLE, DBConstants.POSTER_PATH,
                                    DBConstants.POPULARITY, DBConstants.VOTES_AVG};

    public FavouritesDAO(Context context) {
        mHelper = new FavouritesDBHelper(context);
    }

    public void open() throws SQLException {
        mDb = mHelper.getWritableDatabase();
    }

    public void close() {
        mHelper.close();
    }

    public void addToFavourite(PopularMoviesResponse.Movie movie) {
        ContentValues movieValue = new ContentValues();
        movieValue.put(DBConstants.MOVIE_ID, movie.getId());
        movieValue.put(DBConstants.MOVIE_TITLE, movie.getTitle());
        movieValue.put(DBConstants.POPULARITY, movie.getPopularity());
        movieValue.put(DBConstants.VOTES_AVG, movie.getVote_average());
        movieValue.put(DBConstants.POSTER_PATH, movie.getPoster_path());

        if(mDb.insert(DBConstants.FAVOURITES_TABLE, null, movieValue) != -1);
            Log.d(TAG, movie.getId() + " added");
    }

    public void removeFromFavourites(int movieId) {
        mDb.execSQL("DELETE FROM " + DBConstants.FAVOURITES_TABLE + " WHERE " + DBConstants.MOVIE_ID + " = " + movieId + ";");
        Log.d(TAG, movieId + " removed");
    }

    public ArrayList<PopularMoviesResponse.Movie> getFavourites() {
        ArrayList<PopularMoviesResponse.Movie> favourites = new ArrayList<>();
        Cursor cursor = mDb.query(DBConstants.FAVOURITES_TABLE, allColumns, null, null, null, null, null);
        cursor.moveToFirst();

        while(!cursor.isAfterLast()) {
            favourites.add(cursorToMovie(cursor));
            cursor.moveToNext();
        }

        cursor.close();
        return favourites;
    }

    private PopularMoviesResponse.Movie cursorToMovie(Cursor cursor) {
        PopularMoviesResponse.Movie movie = new PopularMoviesResponse.Movie();
        movie.setId(cursor.getInt(1));
        movie.setTitle(cursor.getString(2));
        movie.setPoster_path(cursor.getString(3));
        movie.setPopularity(cursor.getFloat(4));
        movie.setVote_average(cursor.getFloat(5));

        return movie;
    }

    public boolean isMovieFavourite(int movieId) {
        String[] selectionArgs = {String.valueOf(movieId)};
        Cursor cursor = mDb.query(DBConstants.FAVOURITES_TABLE, allColumns, DBConstants.MOVIE_ID + " = ?", selectionArgs, null, null, null);

        return cursor.getCount() > 0;
    }
}
