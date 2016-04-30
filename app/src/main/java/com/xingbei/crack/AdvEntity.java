package com.xingbei.crack;

import android.os.Parcel;
import android.os.Parcelable;

//闸门信息
public class AdvEntity implements Parcelable {
    public static final Creator<AdvEntity> CREATOR = new Creator<AdvEntity>() {
        @Override
        public AdvEntity createFromParcel(Parcel source) {
            return new AdvEntity(source);
        }

        @Override
        public AdvEntity[] newArray(int size) {
            return new AdvEntity[size];
        }
    };

    private String mac;//蓝牙mac地址
    private String name;//蓝牙名称前两位("LF")
    private int areaid;//carParkId,32位
    private String channel;//闸门名称,最长14位
    private int flag;//0为入口,非0为出口

    protected AdvEntity(Parcel parcel) {
        this.mac = parcel.readString();
        this.name = parcel.readString();
        this.areaid = parcel.readInt();
        this.channel = parcel.readString();
        this.flag = parcel.readInt();
    }

    public AdvEntity(String mac, String name, int areaid, String channel, int flag) {
        this.mac = mac;
        this.name = name;
        this.areaid = areaid;
        this.channel = channel;
        this.flag = flag;
    }

    public final String getMac() {
        return this.mac;
    }

    public final int getAreaid() {
        return this.areaid;
    }

    public final String getName() {
        return this.name;
    }

    public final String getChannel() {
        return this.channel;
    }

    public int describeContents() {
        return 0;
    }

    public final int getFlag() {
        return this.flag;
    }

    public String toString() {
        return "name=" + this.name + ", areaid=" + this.areaid + ", channel=" + this.channel + ", flag=" + this.flag;
    }

    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.mac);
        parcel.writeString(this.name);
        parcel.writeInt(this.areaid);
        parcel.writeString(this.channel);
        parcel.writeInt(this.flag);
    }
}