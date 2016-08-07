package in.surajsau.popularmovies.details.fragment;


import android.content.Intent;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import in.surajsau.popularmovies.IConstants;
import in.surajsau.popularmovies.R;
import in.surajsau.popularmovies.Util;
import in.surajsau.popularmovies.data.FavouritesDAO;
import in.surajsau.popularmovies.details.activity.MovieDetailsView;
import in.surajsau.popularmovies.details.adapter.MovieImagesAdapter;
import in.surajsau.popularmovies.details.adapter.MovieReviewsAdapter;
import in.surajsau.popularmovies.details.presenter.MovieDetailsPresenter;
import in.surajsau.popularmovies.details.presenter.MovieDetailsPresenterImpl;
import in.surajsau.popularmovies.network.models.MovieDetailsResponse;
import in.surajsau.popularmovies.network.models.VideoResponse;
import me.zhanghai.android.materialprogressbar.MaterialProgressBar;

/**
 * A simple {@link Fragment} subclass.
 */
public class MovieDetailsFragment extends Fragment implements MovieDetailsView, View.OnClickListener {

    private static final String TAG = MovieDetailsFragment.class.getSimpleName();

    private int movieId;

    private MovieDetailsPresenter presenter;

    private OnMovieTitleReceivedListener mTitleListener;

    private MovieImagesAdapter mPosterAdapter;
    private MovieReviewsAdapter mReviewsAdapter;

//    @Bind(R.id.toolbar) Toolbar toolbar;
    @Bind(R.id.cvMovieDetails) CardView cvMovieDetails;
    @Bind(R.id.llMovieDetails) LinearLayout llMovieDetails;
    @Bind(R.id.ivMovieBackdrop) ImageView ivMovieBackdrop;
    @Bind(R.id.ivMoviePoster) AppCompatImageView ivMoviePoster;
    @Bind(R.id.tvReleaseDate) AppCompatTextView tvReleaseDate;
    @Bind(R.id.tvVoteAverage) AppCompatTextView tvVoteAverage;
    @Bind(R.id.tvMovieSummary) AppCompatTextView tvMovieSummary;
    @Bind(R.id.btnImdbLink) Button btnImdbLink;
    @Bind(R.id.btnAddFavourites) Button btnAddFavourites;
    @Bind(R.id.btnPlay) Button btnPlay;
    @Bind(R.id.rlMoviePosters) RecyclerView rlMoviePosters;
    @Bind(R.id.rlMovieReviews) RecyclerView rlMovieReviews;
    @Bind(R.id.flMovieReviews) FrameLayout flMovieReviews;
    @Bind(R.id.btnShowReviews) Button btnShowReviews;
    @Bind(R.id.reviewProgress) MaterialProgressBar reviewProgress;
    @Bind(R.id.progress) MaterialProgressBar progress;

    public MovieDetailsFragment() {
        // Required empty public constructor
    }

    public static MovieDetailsFragment getNewInstance(int movieId) {
        Bundle movieBundle = new Bundle();
        movieBundle.putInt(IConstants.MOVIE_ID, movieId);

        MovieDetailsFragment fragment = new MovieDetailsFragment();
        fragment.setArguments(movieBundle);

        return fragment;
    }

    public void setTitleListener(OnMovieTitleReceivedListener listener) {
        mTitleListener = listener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_movie_details, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getDataFromArguments();
        presenter = new MovieDetailsPresenterImpl(this, movieId, new FavouritesDAO(getActivity()));

        presenter.initiateDao();

        setupClickListeners();
        setupGallery();

        if(savedInstanceState != null) {
            presenter.reloadMovieDetail((MovieDetailsResponse) savedInstanceState.getParcelable(IConstants.CURRENT_MOVIE_DETAILS));
        } else {
            presenter.callMovieDetailsAPI();
            presenter.callMovieTrailersAPI();
        }
    }

    public void loadNewMovieDetails(int movieId) {
        this.movieId = movieId;
        presenter.setMovieId(movieId);

        presenter.callMovieDetailsAPI();
        presenter.callMovieTrailersAPI();

        refreshMoviePosters();
        presenter.callMoviePostersAPI();
    }

    @Override
    public void onResume() {
        presenter.initiateDao();
        super.onResume();
    }

    @Override
    public void onPause() {
        presenter.closeDao();
        super.onPause();
    }

    @Override
    public void hidePlayTrailerButton() {
        btnPlay.setVisibility(View.GONE);
    }

