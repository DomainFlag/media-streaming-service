package com.example.cchiv.jiggles.utilities;

import android.content.ContentProviderResult;
import android.content.Context;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.os.AsyncTask;
import android.os.RemoteException;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v7.graphics.Palette;
import android.util.Log;

import com.example.cchiv.jiggles.R;
import com.example.cchiv.jiggles.data.ContentContract;
import com.example.cchiv.jiggles.model.player.Store;
import com.example.cchiv.jiggles.model.player.content.Album;
import com.example.cchiv.jiggles.model.Image;
import com.example.cchiv.jiggles.model.player.content.Track;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ItemScanner {

    private static final int IMAGE_SCALING_WIDTH_SIZE = 640;
    private static final int IMAGE_SCALING_HEIGHT_SIZE = 640;

    private static final String TAG = "ItemScanner";

    private static List<Bitmap> decodeSurroundedBitmapArt(String path) {
        File file = new File(path);
        File parent = file.getParentFile();

        List<Bitmap> bitmaps = new ArrayList<>();

        File[] children = parent.listFiles();
        for(File child : children) {
            String childName = child.getName();
            if(childName.endsWith(".png")
                    || childName.endsWith(".jpg")
                    || childName.endsWith(".jpeg")
                    || childName.endsWith(".gif")
                    || childName.endsWith(".bmp")
                    || childName.endsWith(".webp")) {
                Bitmap bitmap  = BitmapFactory.decodeFile(child.getAbsolutePath());

                bitmaps.add(0, scaleBitmapResource(bitmap));
            }
        }

        return bitmaps;
    }

    public static Bitmap scaleBitmapResource(Bitmap bitmap) {
        if(bitmap.getHeight() != IMAGE_SCALING_HEIGHT_SIZE
                && bitmap.getWidth() != IMAGE_SCALING_WIDTH_SIZE) {

            return Bitmap.createScaledBitmap(bitmap,
                    IMAGE_SCALING_WIDTH_SIZE,
                    IMAGE_SCALING_HEIGHT_SIZE,
                    false);
        } else return bitmap;
    }

    public static String cacheImageResource(Context context, Bitmap image, String title) {
        return MediaStore.Images.Media.insertImage(context.getContentResolver(), image, title, null);
    }

    private static int decodeArtColor(Context context, Bitmap bitmap) {
        Palette palette = Palette.from(bitmap).generate();
        int defaultColor = ContextCompat.getColor(context, R.color.motionPrimaryDarkColor);
        int color = palette.getDarkVibrantColor(defaultColor);

        if(color == defaultColor)
            return palette.getVibrantColor(defaultColor);
        else return color;
    }

    private static Image decodeArtImage(Context context, String title, Bitmap bitmap) {
        String uri = cacheImageResource(context, bitmap, title);
        int color = decodeArtColor(context, bitmap);

        return new Image(uri, color, IMAGE_SCALING_HEIGHT_SIZE, IMAGE_SCALING_WIDTH_SIZE);
    }

    private static List<Image> getCachedBitmap(Context context, String title) {
        List<Image> arts = new ArrayList<>();

        Cursor cursor = MediaStore.Images.Media.query(context.getContentResolver(),
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new String[] {
                        MediaStore.Images.Media.DATA
                },
                MediaStore.Images.Media.TITLE + " LIKE ?",
                new String[] {
                        title + "%"
                }, null);

        int indexImageColumnData = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);

        while(cursor.moveToNext()) {
            String path = cursor.getString(indexImageColumnData);

            Bitmap bitmap = BitmapFactory.decodeFile(path);
            int color = decodeArtColor(context, bitmap);

            Image image = new Image("file:///" + path, color, IMAGE_SCALING_HEIGHT_SIZE, IMAGE_SCALING_WIDTH_SIZE);

            arts.add(0, image);
        }

        return arts;
    }

    private static void decodeBitmapArt(Context context, MediaMetadataRetriever mediaMetadataRetriever, Album album, String path) {
        List<Image> arts = getCachedBitmap(context, album.getName());

        if(arts.size() > 0) {
            album.setArt(arts);
        } else {
            if(mediaMetadataRetriever == null)
                return;

            byte[] rawArt;
            BitmapFactory.Options options = new BitmapFactory.Options();

            rawArt = mediaMetadataRetriever.getEmbeddedPicture();
            if(rawArt != null) {
                Bitmap bitmap = scaleBitmapResource(BitmapFactory.decodeByteArray(rawArt, 0, rawArt.length, options));

                album.setArt(decodeArtImage(context, album.getName(), bitmap));
            } else {
                // No cover art is embedded, we look at locations relative to parent
                List<Bitmap> bitmaps = decodeSurroundedBitmapArt(path);
                for(int g = 0; g < bitmaps.size(); g++) {
                    Bitmap bitmap = bitmaps.get(g);

                    album.setArt(decodeArtImage(context, album.getName()  + "_" + g, bitmap));
                }
            }
        }
    }

    private static void extractMetaData(Context context, MediaMetadataRetriever mediaMetadataRetriever, Store store, String uri) {
        try {
            mediaMetadataRetriever.setDataSource(uri);

            String albumName = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM);
            String trackNumber = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_CD_TRACK_NUMBER);
            String artistName = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
            String title = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
            String genres = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_GENRE);

            List<String> genreList = new ArrayList<>();
            if(genres != null)
                genreList = Arrays.asList(genres.split(" "));

            if(albumName != null && artistName != null) {
                int number = trackNumber == null ? -1 : Integer.valueOf(trackNumber) - 1;

                Track track = new Track(title, uri, number);
                Album album = store.addItem(track, artistName, albumName, genreList);
                if(album != null) {
                    decodeBitmapArt(context, mediaMetadataRetriever, album, uri);
                }
            }
        } catch(IllegalArgumentException e) {
            Log.v(TAG, uri);
        }
    };

    private static void cacheLocalData(Context context, Store store) {
        if(store != null) {
            try {
                // TODO(21) do something with ContentProviderResult[]
                ContentProviderResult[] results = context.getContentResolver()
                        .applyBatch(ContentContract.AUTHORITY, Store.parseValues(store));

                Store.setValues(store, results);
            } catch (RemoteException e) {
                Log.v(TAG, e.toString());
            } catch (OperationApplicationException e) {
                Log.v(TAG, e.toString());
            }
        }
    }

    public static Store resolveLocalMedia(Context context) {
        MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
        Store store = null;

        Cursor cursor = context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                new String[] {
                    MediaStore.Audio.Media.DATA
                }, null, null, null);

        if(cursor != null) {
            store = new Store();
            while(cursor.moveToNext()) {
                int dataColumnIndex = cursor.getColumnIndex(MediaStore.Audio.Media.DATA);

                String path = cursor.getString(dataColumnIndex);

                extractMetaData(context, mediaMetadataRetriever, store, path);
            }

            cursor.close();
        }

        mediaMetadataRetriever.release();

        cacheLocalData(context, store);

        return store;
    }

    public static class AsyncItemScanner extends AsyncTask<Void, Void, Store> {

        public interface AsyncItemScannerListener {
            void asyncItemScannerListener(Store store);
        }

        private WeakReference<Context> weakReference;
        private AsyncItemScannerListener asyncItemScannerListener;

        public AsyncItemScanner(Context context, AsyncItemScannerListener asyncItemScannerListener) {
            this.weakReference = new WeakReference<>(context);
            this.asyncItemScannerListener = asyncItemScannerListener;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Store doInBackground(Void... voids) {
            Context context = this.weakReference.get();
            if(context != null)
                return resolveLocalMedia(context);
            else return null;
        }

        @Override
        protected void onPostExecute(Store store) {
            this.asyncItemScannerListener.asyncItemScannerListener(store);
        }
    }
}