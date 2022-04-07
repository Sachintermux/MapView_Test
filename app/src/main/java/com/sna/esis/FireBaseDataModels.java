package com.sna.esis;

import com.google.firebase.database.PropertyName;

import java.io.Serializable;
import java.util.HashMap;

public class FireBaseDataModels{
    public String parentRootNodeName;
    private int parentRootNodeKey;
    private int childRootNodeLength;

    public FireBaseDataModels( String parentRootNodeName, int parentRootNodeKey, int childRootNodeLength ) {
        this.parentRootNodeName = parentRootNodeName;
        this.parentRootNodeKey = parentRootNodeKey;
        this.childRootNodeLength = childRootNodeLength;
    }


    public FireBaseDataModels() {

    }

    public String getParentRootNodeName() {
        return parentRootNodeName;
    }

    public void setParentRootNodeName( String parentRootNodeName ) {
        this.parentRootNodeName = parentRootNodeName;
    }

    public int getParentRootNodeKey() {
        return parentRootNodeKey;
    }

    public void setParentRootNodeKey( int parentRootNodeKey ) {
        this.parentRootNodeKey = parentRootNodeKey;
    }


    public int getChildRootNodeLength() {
        return childRootNodeLength;
    }

    public void setChildRootNodeLength( int childRootNodeLength ) {
        this.childRootNodeLength = childRootNodeLength;
    }

}
