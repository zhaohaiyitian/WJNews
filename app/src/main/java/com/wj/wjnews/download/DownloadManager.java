package com.wj.wjnews.download;

import android.support.annotation.NonNull;

import java.io.IOException;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by wj on 18-4-30.
 */

public class DownloadManager {
    private static DownloadManager instance=new DownloadManager();
    private static final int MAX_THREAD=2;
    private static final ThreadPoolExecutor sThreadPool=new ThreadPoolExecutor(
            MAX_THREAD, MAX_THREAD, 60, TimeUnit.SECONDS, new LinkedBlockingDeque<Runnable>(),
            new ThreadFactory() {
                @Override
                public Thread newThread(@NonNull Runnable r) {
                    AtomicInteger integer=new AtomicInteger(1);
                    Thread thread=new Thread(r,"dowload thread# "+integer.getAndIncrement());
                    return thread;
                }
            });
    private DownloadManager() {
    }
    public static DownloadManager getInstance() {
        return instance;
    }

    public void download(final String url, final DownloadCallBack callBack) {
        HttpManager.getInstance().asyncRequest(url, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()&&callBack!=null) {
                    callBack.fail(HttpManager.NETWPRK_CODE,"网络出问题了");
                    return;
                }
                long length = response.body().contentLength();
                if (length==-1) {
                    callBack.fail(HttpManager.HTTP_LENGTH_ERROR_CODE,"content length -1");
                    return;
                }
                processDownload(url,length,callBack);

            }
        });


    }

    private void processDownload(String url,long length,DownloadCallBack callback) {
        long threadDownloadSize=length/MAX_THREAD;
        for (int i = 0; i < MAX_THREAD; i++) {
            long startSize=i*threadDownloadSize;
            long end=(i+1)*threadDownloadSize-1;
            sThreadPool.execute(new DownloadRunnable(startSize,end,url,callback));
        }

    }


}
