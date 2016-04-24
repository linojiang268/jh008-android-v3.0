package com.gather.android.http;

import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.internal.Util;

import java.io.File;
import java.io.IOException;

import okio.Buffer;
import okio.BufferedSink;
import okio.Okio;
import okio.Source;

/**
 * Created by Levi on 2015/8/20.
 */
public class UploadProgressRequestBody extends RequestBody {
    private MediaType contentType;
    private File file;
    private UploadProgressListener listener;
    public UploadProgressRequestBody( MediaType contentType,  File file, UploadProgressListener listener){
        this.contentType = contentType;
        this.file = file;
        this.listener = listener;
    }

    @Override
    public MediaType contentType() {
        return contentType;
    }

    public long contentLength() {
        return file.length();
    }

    public void writeTo(BufferedSink sink) throws IOException {
        Source source = null;
        int progress = 0;
        try {
            source = Okio.source(file);

//            sink.writeAll(source);
            Buffer buf = new Buffer();
            long total = contentLength();
            long upload = 0l;
            for (long readCount; (readCount = source.read(buf, 2048)) != -1; ) {
                sink.write(buf, readCount);
                upload += readCount;
                int curProgress = (int) (upload * 100 / total);
                if (curProgress > (progress + 10) || curProgress >= 100){
                    progress = curProgress;
                    listener.onUpload(curProgress);
                }
            }
        } finally {

            Util.closeQuietly(source);
        }

    }

    public interface UploadProgressListener {
        public void onUpload(int progress);
    }
}
