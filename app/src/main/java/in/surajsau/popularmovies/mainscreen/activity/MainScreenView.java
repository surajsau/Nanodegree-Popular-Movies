package in.surajsau.popularmovies.mainscreen.activity;

import in.surajsau.popularmovies.BaseActivityView;
import in.surajsau.popularmovies.mainscreen.adapter.MoviesGridAdapter;
import in.surajsau.popularmovies.network.models.PopularMoviesResponse;

/**
 * Created by MacboolBro on 16/04/16.
 */
public interface MainScreenView extends BaseActivityView {
    void clearMoviesList();
    void addMovieToList(PopularMoviesResponse.Movie movie);
}
