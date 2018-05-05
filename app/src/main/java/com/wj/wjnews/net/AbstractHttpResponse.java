package com.wj.wjnews.net;

import com.wj.wjnews.net.http.HttpResponse;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;

/**
 * Created by wj on 18-5-5.
 */

public abstract class AbstractHttpResponse implements HttpResponse {
    private static final String GZIP="gzip";

    InputStream mGzipInputStream;
    @Override
    public void close() {
        if (mGzipInputStream!=null) {
            try {
                mGzipInputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        closeInternal();
    }

    @Override
    public InputStream getBody() throws IOException {
        InputStream body=getBodyInternal();
        if (isGzip()) {
            return getBodyGzip(body);
        }
        return body;//////
    }

    protected abstract InputStream getBodyInternal();
    protected abstract void closeInternal();
    InputStream getBodyGzip(InputStream body)throws IOException {
        if (this.mGzipInputStream==null) {
            this.mGzipInputStream=new GZIPInputStream(body);
        }
        return mGzipInputStream;
    }


    private boolean isGzip() {
        String contentEncoding = getHeaders().getContentEncoding();
        if (GZIP.equals(contentEncoding)) {
            return true;
        }
        return false;
    }
}
