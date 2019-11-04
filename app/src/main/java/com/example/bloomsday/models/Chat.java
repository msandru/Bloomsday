package com.example.bloomsday.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class Chat implements Parcelable {

    private String receiver;
    private String sender;
    private String message;
    private boolean isseen;
    private @ServerTimestamp
    Date timestamp;

    public Chat(String sender, String receiver, String message, boolean isseen, Date timestamp) {
        this.sender = sender;
        this.receiver = receiver;
        this.message = message;
        this.isseen = isseen;
        this.timestamp = timestamp;
    }

    public Chat(String sender, String receiver, String message) {
        this.sender = sender;
        this.receiver = receiver;
        this.message = message;
    }

    public Chat() {
    }

    protected Chat(Parcel in) {
        receiver = in.readString();
        sender = in.readString();
        message = in.readString();
        isseen = in.readByte() != 0;
    }

    public static final Creator<Chat> CREATOR = new Creator<Chat>() {
        @Override
        public Chat createFromParcel(Parcel in) {
            return new Chat( in );
        }

        @Override
        public Chat[] newArray(int size) {
            return new Chat[size];
        }
    };
    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isIsseen() {
        return isseen;
    }

    public void setIsseen(boolean isseen) {
        this.isseen = isseen;
    }

    @Override
    public int describeContents() {
        return 0;
    }
    @Override
    public String toString() {
        return "UserLocation{" +
                "receiver=" + receiver +
                "sender=" + sender +
                ", timestamp=" + timestamp +
                '}';
    }
    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString( receiver );
        parcel.writeString( sender );
        parcel.writeString( message );
        parcel.writeByte( (byte) (isseen ? 1 : 0) );
    }
}


