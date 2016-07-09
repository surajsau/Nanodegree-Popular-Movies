package in.surajsau.popularmovies.network.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

/**
 * Created by MacboolBro on 08/04/16.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class PopularMoviesResponse {

    private int page;
    private List<Movie> results;

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public List<Movie> getResults() {
        return results;
    }

    public void setResults(List<Movie> results) {
        this.results = results;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Movie {
        private String poster_path;
        private int id;
        private String title;
        private float popularity;
        private float vote_average;

        public String getPoster_path() {
            return poster_path;
        }

        public void setPoster_path(String poster_path) {
            this.poster_path = poster_path;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public float getPopularity() {
            return popularity;
        }

        public void setPopularity(float popularity) {
            this.popularity = popularity;
        }

        public float getVote_average() {
            return vote_average;
        }

        public void setVote_average(float vote_average) {
            this.vote_average = vote_average;
        }
    }
}
