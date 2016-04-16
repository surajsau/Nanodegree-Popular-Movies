package in.surajsau.popularmovies.mainscreen.activity;

import android.support.annotation.IntDef;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import in.surajsau.popularmovies.IConstants;
import in.surajsau.popularmovies.R;
import in.surajsau.popularmovies.mainscreen.adapter.MoviesGridAdapter;
import in.surajsau.popularmovies.network.BaseSubscriber;
import in.surajsau.popularmovies.network.PopularMoviesClient;
import in.surajsau.popularmovies.network.ServiceGenerator;
import in.surajsau.popularmovies.network.models.PopularMoviesResponse;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private PopularMoviesClient client;
    private Subscription movieListSubscription;
    private Subscription movieDataBindingSubscription;

    private static final int POPULARITY = 0;
    private static final int RATING = 1;

    @Bind(R.id.rlMovieList) RecyclerView rlMovieList;

    private MoviesGridAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setupRecyclerView();

        //--inititate and load list
        client = ServiceGenerator.createService(PopularMoviesClient.class);
        callPopularMoviesAPI();
    }

    private void callPopularMoviesAPI() {
        Observable<PopularMoviesResponse> popularMoviesResponse = client.getPopularMovies();

        movieListSubscription = popularMoviesResponse.subscribeOn(Schedulers.newThread())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new MoviesResponseSubcriber());
    }

    private void callTopRatedMoviesAPI() {
        Observable<PopularMoviesResponse> topRatedMoviesResponse = client.getTopRatedMovies();

        movieListSubscription = topRatedMoviesResponse.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new MoviesResponseSubcriber());
    }

    private void populatePopularMoviesList(List<PopularMoviesResponse.Movie> movies) {

        movieDataBindingSubscription = Observable.just(movies)
                .flatMap(new Func1<List<PopularMoviesResponse.Movie>, Observable<PopularMoviesResponse.Movie>>() {
                    @Override
                    public Observable<PopularMoviesResponse.Movie> call(List<PopularMoviesResponse.Movie> movies) {
                        return Observable.from(movies);
                    }
                })
                .subscribe(new MovieItemSubscriber());

    }

    private void setupRecyclerView() {
        mAdapter = new MoviesGridAdapter(this);
        rlMovieList.setLayoutManager(new GridLayoutManager(this, 2));
        rlMovieList.setAdapter(mAdapter);
    }

    private class MoviesResponseSubcriber extends BaseSubscriber<PopularMoviesResponse> {

        @Override
        public void onNext(PopularMoviesResponse popularMoviesResponse) {
            if(popularMoviesResponse != null && popularMoviesResponse.getResults() != null)
                populatePopularMoviesList(popularMoviesResponse.getResults());
        }

        @Override
        public String getSubscriberName() {
            return "Popular movies";
        }
    }

    private class MovieItemSubscriber extends BaseSubscriber<PopularMoviesResponse.Movie> {

        @Override
        public void onNext(PopularMoviesResponse.Movie movie) {
            if(movie != null)
                mAdapter.addMovieToList(movie);
        }

        @Override
        public String getSubscriberName() {
            return "Movie Item";
        }
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
                mAdapter.clearMoviesList();
                callPopularMoviesAPI();
                return true;
            }

            case R.id.menu_ratings: {
                mAdapter.clearMoviesList();
                callTopRatedMoviesAPI();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        movieListSubscription.unsubscribe();
        movieDataBindingSubscription.unsubscribe();
    }

}
