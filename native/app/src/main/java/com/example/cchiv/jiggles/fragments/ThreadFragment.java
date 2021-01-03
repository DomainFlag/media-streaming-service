package com.example.cchiv.jiggles.fragments;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.cchiv.jiggles.Constants;
import com.example.cchiv.jiggles.R;
import com.example.cchiv.jiggles.databinding.FragmentStoreLayoutBinding;
import com.example.cchiv.jiggles.databinding.FragmentThreadLayoutBinding;
import com.example.cchiv.jiggles.model.Thread;
import com.example.cchiv.jiggles.utilities.NetworkUtilities;
import com.example.cchiv.jiggles.utilities.Tools;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;

import static android.app.Activity.RESULT_OK;

public class ThreadFragment extends Fragment {

    private static final String TAG = "ThreadCreatorFragment";

    private static final int ACTION_PICK_CODE = 121;

    public interface OnThreadCreationCallback {
        void onThreadCreationCallback(Thread thread);
    }

    private Context context = null;
    private OnThreadCreationCallback onThreadCreationCallback = null;
    private View rootView = null;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == ACTION_PICK_CODE && resultCode == RESULT_OK) {
            if(data.getData() == null || rootView == null)
                return;

            if(context != null) {
                ContentResolver contentResolver = context.getContentResolver();

                try {
                    Bitmap bitmap = BitmapFactory.decodeStream(contentResolver.openInputStream(data.getData()));

                    RelativeLayout relativeLayout = rootView.findViewById(R.id.thread_caption_layout);
                    relativeLayout.setVisibility(View.VISIBLE);

                    ImageView captionView = rootView.findViewById(R.id.thread_caption);
                    captionView.setImageBitmap(bitmap);
                } catch(FileNotFoundException e) {
                    Log.v(TAG, e.toString());
                }
            }
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        this.context = context;
    }

    public void onAttachThreadCreationCallback(OnThreadCreationCallback onThreadCreationCallback) {
        this.onThreadCreationCallback = onThreadCreationCallback;
    }
    
    private FragmentThreadLayoutBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentThreadLayoutBinding.inflate(inflater, container, false);
        View rootView = binding.getRoot();

        Tools.resolveCallbackUser(user -> {
            Picasso.get()
                    .load(user.getCaption())
                    .placeholder(R.drawable.ic_account)
                    .error(R.drawable.ic_account)
                    .into(binding.threadAccount);
        });

        binding.threadClose.setOnClickListener(view -> {
            FragmentManager fragmentManager = getFragmentManager();

            if(fragmentManager != null)
                fragmentManager.beginTransaction().remove(this).commit();
        });

        binding.threadCaptionClose.setOnClickListener(view -> {
            ((ImageView) rootView.findViewById(R.id.thread_caption)).setImageBitmap(null);
            (rootView.findViewById(R.id.thread_caption_layout)).setVisibility(View.GONE);
        });

        binding.threadUploadMedia.setOnClickListener((view) -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(Intent.createChooser(intent, "Select Thumbnail"), ACTION_PICK_CODE);
        });

        binding.threadEditSubmit.setOnClickListener((view) -> {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) ((ImageView) rootView.findViewById(R.id.thread_caption)).getDrawable();
            Bitmap bitmap = null;
            if(bitmapDrawable != null)
                bitmap = bitmapDrawable.getBitmap();

            String content = ((TextView) rootView.findViewById(R.id.thread_content)).getEditableText().toString();

            SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.AUTH_TOKEN, Context.MODE_PRIVATE);
            String token = sharedPreferences.getString(Constants.TOKEN, null);

            AsyncTaskEncoder asyncTaskEncoder = new AsyncTaskEncoder(bitmap, content, token);
            asyncTaskEncoder.execute();
        });

        return rootView;
    }

    public class AsyncTaskEncoder extends AsyncTask<Void, Void, Void> {

        private static final String JSON_CAPTION_KEY = "caption";
        private static final String JSON_CONTENT_KEY = "content";

        private String token;
        private Bitmap bitmap;
        private String content;

        public AsyncTaskEncoder(Bitmap bitmap, String content, String token) {
            this.bitmap = bitmap;
            this.content = content;
            this.token = token;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        private String encodeBitmap(Bitmap bitmap) {
            if(bitmap != null) {
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

                bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);

                return Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);
            }

            return "";
        }

        @Override
        protected Void doInBackground(Void... voids) {
            String caption = encodeBitmap(bitmap);

            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put(JSON_CAPTION_KEY, caption);
                jsonObject.put(JSON_CONTENT_KEY, content);

                NetworkUtilities.ResolveCreateThread resolveCreateThread = new NetworkUtilities.ResolveCreateThread(thread -> {
                    if(onThreadCreationCallback != null)
                        onThreadCreationCallback.onThreadCreationCallback(thread);
                }, jsonObject, token);
            } catch(JSONException e) {
                Log.v(TAG, e.toString());
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void v) {
            super.onPostExecute(v);
        }
    }
}
