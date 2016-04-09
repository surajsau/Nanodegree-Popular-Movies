package in.surajsau.popularmovies.mainscreen.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import in.surajsau.popularmovies.R;
import in.surajsau.popularmovies.mainscreen.adapter.PopularMoviesGridAdapter;
import in.surajsau.popularmovies.network.BaseSubscriber;
import in.surajsau.popularmovies.network.PopularMoviesClient;
import in.surajsau.popularmovies.network.ServiceGenerator;
import in.surajsau.popularmovies.network.models.PopularMoviesResponse;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private PopularMoviesClient client;

    @Bind(R.id.rlMovieList) RecyclerView rlMovieList;

    private PopularMoviesGridAdapter mAdapter;

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

        popularMoviesResponse.subscribeOn(Schedulers.newThread())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new PopularMoviesResponseSubcriber());
    }

    private void populatePopularMoviesList(List<PopularMoviesResponse.Movie> movies) {

        Observable.just(movies)
                .flatMap(new Func1<List<PopularMoviesResponse.Movie>, Observable<PopularMoviesResponse.Movie>>() {
                    @Override
                    public Observable<PopularMoviesResponse.Movie> call(List<PopularMoviesResponse.Movie> movies) {
                        return Observable.from(movies);
                    }
                })
                .subscribe(new MovieItemSubscriber());

    }

    private void setupRecyclerView() {
        mAdapter = new PopularMoviesGridAdapter(this);
        rlMovieList.setLayoutManager(new GridLayoutManager(this, 2));
        rlMovieList.setAdapter(mAdapter);
    }

    private class PopularMoviesResponseSubcriber extends BaseSubscriber<PopularMoviesResponse> {

        @Override
        public void onNext(PopularMoviesResponse popularMoviesResponse) {
            if(popularMoviesResponse != null && popularMoviesResponse.getResults() != null)
                populatePopularMoviesList(popularMoviesResponse.getResults());
        }

    }

    private class MovieItemSubscriber extends BaseSubscriber<PopularMoviesResponse.Movie> {

        @Override
        public void onNext(PopularMoviesResponse.Movie movie) {
            if(movie != null)
                mAdapter.addMovieToList(movie);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}
