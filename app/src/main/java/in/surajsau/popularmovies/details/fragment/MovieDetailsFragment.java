package in.surajsau.popularmovies.details.fragment;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import butterknife.Bind;
import butterknife.ButterKnife;
import in.surajsau.popularmovies.IConstants;
import in.surajsau.popularmovies.R;
import in.surajsau.popularmovies.Util;
import in.surajsau.popularmovies.details.activity.MovieDetailsView;
import in.surajsau.popularmovies.details.adapter.MovieImagesAdapter;
import in.surajsau.popularmovies.details.presenter.MovieDetailsPresenter;
import in.surajsau.popularmovies.details.presenter.MovieDetailsPresenterImpl;
import in.surajsau.popularmovies.network.models.MovieDetailsResponse;
import me.zhanghai.android.materialprogressbar.MaterialProgressBar;

/**
 * A simple {@link Fragment} subclass.
 */
public class MovieDetailsFragment extends Fragment implements MovieDetailsView, View.OnClickListener {

    private static final String TAG = MovieDetailsFragment.class.getSimpleName();

    private int movieId;
    private String movieTitle;

    private MovieDetailsPresenter presenter;

    private MovieImagesAdapter mPosterAdapter;

//    @Bind(R.id.toolbar) Toolbar toolbar;
    @Bind(R.id.cvMovieDetails) CardView cvMovieDetails;
    @Bind(R.id.llMovieDetails) LinearLayout llMovieDetails;
    @Bind(R.id.ivMovieBackdrop) ImageView ivMovieBackdrop;
    @Bind(R.id.ivMoviePoster) AppCompatImageView ivMoviePoster;
    @Bind(R.id.tvReleaseDate) AppCompatTextView tvReleaseDate;
    @Bind(R.id.tvVoteAverage) AppCompatTextView tvVoteAverage;
    @Bind(R.id.tvMovieSummary) AppCompatTextView tvMovieSummary;
    @Bind(R.id.btnImdbLink) Button btnImdbLink;
    @Bind(R.id.rlMoviePosters) RecyclerView rlMoviePosters;
    @Bind(R.id.progress) MaterialProgressBar progress;

    public MovieDetailsFragment() {
        // Required empty public constructor
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
        presenter = new MovieDetailsPresenterImpl(this, movieId);

        setupClickListeners();
        setupGallery();

        presenter.callMovieDetailsAPI();
    }

    private void getDataFromArguments() {
        if(getArguments() != null) {
            movieId = getArguments().getInt(IConstants.MOVIE_ID);
            movieTitle = getArguments().getString(IConstants.MOVIE_TITLE);
        }
    }

    private void setupClickListeners() {
        btnImdbLink.setOnClickListener(this);
    }

    private void setupGallery() {
        mPosterAdapter = new MovieImagesAdapter(getActivity());

        LinearLayoutManager llPostersManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);

        rlMoviePosters.setLayoutManager(llPostersManager);

        rlMoviePosters.setAdapter(mPosterAdapter);
    }

    @Override
    public MovieImagesAdapter getMoviePosterAdapter() {
        return mPosterAdapter;
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
    public void populateDataFromResponse(MovieDetailsResponse res) {
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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnImdbLink: {
                presenter.openImdbLink();
            }
            break;
        }
    }
}
