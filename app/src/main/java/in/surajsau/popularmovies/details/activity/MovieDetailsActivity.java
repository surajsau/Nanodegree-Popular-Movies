package in.surajsau.popularmovies.details.activity;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import butterknife.Bind;
import butterknife.ButterKnife;
import in.surajsau.popularmovies.IConstants;
import in.surajsau.popularmovies.R;
import in.surajsau.popularmovies.mainscreen.utils.Util;
import in.surajsau.popularmovies.network.BaseSubscriber;
import in.surajsau.popularmovies.network.PopularMoviesClient;
import in.surajsau.popularmovies.network.ServiceGenerator;
import in.surajsau.popularmovies.network.models.MovieDetailsResponse;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MovieDetailsActivity extends AppCompatActivity implements View.OnClickListener{

    private static final String TAG = MovieDetailsActivity.class.getSimpleName();

    private PopularMoviesClient client;

    @Bind(R.id.toolbar) Toolbar toolbar;
    @Bind(R.id.btnShare) FloatingActionButton btnShare;
    @Bind(R.id.ivMovieBackdrop) ImageView ivMovieBackdrop;

    Subscription movieDetailsSubscription;

    private String mMovieTitle;
    private int mMovieId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);
        ButterKnife.bind(this);

        getDataFromBundle();
        setupToolbar();
        setOnClickListeners();

        client = ServiceGenerator.createService(PopularMoviesClient.class);
        callMovieDetailsAPI();

    }

    private void getDataFromBundle() {
        if(getIntent() != null) {
            mMovieTitle = getIntent().getStringExtra(IConstants.MOVIE_TITLE);
            mMovieId = getIntent().getIntExtra(IConstants.MOVIE_ID, -1);
        }
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(mMovieTitle);
    }

    private void setOnClickListeners() {
        btnShare.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnShare: {

            }
            break;
        }
    }

    private void callMovieDetailsAPI() {
        Observable<MovieDetailsResponse> movieDetailsResponse = client.getMovieDetails(mMovieId);

        movieDetailsSubscription = movieDetailsResponse.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new MovieDetailsSubscriber());
    }

    private class MovieDetailsSubscriber extends BaseSubscriber<MovieDetailsResponse> {

        @Override
        public void onNext(MovieDetailsResponse movieDetailsResponse) {
            Picasso.with(MovieDetailsActivity.this)
                    .load(Util.getBackdropImageUrl(movieDetailsResponse.getBackdrop_path()))
                    .into(ivMovieBackdrop);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        movieDetailsSubscription.unsubscribe();
    }
}
