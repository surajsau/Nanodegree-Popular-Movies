package in.surajsau.popularmovies.details.presenter;

import java.util.List;

import in.surajsau.popularmovies.details.activity.MovieDetailsView;
import in.surajsau.popularmovies.network.BaseSubscriber;
import in.surajsau.popularmovies.network.PopularMoviesClient;
import in.surajsau.popularmovies.network.ServiceGenerator;
import in.surajsau.popularmovies.network.models.MovieDetailsResponse;
import in.surajsau.popularmovies.network.models.MovieImagesResponse;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by MacboolBro on 16/04/16.
 */
public class MovieDetailsPresenterImpl implements MovieDetailsPresenter {

    private MovieDetailsView mView;

    private PopularMoviesClient client;

    private Subscription movieDetailsSubscription;
    private Subscription moviePosterSubscription;

    private String imdbUrl;
    private int mMovieId;

    public MovieDetailsPresenterImpl(MovieDetailsView view, int movieId) {
        mView = view;
        mMovieId = movieId;

        client = ServiceGenerator.createService(PopularMoviesClient.class);
    }

    @Override
    public void onDestroy() {
        if(movieDetailsSubscription != null && !movieDetailsSubscription.isUnsubscribed())
            movieDetailsSubscription.unsubscribe();

        if(moviePosterSubscription != null && !moviePosterSubscription.isUnsubscribed())
            moviePosterSubscription.unsubscribe();
    }

    @Override
    public void callMovieDetailsAPI() {
        mView.showProgress();

        Observable<MovieDetailsResponse> movieDetailsResponse = client.getMovieDetails(mMovieId);

        movieDetailsSubscription = movieDetailsResponse.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new MovieDetailsSubscriber());
    }

    @Override
    public void callMoviePostersAPI() {
        Observable<MovieImagesResponse> movieImagesResponse = client.getMovieImages(mMovieId);

        moviePosterSubscription = movieImagesResponse.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new MovieImageResponseSubscriber());
    }

    @Override
    public void openImdbLink() {
        mView.openImdb(imdbUrl);
    }

    private class MovieDetailsSubscriber extends BaseSubscriber<MovieDetailsResponse> {

        @Override
        public void onNext(MovieDetailsResponse movieDetailsResponse) {
            if(movieDetailsResponse != null) {
                mView.loadMovieBackdropImage(movieDetailsResponse.getBackdrop_path());
                mView.loadMoviePosterImage(movieDetailsResponse.getPoster_path());

                mView.populateDataFromResponse(movieDetailsResponse);

                imdbUrl = movieDetailsResponse.getImdb_id();
            }
        }

        @Override
        public String getSubscriberName() {
            return "Movie Details";
        }

        @Override
        public void onCompleted() {
            super.onCompleted();

            //--show the cards
            mView.hideProgress();
            mView.onMovieDetailsResponseComplete();

            callMoviePostersAPI();
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
                    .flatMap(Observable::from)
                    .map(poster -> {
                        if(poster != null)
                            return poster.getFile_path();
                        return null;
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
            mView.getMoviePosterAdapter().addMoviePosterUrl(movieUrl);
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

}
