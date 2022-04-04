package com.sna.esis.view_models;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import com.sna.esis.LocationSpeedLimitModels;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity_ViewModel extends ViewModel {

    public MutableLiveData<HashMap<Integer, String>> parentNode_hashMap = new MutableLiveData<>(new HashMap<>());
    public MutableLiveData<ArrayList<LocationSpeedLimitModels>> locationList = new MutableLiveData<>(new ArrayList<>());

    public void setParentNode_hashMap(HashMap<Integer , String> hashMap){
        parentNode_hashMap.setValue(hashMap);
    }

    public void setLocationList(ArrayList<LocationSpeedLimitModels> locationList){
        this.locationList.setValue(locationList);
    }

}
