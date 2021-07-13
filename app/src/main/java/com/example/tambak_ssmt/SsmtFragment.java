package com.example.tambak_ssmt;


import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatRadioButton;
import androidx.fragment.app.Fragment;

import android.renderscript.Sampler;
import android.text.BoringLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ncorti.slidetoact.SlideToActView;

import java.util.HashMap;
import java.util.Set;


/**
 * A simple {@link Fragment} subclass.
 */
public class SsmtFragment extends Fragment{

    private static final String TAG = MainActivity.class.getSimpleName();
    private SlideToActView ssmtSlideButton;
    private Button ssmtStopButton;
    private Boolean gateOpen, gateStop, userAuthorization;
    private FirebaseDatabase database;
    private DatabaseReference gateReference, userReference;

    private String uid;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;



    public SsmtFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_ssmt, container, false);

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ssmtSlideButton = view.findViewById(R.id.ssmt_slide_button);
        ssmtStopButton = view.findViewById(R.id.ssmt_stop_button);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        uid = currentUser.getUid();

        database = FirebaseDatabase.getInstance();
        gateReference = database.getReference("GateSsmt");
        userReference = database.getReference("Users");

        // Read gate status from the database
        gateReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                gateOpen = dataSnapshot.child("gateOpen").getValue(Boolean.class);
                gateStop = dataSnapshot.child("gateStop").getValue(Boolean.class);
                System.out.println("gateOpen:         " + gateOpen);

                if(gateOpen == true) {
                    ssmtSlideButton.setOuterColor(Color.parseColor("#FDCC44")); //Orange
                    ssmtSlideButton.setIconColor(Color.parseColor("#FDCC44"));  //Orange
                    ssmtSlideButton.setText("        Geser untuk menutup");
                }
                else if(gateOpen == false) {
                    ssmtSlideButton.setOuterColor(Color.parseColor("#02F7B5")); //Green
                    ssmtSlideButton.setIconColor(Color.parseColor("#02F7B5"));  //Green
                    ssmtSlideButton.setText("        Geser untuk membuka");
                }

                Log.d(TAG, "Value is: " + gateOpen + " " + gateStop);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

        //Read user authorization to open the gate from the database
        userReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                userAuthorization = dataSnapshot.child(uid).child("gateAccess").getValue(Boolean.class);
                System.out.println("userAuthorization:         " + userAuthorization);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });


        //On click on slide button
        ssmtSlideButton.setOnSlideCompleteListener(new SlideToActView.OnSlideCompleteListener() {
            @Override
            public void onSlideComplete(SlideToActView slideToActView) {
                if(gateOpen == true && userAuthorization == true) {
                    //Close gate
                    HashMap<String, Object> hashMap = new HashMap<String, Object>();
                    hashMap.put("gateOpen", false);
                    gateReference.updateChildren(hashMap);

                    ssmtSlideButton.setOuterColor(Color.parseColor("#02F7B5")); //Green
                    ssmtSlideButton.setIconColor(Color.parseColor("#02F7B5"));  //Green
                    ssmtSlideButton.setText("        Geser untuk membuka");
                }
                else if(gateOpen == false && userAuthorization == true)
                {
                    //Open gate
                    HashMap<String, Object> hashMap = new HashMap<String, Object>();
                    hashMap.put("gateOpen", true);
                    gateReference.updateChildren(hashMap);

                    ssmtSlideButton.setOuterColor(Color.parseColor("#FDCC44")); //Orange
                    ssmtSlideButton.setIconColor(Color.parseColor("#FDCC44"));  //Orange
                    ssmtSlideButton.setText("        Geser untuk menutup");
                }
                else if (gateOpen == true && userAuthorization == false)
                {
                    Toast.makeText(SsmtFragment.this.getActivity().getBaseContext(),
                            "Anda tidak memiliki akses ke gerbang ini. Silahkan menghubungi admin",
                            Toast.LENGTH_SHORT).show();
                }
                else if (gateOpen == false && userAuthorization == false)
                {
                    Toast.makeText(SsmtFragment.this.getActivity().getBaseContext(),
                            "Anda tidak memiliki akses ke gerbang ini. Silahkan menghubungi admin",
                            Toast.LENGTH_SHORT).show();
                }
                ssmtSlideButton.resetSlider();
            }
        });

        //On click on stop button
        ssmtStopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(gateStop == false && userAuthorization == true)
                {
                    gateStop = true;

                    HashMap<String, Object> hashMap = new HashMap<String, Object>();
                    hashMap.put("gateStop", gateStop);
                    gateReference.updateChildren(hashMap);
                }
                else
                {
                    Log.d(TAG, "gateStop is not set back to false by Arduino");
                }
            }
        });

    }

}
