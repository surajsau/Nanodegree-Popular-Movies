package in.surajsau.popularmovies.mainscreen.presenter;

import java.util.ArrayList;

import in.surajsau.popularmovies.BaseActivityPresenter;
import in.surajsau.popularmovies.data.FavouritesDAO;
import in.surajsau.popularmovies.network.models.PopularMoviesResponse;

/**
 * Created by MacboolBro on 16/04/16.
 */
public interface MainScreenPresenter extends BaseActivityPresenter {
    ArrayList<PopularMoviesResponse.Movie> getCurrentMovieList();
    void reloadMovies(ArrayList<PopularMoviesResponse.Movie> movies);

    void callPopularMoviesAPI();
    void callTopRatedMoviesAPI();

    void onPopularMenuSelected();
    void onRatingsMenuSelected();
    void onFavouritesMenuSelected();

    void initiateDao();
    void closeDao();
}
