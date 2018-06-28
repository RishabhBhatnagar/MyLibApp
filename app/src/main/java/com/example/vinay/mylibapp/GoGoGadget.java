package com.example.vinay.mylibapp;

import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;

import com.example.vinay.mylibapp.data.Book;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by vinay on 14-06-2018.
 */
public class GoGoGadget implements Runnable {
    // Thread class to do Network requests

    // References to Calling activity
    private MyCallback myCallback;
    private Handler handler; // Handler object needed to post messages to calling activity

    // Urls to be accessed
    // They are passed in a Bundle to Constructor
    private String gUrlMainPage;
    private String gUrlLoginFormAction;
    private String gUrlOutDocs;
    // Keys to access the Bundle
    public static final String keyMainPage = "main";
    public static final String keyLoginForm = "login";
    public static final String keyOutDocs = "outdocs";


    // Cookies that will be used to log in
    private Map<String, String> cookies = null;

    private int action; // This variable determines what we wish to do with current object
    // Possible parameters for this are:
    public static final int LOGIN_AND_GET_COOKIES = 0;
    public static final int GET_OUT_DOCS = 1;
    public static final int SEND_REISSUE = 2;

    // private variable which should not be set by caller activity
    private static final int NO_INTERNET = -1;
    // should be set in the check internet block
    // and assign ERROR_NO_INTERNET so
    // that final handler section can call the error handler

    // This object will return results to Main
    private Bundle result;
    // Keys for result bundle
    private static final String rKeyName = "name";
    private static final String rKeyListBooks = "listBooks";


    private static final int UNINITIALIZED = 124;
    private int resultCode = UNINITIALIZED;
    // To get result back to activity
    // It has to be initialized with correct values
    // Initialize it in the switch(action){} block
    // Such that after execution of that block
    // resultCode is not UNINITIALIZED
    // If it is still UNINITIALIZED, a RunTimeException will be thrown


    // Valid resultCodes
    // 1) Result was obtained
    public static final int RETURN_NAME = 23;
    public static final int RETURN_LIST_BOOKS = 24;
    public static final int RETURN_NO_BORROWED_BOOKS = 25;
    // 2) Error was obtained
    public static final int ERROR_NOT_LOGGED_IN = 52;
    public static final int ERROR_INCORRECT_PID_OR_PASSWORD = 53;
    public static final int ERROR_NO_INTERNET = 54;
    public static final int ERROR_SERVER_UNREACHABLE = 55;


    private void setErrorServerUnreachable() {
        // Call this method in the catch blocks of each web request
        resultCode = ERROR_SERVER_UNREACHABLE;
    }

    public void setCookies(Map<String, String> cookies) {
        // Use this method to setCookies for all GoGoGadget objects
        // except the first
        // Since the cookies member for the very first successful
        // GoGoGadget object, has to be used for successive logins
        this.cookies = cookies;
    }

    public Map<String, String> getCookies() {
        return this.cookies;
    }

    GoGoGadget(MyCallback myCallback, Bundle bundleURLs, int action, Handler handler) {

        this.myCallback = myCallback;
        // Callback reference is needed
        // To perform GUI actions and interact with activity

        this.gUrlMainPage = bundleURLs.getString(keyMainPage);
        this.gUrlLoginFormAction = bundleURLs.getString(keyLoginForm);
        this.gUrlOutDocs = bundleURLs.getString(keyOutDocs);

        this.action = action;
        this.result = new Bundle();
        this.cookies = null;

        this.handler = handler;
    }

