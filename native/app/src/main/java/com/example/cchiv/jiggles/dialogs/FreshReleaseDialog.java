package com.example.cchiv.jiggles.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.cchiv.jiggles.R;
import com.example.cchiv.jiggles.model.Release;
import com.example.cchiv.jiggles.utilities.Tools;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FreshReleaseDialog extends DialogFragment {

    private static final String TAG = "FreshReleaseDialog";

    public interface OnActionClickRelease {
        void onClickRelease(Release release);
    }

    private View rootView;

    @BindView(R.id.dialog_fresh_title) TextView textTitleView;

    @BindView(R.id.dialog_release_title) TextView textReleaseTitleView;
    @BindView(R.id.dialog_release_artist) TextView textReleaseArtistView;
    @BindView(R.id.dialog_fresh_caption) ImageView textReleaseArtView;
    @BindView(R.id.dialog_release_impact) ImageView textReleaseImpactView;
    @BindView(R.id.dialog_release_score) TextView textReleaseScoreView;
    @BindView(R.id.dialog_release_action) View textReleaseActionView;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        FragmentActivity fragmentActivity = getActivity();
        if(fragmentActivity != null) {
            // Get the layout inflater
            LayoutInflater inflater = fragmentActivity.getLayoutInflater();

            // Pass null as the parent view because its going in the dialog layout
            rootView = inflater.inflate(R.layout.dialog_fresh_layout, null, false);

            rootView.setVisibility(View.GONE);
            rootView.setOnClickListener(view -> dismiss());

            setShowsDialog(true);

            // Inflate and set the layout for the dialog
            builder.setView(rootView);

            ButterKnife.bind(this, rootView);
        } else {
            setShowsDialog(false);
            dismiss();
        }

        return builder.create();
    }


    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);

        setShowsDialog(false);
    }

    public void onUpdateDialog(FragmentManager fragmentManager, List<Release> releases, OnActionClickRelease onActionClickRelease) {
        if(!getShowsDialog() || releases == null || releases.size() == 0)
            return;

        if(fragmentManager == null)
            return;

        Release release = releases.get(0);

        // Title
        textTitleView.setText(getString(R.string.dialog_fresh_title, "album"));

        // Metadata
        textReleaseTitleView.setText(release.getTitle());
        textReleaseArtistView.setText(getString(R.string.app_component_author, release.getArtist()));

        // Action
        textReleaseActionView.setOnClickListener(view -> onActionClickRelease.onClickRelease(release));

        // Score
        Tools.ScoreView scoreView = new Tools.ScoreView(
                textReleaseScoreView,
                textReleaseImpactView
        );
        Tools.onComputeScore(getActivity(), release.getReviews(), scoreView);

        // Art cover
        Picasso.get()
                .load(release.getUrl())
                .into(new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        textReleaseArtView.setImageBitmap(bitmap);

                        rootView.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                        Log.v(TAG, e.toString());
                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {}
                });
    }
}
