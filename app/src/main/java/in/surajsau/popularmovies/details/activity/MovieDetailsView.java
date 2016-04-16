package in.surajsau.popularmovies.details.activity;

import android.content.Context;

import in.surajsau.popularmovies.BaseActivityView;
import in.surajsau.popularmovies.details.adapter.MovieImagesAdapter;
import in.surajsau.popularmovies.network.models.MovieDetailsResponse;

/**
 * Created by MacboolBro on 16/04/16.
 */
public interface MovieDetailsView extends BaseActivityView {
    MovieImagesAdapter getMoviePosterAdapter();

    Context getContext();

    void loadMoviePosterImage(String posterUrl);
    void loadMovieBackdropImage(String backdropUrl);

    void populateDataFromResponse(MovieDetailsResponse res);

    void onMovieDetailsResponseComplete();

    void openImdb(String imdbUrl);
}
