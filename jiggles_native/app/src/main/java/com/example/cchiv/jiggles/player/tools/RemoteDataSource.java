package com.example.cchiv.jiggles.player.tools;

import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DataSpec;
import com.google.android.exoplayer2.upstream.TransferListener;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PipedInputStream;

public class RemoteDataSource implements DataSource {

    private static final String TAG = "RemoteDataSource";

    private TransferListener<? super RemoteDataSource> mTransferListener;
    private ByteArrayOutputStream mByteArrayOutputStream;
    private PipedInputStream mInputStream;
    private byte[] mCachedStream;
    private Uri mUri;
    private boolean mDataTransfer = false;
    private long mBytesRemaining = -1;

    private long playerPos = 0;

    public RemoteDataSource(TransferListener<? super RemoteDataSource> transferListener, PipedInputStream inputStream) {
        mByteArrayOutputStream = new ByteArrayOutputStream();
        mTransferListener = transferListener;
        mInputStream = inputStream;
    }

    @Override
    public long open(DataSpec dataSpec) {
        mUri = dataSpec.uri;
        playerPos = dataSpec.position;

        if(mTransferListener != null) {
            mTransferListener.onTransferStart(this, dataSpec);
        }

        return C.LENGTH_UNSET;
    }

    private void computeBytesRemaining() throws IOException {
        mBytesRemaining = mInputStream.available();
        if(mBytesRemaining == 0) {
            mBytesRemaining = C.LENGTH_UNSET;
        }
    }

    private int getBytesToRead(int readLength) {
        if(mBytesRemaining == C.LENGTH_UNSET) {
            return readLength;
        }

        return (int) Math.min(mBytesRemaining, readLength);
    }

    @Override
    public int read(byte[] buffer, int offset, int readLength) throws IOException {
        computeBytesRemaining();

        if(readLength == 0) {
            return 0;
        } else if(mBytesRemaining == 0) {
            return C.RESULT_END_OF_INPUT;
        }

        int bytesRead = -1, bytesToRead = getBytesToRead(readLength);
        try {
            if(!mDataTransfer)
                bytesRead = mInputStream.read(buffer, offset, bytesToRead);
            else {
                if(playerPos != mCachedStream.length) {
                    bytesToRead = Math.min(bytesToRead, mCachedStream.length - (int) playerPos);

                    System.arraycopy(mCachedStream, (int) playerPos, buffer, offset, bytesToRead);

                    playerPos += bytesToRead;
                    bytesRead = bytesToRead;
                }
            }

            if(!mDataTransfer && bytesRead != -1) {
                mByteArrayOutputStream.write(buffer, offset, bytesRead);
            }
        } catch(IOException e) {
            Log.v(TAG, e.toString());
        }

        if(bytesRead == -1) {
            if(!mDataTransfer) {
                mInputStream.close();
                mDataTransfer = true;

                setCachedStream(mByteArrayOutputStream);
            }

            return C.RESULT_END_OF_INPUT;
        }

        if(mTransferListener != null) {
            mTransferListener.onBytesTransferred(this, bytesRead);
        }

        return bytesRead;
    }

    private void setCachedStream(ByteArrayOutputStream byteArrayOutputStream) {
        mCachedStream = byteArrayOutputStream.toByteArray();
    }

    @Nullable
    @Override
    public Uri getUri() {
        return mUri;
    }

    @Override
    public void close() {
        try {
            if(mInputStream != null) {
                mInputStream.close();
            }
        } catch(IOException e) {
            Log.v(TAG, e.toString());
        } finally {
            if(mTransferListener != null) {
                mTransferListener.onTransferEnd(this);
            }
        }
    }
}