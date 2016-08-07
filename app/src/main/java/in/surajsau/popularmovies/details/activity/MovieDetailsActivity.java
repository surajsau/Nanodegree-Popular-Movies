package in.surajsau.popularmovies.details.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import butterknife.Bind;
import butterknife.ButterKnife;
import in.surajsau.popularmovies.IConstants;
import in.surajsau.popularmovies.R;
import in.surajsau.popularmovies.details.adapter.MovieImagesAdapter;
import in.surajsau.popularmovies.Util;
import in.surajsau.popularmovies.details.fragment.MovieDetailsFragment;
import in.surajsau.popularmovies.details.presenter.MovieDetailsPresenter;
import in.surajsau.popularmovies.details.presenter.MovieDetailsPresenterImpl;
import in.surajsau.popularmovies.network.models.MovieDetailsResponse;
import me.zhanghai.android.materialprogressbar.MaterialProgressBar;

public class MovieDetailsActivity extends AppCompatActivity implements MovieDetailsFragment.OnMovieTitleReceivedListener{

    private static final String TAG = MovieDetailsActivity.class.getSimpleName();

    private MovieDetailsFragment mFragment;

    private int mMovieId;

    @Bind(R.id.toolbar) Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);
        ButterKnife.bind(MovieDetailsActivity.this);

        getDataFromBundle();

        setupToolbar();

    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        startMovieDetailsFragment();
    }

    private void getDataFromBundle() {
        if(getIntent() != null) {
            mMovieId = getIntent().getIntExtra(IConstants.MOVIE_ID, -1);
        }
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Movie Details...");
    }

    private void startMovieDetailsFragment() {
        Bundle movieBundle = new Bundle();
        movieBundle.putInt(IConstants.MOVIE_ID, mMovieId);

        mFragment = (MovieDetailsFragment) getSupportFragmentManager().findFragmentByTag(IConstants.MOVIE_DETAILS_FRAGMENT);

        if(mFragment == null) {
            mFragment = new MovieDetailsFragment();
            mFragment.setArguments(movieBundle);
        }

        mFragment.setTitleListener(this);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.flMovieDetails, mFragment, IConstants.MOVIE_DETAILS_FRAGMENT)
                .commit();
    }

    @Override
    public void onMovieTitleReceived(String title) {
        getSupportActionBar().setTitle(title);
    }
}
