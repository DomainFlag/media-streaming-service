package com.example.cchiv.jiggles.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.cchiv.jiggles.R;
import com.example.cchiv.jiggles.databinding.DialogFreshLayoutBinding;
import com.example.cchiv.jiggles.databinding.FragmentThreadLayoutBinding;
import com.example.cchiv.jiggles.model.Release;
import com.example.cchiv.jiggles.utilities.Tools;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.List;


public class FreshReleaseDialog extends DialogFragment {

    private static final String TAG = "FreshReleaseDialog";
    private DialogFreshLayoutBinding binding;

    public interface OnActionClickRelease {
        void onClickRelease(Release release);
    }

    private View rootView;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        FragmentActivity fragmentActivity = getActivity();
        if(fragmentActivity != null) {
            // Get the layout inflater
            LayoutInflater inflater = fragmentActivity.getLayoutInflater();

            // Pass null as the parent view because its going in the dialog layout
            binding = DialogFreshLayoutBinding.inflate(inflater, null, false);
            rootView = binding.getRoot();

            rootView.setVisibility(View.GONE);
            rootView.setOnClickListener(view -> dismiss());

            setShowsDialog(true);

            // Inflate and set the layout for the dialog
            builder.setView(rootView);
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
        binding.dialogFreshTitle.setText(getString(R.string.dialog_fresh_title, "album"));

        // Metadata
        binding.dialogReleaseTitle.setText(release.getTitle());
        binding.dialogReleaseArtist.setText(getString(R.string.app_component_author, release.getArtist()));

        // Action
        binding.dialogReleaseAction.setOnClickListener(view -> onActionClickRelease.onClickRelease(release));

        // Score
        Tools.ScoreView scoreView = new Tools.ScoreView(
                binding.dialogReleaseScore,
                binding.dialogReleaseImpact
        );
        Tools.onComputeScore(getActivity(), release.getReviews(), scoreView);

        // Art cover
        Picasso.get()
                .load(release.getUrl())
                .into(new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        binding.dialogFreshCaption.setImageBitmap(bitmap);

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
