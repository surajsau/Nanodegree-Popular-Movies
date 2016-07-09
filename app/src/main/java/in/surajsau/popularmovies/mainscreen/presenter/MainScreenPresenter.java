package in.surajsau.popularmovies.mainscreen.presenter;

import in.surajsau.popularmovies.BaseActivityPresenter;
import in.surajsau.popularmovies.data.FavouritesDAO;

/**
 * Created by MacboolBro on 16/04/16.
 */
public interface MainScreenPresenter extends BaseActivityPresenter {
    void callPopularMoviesAPI();
    void callTopRatedMoviesAPI();

    void onPopularMenuSelected();
    void onRatingsMenuSelected();
    void onFavouritesMenuSelected();

    void initiateDao();
    void closeDao();
}
