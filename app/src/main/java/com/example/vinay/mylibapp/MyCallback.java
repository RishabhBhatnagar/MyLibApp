package com.example.vinay.mylibapp;

import java.util.List;

/**
 * Created by vinay on 14-06-2018.
 */

public interface MyCallback {

    void sendBooksToCaller(List<Book> books);
    void sendStudentNameToCaller(String name);
    void passErrorsToCaller(int errorCode);
    String getPid();
    String getPwd();
    boolean isConnectedToInternet();

}
