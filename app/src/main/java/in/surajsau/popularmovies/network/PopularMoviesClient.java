package in.surajsau.popularmovies.network;

import in.surajsau.popularmovies.network.models.MovieDetailsResponse;
import in.surajsau.popularmovies.network.models.PopularMoviesResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import rx.Observable;

/**
 * Created by MacboolBro on 08/04/16.
 */
public interface PopularMoviesClient {

    @GET("/movie/popular")
    Observable<PopularMoviesResponse> getPopularMovies();

    @GET("/movie/{movieId}")
    Call<MovieDetailsResponse> getMovieDetails(@Path("movieId") String movieId);
}
