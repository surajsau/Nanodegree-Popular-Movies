package in.surajsau.popularmovies.details.activity;

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

public class MovieDetailsActivity extends AppCompatActivity implements View.OnClickListener{

    private static final String TAG = MovieDetailsActivity.class.getSimpleName();

    private PopularMoviesClient client;

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

    private Subscription movieDetailsSubscription;
    private Subscription moviePosterSubscription;

    private MovieImagesAdapter mPosterAdapter;
//    private MovieImagesAdapter mBackdropAdapter;

    private String mMovieTitle;
    private int mMovieId;
    private String imdbUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);
        ButterKnife.bind(this);

        getDataFromBundle();
        setupToolbar();
        setOnClickListeners();

        setupGallery();

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
                openImdbLink();
            }
            break;
        }
    }

    private void callMovieDetailsAPI() {
        showProgress();

        Observable<MovieDetailsResponse> movieDetailsResponse = client.getMovieDetails(mMovieId);

        movieDetailsSubscription = movieDetailsResponse.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new MovieDetailsSubscriber());
    }

    private void callMovieImagesAPI() {
        Observable<MovieImagesResponse> movieImagesResponse = client.getMovieImages(mMovieId);

        moviePosterSubscription = movieImagesResponse.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new MovieImageResponseSubscriber());

    }

    private class MovieDetailsSubscriber extends BaseSubscriber<MovieDetailsResponse> {

        @Override
        public void onNext(MovieDetailsResponse movieDetailsResponse) {
            Picasso.with(MovieDetailsActivity.this)
                    .load(Util.getBackdropImageUrl(movieDetailsResponse.getBackdrop_path()))
                    .into(ivMovieBackdrop);

            Picasso.with(MovieDetailsActivity.this)
                    .load(Util.getPosterImageUrlForDetails(movieDetailsResponse.getPoster_path()))
                    .into(ivMoviePoster);

            tvReleaseDate.setText(movieDetailsResponse.getRelease_date());
            tvVoteAverage.setText(movieDetailsResponse.getVote_average() + "");
            tvMovieSummary.setText(movieDetailsResponse.getOverview());

            imdbUrl = movieDetailsResponse.getImdb_id();
        }

        @Override
        public String getSubscriberName() {
            return "Movie Details";
        }

        @Override
        public void onCompleted() {
            super.onCompleted();

            //--show the cards
            hideProgress();
            cvMovieDetails.setVisibility(View.VISIBLE);
            llMovieDetails.setVisibility(View.VISIBLE);

            callMovieImagesAPI();
        }
    }

    private class MovieImageResponseSubscriber extends BaseSubscriber<MovieImagesResponse> {

        @Override
        public String getSubscriberName() {
            return "Movie Image response";
        }

        @Override
        public void onNext(MovieImagesResponse movieImagesResponse) {
            Observable.just(movieImagesResponse.getPosters())
                .flatMap(new Func1<List<MovieImagesResponse.Poster>, Observable<MovieImagesResponse.Poster>>() {
                    @Override
                    public Observable<MovieImagesResponse.Poster> call(List<MovieImagesResponse.Poster> posters) {
                        return Observable.from(posters);
                    }
                })
                .map(new Func1<MovieImagesResponse.Poster, String>() {
                    @Override
                    public String call(MovieImagesResponse.Poster poster) {
                        if(poster != null)
                            return poster.getFile_path();
                        return null;
                    }
                })
                .subscribe(new MoviePostersSubscriber());

//            Observable.just(movieImagesResponse.getBackdrops())
//                    .flatMap(new Func1<List<MovieImagesResponse.Backdrop>, Observable<MovieImagesResponse.Backdrop>>() {
//                        @Override
//                        public Observable<MovieImagesResponse.Backdrop> call(List<MovieImagesResponse.Backdrop> backdrops) {
//                            return Observable.from(backdrops);
//                        }
//                    })
//                    .map(new Func1<MovieImagesResponse.Backdrop, String>() {
//                        @Override
//                        public String call(MovieImagesResponse.Backdrop backdrop) {
//                            if(backdrop != null)
//                                return backdrop.getFile_path();
//                            return null;
//                        }
//                    })
//                    .subscribe(new MovieBackdropSubscriber());
        }
    }

    private class MoviePostersSubscriber extends BaseSubscriber<String> {
        @Override
        public void onNext(String movieUrl) {
            mPosterAdapter.addMoviePosterUrl(movieUrl);
        }

        @Override
        public String getSubscriberName() {
            return "Movie Posters";
        }
    }

//    private class MovieBackdropSubscriber extends BaseSubscriber<String> {
//        @Override
//        public void onNext(String movieUrl) {
//            mBackdropAdapter.addMoviePosterUrl(movieUrl);
//        }
//
//        @Override
//        public String getSubscriberName() {
//            return "Movie Backdrop";
//        }
//    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        movieDetailsSubscription.unsubscribe();
        moviePosterSubscription.unsubscribe();
    }

    private void openImdbLink() {
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

    private void showProgress() {
        if(progress.getVisibility() == View.GONE)
            progress.setVisibility(View.VISIBLE);
    }

    private void hideProgress() {
        if(progress.getVisibility() == View.VISIBLE)
            progress.setVisibility(View.GONE);
    }

}
