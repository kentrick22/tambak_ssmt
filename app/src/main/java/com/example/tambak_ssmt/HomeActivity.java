package com.example.tambak_ssmt;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatRadioButton;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class HomeActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private Button admin_button, homeOtpButton, homeLogoutButton;
    private AppCompatRadioButton homeSsmtButton, homeSwamButton, homeTbjButton;
    private FragmentTransaction fragmentTransaction;
    private Boolean isAdmin;
    private String uid;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private FirebaseDatabase database;
    private DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        admin_button = findViewById(R.id.home_admin_button);
        homeOtpButton = findViewById(R.id.home_otp_button);
        homeSsmtButton = findViewById(R.id.home_ssmt_button);
        homeSwamButton = findViewById(R.id.home_swam_button);
        homeTbjButton = findViewById(R.id.home_tbj_button);
        homeLogoutButton = findViewById(R.id.home_logout_button);

        fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setReorderingAllowed(true);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        uid = currentUser.getUid();

        database = FirebaseDatabase.getInstance();
        reference = database.getReference("Users");

        // Read from the database
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                isAdmin = dataSnapshot.child(uid).child("admin").getValue(Boolean.class);
                Log.d(TAG, "isAdmin = " + isAdmin);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

        admin_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isAdmin == true)
                    openAdminActivity();
                else if(isAdmin == false)
                    openNoAdminActivity();
            }
        });

        homeLogoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();
                //Direct the user to the login page
                openLoginActivity();
            }
        });

        //Add SsmtFragment to the container
        fragmentTransaction.replace(R.id.home_white_box, new SsmtFragment());
        fragmentTransaction.commit();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onUserLeaveHint() {
        onBackPressed();
    }

    public void onRadioButtonClicked(View view) {
        fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setReorderingAllowed(true);

        // Is the button now checked?
        boolean checked = ((AppCompatRadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.home_ssmt_button:
                if (checked) {
                    homeSsmtButton.setTextColor(Color.parseColor("#2D8FFE"));
                    homeSwamButton.setTextColor(Color.WHITE);
                    homeTbjButton.setTextColor(Color.WHITE);

                    //Add SsmtFragment to the container
                    fragmentTransaction.replace(R.id.home_white_box, new SsmtFragment());
                    fragmentTransaction.commit();
                }
                break;
            case R.id.home_swam_button:
                if (checked) {
                    homeSwamButton.setTextColor(Color.parseColor("#2D8FFE"));
                    homeSsmtButton.setTextColor(Color.WHITE);
                    homeTbjButton.setTextColor(Color.WHITE);

                    //Add SwamFragment to the container
                    fragmentTransaction.replace(R.id.home_white_box, new SwamFragment());
                    fragmentTransaction.commit();
                }
                break;
            case R.id.home_tbj_button:
                if (checked) {
                    homeTbjButton.setTextColor(Color.parseColor("#2D8FFE"));
                    homeSsmtButton.setTextColor(Color.WHITE);
                    homeSwamButton.setTextColor(Color.WHITE);

                    //Add SsmtFragment to the container
                    fragmentTransaction.replace(R.id.home_white_box, new TbjFragment());
                    fragmentTransaction.commit();
                }
                break;
        }
    }

    public void openAdminActivity()
    {
        Intent intent = new Intent(this, AdminActivity.class);
        startActivity(intent);
    }

    public void openNoAdminActivity()
    {
        Intent intent = new Intent(this, NoAdminActivity.class);
        startActivity(intent);
    }

    public void openLoginActivity()
    {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
