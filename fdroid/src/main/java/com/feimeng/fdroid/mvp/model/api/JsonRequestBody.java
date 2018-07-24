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
    private static JsonRequestBody sInstance;
    private ThreadLocal<Map<String, Object>> mThreadLocal;
    private MediaType mJsonType;

    private JsonRequestBody() {
        mThreadLocal = new ThreadLocal<>();
        mJsonType = MediaType.parse("application/json;charset=UTF-8");
    }

    public static JsonRequestBody getInstance() {
        if (sInstance == null) {
            synchronized (JsonRequestBody.class) {
                if (sInstance == null) {
                    sInstance = new JsonRequestBody();
                }
            }
        }
        return sInstance;
    }

    public Map<String, Object> getMap() {
        Map<String, Object> map = mThreadLocal.get();
        if (map == null) {
            map = new HashMap<>();
            mThreadLocal.set(map);
        } else {
            map.clear();
        }
        return map;
    }

    public MediaType getJsonType() {
        return mJsonType;
    }

    public RequestBody build(Gson gson) {
        return RequestBody.create(mJsonType, gson.toJson(mThreadLocal.get()));
    }
}
