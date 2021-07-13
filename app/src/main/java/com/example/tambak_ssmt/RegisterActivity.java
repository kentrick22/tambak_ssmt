package com.example.tambak_ssmt;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private EditText signupName, signupEmail, signupPassword, signupConfPassword;
    private Button signupButton;
    private TextView signupToLogin2;
    private FirebaseAuth mAuth;         //For authentication
    private FirebaseDatabase database;  //For user properties database
    private DatabaseReference reference;//For user properties database

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        signupName = findViewById(R.id.signup_name);
        signupEmail = findViewById(R.id.signup_email);
        signupPassword = findViewById(R.id.signup_conf_password);
        signupButton = findViewById(R.id.signup_button);
        signupConfPassword = findViewById(R.id.signup_conf_password);
        signupToLogin2 = findViewById(R.id.signup_to_login_2);
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        reference = database.getReference("Users");

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                register();
            }
        });

        signupToLogin2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openLoginActivity();
            }
        });
    }

    private void register()
    {
        //User's properties
        final String name = signupName.getText().toString();
        final String email = signupEmail.getText().toString();
        final String password = signupPassword.getText().toString();
        final String confPassword = signupConfPassword.getText().toString();

        if(TextUtils.isEmpty(name))
        {
            signupName.setError("Kolom nama tidak boleh kosong");
            return;
        }
        else if(TextUtils.isEmpty(email))
        {
            signupEmail.setError("Kolom email tidak boleh kosong");
            return;
        }
        else if(!isValidEmail(email))
        {
            signupEmail.setError(("Email tidak valid"));
            return;
        }
        else if(TextUtils.isEmpty(password))
        {
            signupPassword.setError("Kolom password tidak boleh kosong");
            return;
        }
        else if(TextUtils.isEmpty(confPassword))
        {
            signupConfPassword.setError("Kolom konfirmasi password tidak boleh kosong");
            return;
        }
        else if(!password.equals(confPassword))
        {
            signupPassword.setError("Kolom password dan konfirmasi password harus sama");
            return;
        }
        else if(password.length() < 8)
        {
            signupPassword.setError(("Password harus 8 karakter atau lebih"));
            return;
        }

        //Register account to Firebase Authentication
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");

                            //Properties
                            FirebaseUser currentUser = mAuth.getCurrentUser();
                            String uid = currentUser.getUid();
                            Boolean gateAccess = false;
                            Boolean admin = false;

                            //Add User's properties inside Realtime Database
                            User user = new User(name, uid, password, gateAccess, admin);

                            reference.child(uid).setValue(user);

                            //Update UI
                            updateUI(currentUser);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(RegisterActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    }
                });

    }

    // [START on_start_check_user]
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            updateUI(currentUser);
        }
    }
    // [END on_start_check_user]

    private void updateUI(FirebaseUser currentUser)
    {
        if(currentUser == null)
        {
            Toast.makeText(this, "Proses sign up gagal", Toast.LENGTH_SHORT).show();
        }
        else
        {
            Toast.makeText(this,"Proses sign up berhasil",Toast.LENGTH_LONG).show();
            openHomeActivity();
        }
    }

    private void openHomeActivity()
    {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
        finish();
    }

    private void openLoginActivity()
    {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    private boolean isValidEmail(CharSequence target)
    {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }
}
