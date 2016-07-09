package in.surajsau.popularmovies.details.presenter;

import in.surajsau.popularmovies.BaseActivityPresenter;
import in.surajsau.popularmovies.network.models.PopularMoviesResponse;

/**
 * Created by MacboolBro on 16/04/16.
 */
public interface MovieDetailsPresenter extends BaseActivityPresenter {
    void callMovieTrailersAPI();
    void callMovieDetailsAPI();
    void callMoviePostersAPI();

    void openImdbLink();
    void addMovieToFavourites();
    void showOrHideReviews(boolean show);

    void initiateDao();
    void closeDao();

    void startTrailer();
    void startTrailerFromChoice(String url);
}
