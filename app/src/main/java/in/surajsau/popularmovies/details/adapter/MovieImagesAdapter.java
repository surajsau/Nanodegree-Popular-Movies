package in.surajsau.popularmovies.details.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import in.surajsau.popularmovies.R;
import in.surajsau.popularmovies.Util;

/**
 * Created by MacboolBro on 16/04/16.
 */
public class MovieImagesAdapter extends RecyclerView.Adapter<MovieImagesAdapter.MovieImagesViewHolder> {

    private Context mContext;
    private ArrayList<String> moviePosterUrls;
    int count;

    public MovieImagesAdapter(Context context) {
        mContext = context;
        moviePosterUrls = new ArrayList<>();
        count = 0;
    }

    @Override
    public MovieImagesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View galleryItemView = LayoutInflater.from(mContext).inflate(R.layout.movie_poster_layout, parent, false);
        return new MovieImagesViewHolder(galleryItemView);
    }

    @Override
    public void onBindViewHolder(MovieImagesViewHolder holder, int position) {
        if(moviePosterUrls.get(position) != null) {
            Picasso.with(mContext).load(Util.getPosterImageUrlForGallery(moviePosterUrls.get(position)))
                    .into(holder.ivGalleryMoviePoster);
        }
    }

    @Override
    public int getItemCount() {
        return moviePosterUrls.size();
    }

    public void addMoviePosterUrl(String url) {
        if(url != null) {
            moviePosterUrls.add(url);
            notifyItemInserted(count);
            count++;
        }
    }

    public class MovieImagesViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.ivGalleryMoviePoster) ImageView ivGalleryMoviePoster;

        public MovieImagesViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
