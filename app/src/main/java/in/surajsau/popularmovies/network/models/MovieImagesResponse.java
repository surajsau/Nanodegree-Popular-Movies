package in.surajsau.popularmovies.network.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;
import java.util.List;

/**
 * Created by MacboolBro on 16/04/16.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class MovieImagesResponse {

    private int id;
//    private List<Backdrop> backdrops;
    private List<Poster> posters;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

//    public List<Backdrop> getBackdrops() {
//        return backdrops;
//    }
//
//    public void setBackdrops(List<Backdrop> backdrops) {
//        this.backdrops = backdrops;
//    }

    public List<Poster> getPosters() {
        return posters;
    }

    public void setPosters(List<Poster> posters) {
        this.posters = posters;
    }

//    public static class Backdrop extends ImageValueObject {}

    public static class Poster extends ImageValueObject {}

}