    @Override
    public void startTrailerOnYoutube(String url) {
        try {
            Intent youtubeIntent = new Intent(Intent.ACTION_VIEW);
            youtubeIntent.setData(Uri.parse(IConstants.BASE_YOUTUBE_APP_URL.concat(url)));
            startActivity(youtubeIntent);
        } catch (Exception e) {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW);
            browserIntent.setData(Uri.parse(IConstants.BASE_YOUTUBE_URL.concat(url)));
            startActivity(browserIntent);
        }
    }

    @Override
    public void showTrailerChooserDialog(final ArrayList<VideoResponse.Video> videos, ArrayList<String> descriptions) {
        MaterialDialog dlg = new MaterialDialog.Builder(getActivity())
                .title("Choose trailer")
                .items(descriptions)
                .itemsCallbackSingleChoice(0, new MaterialDialog.ListCallbackSingleChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                        presenter.startTrailerFromChoice(videos.get(which).getKey());
                        return true;
                    }
                })
                .build();
        dlg.show();
    }

    private void getDataFromArguments() {
        if(getArguments() != null) {
            movieId = getArguments().getInt(IConstants.MOVIE_ID);
        }
    }

    private void setupClickListeners() {
        btnImdbLink.setOnClickListener(this);
        btnAddFavourites.setOnClickListener(this);
        btnShowReviews.setOnClickListener(this);
        btnPlay.setOnClickListener(this);
    }

    private void setupGallery() {
        mPosterAdapter = new MovieImagesAdapter(getActivity());
        mReviewsAdapter = new MovieReviewsAdapter(getActivity());

        LinearLayoutManager llPostersManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        LinearLayoutManager llReviewsManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);

        rlMoviePosters.setLayoutManager(llPostersManager);
        rlMovieReviews.setLayoutManager(llReviewsManager);

        rlMoviePosters.setAdapter(mPosterAdapter);
        rlMovieReviews.setAdapter(mReviewsAdapter);
    }

    @Override
    public void addMoviePosterUrl(String url) {
        mPosterAdapter.addMoviePosterUrl(url);
    }

    private void refreshMoviePosters() {
        mPosterAdapter.refreshMoviePosters();
    }

    @Override
    public MovieReviewsAdapter getMovieReviewAdapter() {
        return mReviewsAdapter;
    }

    @Override
    public void loadMoviePosterImage(String posterUrl) {
        Picasso.with(getActivity())
                .load(Util.getPosterImageUrlForDetails(posterUrl))
                .into(ivMoviePoster);
    }

    @Override
    public void loadMovieBackdropImage(String backdropUrl) {
        Picasso.with(getActivity())
                .load(Util.getBackdropImageUrl(backdropUrl))
                .into(ivMovieBackdrop);
    }

    @Override
    public void populateSummaryAndDatesFromResponse(MovieDetailsResponse res) {
        if(mTitleListener != null)
            mTitleListener.onMovieTitleReceived(res.getTitle());

        tvReleaseDate.setText(res.getRelease_date());
        tvVoteAverage.setText(res.getVote_average() + "");
        tvMovieSummary.setText(res.getOverview());
    }

    @Override
    public void onMovieDetailsResponseComplete() {
        cvMovieDetails.setVisibility(View.VISIBLE);
        llMovieDetails.setVisibility(View.VISIBLE);
    }

    @Override
    public void openImdb(String imdbUrl) {
        if(!TextUtils.isEmpty(imdbUrl)) {
            try {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("imdb:///title/" + imdbUrl)));
            } catch (Exception e) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.imdb.com/title/" + imdbUrl)));
            }
        } else {
            Toast.makeText(getActivity(), "Cannot open link", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void updateFavouriteButton(boolean isFavourite) {
        btnAddFavourites.setBackgroundColor(ContextCompat.getColor(getActivity(),
                isFavourite ? R.color.colorAddedToFavourites:R.color.colorFavourites));
        btnAddFavourites.setText(isFavourite ? "Favourite" : "Add to Favourites");
    }

    @Override
    public void showOrHideReviews(boolean show) {
        flMovieReviews.setVisibility(show ? View.VISIBLE : View.GONE);

    }

    @Override
    public void showProgress() {
        if(progress.getVisibility() == View.GONE)
            progress.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgress() {
        if(progress.getVisibility() == View.VISIBLE)
            progress.setVisibility(View.GONE);
    }

    @Override
    public void hideReviewsSpinner() {
        if(reviewProgress.getVisibility() == View.VISIBLE)
            reviewProgress.setVisibility(View.GONE);
    }

    @Override
    public void showReviewSpinner() {
        if(reviewProgress.getVisibility() == View.GONE)
            reviewProgress.setVisibility(View.VISIBLE);
    }

    @Override
    public void showReviews() {
        if(rlMovieReviews.getVisibility() == View.GONE)
            rlMovieReviews.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnImdbLink: {
                presenter.openImdbLink();
            }
            break;

            case R.id.btnAddFavourites: {
                presenter.addMovieToFavourites();
            }
            break;

            case R.id.btnShowReviews: {
                presenter.showOrHideReviews(flMovieReviews.getVisibility() == View.GONE);
            }
            break;

            case R.id.btnPlay: {
                presenter.startTrailer();
            }
            break;
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(IConstants.CURRENT_MOVIE_DETAILS, presenter.getCurrentMovieDetails());
    }

    public interface OnMovieTitleReceivedListener {
        void onMovieTitleReceived(String title);
    }
}
