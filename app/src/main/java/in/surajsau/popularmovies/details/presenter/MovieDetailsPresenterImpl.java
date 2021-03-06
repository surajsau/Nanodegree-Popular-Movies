package in.surajsau.popularmovies.details.presenter;

import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

import in.surajsau.popularmovies.IConstants;
import in.surajsau.popularmovies.data.FavouritesDAO;
import in.surajsau.popularmovies.details.activity.MovieDetailsView;
import in.surajsau.popularmovies.network.BaseSubscriber;
import in.surajsau.popularmovies.network.PopularMoviesClient;
import in.surajsau.popularmovies.network.ServiceGenerator;
import in.surajsau.popularmovies.network.models.MovieDetailsResponse;
import in.surajsau.popularmovies.network.models.MovieImagesResponse;
import in.surajsau.popularmovies.network.models.PopularMoviesResponse;
import in.surajsau.popularmovies.network.models.ReviewsResponse;
import in.surajsau.popularmovies.network.models.VideoResponse;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by MacboolBro on 16/04/16.
 */
public class MovieDetailsPresenterImpl implements MovieDetailsPresenter, FavouritesDAO.DBQueryListener {

    private MovieDetailsView mView;

    private PopularMoviesClient client;

    private Subscription movieDetailsSubscription;
    private Subscription moviePosterSubscription;
    private Subscription movieReviewSubscription;
    private Subscription movieVideoSubscription;

    private MovieDetailsResponse mDetails;

    private String imdbUrl;
    private int mMovieId;

    private FavouritesDAO mDao;

    private ArrayList<VideoResponse.Video> trailers;
    private ArrayList<String> trailerDescriptions;

    private PopularMoviesResponse.Movie movie;

    private boolean isMovieFavourite;

    public MovieDetailsPresenterImpl(MovieDetailsView view, int movieId, FavouritesDAO dao) {
        mView = view;
        mMovieId = movieId;
        mDao = dao;

        trailers = new ArrayList<>();
        trailerDescriptions = new ArrayList<>();

        client = ServiceGenerator.createService(PopularMoviesClient.class);

        mDao.setDBQueryListener(this);
    }

    @Override
    public void setMovieId(int id) {
        mMovieId = id;
    }

    @Override
    public void onDestroy() {
        if(movieDetailsSubscription != null && !movieDetailsSubscription.isUnsubscribed())
            movieDetailsSubscription.unsubscribe();

        if(moviePosterSubscription != null && !moviePosterSubscription.isUnsubscribed())
            moviePosterSubscription.unsubscribe();

        if(movieReviewSubscription != null && !movieReviewSubscription.isUnsubscribed())
            movieReviewSubscription.unsubscribe();
    }

    @Override
    public MovieDetailsResponse getCurrentMovieDetails() {
        return mDetails;
    }

    @Override
    public void reloadMovieDetail(MovieDetailsResponse details) {
        mDetails = details;
        populateDataIntoDetails(mDetails);
        mView.hideProgress();
        mView.onMovieDetailsResponseComplete();

        callMoviePostersAPI();
    }

    @Override
    public void callMovieTrailersAPI() {
        Observable<VideoResponse> videoResponse = client.getMovieVideos(mMovieId);

        movieVideoSubscription = videoResponse.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new MovieVideoResponseSubscriber());
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
        mView.showReviewSpinner();

        Observable<MovieImagesResponse> movieImagesResponse = client.getMovieImages(mMovieId);

