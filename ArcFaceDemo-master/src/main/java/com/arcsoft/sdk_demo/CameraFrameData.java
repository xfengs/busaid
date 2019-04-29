package com.arcsoft.sdk_demo;

/**
 * Created by gqj3375 on 2017/7/14.
 */

public class CameraFrameData {
    //image data.
    public byte[] mData;
    public int mWidth, mHeight, mFormat;

    //user data
    public Object mParams;
    //timestamp
    public long mTimeStamp;

    public CameraFrameData(int w, int h, int f, int size) {
        mWidth = w;
        mHeight = h;
        mFormat = f;
        mData = new byte[size];
        mParams = null;
        mTimeStamp = System.nanoTime();
    }

    public byte[] getData() {
        return mData;
    }

    public void setData(byte[] mData) {
        this.mData = mData;
    }

    public Object getParams() {
        return mParams;
    }

    public void setParams(Object mParams) {
        this.mParams = mParams;
    }
}
