package com.sna.esis;

public class LocationSpeedLimitModels {

    private String RootName;
    private String NameLocation;
    private String SpeedLimit;
    private String Latitude;
    private String Longitude;
    private String ParentRooNodeKey;
    private String Type;

    public LocationSpeedLimitModels( String rootName, String nameLocation, String speedLimit, String latitude, String longitude , String type) {
        RootName = rootName;
        NameLocation = nameLocation;
        SpeedLimit = speedLimit;
        Latitude = latitude;
        Longitude = longitude;
        Type = type;
    }

    public LocationSpeedLimitModels( String rootName, String nameLocation, String speedLimit, String latitude, String longitude, String parentRooNodeKey, String type ) {
        RootName = rootName;
        NameLocation = nameLocation;
        SpeedLimit = speedLimit;
        Latitude = latitude;
        Longitude = longitude;
        ParentRooNodeKey = parentRooNodeKey;
        Type = type;
    }

    public String getType() {
        return Type;
    }

    public void setType( String type ) {
        Type = type;
    }

    public String getParentRooNodeKey() {
        return ParentRooNodeKey;
    }

    public void setParentRooNodeKey( String parentRooNodeKey ) {
        ParentRooNodeKey = parentRooNodeKey;
    }

    public String getRootName() {
        return RootName;
    }

    public void setRootName( String rootName ) {
        RootName = rootName;
    }

    public String getNameLocation() {
        return NameLocation;
    }

    public void setNameLocation( String nameLocation ) {
        NameLocation = nameLocation;
    }

    public String getSpeedLimit() {
        return SpeedLimit;
    }

    public void setSpeedLimit( String speedLimit ) {
        SpeedLimit = speedLimit;
    }

    public String getLatitude() {
        return Latitude;
    }

    public void setLatitude( String latitude ) {
        Latitude = latitude;
    }

    public String getLongitude() {
        return Longitude;
    }

    public void setLongitude( String longitude ) {
        Longitude = longitude;
    }


}

