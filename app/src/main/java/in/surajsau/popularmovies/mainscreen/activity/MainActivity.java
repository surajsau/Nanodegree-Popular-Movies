package in.surajsau.popularmovies.mainscreen.activity;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import butterknife.Bind;
import butterknife.ButterKnife;
import in.surajsau.popularmovies.IConstants;
import in.surajsau.popularmovies.R;
import in.surajsau.popularmovies.details.activity.MovieDetailsActivity;
import in.surajsau.popularmovies.details.fragment.MovieDetailsFragment;
import in.surajsau.popularmovies.mainscreen.adapter.MoviesGridAdapter;
import in.surajsau.popularmovies.mainscreen.fragment.MainFragment;
import in.surajsau.popularmovies.mainscreen.presenter.MainScreenPresenter;
import in.surajsau.popularmovies.mainscreen.presenter.MainScreenPresenterImpl;
import me.zhanghai.android.materialprogressbar.MaterialProgressBar;

public class MainActivity extends AppCompatActivity implements MainFragment.OnMovieClickedListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    private boolean isTwoPaneLayout;

    private MainFragment mMainFragment;
    private MovieDetailsFragment mDetailsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        isTwoPaneLayout = (findViewById(R.id.flMovieDetails) != null);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        showMovieListFragment();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_movie_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(mMainFragment != null) {
            switch (item.getItemId()) {
                case R.id.menu_popularity: {
                    mMainFragment.onPopularMenuSelected();
                }
                break;

                case R.id.menu_ratings: {
                    mMainFragment.onRatingsMenuSelected();
                }
                break;

                case R.id.menu_favourites: {
                    mMainFragment.onFavouritesMenuSelected();
                }
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void showMovieListFragment() {
        mMainFragment = (MainFragment) getSupportFragmentManager().findFragmentByTag(IConstants.MOVIE_LIST_FRAGMENT);

        if(mMainFragment == null) {
            mMainFragment = new MainFragment();
        }

        mMainFragment.setOnMovieClickedListener(this);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.flMovieList, mMainFragment, IConstants.MOVIE_LIST_FRAGMENT)
                .commit();
    }

    private void showDetailsFragment(int id) {
        mDetailsFragment = (MovieDetailsFragment) getSupportFragmentManager().findFragmentByTag(IConstants.MOVIE_DETAILS_FRAGMENT);

        if(mDetailsFragment == null) {
            mDetailsFragment = MovieDetailsFragment.getNewInstance(id);
        } else {
            mDetailsFragment.loadNewMovieDetails(id);
        }

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.flMovieDetails, mDetailsFragment, IConstants.MOVIE_DETAILS_FRAGMENT)
                .commit();
    }

    @Override
    public void onMovieClicked(int id) {
        if(isTwoPaneLayout) {
            showDetailsFragment(id);
        } else {
            startMovieDetailsActivity(id);
        }
    }

    private void startMovieDetailsActivity(int movieId) {
        Intent movieDetailsIntent = new Intent(this, MovieDetailsActivity.class);
        movieDetailsIntent.putExtra(IConstants.MOVIE_ID, movieId);
        startActivity(movieDetailsIntent);
    }
}
