package in.surajsau.popularmovies.details.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import in.surajsau.popularmovies.IConstants;
import in.surajsau.popularmovies.R;
import in.surajsau.popularmovies.details.adapter.MovieImagesAdapter;
import in.surajsau.popularmovies.Util;
import in.surajsau.popularmovies.details.presenter.MovieDetailsPresenter;
import in.surajsau.popularmovies.details.presenter.MovieDetailsPresenterImpl;
import in.surajsau.popularmovies.network.BaseSubscriber;
import in.surajsau.popularmovies.network.PopularMoviesClient;
import in.surajsau.popularmovies.network.ServiceGenerator;
import in.surajsau.popularmovies.network.models.MovieDetailsResponse;
import in.surajsau.popularmovies.network.models.MovieImagesResponse;
import me.zhanghai.android.materialprogressbar.MaterialProgressBar;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class MovieDetailsActivity extends AppCompatActivity implements View.OnClickListener, MovieDetailsView{

    private static final String TAG = MovieDetailsActivity.class.getSimpleName();

    private MovieDetailsPresenter presenter;

    @Bind(R.id.toolbar) Toolbar toolbar;
    @Bind(R.id.cvMovieDetails) CardView cvMovieDetails;
    @Bind(R.id.llMovieDetails) LinearLayout llMovieDetails;
    @Bind(R.id.ivMovieBackdrop) ImageView ivMovieBackdrop;
    @Bind(R.id.ivMoviePoster) ImageView ivMoviePoster;
    @Bind(R.id.tvReleaseDate) TextView tvReleaseDate;
    @Bind(R.id.tvVoteAverage) TextView tvVoteAverage;
    @Bind(R.id.tvMovieSummary) TextView tvMovieSummary;
    @Bind(R.id.btnImdbLink) Button btnImdbLink;
    @Bind(R.id.rlMoviePosters) RecyclerView rlMoviePosters;
    @Bind(R.id.progress) MaterialProgressBar progress;
//    @Bind(R.id.rlMovieBackdrops) RecyclerView rlMovieBackdrops;

    private MovieImagesAdapter mPosterAdapter;
//    private MovieImagesAdapter mBackdropAdapter;

    private String mMovieTitle;
    private int mMovieId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);
        ButterKnife.bind(this);
        getDataFromBundle();

        presenter = new MovieDetailsPresenterImpl(this, mMovieId);

        setupToolbar();
        setOnClickListeners();

        setupGallery();

        presenter.callMovieDetailsAPI();

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
        btnImdbLink.setOnClickListener(this);
    }

    private void setupGallery() {
        mPosterAdapter = new MovieImagesAdapter(this);
//        mBackdropAdapter = new MovieImagesAdapter(this);

        LinearLayoutManager llPostersManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
//        LinearLayoutManager llBackdropsManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);

        rlMoviePosters.setLayoutManager(llPostersManager);
//        rlMovieBackdrops.setLayoutManager(llBackdropsManager);

        rlMoviePosters.setAdapter(mPosterAdapter);
//        rlMovieBackdrops.setAdapter(mBackdropAdapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnImdbLink: {
                presenter.openImdbLink();
            }
            break;
        }
    }

    @Override
    public MovieImagesAdapter getMoviePosterAdapter() {
        return mPosterAdapter;
    }

    @Override
    public Context getContext() {
        return MovieDetailsActivity.this;
    }

    @Override
    public void loadMoviePosterImage(String posterUrl) {
        Picasso.with(MovieDetailsActivity.this)
                .load(Util.getPosterImageUrlForDetails(posterUrl))
                .into(ivMoviePoster);
    }

    @Override
    public void loadMovieBackdropImage(String backdropUrl) {
        Picasso.with(MovieDetailsActivity.this)
                .load(Util.getBackdropImageUrl(backdropUrl))
                .into(ivMovieBackdrop);
    }

    @Override
    public void populateDataFromResponse(MovieDetailsResponse res) {
        tvReleaseDate.setText(res.getRelease_date());
        tvVoteAverage.setText(res.getVote_average() + "");
        tvMovieSummary.setText(res.getOverview());
    }

    @Override
    public void onMovieDetailsResponseComplete() {
        cvMovieDetails.setVisibility(View.VISIBLE);
        llMovieDetails.setVisibility(View.VISIBLE);
    }

    @Override
    public void openImdb(String imdbUrl) {
        if(!TextUtils.isEmpty(imdbUrl)) {
            try {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("imdb:///title/" + imdbUrl)));
            } catch (Exception e) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.imdb.com/title/" + imdbUrl)));
            }
        } else {
            Toast.makeText(this, "Cannot open link", Toast.LENGTH_SHORT).show();
        }
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

    @Override
    protected void onDestroy() {
        presenter.onDestroy();
        super.onDestroy();
    }



}
