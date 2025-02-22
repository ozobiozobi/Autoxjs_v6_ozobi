package com.stardust.autojs.core.http;

import android.util.Log;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.SocketTimeoutException;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Stardust on 2018/4/11.
 */

public class MutableOkHttp extends OkHttpClient {
    private final Builder mOkHttpClient;// Modified by Ozobi : 添加: 设置代理
    private int mMaxRetries = 3;
    private long mTimeout = 10 * 1000;// Modified by Ozobi - 2025/01/31 : 3000 -> 1000
    private Interceptor mRetryInterceptor = chain -> {
        Request request = chain.request();
        Response response = null;
        int tryCount = 1;// Modified by Ozobi - 2025/01/31 > 设置 http 最大尝试次数、超时时间 : 0 -> 1
        do {
            if(tryCount > getMaxRetries()){
                Log.d("ozobiLog","次数达到限制");
                throw new SocketTimeoutException();
            }
            boolean succeed;
            try {
                if (response != null) {
                    response.close();
                }
                response = chain.proceed(request);
                succeed = response.isSuccessful();
            } catch (SocketTimeoutException e) {
                succeed = false;
                if (tryCount >= getMaxRetries()) {
                    throw e;
                }
            }
            if (succeed) {
                return response;
            }
            tryCount++;
        } while (true);
    };

    public MutableOkHttp() {
        mOkHttpClient = new OkHttpClient.Builder();
    }

    public Builder client() {
        for (Interceptor interceptor : getInterceptors()) {
            mOkHttpClient.addInterceptor(interceptor);
        }
        // <
        return mOkHttpClient;
    }
//    protected Builder newClient(Builder builder) {
//        builder.readTimeout(getTimeout(), TimeUnit.MILLISECONDS)
//                .writeTimeout(getTimeout(), TimeUnit.MILLISECONDS)
//                .connectTimeout(getTimeout(), TimeUnit.MILLISECONDS);
//        for (Interceptor interceptor : getInterceptors()) {
//            builder.addInterceptor(interceptor);
//        }
//        return builder;
//    }

    public Iterable<? extends Interceptor> getInterceptors() {
        return Collections.singletonList(mRetryInterceptor);
    }

    public int getMaxRetries() {
        return mMaxRetries;
    }

    public void setMaxRetries(int maxRetries) {
        mMaxRetries = maxRetries;
    }

    public long getTimeout() {
        return mTimeout;
    }

    public void setTimeout(long timeout) {
        mTimeout = timeout;
        // Annotated by Ozobi - 2025/01/31 > 设置 http 最大尝试次数、超时时间
        // muteClient();
        // Added by Ozobi > 设置 http 最大尝试次数、超时时间
        mOkHttpClient.readTimeout(getTimeout(), TimeUnit.MILLISECONDS)
                .writeTimeout(getTimeout(), TimeUnit.MILLISECONDS)
                .connectTimeout(getTimeout(), TimeUnit.MILLISECONDS);
    }

    // Added by Ozobi - 2025/02/01 > 添加: 设置代理
    public void setProxy(String host, int port){
        mOkHttpClient.proxy(new Proxy(Proxy.Type.HTTP, new InetSocketAddress(host,port)));
    }
    // <

//    public synchronized void muteClient(Builder builder) {
//        mOkHttpClient = newClient(builder);
//    }

//    protected synchronized void muteClient() {
//        mOkHttpClient = newClient(mOkHttpClient.newBuilder());
//    }
}
