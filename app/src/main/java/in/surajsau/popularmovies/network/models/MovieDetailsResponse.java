package in.surajsau.popularmovies.network.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

/**
 * Created by MacboolBro on 08/04/16.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class MovieDetailsResponse implements Parcelable {

    private String backdrop_path;
    private int id;
    private String imdb_id;
    private String overview;
    private float popularity;
    private String poster_path;
    private String release_date;
    private String title;
    private float vote_average;

    public MovieDetailsResponse(){}

    protected MovieDetailsResponse(Parcel in) {
        backdrop_path = in.readString();
        id = in.readInt();
        imdb_id = in.readString();
        overview = in.readString();
        popularity = in.readFloat();
        poster_path = in.readString();
        release_date = in.readString();
        title = in.readString();
        vote_average = in.readFloat();
    }

    public static final Creator<MovieDetailsResponse> CREATOR = new Creator<MovieDetailsResponse>() {
        @Override
        public MovieDetailsResponse createFromParcel(Parcel in) {
            return new MovieDetailsResponse(in);
        }

        @Override
        public MovieDetailsResponse[] newArray(int size) {
            return new MovieDetailsResponse[size];
        }
    };

    public String getBackdrop_path() {
        return backdrop_path;
    }

    public void setBackdrop_path(String backdrop_path) {
        this.backdrop_path = backdrop_path;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getImdb_id() {
        return imdb_id;
    }

    public void setImdb_id(String imdb_id) {
        this.imdb_id = imdb_id;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public float getPopularity() {
        return popularity;
    }

    public void setPopularity(float popularity) {
        this.popularity = popularity;
    }

    public String getPoster_path() {
        return poster_path;
    }

    public void setPoster_path(String poster_path) {
        this.poster_path = poster_path;
    }

    public String getRelease_date() {
        return release_date;
    }

    public void setRelease_date(String release_date) {
        this.release_date = release_date;
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public float getVote_average() {
        return vote_average;
    }

    public void setVote_average(float vote_average) {
        this.vote_average = vote_average;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(backdrop_path);
        parcel.writeInt(id);
        parcel.writeString(imdb_id);
        parcel.writeString(overview);
        parcel.writeFloat(popularity);
        parcel.writeString(poster_path);
        parcel.writeString(release_date);
        parcel.writeString(title);
        parcel.writeFloat(vote_average);
    }
}
