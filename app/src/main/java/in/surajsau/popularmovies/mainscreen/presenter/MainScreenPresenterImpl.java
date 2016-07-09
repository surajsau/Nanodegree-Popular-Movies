package in.surajsau.popularmovies.mainscreen.presenter;

import java.util.List;

import in.surajsau.popularmovies.data.FavouritesDAO;
import in.surajsau.popularmovies.mainscreen.activity.MainScreenView;
import in.surajsau.popularmovies.network.BaseSubscriber;
import in.surajsau.popularmovies.network.PopularMoviesClient;
import in.surajsau.popularmovies.network.ServiceGenerator;
import in.surajsau.popularmovies.network.models.PopularMoviesResponse;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by MacboolBro on 16/04/16.
 */
public class MainScreenPresenterImpl implements MainScreenPresenter {

    private MainScreenView mView;
    private PopularMoviesClient client;

    private Subscription movieListSubscription;
    private Subscription movieDataBindingSubscription;

    private FavouritesDAO mDao;

    public MainScreenPresenterImpl(MainScreenView view, FavouritesDAO dao) {
        mView = view;
        mDao = dao;
        client = ServiceGenerator.createService(PopularMoviesClient.class);
    }

    @Override
    public void callPopularMoviesAPI() {
        mView.showProgress();

        Observable<PopularMoviesResponse> popularMoviesResponse = client.getPopularMovies();

        movieListSubscription = popularMoviesResponse.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new MoviesResponseSubcriber());
    }

    @Override
    public void callTopRatedMoviesAPI() {
        mView.showProgress();

        Observable<PopularMoviesResponse> topRatedMoviesResponse = client.getTopRatedMovies();

        movieListSubscription = topRatedMoviesResponse.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new MoviesResponseSubcriber());
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

        @Override
        public void onCompleted() {
            super.onCompleted();
            mView.hideProgress();
        }
    }

    private class MovieItemSubscriber extends BaseSubscriber<PopularMoviesResponse.Movie> {

        @Override
        public void onNext(PopularMoviesResponse.Movie movie) {
            if(movie != null)
                mView.getMovieGridAdapter().addMovieToList(movie);
        }

        @Override
        public String getSubscriberName() {
            return "Movie Item";
        }
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

    @Override
    public void onDestroy() {
        if(movieListSubscription != null && !movieListSubscription.isUnsubscribed())
            movieListSubscription.unsubscribe();

        if(movieDataBindingSubscription != null && !movieDataBindingSubscription.isUnsubscribed())
            movieDataBindingSubscription.unsubscribe();
    }

    @Override
    public void onPopularMenuSelected() {
        mView.getMovieGridAdapter().clearMoviesList();
        callPopularMoviesAPI();
    }

    @Override
    public void onRatingsMenuSelected() {
        mView.getMovieGridAdapter().clearMoviesList();
        callTopRatedMoviesAPI();
    }

    @Override
    public void onFavouritesMenuSelected() {
        mView.getMovieGridAdapter().clearMoviesList();
        populatePopularMoviesList(mDao.getFavourites());
    }

    @Override
    public void initiateDao() {
        mDao.open();
    }

    @Override
    public void closeDao() {
        mDao.close();
    }
}
