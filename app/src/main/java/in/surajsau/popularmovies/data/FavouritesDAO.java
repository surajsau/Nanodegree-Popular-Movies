package in.surajsau.popularmovies.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
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

    private DBQueryListener mListener;

    private String[] allColumns = {DBConstants._ID, DBConstants.MOVIE_ID, DBConstants.MOVIE_TITLE, DBConstants.POSTER_PATH,
                                    DBConstants.POPULARITY, DBConstants.VOTES_AVG};

    public FavouritesDAO(Context context) {
        mHelper = new FavouritesDBHelper(context);
    }

    public void open() throws SQLException {
        mDb = mHelper.getWritableDatabase();
    }

    public void setDBQueryListener(DBQueryListener listener) {
        mListener = listener;
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

        new AddMovieToFavouritesTask(movie.getId()).execute(movieValue);
    }

    public void removeFromFavourites(int movieId) {
        new RemoveMovieFromFavouritesTask().execute(movieId);
    }

    public void getFavourites() {
        new GetFavoritesTask().execute();
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

    public void checkIfMovieIsFavourite(int movieId) {
        new CheckFavouriteMovieTask().execute(movieId);
    }

    private class AddMovieToFavouritesTask extends AsyncTask<ContentValues, Void, Boolean> {
        private int mMovieId;

        public AddMovieToFavouritesTask(int movieId) {
            mMovieId = movieId;
        }

        @Override
        protected Boolean doInBackground(ContentValues... movieValue) {
            return mDb.insert(DBConstants.FAVOURITES_TABLE, null, movieValue[0]) != -1;
        }

        @Override
        protected void onPostExecute(Boolean isSuccess) {
            mListener.onMovieAddedToFavouritesListener(isSuccess);
            if(isSuccess)
                Log.d(TAG, mMovieId + " added");
            else
                Log.d(TAG, mMovieId + " insertion failed");
        }

        @Override
        protected void onCancelled() {
            Log.d(TAG, mMovieId + " insertion failed");
            mListener.onMovieAddedToFavouritesListener(false);
        }
    }

    private class GetFavoritesTask extends AsyncTask<Void, Void, Cursor> {

        @Override
        protected Cursor doInBackground(Void... voids) {
            return mDb.query(DBConstants.FAVOURITES_TABLE, allColumns, null, null, null, null, null);
        }

        @Override
        protected void onPostExecute(Cursor cursor) {
            ArrayList<PopularMoviesResponse.Movie> favourites = new ArrayList<>();
            cursor.moveToFirst();

            while(!cursor.isAfterLast()) {
                favourites.add(cursorToMovie(cursor));
                cursor.moveToNext();
            }

            cursor.close();
            mListener.onFavouritesFoundListener(favourites);
        }
    }

    private class CheckFavouriteMovieTask extends AsyncTask<Integer, Void, Cursor> {

        @Override
        protected Cursor doInBackground(Integer... movieId) {
            String[] selectionArgs = {String.valueOf(movieId[0])};
            return mDb.query(DBConstants.FAVOURITES_TABLE, allColumns, DBConstants.MOVIE_ID + " = ?", selectionArgs, null, null, null);
        }

        @Override
        protected void onPostExecute(Cursor cursor) {
            mListener.onMovieIsFoundInFavouritesListener(cursor.getCount() > 0);
        }
    }

    private class RemoveMovieFromFavouritesTask extends AsyncTask<Integer, Void, Void> {

        private int mMovieId;

        @Override
        protected Void doInBackground(Integer... movieId) {
            mMovieId = movieId[0];
            mDb.execSQL("DELETE FROM " + DBConstants.FAVOURITES_TABLE + " WHERE " + DBConstants.MOVIE_ID + " = " + movieId[0] + ";");
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            Log.d(TAG, mMovieId + " removed");
            mListener.onMovieRemovedFromFavouritesListener(true);
        }

        @Override
        protected void onCancelled() {
            Log.d(TAG, mMovieId + " removal failed");
            mListener.onMovieRemovedFromFavouritesListener(false);
        }
    }

    public interface DBQueryListener {
        void onFavouritesFoundListener(ArrayList<PopularMoviesResponse.Movie> favouriteMovies);
        void onMovieIsFoundInFavouritesListener(boolean isFavourite);
        void onMovieAddedToFavouritesListener(boolean success);
        void onMovieRemovedFromFavouritesListener(boolean success);
    }

}
