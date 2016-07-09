package in.surajsau.popularmovies.details.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import in.surajsau.popularmovies.R;
import in.surajsau.popularmovies.network.models.ReviewsResponse;

/**
 * Created by surajkumarsau on 09/07/16.
 */
public class MovieReviewsAdapter extends RecyclerView.Adapter<MovieReviewsAdapter.MovireReviewViewHolder>{

    private Context mContext;

    private ArrayList<ReviewsResponse.Review> mReviews;

    public MovieReviewsAdapter(Context context) {
        mContext = context;
        mReviews = new ArrayList<>();
    }

    @Override
    public MovireReviewViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View reviewLayout = LayoutInflater.from(mContext).inflate(R.layout.row_movie_review, parent, false);
        return new MovireReviewViewHolder(reviewLayout);
    }

    @Override
    public void onBindViewHolder(MovireReviewViewHolder holder, int position) {
        if(mReviews.get(position) != null) {
            holder.tvAuthor.setText(mReviews.get(position).getAuthor());
            holder.tvContent.setText(mReviews.get(position).getContent());
            holder.btnReadMore.setTag(mReviews.get(position).getUrl());
        }
    }

    @Override
    public int getItemCount() {
        return mReviews.size();
    }

    public void addReview(ReviewsResponse.Review review) {
        mReviews.add(review);
        notifyItemInserted(mReviews.size() - 1);
    }

    public class MovireReviewViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        @Bind(R.id.tvAuthor) TextView tvAuthor;
        @Bind(R.id.tvContent) TextView tvContent;
        @Bind(R.id.btnReadMore) Button btnReadMore;

        public MovireReviewViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            btnReadMore.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW);
            browserIntent.setData(Uri.parse(v.getTag().toString()));
            mContext.startActivity(browserIntent);
        }
    }
}
