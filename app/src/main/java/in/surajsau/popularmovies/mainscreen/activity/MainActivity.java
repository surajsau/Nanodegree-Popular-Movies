package in.surajsau.popularmovies.mainscreen.activity;

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
import in.surajsau.popularmovies.R;
import in.surajsau.popularmovies.mainscreen.adapter.MoviesGridAdapter;
import in.surajsau.popularmovies.mainscreen.presenter.MainScreenPresenter;
import in.surajsau.popularmovies.mainscreen.presenter.MainScreenPresenterImpl;
import me.zhanghai.android.materialprogressbar.MaterialProgressBar;

public class MainActivity extends AppCompatActivity implements MainScreenView {

    private static final String TAG = MainActivity.class.getSimpleName();

    private MainScreenPresenter presenter;

    @Bind(R.id.rlMovieList) RecyclerView rlMovieList;
    @Bind(R.id.progress) MaterialProgressBar progress;

    private MoviesGridAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setupRecyclerView();

        presenter = new MainScreenPresenterImpl(this);

        //--load popular list by default
        presenter.callPopularMoviesAPI();
    }

    private void setupRecyclerView() {
        mAdapter = new MoviesGridAdapter(this);
        rlMovieList.setLayoutManager(new GridLayoutManager(this, 2));
        rlMovieList.setAdapter(mAdapter);
    }

    @Override
    public MoviesGridAdapter getMovieGridAdapter() {
        return mAdapter;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_movie_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_popularity: {
                presenter.onPopularMenuSelected();
                return true;
            }

            case R.id.menu_ratings: {
                presenter.onRatingsMenuSelected();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        presenter.onDestroy();
        super.onDestroy();
    }

    @Override
    public void showProgress() {
        if(progress.getVisibility() == View.GONE)
            progress.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgress() {
        if(progress.getVisibility() == View.VISIBLE)
            progress.setVisibility(View.GONE);
    }

}
