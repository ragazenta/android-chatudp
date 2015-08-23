package io.github.ragazenta.chatudp.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by renjaya on 8/23/15.
 */
public class Chat implements Parcelable {

    private String message;
    private String sender;
    private String timestamp;

    public Chat() {
    }

    public Chat(Parcel source) {
        this.message = source.readString();
        this.sender = source.readString();
        this.timestamp = source.readString();
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(message);
        dest.writeString(sender);
        dest.writeString(timestamp);
    }

    public static final Creator<Chat> CREATOR = new Creator<Chat>() {
        @Override
        public Chat createFromParcel(Parcel source) {
            return new Chat(source);
        }

        @Override
        public Chat[] newArray(int size) {
            return new Chat[size];
        }
    };
}
