package com.example.dutmed;

public interface ResponseCallBack {
    void onResponse(String response);

    void onError(Throwable throwable);

}
