package com.lance.library.httpservice;

import java.util.Map;

import io.reactivex.Observable;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import retrofit2.http.Url;

/**
 * Created by Tjcx on 2017/9/15.
 */

public interface HttpService {

    @FormUrlEncoded
    @POST
    Observable<HttpBaseBean> doPost(@Url String url, @FieldMap Map<String, Object> params);

}
