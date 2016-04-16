package in.surajsau.popularmovies.details.presenter;

import in.surajsau.popularmovies.BaseActivityPresenter;

/**
 * Created by MacboolBro on 16/04/16.
 */
public interface MovieDetailsPresenter extends BaseActivityPresenter {
    void callMovieDetailsAPI();
    void callMoviePostersAPI();

    void openImdbLink();
}
