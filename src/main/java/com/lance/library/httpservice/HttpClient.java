package com.lance.library.httpservice;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.LongSerializationPolicy;
import com.google.gson.reflect.TypeToken;
import com.lance.library.custom.Constant;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Tjcx on 2017/9/15.
 */

public class HttpClient {

    private Retrofit mRetrofit;

    public HttpClient() {

        mRetrofit = new Retrofit.Builder().client(createClient()).baseUrl(Constant.BASE_URL)
                .addConverterFactory(initGsonFactory())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
    }

    private OkHttpClient createClient() {
        return new OkHttpClient.Builder().connectTimeout(1500, TimeUnit.MILLISECONDS)
                .addNetworkInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Request.Builder requestBuilder = chain.request().newBuilder();
                        requestBuilder.addHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
                        return chain.proceed(requestBuilder.build());
                    }
                }).build();
    }

    private GsonConverterFactory initGsonFactory() {
        GsonBuilder gb = new GsonBuilder().enableComplexMapKeySerialization()
                .setLongSerializationPolicy(LongSerializationPolicy.DEFAULT);
        gb.registerTypeAdapter(
                new TypeToken<HashMap<String, Object>>() {
                }.getType()
                , new JsonDeserializer<HashMap<String, Object>>() {
                    @Override
                    public HashMap<String, Object> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                        HashMap<String, Object> treeMap = new HashMap<>();
                        JsonObject jsonObject = json.getAsJsonObject();
                        Set<Map.Entry<String, JsonElement>> entrySet = jsonObject.entrySet();
                        for (Map.Entry<String, JsonElement> entry : entrySet) {
                            Object ot = entry.getValue();
                            if (ot instanceof JsonPrimitive) {
                                treeMap.put(entry.getKey(), ((JsonPrimitive) ot).getAsString());
                            } else {
                                treeMap.put(entry.getKey(), ot);
                            }
                        }
                        return treeMap;
                    }
                });
        return GsonConverterFactory.create(gb.create());
    }

    private Consumer<HttpBaseBean> callbackFirtStep = new Consumer<HttpBaseBean>() {
        @Override
        public void accept(HttpBaseBean httpBaseBean) throws Exception {
            Log.e("callbackFirtStep", "-----------------" + httpBaseBean.getErrorCode()
                    + "-----------" + httpBaseBean.getMobileToken());

        }
    };

    public Observable doPost(String url, @NonNull Map<String, Object> params, Callback callback) {
        Log.e("doPost------", url + "--------doPost");
        HttpService service = getInstance().mRetrofit.create(HttpService.class);
        Observable<HttpBaseBean> observable = service.doPost(url, params);
        attach(observable, callback);
        return observable;
    }

    private void attach(final Observable<HttpBaseBean> observable, final Callback callback) {
        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(callbackFirtStep)
                .subscribe(new Observer<HttpBaseBean>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        Log.e("onSubscribe--------", "------------");
                        if (callback != null)
                            callback.onStart(d);
                    }

                    @Override
                    public void onNext(@NonNull HttpBaseBean httpBaseBean) {
                        Log.e("onNext--------", "------------" + httpBaseBean.toString());
                        if (callback != null && httpBaseBean.isFirstStepSuccess()) {
                            callback.onSuccess(httpBaseBean);
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Log.e("onError--------", "------------");
                        if (callback != null) {
                            callback.onError(e);
                        }
                    }

                    @Override
                    public void onComplete() {
                        Log.e("onComplete--------", "------------");
                        if (callback != null) {
                            callback.onComplete();
                        }
                    }
                });
    }

    public static HttpClient getInstance() {
        return SingleHolder.mSingleton;
    }

    private static class SingleHolder {
        private static final HttpClient mSingleton = new HttpClient();
    }

    public interface Callback {

        void onStart(Disposable disposable);

        void onSuccess(HttpBaseBean result);

        void onError(Throwable throwable);

        void onComplete();
    }

}
