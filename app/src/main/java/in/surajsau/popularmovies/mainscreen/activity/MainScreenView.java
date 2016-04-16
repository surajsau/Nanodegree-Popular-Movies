package in.surajsau.popularmovies.mainscreen.activity;

import in.surajsau.popularmovies.BaseActivityView;
import in.surajsau.popularmovies.mainscreen.adapter.MoviesGridAdapter;

/**
 * Created by MacboolBro on 16/04/16.
 */
public interface MainScreenView extends BaseActivityView {
    MoviesGridAdapter getMovieGridAdapter();
}
