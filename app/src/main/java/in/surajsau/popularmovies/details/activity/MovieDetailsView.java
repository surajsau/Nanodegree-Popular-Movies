package in.surajsau.popularmovies.details.activity;

import android.content.Context;

import java.util.ArrayList;

import in.surajsau.popularmovies.BaseActivityView;
import in.surajsau.popularmovies.details.adapter.MovieImagesAdapter;
import in.surajsau.popularmovies.details.adapter.MovieReviewsAdapter;
import in.surajsau.popularmovies.network.models.MovieDetailsResponse;
import in.surajsau.popularmovies.network.models.VideoResponse;

/**
 * Created by MacboolBro on 16/04/16.
 */
public interface MovieDetailsView extends BaseActivityView {
    MovieImagesAdapter getMoviePosterAdapter();
    MovieReviewsAdapter getMovieReviewAdapter();

    void loadMoviePosterImage(String posterUrl);
    void loadMovieBackdropImage(String backdropUrl);

    void populateSummaryAndDatesFromResponse(MovieDetailsResponse res);

    void onMovieDetailsResponseComplete();

    void openImdb(String imdbUrl);

    void updateFavouriteButton(boolean isFavourite);

    void showOrHideReviews(boolean show);

    void showReviews();
    void showReviewSpinner();
    void hideReviewsSpinner();

    void hidePlayTrailerButton();

    void startTrailerOnYoutube(String url);
    void showTrailerChooserDialog(ArrayList<VideoResponse.Video> videos, ArrayList<String> descriptions);
}
