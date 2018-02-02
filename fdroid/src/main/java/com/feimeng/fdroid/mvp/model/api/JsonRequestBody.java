package com.feimeng.fdroid.mvp.model.api;

import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.RequestBody;

/**
 * Created by feimeng on 2018/2/2.
 */
public class JsonRequestBody {
    private static JsonRequestBody mInstance;
    private ThreadLocal<Map<String, Object>> mThreadLocal;
    private MediaType mJsonType;
    private Gson mGson;

    private JsonRequestBody() {
        mThreadLocal = new ThreadLocal<>();
        mThreadLocal.set(new HashMap<String, Object>());
        mJsonType = MediaType.parse("application/json;charset=UTF-8");
        mGson = new Gson();
    }

    public static JsonRequestBody getInstance() {
        if (mInstance == null) {
            synchronized (JsonRequestBody.class) {
                if (mInstance == null) {
                    mInstance = new JsonRequestBody();
                }
            }
        }
        return mInstance;
    }

    public JsonRequestBody put(String key, Object value) {
        Map<String, Object> map = mThreadLocal.get();
        map.put(key, value);
        return this;
    }

    public RequestBody build() {
        return RequestBody.create(mJsonType, mGson.toJson(mThreadLocal.get()));
    }
}