        moviePosterSubscription = movieImagesResponse.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new MovieImageResponseSubscriber());
    }

    private void callMovieReviewsAPI() {
        Observable<ReviewsResponse> movieReviewResponse = client.getMovieReviews(mMovieId);

        movieReviewSubscription = movieReviewResponse.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new MovieReviewResponseSubscriber());
    }

    @Override
    public void openImdbLink() {
        mView.openImdb(imdbUrl);
    }

    @Override
    public void addMovieToFavourites() {
        if(isMovieFavourite)
            mDao.removeFromFavourites(movie.getId());
        else
            mDao.addToFavourite(movie);
    }

    @Override
    public void showOrHideReviews(boolean show) {
        mView.showOrHideReviews(show);
        if(show) {
            if(mView.getMovieReviewAdapter().getItemCount() == 0) {
                mView.showReviewSpinner();
                callMovieReviewsAPI();
            } else {
                mView.showReviews();
            }
        }
    }

    @Override
    public void startTrailer() {
        if(trailers.size() > 1) {
            mView.showTrailerChooserDialog(trailers, trailerDescriptions);
        } else if(trailers.size() == 1) {
            mView.startTrailerOnYoutube(trailers.get(0).getKey());
        } else {
            mView.hidePlayTrailerButton();
        }
    }

    @Override
    public void startTrailerFromChoice(String url) {
        mView.startTrailerOnYoutube(url);
    }

    @Override
    public void initiateDao() {
        mDao.open();
    }

    @Override
    public void closeDao() {
        mDao.close();
    }

    @Override
    public void onFavouritesFoundListener(ArrayList<PopularMoviesResponse.Movie> favouriteMovies) {}

    @Override
    public void onMovieIsFoundInFavouritesListener(boolean isFavourite) {
        isMovieFavourite = isFavourite;
        mView.updateFavouriteButton(isFavourite);
    }

    @Override
    public void onMovieAddedToFavouritesListener(boolean addingSuccess) {
        mView.updateFavouriteButton(addingSuccess);
        isMovieFavourite = addingSuccess ? true : false;
    }

    @Override
    public void onMovieRemovedFromFavouritesListener(boolean removalSuccess) {
        mView.updateFavouriteButton(!removalSuccess);
        isMovieFavourite = removalSuccess ? false : true;
    }

    private class MovieDetailsSubscriber extends BaseSubscriber<MovieDetailsResponse> {

        @Override
        public void onNext(MovieDetailsResponse movieDetailsResponse) {
            if(movieDetailsResponse != null) {
                mDetails = movieDetailsResponse;
                populateDataIntoDetails(mDetails);
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

        }
    }

    private class MovieReviewResponseSubscriber extends BaseSubscriber<ReviewsResponse> {

        @Override
        public String getSubscriberName() {
            return "Movie Review response";
        }

        @Override
        public void onNext(ReviewsResponse reviewsResponse) {
            Observable.just(reviewsResponse.getResults())
                    .flatMap(new Func1<ArrayList<ReviewsResponse.Review>, Observable<ReviewsResponse.Review>>() {
                        @Override
                        public Observable<ReviewsResponse.Review> call(ArrayList<ReviewsResponse.Review> reviews) {
                            return Observable.from(reviews);
                        }
                    })
                    .subscribe(new MovieReviewsSubscriber());
        }
    }

    private class MovieReviewsSubscriber extends BaseSubscriber<ReviewsResponse.Review> {

        @Override
        public String getSubscriberName() {
            return "Movie reviews";
        }

        @Override
        public void onNext(ReviewsResponse.Review review) {
            mView.getMovieReviewAdapter().addReview(review);
        }

        @Override
        public void onCompleted() {
            super.onCompleted();
            mView.hideReviewsSpinner();
            mView.showReviews();
        }

        @Override
        public void onError(Throwable e) {
            super.onError(e);
            mView.hideReviewsSpinner();
        }
    }

    private class MoviePostersSubscriber extends BaseSubscriber<String> {
        @Override
        public void onNext(String movieUrl) {
            mView.addMoviePosterUrl(movieUrl);
        }

        @Override
        public String getSubscriberName() {
            return "Movie Posters";
        }
    }

    private class MovieVideoResponseSubscriber extends BaseSubscriber<VideoResponse> {

        @Override
        public String getSubscriberName() {
            return "Movie Video response";
        }

        @Override
        public void onNext(VideoResponse videoResponse) {
            Observable.just(videoResponse.getResults())
                    .flatMap(new Func1<ArrayList<VideoResponse.Video>, Observable<VideoResponse.Video>>() {
                        @Override
                        public Observable<VideoResponse.Video> call(ArrayList<VideoResponse.Video> videos) {
                            return Observable.from(videos);
                        }
                    })
                    .subscribe(new MovieVideoSubscriber());
        }
    }

    private class MovieVideoSubscriber extends BaseSubscriber<VideoResponse.Video> {

        @Override
        public String getSubscriberName() {
            return "Movie video";
        }

        @Override
        public void onNext(VideoResponse.Video video) {
            trailers.add(video);
            trailerDescriptions.add(video.getName());
        }

        @Override
        public void onCompleted() {
            if(trailers.isEmpty())
                mView.hidePlayTrailerButton();
        }
    }

    private void populateDataIntoDetails(MovieDetailsResponse movieDetailsResponse) {
        mView.loadMovieBackdropImage(movieDetailsResponse.getBackdrop_path());
        mView.loadMoviePosterImage(movieDetailsResponse.getPoster_path());

        mView.populateSummaryAndDatesFromResponse(movieDetailsResponse);

        imdbUrl = movieDetailsResponse.getImdb_id();

        movie = new PopularMoviesResponse.Movie();
        movie.setId(movieDetailsResponse.getId());
        movie.setTitle(movieDetailsResponse.getTitle());
        movie.setVote_average(movieDetailsResponse.getVote_average());
        movie.setPoster_path(movieDetailsResponse.getPoster_path());
        movie.setPopularity(movieDetailsResponse.getPopularity());

        mDao.checkIfMovieIsFavourite(movie.getId());
    }

}
