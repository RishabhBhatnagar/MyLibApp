package com.example.vinay.mylibapp;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by vinay on 14-06-2018.
 */

public class Book implements Parcelable {

    // Implement Parcelable
    // so that we can pass Book into a Bundle

    // All variables are kept as string
    // They can be parsed later in the calling code
    private String acc_no;
    private String title;
    private String dueDate;
    private String fineAmount;
    private String renewCount;
    private String reservations;
    private boolean canRenew;

    // Parcellable overrides
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int i) {
        dest.writeString(acc_no);
        dest.writeString(title);
        dest.writeString(dueDate);
        dest.writeString(fineAmount);
        dest.writeString(renewCount);
        dest.writeString(reservations);

        // Refer:
        // https://stackoverflow.com/questions/6201311/how-to-read-write-a-boolean-when-implementing-the-parcelable-interface
        dest.writeByte((byte) (canRenew ? 1 : 0));
    }

    public static final Creator CREATOR = new Creator() {
        public Book createFromParcel(Parcel in) {
            return new Book(in);
        }

        public Book[] newArray(int size) {
            return new Book[size];
        }
    };

    private Book(Parcel in) {
        acc_no = in.readString();
        title = in.readString();
        dueDate = in.readString();
        fineAmount = in.readString();
        renewCount = in.readString();
        reservations = in.readString();

        // Refer:
        // https://stackoverflow.com/questions/6201311/how-to-read-write-a-boolean-when-implementing-the-parcelable-interface
        canRenew = in.readByte() != 0;

    }

    public Book() {
    }

    public void setAcc_no(String acc_no) {
        this.acc_no = acc_no;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    public void setFineAmount(String fineAmount) {
        this.fineAmount = fineAmount;
    }

    public void setRenewCount(String renewCount) {
        this.renewCount = renewCount;
    }

    public void setReservations(String reservations) {
        this.reservations = reservations;
    }

    public void setCanRenew(boolean canRenew) {
        this.canRenew = canRenew;
    }

    @Override
    public String toString() {
        return "Book{" +
                "acc_no='" + acc_no + '\'' +
                ", title='" + title + '\'' +
                ", dueDate='" + dueDate + '\'' +
                ", fineAmount='" + fineAmount + '\'' +
                ", renewCount='" + renewCount + '\'' +
                ", reservations='" + reservations + '\'' +
                ", canRenew=" + canRenew +
                '}';
    }

    public String getAcc_no() {
        return acc_no;
    }

    public String getTitle() {
        return title;
    }

    public String getDueDate() {
        return dueDate;
    }

    public String getFineAmount() {
        return fineAmount;
    }

    public String getRenewCount() {
        return renewCount;
    }

    public String getReservations() {
        return reservations;
    }

    public boolean isCanRenew() {
        return canRenew;
    }

}
