package in.surajsau.popularmovies.mainscreen.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import butterknife.Bind;
import butterknife.ButterKnife;
import in.surajsau.popularmovies.R;
import in.surajsau.popularmovies.mainscreen.activity.MainScreenView;
import in.surajsau.popularmovies.mainscreen.adapter.MoviesGridAdapter;
import in.surajsau.popularmovies.mainscreen.presenter.MainScreenPresenter;
import in.surajsau.popularmovies.mainscreen.presenter.MainScreenPresenterImpl;
import me.zhanghai.android.materialprogressbar.MaterialProgressBar;

public class MainFragment extends Fragment implements MainScreenView, MoviesGridAdapter.OnMovieClickedListener {

    private static final String TAG = MainFragment.class.getSimpleName();

    private MainScreenPresenter presenter;

    @Bind(R.id.rlMovieList)
    RecyclerView rlMovieList;
    @Bind(R.id.progress)
    MaterialProgressBar progress;

    private MoviesGridAdapter mAdapter;

    private OnMovieClickedListener mListener;

    public MainFragment() {
        // Required empty public constructor
    }

    public void setOnMovieClickedListener(OnMovieClickedListener listener) {
        mListener = listener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setupRecyclerView();

        presenter = new MainScreenPresenterImpl(this);

        //--load popular list by default
        presenter.callPopularMoviesAPI();
    }

    private void setupRecyclerView() {
        mAdapter = new MoviesGridAdapter(getActivity());
        mAdapter.setOnMovieClickListener(this);

        rlMovieList.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        rlMovieList.setAdapter(mAdapter);
    }

    @Override
    public MoviesGridAdapter getMovieGridAdapter() {
        return mAdapter;
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
    public void onContentClicked(int movieId, String movieTitle) {
        mListener.onMovieClicked(movieId, movieTitle);
    }

    public interface OnMovieClickedListener {
        void onMovieClicked(int id, String movieTitle);
    }

    public void onPopularMenuSelected() {
        presenter.onPopularMenuSelected();
    }

    public void onRatingsMenuSelected() {
        presenter.onRatingsMenuSelected();
    }
}
