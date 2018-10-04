package com.example.cchiv.jiggles.utilities;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.provider.MediaStore;
import android.util.Log;

import com.example.cchiv.jiggles.model.Album;
import com.example.cchiv.jiggles.model.Collection;
import com.example.cchiv.jiggles.model.Track;

import java.io.File;
import java.io.FileDescriptor;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ItemScanner {

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
                bitmaps.add(0, BitmapFactory.decodeFile(child.getAbsolutePath()));
            }
        }

        return bitmaps;
    }

    private static void decodeBitmapArt(MediaMetadataRetriever mediaMetadataRetriever, Album album, String path) {
        if(mediaMetadataRetriever == null)
            return;

        byte[] rawArt;
        BitmapFactory.Options options = new BitmapFactory.Options();

        rawArt = mediaMetadataRetriever.getEmbeddedPicture();

        // If rawArt is null then no cover art is embedded
        if(rawArt != null)
            album.setArt(BitmapFactory.decodeByteArray(rawArt, 0, rawArt.length, options));
        else album.setArt(decodeSurroundedBitmapArt(path));
    }

    private static void extractMetaData(MediaMetadataRetriever mediaMetadataRetriever, Collection collection, String path) {
        try {
            mediaMetadataRetriever.setDataSource(path);

            String albumName = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM);
            String artistName = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
            String title = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
            String genres = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_GENRE);
            List<String> genreList = new ArrayList<>();
            if(genres != null)
                genreList = Arrays.asList(genres.split(" "));

            if(albumName != null && artistName != null) {
                Track track = new Track(title, path);

                Album album = collection.addItem(track, artistName, albumName, genreList);
                if(album != null)
                    decodeBitmapArt(mediaMetadataRetriever, album, path);
            }
        } catch(IllegalArgumentException e) {
            Log.v(TAG, path);
        }
    };

    public static Collection media(Context context) {
        MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();

        Cursor cursor = context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                new String[] {
                    MediaStore.Audio.Media.DATA
                }, null, null, null);

        Collection collection = null;
        if(cursor != null) {
            collection = new Collection();
            while(cursor.moveToNext()) {
                int dataColumnIndex = cursor.getColumnIndex(MediaStore.Audio.Media.DATA);

                String path = cursor.getString(dataColumnIndex);

                extractMetaData(mediaMetadataRetriever, collection, path);
            }

            cursor.close();
        }

        mediaMetadataRetriever.release();

        return collection;
    }

    public static Track getTrack(Context context, int albumIndex, int trackIndex) {
        Collection collection = media(context);
        Album album = collection.getAlbums().get(albumIndex);

        return album.getTracks().get(trackIndex);
    }

    public static Collection scan(Context context) {
        AssetManager assetManager = context.getAssets();

        try {
            String[] files = assetManager.list("samples");
            Collection collection = new Collection();

            MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
            for(String file : files) {
                String path = "samples/" + file;

                AssetFileDescriptor assetFileDescriptor = assetManager.openFd(path);
                FileDescriptor fileDescriptor = assetFileDescriptor.getFileDescriptor();

                mediaMetadataRetriever.setDataSource(fileDescriptor, assetFileDescriptor.getStartOffset(), assetFileDescriptor.getLength());

                String albumName = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM);
                String artistName = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
                String title = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
                String genres = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_GENRE);
                List<String> genreList = new ArrayList<>();
                if(genres != null)
                    genreList = Arrays.asList(genres.split(" "));

                Track track = new Track(title, path);
//                track.setArt(decodeBitmapArt());

                collection.addItem(track, artistName, albumName, genreList);

                assetFileDescriptor.close();
            }

            mediaMetadataRetriever.release();

            return collection;
        } catch(IOException e) {
            Log.v(TAG, e.toString());
        }

        return null;
    }

    public void check(String tag) {
        if(tag != null)
            Log.v(TAG, tag);
    }
}
