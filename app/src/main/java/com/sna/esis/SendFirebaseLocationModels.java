package com.sna.esis;

import com.google.firebase.database.PropertyName;

import java.util.HashMap;

public class SendFirebaseLocationModels {

    private String mName;
    private Double mSpeedLimit;
    private HashMap<String, Double> mLocation;
    private String mType;



    @PropertyName("Type")
    public String getmType() {
        return mType;
    }

    @PropertyName("Type")
    public void setmType( String mType ) {
        this.mType = mType;
    }

    public SendFirebaseLocationModels() {

    }

    public SendFirebaseLocationModels( String mName, Double mSpeedLimit, HashMap<String, Double> mLocation,String mType ) {
        this.mName = mName;
        this.mSpeedLimit = mSpeedLimit;
        this.mLocation = mLocation;
        this.mType = mType;
    }

    @PropertyName("Name")
    public String getmName() {
        return mName;
    }

    @PropertyName("Name")
    public void setmName( String mName ) {
        this.mName = mName;
    }

    @PropertyName("SpeedLimit")
    public Double getmSpeedLimit() {
        return mSpeedLimit;
    }

    @PropertyName("SpeedLimit")
    public void setmSpeedLimit( Double mSpeedLimit ) {
        this.mSpeedLimit = mSpeedLimit;
    }

    @PropertyName("Location")
    public HashMap<String, Double> getmLocation() {
        return mLocation;
    }

    @PropertyName("Location")
    public void setmLocation( HashMap<String, Double> mLocation ) {
        this.mLocation = mLocation;
    }

    public static void main( String[] args ) {
        System.out.println("Hello");
    }
}
