package org.sfitengg.library.mylibapp.data;

import android.os.Parcel;
import android.os.Parcelable;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;

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

    // <input> for reissuing
    private Element inp_accno;
    private Element inp_media;
    private Element inp_chk;

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

        // Write outerHtml of input tags
        dest.writeString(inp_accno.outerHtml());
        dest.writeString(inp_media.outerHtml());
        dest.writeString(inp_chk.outerHtml());
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

        // Read the input tags
        inp_accno = Jsoup.parse(in.readString()).select("input").first();
        inp_media = Jsoup.parse(in.readString()).select("input").first();
        inp_chk = Jsoup.parse(in.readString()).select("input").first();


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

    public void setInp_accno(Element inp_accno) {
        this.inp_accno = inp_accno;
    }

    public void setInp_media(Element inp_media) {
        this.inp_media = inp_media;
    }

    public void setInp_chk(Element inp_chk) {
        this.inp_chk = inp_chk;
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

    public Element getInp_accno() {
        return inp_accno;
    }

    public Element getInp_media() {
        return inp_media;
    }

    public Element getInp_chk() {
        return inp_chk;
    }

    public boolean isInpNull() {
        return inp_accno == null
                || inp_media == null
                || inp_chk == null;
    }
}
