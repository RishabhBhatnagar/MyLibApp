package org.sfitengg.library.mylibapp;

import org.sfitengg.library.mylibapp.data.Book;

import java.util.List;


public interface MyCallback {

    void sendBooksToCaller(List<Book> books);
    void sendStudentNameToCaller(String name);
    void passErrorsToCaller(int errorCode);
    void postToOutDocsSuccess();
    String getPid();
    String getPwd();
    boolean isConnectedToInternet();

}
