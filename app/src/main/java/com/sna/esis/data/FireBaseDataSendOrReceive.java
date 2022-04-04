package com.sna.esis.data;

import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sna.esis.LocationSpeedLimitModels;
import com.sna.esis.view_models.MainActivity_ViewModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class FireBaseDataSendOrReceive {
    private Context context;
    private MainActivity_ViewModel viewModel;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference myRef;
    private String path;

    public FireBaseDataSendOrReceive( Context context, MainActivity_ViewModel viewModel ) {
        this.context = context;
        this.viewModel = viewModel;
    }

    private void sendData() {

    }

    public void getRootNamesData( String path ) {
        this.path = path;
        myRef = database.getReference(path);
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange( @NonNull DataSnapshot snapshot ) {
                try {
                    HashMap<Integer, String> hashMap = new HashMap<>();
                    for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                        int rootNodeKey = Integer.parseInt(snapshot1.getKey().toString());
                        String rootNodeName = Objects.requireNonNull(snapshot1.child("Name").getValue()).toString();
                        hashMap.put(rootNodeKey, rootNodeName);
                    }
                    viewModel.setParentNode_hashMap(hashMap);
                } catch (NullPointerException e) {
                    System.out.println(e);
                    Toast.makeText(context, "e   =" + e, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled( @NonNull DatabaseError error ) {

            }
        });

    }

    public void getLowerNodeData( int ParentNodeKey, String ParentNodeName ) {
        System.out.println("Key  = " + ParentNodeKey + "    Name = " + ParentNodeName);
        DatabaseReference myRef1 = database.getReference(path).child(String.valueOf(ParentNodeKey));
        myRef1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange( @NonNull DataSnapshot snapshot ) {
                ArrayList<LocationSpeedLimitModels> limitModelsList = new ArrayList<>();
                try{
                    for(DataSnapshot snapshot1 : snapshot.getChildren()){
                        if(snapshot1.child("Name").getValue() != null && snapshot1.child("SpeedLimit").getValue() != null){
                            String Name = snapshot1.child("Name").getValue().toString();
                            String SpeedLimit= snapshot1.child("SpeedLimit").getValue().toString();
                            String latitude = snapshot1.child("Location").child("Latitude").getValue().toString();
                            String longitude = snapshot1.child("Location").child("Longitude").getValue().toString();
                            limitModelsList.add(new LocationSpeedLimitModels(ParentNodeName,Name,SpeedLimit,latitude,longitude,ParentNodeName));
                        }
                    }
                    viewModel.setLocationList(limitModelsList);
                }
                catch (NullPointerException exception){

                }
            }

            @Override
            public void onCancelled( @NonNull DatabaseError error ) {

            }
        });

    }

}
