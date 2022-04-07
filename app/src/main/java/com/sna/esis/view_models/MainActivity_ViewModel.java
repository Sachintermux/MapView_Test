package com.sna.esis.view_models;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.sna.esis.FireBaseDataModels;
import com.sna.esis.LocationSpeedLimitModels;

import java.util.ArrayList;

public class MainActivity_ViewModel extends ViewModel {

    public MutableLiveData<ArrayList<FireBaseDataModels>> firebaseDataModels = new MutableLiveData<>(new ArrayList<>());
    public MutableLiveData<ArrayList<LocationSpeedLimitModels>> locationList = new MutableLiveData<>(new ArrayList<>());

    public void setFirebaseDataModels( ArrayList<FireBaseDataModels> models ) {
        firebaseDataModels.setValue(models);
    }

    public void setLocationList( ArrayList<LocationSpeedLimitModels> locationList ) {
        this.locationList.setValue(locationList);
    }

}
