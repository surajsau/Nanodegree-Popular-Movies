package in.surajsau.popularmovies.mainscreen.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import in.surajsau.popularmovies.IConstants;
import in.surajsau.popularmovies.R;
import in.surajsau.popularmovies.details.activity.MovieDetailsActivity;
import in.surajsau.popularmovies.network.models.PopularMoviesResponse;

/**
 * Created by MacboolBro on 08/04/16.
 */
public class PopularMoviesGridAdapter extends RecyclerView.Adapter<PopularMoviesGridAdapter.PopularMoviesGridViewHolder> {

    private List<PopularMoviesResponse.Movie> mMovies;
    private Context mContext;

    private PopularMoviesGridAdapter(Context context, List<PopularMoviesResponse.Movie> movies) {
        mContext = context;
        mMovies = movies;
    }

    @Override
    public PopularMoviesGridViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View gridItemLayout = LayoutInflater.from(mContext).inflate(R.layout.movie_grid_card_layout, parent, false);
        return new PopularMoviesGridViewHolder(gridItemLayout);
    }

    @Override
    public void onBindViewHolder(PopularMoviesGridViewHolder holder, int position) {
        if(mMovies.get(position) != null) {
            PopularMoviesResponse.Movie movie = mMovies.get(position);
            holder.tvMovieName.setText(movie.getTitle());
            holder.tvMoviePopularity.setText(String.format("%.2f", movie.getPopularity()));
            holder.tvMovieRating.setText(String.format("%.2f", movie.getVote_average()));
            holder.cvMovieGrid.setTag(movie);
        }
    }

    @Override
    public int getItemCount() {
        return mMovies.size();
    }

    public class PopularMoviesGridViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        @Bind(R.id.cvMovieGrid) CardView cvMovieGrid;
        @Bind(R.id.tvMovieName) TextView tvMovieName;
        @Bind(R.id.tvMovieRating) TextView tvMovieRating;
        @Bind(R.id.tvMoviePopularity) TextView tvMoviePopularity;


        public PopularMoviesGridViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(itemView);

            cvMovieGrid.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            PopularMoviesResponse.Movie movie = (PopularMoviesResponse.Movie) v.getTag();
            startMovieDetailsActivity(movie.getId(), movie.getTitle());
        }
    }

    private void startMovieDetailsActivity(int movieId, String movieTitle) {
        Intent movieDetailsIntent = new Intent(mContext, MovieDetailsActivity.class);
        movieDetailsIntent.putExtra(IConstants.MOVIE_TITLE, movieTitle);
        movieDetailsIntent.putExtra(IConstants.MOVIE_ID, movieId);
        mContext.startActivity(movieDetailsIntent);
    }
}
