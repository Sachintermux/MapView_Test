package com.sna.esis;

public class LocationSpeedLimitModels {

    private String RootName;
    private String NameLocation;
    private String SpeedLimit;
    private String Latitude;
    private String Longitude;
    private String ParentRooNodeKey;

    public LocationSpeedLimitModels( String rootName, String nameLocation, String speedLimit, String latitude, String longitude ) {
        RootName = rootName;
        NameLocation = nameLocation;
        SpeedLimit = speedLimit;
        Latitude = latitude;
        Longitude = longitude;
    }

    public LocationSpeedLimitModels( String rootName, String nameLocation, String speedLimit, String latitude, String longitude, String parentRooNodeKey ) {
        RootName = rootName;
        NameLocation = nameLocation;
        SpeedLimit = speedLimit;
        Latitude = latitude;
        Longitude = longitude;
        ParentRooNodeKey = parentRooNodeKey;
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
