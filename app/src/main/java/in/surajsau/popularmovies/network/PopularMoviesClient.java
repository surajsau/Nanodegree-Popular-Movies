package in.surajsau.popularmovies.network;

import in.surajsau.popularmovies.network.models.MovieDetailsResponse;
import in.surajsau.popularmovies.network.models.MovieImagesResponse;
import in.surajsau.popularmovies.network.models.PopularMoviesResponse;
import in.surajsau.popularmovies.network.models.ReviewsResponse;
import in.surajsau.popularmovies.network.models.VideoResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import rx.Observable;

/**
 * Created by MacboolBro on 08/04/16.
 */
public interface PopularMoviesClient {

    @GET("movie/popular")
    Observable<PopularMoviesResponse> getPopularMovies();

    @GET("movie/top_rated")
    Observable<PopularMoviesResponse> getTopRatedMovies();

    @GET("movie/{movieId}")
    Observable<MovieDetailsResponse> getMovieDetails(@Path("movieId") int movieId);

    @GET("movie/{movieId}/images")
    Observable<MovieImagesResponse> getMovieImages(@Path("movieId") int movieId);

    @GET("movie/{movieId}/reviews")
    Observable<ReviewsResponse> getMovieReviews(@Path("movieId") int movieId);

    @GET("movie/{movieId}/videos")
    Observable<VideoResponse> getMovieVideos(@Path("movieId") int movieid);

}
