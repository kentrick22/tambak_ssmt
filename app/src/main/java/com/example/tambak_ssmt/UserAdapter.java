package com.example.tambak_ssmt;

import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

/**************************************************************
 * sources:
 * //General source on recyclerview
 * https://guides.codepath.com/android/using-the-recyclerview
 * //Source on recyclerview and firebase realtime database
 * https://medium.com/@avinriyan/android-x-firebase-menampilkan-data-list-7a73ccc2412f
 * https://www.geeksforgeeks.org/how-to-populate-recyclerview-with-firebase-data-using-firebaseui-in-android-studio/
 **************************************************************/

public class UserAdapter extends FirebaseRecyclerAdapter<User, UserAdapter.ViewHolder> {
    private static final String TAG = MainActivity.class.getSimpleName();
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private FirebaseDatabase database;
    private DatabaseReference reference;
    private Boolean gateAccess;
    private String uid;

    /**
     * Initialize a {@link RecyclerView.Adapter} that listens to a Firebase query. See
     * {@link FirebaseRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public UserAdapter(@NonNull FirebaseRecyclerOptions options) { super(options); }



    public class ViewHolder extends RecyclerView.ViewHolder{

        //ViewHolder
        public TextView name;
        public ToggleButton button;
        String selectedUid;


        public ViewHolder(View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.item_name);
            button = itemView.findViewById(R.id.item_button);

            mAuth = FirebaseAuth.getInstance();
            currentUser = mAuth.getCurrentUser();
            uid = currentUser.getUid();
            database = FirebaseDatabase.getInstance();
            reference = database.getReference("Users");


            //Read the database
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // This method is called once with the initial value and again
                    // whenever data at this location is updated.
                    gateAccess = dataSnapshot.child(selectedUid).child("gateAccess").getValue(Boolean.class);

                    if(gateAccess == true) {
                        button.setChecked(true);
                    }
                    else if(gateAccess == false) {
                        button.setChecked(false);
                    }
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    // Failed to read value
                    Log.w(TAG, "Failed to read value.", error.toException());
                }
            });

            //Listen to toggle button clicks
            button.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isChecked) {
                        //Give access
                        HashMap<String, Object> hashMap = new HashMap<String, Object>();
                        hashMap.put("gateAccess", true);
                        reference.child(selectedUid).updateChildren(hashMap);

                        Log.d(TAG, "User " + selectedUid + " access granted");
                    } else {
                        //Stop access
                        HashMap<String, Object> hashMap = new HashMap<String, Object>();
                        hashMap.put("gateAccess", false);
                        reference.child(selectedUid).updateChildren(hashMap);

                        Log.d(TAG, "User " + selectedUid + " access terminated");
                    }
                }
            });
        }


    }

    @Override
    protected void onBindViewHolder(@NonNull final ViewHolder holder, final int position, @NonNull User model) {
        //Get the name to be displayed in the viewHolder
        holder.name.setText(model.getName());

        //Get the uid selected to be passed to the holder.selectedUid variable
        holder.selectedUid = model.getUid();

    }

    @NonNull
    @Override
    public UserAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        View userView = inflater.inflate(R.layout.item_user, parent, false);

        ViewHolder viewHolder = new ViewHolder(userView);
        return viewHolder;
    }



}
