package in.surajsau.popularmovies.mainscreen.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import in.surajsau.popularmovies.IConstants;
import in.surajsau.popularmovies.R;
import in.surajsau.popularmovies.details.activity.MovieDetailsActivity;
import in.surajsau.popularmovies.Util;
import in.surajsau.popularmovies.network.models.PopularMoviesResponse;

/**
 * Created by MacboolBro on 08/04/16.
 */
public class MoviesGridAdapter extends RecyclerView.Adapter<MoviesGridAdapter.PopularMoviesGridViewHolder> {

    private List<PopularMoviesResponse.Movie> mMovies;
    private Context mContext;

    private OnMovieClickedListener mListener;

    public MoviesGridAdapter(Context context) {
        mContext = context;
        mMovies = new ArrayList<>();
    }

    public void addMovieToList(PopularMoviesResponse.Movie movie) {
        mMovies.add(movie);
        notifyDataSetChanged();
    }

    public void setOnMovieClickListener(@NonNull OnMovieClickedListener listener) {
        mListener = listener;
    }

    public void clearMoviesList() {
        mMovies.clear();
        notifyDataSetChanged();
    }

    @Override
    public PopularMoviesGridViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View gridItemLayout = LayoutInflater.from(mContext).inflate(R.layout.movie_grid_card_layout, parent, false);
        return new PopularMoviesGridViewHolder(gridItemLayout);
    }

    @Override
    public void onBindViewHolder(PopularMoviesGridViewHolder holder, int position) {
        if(mMovies != null && mMovies.get(position) != null) {
            PopularMoviesResponse.Movie movie = mMovies.get(position);
            holder.tvMovieName.setText(movie.getTitle());
            Picasso.with(mContext)
                    .load(Util.getPosterImageUrl(movie.getPoster_path()))
                    .into(holder.ivMoviePoster);
            holder.tvMoviePopularity.setText(String.format("%.2f", movie.getPopularity()));
            holder.tvMovieRating.setText(String.format("%.2f", movie.getVote_average()));
            holder.llMovieGrid.setTag(movie);
        }
    }

    @Override
    public int getItemCount() {
        return mMovies.size();
    }

    public class PopularMoviesGridViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        @Bind(R.id.llMovieGrid) LinearLayout llMovieGrid;
        @Bind(R.id.ivMoviePoster) AppCompatImageView ivMoviePoster;
        @Bind(R.id.tvMovieName) AppCompatTextView tvMovieName;
        @Bind(R.id.tvMovieRating) AppCompatTextView tvMovieRating;
        @Bind(R.id.tvMoviePopularity) AppCompatTextView tvMoviePopularity;


        public PopularMoviesGridViewHolder(View itemView) {
            super(itemView);
            try {
                ButterKnife.bind(PopularMoviesGridViewHolder.this, itemView);
            } catch (Exception e) {
                e.printStackTrace();
            }

            llMovieGrid.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            PopularMoviesResponse.Movie movie = (PopularMoviesResponse.Movie) v.getTag();
            mListener.onContentClicked(movie.getId(), movie.getTitle());
        }
    }

    public interface OnMovieClickedListener {
        void onContentClicked(int movieId, String movieTitle);
    }
}
