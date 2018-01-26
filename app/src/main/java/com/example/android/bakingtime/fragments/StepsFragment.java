package com.example.android.bakingtime.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.media.session.MediaButtonReceiver;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.bakingtime.R;
import com.example.android.bakingtime.data.LocalStore;
import com.example.android.bakingtime.databinding.FragmentStepsBinding;
import com.example.android.bakingtime.models.Recipe;
import com.example.android.bakingtime.models.Steps;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import static com.example.android.bakingtime.activities.DetailsActivity.STEPS_KEY;
import static com.example.android.bakingtime.activities.MainActivity.RECIPE_KEY;
import static com.example.android.bakingtime.utilities.NetworkUtils.loadImage;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link StepsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link StepsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StepsFragment extends Fragment implements ExoPlayer.EventListener{
    private static final String LOG_TAG = StepsFragment.class.getSimpleName();
    private static final String TAB_POSITION = "position";
    private static final String VID_STATE = "video_state";
    private static final String VID_POSITION = "video_position";

    private SimpleExoPlayer mExoPlayer;
    private SimpleExoPlayerView mExoPlayerView;
    private TextView tvDescription;
    private ImageView stepsImage;

    private static MediaSessionCompat mMediaSession;
    private PlaybackStateCompat.Builder mStateBuilder;
    private Steps steps;
    private Recipe recipe;
    private int position;
    private long vidPosition = C.TIME_UNSET;
    private Uri videoUri;
    private OnFragmentInteractionListener mListener;

    private boolean playWhenReady = true;

    public StepsFragment() {
        // Required empty public constructor
    }

    public static StepsFragment newInstance(Steps steps, int position, Recipe recipe) {
        StepsFragment fragment = new StepsFragment();
        Bundle args = new Bundle();
        args.putParcelable(STEPS_KEY, steps);
        args.putParcelable(RECIPE_KEY, recipe);
        args.putInt(TAB_POSITION, position);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            steps = getArguments().getParcelable(STEPS_KEY);
            recipe = getArguments().getParcelable(RECIPE_KEY);
            position = getArguments().getInt(TAB_POSITION);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        FragmentStepsBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_steps, container, false);
        View rootView = binding.getRoot();

        mExoPlayerView = binding.playerView;
        tvDescription = binding.tvStepDirections;
        stepsImage = binding.stepsImage;
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (savedInstanceState != null){
            playWhenReady = savedInstanceState.getBoolean(VID_STATE);
            vidPosition = savedInstanceState.getLong(VID_POSITION);
        }

        LocalStore localStore = new LocalStore(getActivity());
        steps = localStore.getStep(position, recipe);
        tvDescription.setText(steps.getDescription());

        videoUri = Uri.parse(steps.getVideoURL());
        String thumbnailURL = steps.getThumbnailURL();

        Log.w(LOG_TAG, "onViewCreated " + playWhenReady);

        if (!thumbnailURL.isEmpty()){
            loadImage(thumbnailURL, stepsImage, getContext());
        }
        showVideo();
    }

    private void showVideo(){
        if (videoUri != null && !videoUri.getPath().isEmpty()){
            setViewVisibility(mExoPlayerView, true);
            initializeMediaSession();
            initializePlayer(videoUri);

            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
                expandVideoView(mExoPlayerView);
                setViewVisibility(tvDescription, true);
                hideSystemUI();

            }
        }else {
            setViewVisibility(mExoPlayerView, false);
        }
    }

    private void setViewVisibility(View view, boolean show) {
        Log.w(LOG_TAG, "setViewVisibility " + show);
        if (show) {
            view.setVisibility(View.VISIBLE);
        } else {
            view.setVisibility(View.GONE);
        }
    }

    private void expandVideoView(SimpleExoPlayerView exoPlayer) {
        exoPlayer.getLayoutParams().height = LayoutParams.MATCH_PARENT;
        exoPlayer.getLayoutParams().width = LayoutParams.MATCH_PARENT;
    }

    // Src: https://developer.android.com/training/system-ui/immersive.html
    private void hideSystemUI() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getActivity().getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE);
        }
    }

    private void initializeMediaSession() {
        Log.w(LOG_TAG, "initializeMediaSession " + steps.getShortDescription());
        mMediaSession = new MediaSessionCompat(getContext(), LOG_TAG);
        mMediaSession.setFlags(
                MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS |
                        MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);

        mMediaSession.setMediaButtonReceiver(null);
        mStateBuilder = new PlaybackStateCompat.Builder()
                .setActions(
                        PlaybackStateCompat.ACTION_PLAY |
                                PlaybackStateCompat.ACTION_PAUSE |
                                PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS |
                                PlaybackStateCompat.ACTION_PLAY_PAUSE);

        mMediaSession.setPlaybackState(mStateBuilder.build());
//        mMediaSession.setCallback(new MySessionCallback());
        mMediaSession.setActive(true);
    }

    private void initializePlayer(Uri mediaUri) {
        Log.w(LOG_TAG, "initializePlayer " + vidPosition);
        if (mExoPlayer == null) {
            // Create an instance of the ExoPlayer.
            TrackSelector trackSelector = new DefaultTrackSelector();
            LoadControl loadControl = new DefaultLoadControl();
            mExoPlayer = ExoPlayerFactory.newSimpleInstance(getContext(), trackSelector, loadControl);
            mExoPlayerView.setPlayer(mExoPlayer);

            // Set the ExoPlayer.EventListener to this activity.
            mExoPlayer.addListener(this);

            // Prepare the MediaSource.
            String userAgent = Util.getUserAgent(getContext(), "BakingTime");
            MediaSource mediaSource = new ExtractorMediaSource(mediaUri, new DefaultDataSourceFactory(
                    getContext(), userAgent), new DefaultExtractorsFactory(), null, null);
            if (vidPosition != C.TIME_UNSET) mExoPlayer.seekTo(vidPosition);
            mExoPlayer.prepare(mediaSource);
            mExoPlayer.setPlayWhenReady(playWhenReady);
        }
    }

    /**
     * Release ExoPlayer.
     */
    private void releasePlayer() {
        if (mExoPlayer != null){
            playWhenReady = mExoPlayer.getPlayWhenReady();
            vidPosition = mExoPlayer.getCurrentPosition();
            mExoPlayer.stop();
            mExoPlayer.release();
            mExoPlayer = null;
            Log.w(LOG_TAG, "releasePlayer playWhenReady "+ playWhenReady );
        }

        if(mMediaSession!=null) {
            mMediaSession.setActive(false);
        }
    }

    @Override
    public void onTimelineChanged(Timeline timeline, Object manifest) {

    }

    @Override
    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

    }

    @Override
    public void onLoadingChanged(boolean isLoading) {

    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        vidPosition = mExoPlayer.getCurrentPosition();
        if((playbackState == ExoPlayer.STATE_READY) && playWhenReady){
            mStateBuilder.setState(PlaybackStateCompat.STATE_PLAYING,
                    vidPosition, 1f);
        } else if((playbackState == ExoPlayer.STATE_READY)) {
            mStateBuilder.setState(PlaybackStateCompat.STATE_PAUSED,
                    vidPosition, 1f);
        }
        Log.w(LOG_TAG, "onPlayerStateChanged position = "+ vidPosition );
        mMediaSession.setPlaybackState(mStateBuilder.build());
    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {

    }

    @Override
    public void onPositionDiscontinuity() {

    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(VID_STATE, playWhenReady);
        outState.putLong(VID_POSITION, vidPosition);
        Log.w(LOG_TAG, "onSaveInstanceState "+ playWhenReady );
    }

    @Override
    public void onPause(){
        super.onPause();
        releasePlayer();
        Log.w(LOG_TAG, "onPause playWhenReady "+ playWhenReady );
    }

//    private class MySessionCallback extends MediaSessionCompat.Callback {
//        @Override
//        public void onPlay() {
//            Log.w(LOG_TAG, "MySessionCallback onPlay = "+ position );
//            mExoPlayer.setPlayWhenReady(true);
//        }
//
//        @Override
//        public void onPause() {
//            Log.w(LOG_TAG, "MySessionCallback onPause = "+ position );
//            mExoPlayer.setPlayWhenReady(false);
//        }
//
//        @Override
//        public void onSkipToPrevious() {
//            Log.w(LOG_TAG, "MySessionCallback onSkipToPrevious = "+ position );
//            mExoPlayer.seekTo(0);
//        }
//    }

    /**
     * Broadcast Receiver registered to receive the MEDIA_BUTTON intent coming from clients.
     */
    public static class MediaReceiver extends BroadcastReceiver {

        public MediaReceiver() {
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            MediaButtonReceiver.handleIntent(mMediaSession, intent);
        }
    }
}
