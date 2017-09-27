package com.lance.library.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;

import com.lance.library.httpservice.HttpBaseBean;
import com.lance.library.httpservice.HttpClient;

import java.util.ArrayDeque;
import java.util.Map;
import java.util.Queue;

import io.reactivex.disposables.Disposable;

/**
 * Created by Tjcx on 2017/8/29.
 */

public class BasicActivity extends FragmentActivity {

    private Queue<Disposable> obQueue = new ArrayDeque<>(0);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    protected void doPost(String url, Map<String, Object> params, DecorateCallback callback) {
        HttpClient.getInstance().doPost(url, params, callback);
    }

    @Override
    protected void onDestroy() {
        Disposable disposable = obQueue.poll();
        while (disposable != null) {
            if (!disposable.isDisposed())
                disposable.dispose();
            disposable = obQueue.poll();
        }
        setContentView(new View(this));
        super.onDestroy();
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        Log.d(getClass().getSimpleName(), "------finalize------");
    }

    protected abstract class DecorateCallback implements HttpClient.Callback {

        protected abstract void onRequestStar();

        protected abstract void onReqestSuccess(HttpBaseBean result);

        protected abstract void onRequestComplete();

        protected abstract void onReqestError(Throwable throwable);

        @Override
        public void onStart(Disposable disposable) {
            obQueue.add(disposable);
            onRequestStar();
        }

        @Override
        public void onSuccess(final HttpBaseBean result) {

            Log.d("DecorateCallback", getClass().getSimpleName() + "--onSuccess");
            onReqestSuccess(result);
        }

        @Override
        public void onError(Throwable throwable) {
            Log.e(BasicActivity.this.getClass().getSimpleName(), throwable.getMessage());
            onReqestError(throwable);
        }

        @Override
        public void onComplete() {
            onRequestComplete();
        }
    }
}
