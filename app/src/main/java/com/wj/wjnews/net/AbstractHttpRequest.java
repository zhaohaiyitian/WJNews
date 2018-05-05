package com.wj.wjnews.net;

import com.wj.wjnews.net.http.HttpHeader;
import com.wj.wjnews.net.http.HttpRequest;
import com.wj.wjnews.net.http.HttpResponse;

import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.ZipOutputStream;

/**
 * Created by wj on 18-5-5.
 */

public abstract class AbstractHttpRequest implements HttpRequest{
    public static final String GZIP="gzip";
    private HttpHeader mHeader=new HttpHeader();
    private ZipOutputStream mZip;
    private boolean executed;

    @Override
    public HttpHeader getHeaders() {
        return mHeader;
    }

    @Override
    public OutputStream getBody() {
        OutputStream body=getBodyOutputStream();
        if (isGzip()) {
            return getGzipOutStream(body);
        }
        return null;
    }

    private OutputStream getGzipOutStream(OutputStream body) {
        if (this.mZip==null) {
            return new ZipOutputStream(body);
        }
        return mZip;
    }

    protected abstract OutputStream getBodyOutputStream();

    private boolean isGzip() {
        String contentEncoding = getHeaders().getContentEncoding();
        if (GZIP.equals(contentEncoding)) {
            return true;
        }
        return false;
    }

    @Override
    public HttpResponse execute() throws IOException {
        if (mZip!=null) {
            mZip.close();
        }
        HttpResponse response=executeInternal(mHeader);
        executed=true;
        return response;
    }

    protected abstract HttpResponse executeInternal(HttpHeader header) throws IOException;
}
