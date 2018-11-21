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
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.cchiv.jiggles.Constants;
import com.example.cchiv.jiggles.R;
import com.example.cchiv.jiggles.utilities.NetworkUtilities;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;

import static android.app.Activity.RESULT_OK;

public class ThreadFragment extends Fragment {

    private static final String TAG = "ThreadCreatorFragment";

    private static final int ACTION_PICK_CODE = 121;

    private Context context = null;
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

                    ImageView thumbnail = rootView.findViewById(R.id.thread_content);
                    thumbnail.setImageBitmap(bitmap);
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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_thread_layout, container, false);

        rootView.findViewById(R.id.thread_content).setOnClickListener((view) -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(Intent.createChooser(intent, "Select Thumbnail"), ACTION_PICK_CODE);
        });

        rootView.findViewById(R.id.thread_edit_submit).setOnClickListener((view) -> {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) ((ImageView) rootView.findViewById(R.id.thread_content)).getDrawable();
            Bitmap bitmap = bitmapDrawable.getBitmap();

            String content = ((TextView) rootView.findViewById(R.id.thread_content)).getEditableText().toString();

            SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.AUTH_TOKEN, Context.MODE_PRIVATE);
            String token = sharedPreferences.getString(Constants.TOKEN, null);

            AsyncTaskEncoder asyncTaskEncoder = new AsyncTaskEncoder(bitmap, content, token);
            asyncTaskEncoder.execute();
        });

        return rootView;
    }

    public static class AsyncTaskEncoder extends AsyncTask<Void, Void, Void> {

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

        @Override
        protected Void doInBackground(Void... voids) {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);

            String thumbnail = Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);

            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put(JSON_CAPTION_KEY, thumbnail);
                jsonObject.put(JSON_CONTENT_KEY, content);

                NetworkUtilities.ResolveCreateThread resolveCreateThread = new NetworkUtilities
                        .ResolveCreateThread(thread -> {
                            // Update the ui with the new thread
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