    @Override
    public void run() {

        // Check if we have internet
        if (!myCallback.isConnectedToInternet()) {
            action = NO_INTERNET;
        }

        // Start
        switch (action) {
            case NO_INTERNET:
                // If no internet
                // Assign resultCode value
                resultCode = ERROR_NO_INTERNET;
                break;

            case LOGIN_AND_GET_COOKIES:
                //region LOGIN
                try {
                    Connection.Response loginForm = Jsoup.connect(this.gUrlMainPage)
                            .method(Connection.Method.GET)
                            .timeout(100000)
                            .execute();
                    cookies = loginForm.cookies();

                    Document docMainPage = loginForm.parse();

                    // default input tags whose values we need to mention as they as in the source
                    String submitValue = docMainPage.select("input[type=submit]").attr("value");
                    String resetValue = docMainPage.select("input[type=reset]").attr("value");

                    Connection.Response responseLoginForm = Jsoup.connect(this.gUrlLoginFormAction)
                            .cookies(this.cookies)
                            .data("m_mem_id", myCallback.getPid())
                            .data("m_mem_pwd", myCallback.getPwd())
                            .data("Submit", submitValue)
                            .data("Submit2", resetValue)
                            .method(Connection.Method.POST)
                            .execute();

                    Document docProfilePage = responseLoginForm.parse();

                    // If following tag is present, then we have successfully logged in
                    String name = docProfilePage.select("font[color=purple][face=Arial][size=3]").html();
                    // This tag contains name of student

                    if (name.length() > 0) {
                        // Correct Login
                        this.result.putString(rKeyName, name);
                        this.resultCode = RETURN_NAME;
                    } else {
                        // incorrect login
                        this.resultCode = ERROR_INCORRECT_PID_OR_PASSWORD;
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                    // Set server unreachable error code
                    setErrorServerUnreachable();
                }
                //endregion
                break;
            case GET_OUT_DOCS:
                //region Get Outstanding Documents Page
                if (cookies == null) {
                    // Can't login without the original cookies
                    resultCode = ERROR_NOT_LOGGED_IN;
                } else {
                    //Check outstanding documents
                    try {
                        Connection.Response outR = Jsoup.connect(gUrlOutDocs)
                                .cookies(cookies)
                                .method(Connection.Method.GET)
                                .execute();
                        Document docOutDocs = outR.parse();

                        // Inside these tags, the attributes are present
                        // <td valign="top">
                        // <font face="Arial" color="black" size="2">

                        // We get a list of all elements that satisfy the following params
                        Elements elList = docOutDocs.select("tr td[valign=top]");

                        if(elList.size() >0){
                            //region User has borrowed books
                            // if elList has more than one element
                            // this means that the table which shows the books exists

                            List<Book> bookList = new ArrayList<>();

                            int noOfBooks = elList.size() / 7;
                            // Since there are seven attributes for each book
                            // -1 is not needed since when last row is empty
                            // it doesn't have [valign=top] tag
                            for (int i = 0; i < noOfBooks; i++) {
                                Book book = new Book();
                                book.setAcc_no(       elList.get(i * 7 + 0).text().trim());
                                book.setTitle(        elList.get(i * 7 + 1).text().trim());
                                book.setDueDate(      elList.get(i * 7 + 2).text().trim());
                                book.setFineAmount(   elList.get(i * 7 + 3).text().trim());
                                book.setRenewCount(   elList.get(i * 7 + 4).text().trim());
                                book.setReservations( elList.get(i * 7 + 5).text().trim());

                                // Check if book can be renew or not
                                String canRenew = elList.get(i * 7 + 6).text().trim();
                                if (canRenew.equals("&nbsp;") || canRenew.contains("&nb")) {
                                    // book can't be renewed right now
                                    book.setCanRenew(false);
                                } else {
                                    //if we have input tag
                                    // book can be renewed
                                    book.setCanRenew(true);
                                }

                                bookList.add(book);

                            }// for

                            // Put the list of books in result bundle
                            resultCode = RETURN_LIST_BOOKS;
                            result.putParcelableArrayList(rKeyListBooks, (ArrayList<? extends Parcelable>) bookList);
                            // Casting is needed since Book is Parcelable,
                            // but bookList is ArrayList which is not Parcelable
                            //endregion
                        }//if
                        else {
                            //region User has borrowed no books
                            // if elList doesn't have elements, then that means
                            // user hasn't borrowed any books
                            resultCode = RETURN_NO_BORROWED_BOOKS;
                            //endregion
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                        // Set server unreachable error code
                        setErrorServerUnreachable();
                    }
                }//else
                //endregion
                break;
            default:
                // action variable has to be initialized
                // with the valid constant
                throw new RuntimeException("action should be initialized with correct value\n" +
                        "You are seeing this because you didn't use a correct constant for it\n" +
                        "Check the constructor call where you made this object");
        }//switch

        // end of network operations
        // send message back to activity
        // so that it will handle the rest via the callback
        this.handler.post(new Runnable() {
            // refer to Beginning Android 4 development
            // page 172 / 199
            // for handler.post()
            @Override
            public void run() {
                //progressBar.setVisibility(View.GONE);

                switch (resultCode) {
                    // RESULTS SECTION
                    // Result objects are passed via Callback to calling Activity
                    case RETURN_NAME:
                        myCallback.sendStudentNameToCaller(result.getString(rKeyName));
                        break;
                    case RETURN_LIST_BOOKS:
                        List<Book> books = new ArrayList<>();
                        for (Parcelable p : result.getParcelableArrayList(rKeyListBooks)) {
                            Book b = (Book) p;
                            books.add(b);
                        }
                        myCallback.sendBooksToCaller(books);
                        break;
                    case RETURN_NO_BORROWED_BOOKS:
                        myCallback.sendBooksToCaller(null);
                        break;

                    // ERRORS SECTION
                    case ERROR_NOT_LOGGED_IN:
                    case ERROR_NO_INTERNET:
                    case ERROR_SERVER_UNREACHABLE:
                    case ERROR_INCORRECT_PID_OR_PASSWORD:
                        myCallback.passErrorsToCaller(resultCode);
                        break;

                    case UNINITIALIZED:
                        // In the above switch block for variable 'action'
                        // ie
                        // switch(action){}
                        //
                        // at the end of each block
                        // requestCode should be initialized
                        // if it's not, this exception is thrown
                        throw new RuntimeException("resultCode should not be uninitialized");
                    default:
                        throw new RuntimeException("resultCode was not set to valid constant\n"
                                + "If it was, then the corresponding switch case block wasn't declared.\n"
                                + "Create a case block, define the constant and try again");
                }//switch

                // End of everything
            }// run method of handler Runnable
        });// handler.post()
    }// run method of GoGoGadget
}// class GoGoGadget
