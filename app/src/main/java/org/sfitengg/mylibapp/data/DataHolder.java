package org.sfitengg.mylibapp.data;

import android.os.Bundle;

import org.sfitengg.mylibapp.GoGoGadget;


public class DataHolder {
    // Using a singleton design pattern, since we only need one instance for this class

    // Single instance for DataHolder that will be shared throughout the application
    private static DataHolder dataHolder;

    // Main Page where login Form is present
    // previous page was here:
    // private static String urlMainPage = "http://115.248.171.105:82/webopac/";
    private static String urlMainPage = "http://114.143.70.90:82/webopac/";

    // Complete url to the form action attribute
    // where we send a POST
    private static String urlLoginFormAction = urlMainPage + "opac.asp?m_firsttime=Y&m_memchk_flg=T";

    // Url of docs page
    private static String urlOutDocsPage = urlMainPage + "l_renew.asp";

    // Url where reissue form is sent
    private static String urlOutFormAction = urlMainPage + "l_renew1.asp";

    private final Bundle bundleURLs;

    public Bundle getBundleURLs() {
        return bundleURLs;
    }

    private DataHolder() {

        boolean testing = false;
        if (testing) {
            urlMainPage = "http://192.168.1.59:5000/";
            urlLoginFormAction = urlMainPage + "userpage";
            urlOutDocsPage = urlMainPage + "out_docs";
            urlOutFormAction = urlMainPage + "renew_success";
        }

        // Create a bundle to pass in the URLs to the GoGoGadget object
        bundleURLs = new Bundle();
        bundleURLs.putString(GoGoGadget.keyMainPage, urlMainPage);
        bundleURLs.putString(GoGoGadget.keyLoginForm, urlLoginFormAction);
        bundleURLs.putString(GoGoGadget.keyOutDocs, urlOutDocsPage);
        bundleURLs.putString(GoGoGadget.keyOutForm, urlOutFormAction);
    }

    public static DataHolder getDataHolder(){
        if(dataHolder == null){
            dataHolder = new DataHolder();
        }
        return dataHolder;
    }
}
