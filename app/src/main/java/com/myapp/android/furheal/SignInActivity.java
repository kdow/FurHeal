package com.myapp.android.furheal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseError;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.myapp.android.furheal.model.Pet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class SignInActivity extends AppCompatActivity {

    private static final String TAG = "SignInDetail";

    private static final int RC_SIGN_IN = 0;
    static final int SELECT_PET_REQUEST = 1;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseAuth mAuth;
    private Button btnSignOut;
    private Button btnSignIn;
    private TextView txtEmail;
    private TextView txtUser;
    private ImageView petPhoto;
    String mPet;
    private String userPet;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference collectionReference = db.collection("users");

    List<AuthUI.IdpConfig> providers = Arrays.asList(
            new AuthUI.IdpConfig.EmailBuilder().build());

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        for (String provider : AuthUI.SUPPORTED_PROVIDERS) {
            Log.v(this.getClass().getName(), provider);
        }

        mAuth = FirebaseAuth.getInstance();
        btnSignIn = (Button) findViewById(R.id.btnSignIn);
        btnSignOut = (Button) findViewById(R.id.btnSignOut);

        txtEmail =(TextView) findViewById(R.id.txtEmail);
        txtUser = (TextView) findViewById(R.id.txtUser);
        petPhoto = (ImageView) findViewById(R.id.nurseJazzy);

        mAuthListener = new FirebaseAuth.AuthStateListener() {

            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                updateUi();
            }
        };
    }

    private void updateUi() {
        FirebaseUser user = mAuth.getCurrentUser();
        final Spinner spinner = (Spinner) findViewById(R.id.spinner);
        Button btnSelect = (Button) findViewById(R.id.select_pet);

        if (user == null) {
            btnSignIn.setVisibility(View.VISIBLE);
            btnSignOut.setVisibility(View.GONE);
            txtEmail.setVisibility(View.GONE);
            txtUser.setVisibility(View.GONE);
            petPhoto.setVisibility(View.VISIBLE);
            spinner.setVisibility(View.GONE);
            btnSelect.setVisibility(View.GONE);
        } else {
            btnSignIn.setVisibility(View.GONE);
            btnSignOut.setVisibility(View.VISIBLE);
            txtEmail.setVisibility(View.VISIBLE);
            txtUser.setVisibility(View.VISIBLE);
            petPhoto.setVisibility(View.VISIBLE);
            spinner.setVisibility(View.VISIBLE);
            btnSelect.setVisibility(View.VISIBLE);

            txtUser.setText(user.getDisplayName());
            txtEmail.setText(user.getEmail());

            mPet = getResources().getStringArray(R.array.pet_options_array)[0];

            // Get reference of widgets from XML layout

//            String currentUser = FirebaseAuth.getInstance().getCurrentUser().getUid();
//
//
//            collectionReference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                @Override
//                public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                    if (task.isSuccessful()) {
//                        for (QueryDocumentSnapshot document : task.getResult()) {
////                            String userPet = document.getData().get("pets").toString();
//                            Log.d(TAG, document.getId() + " => " + document.getData());
//                        }
//                    } else {
//                        Log.d(TAG, "Error getting documents: ", task.getException());
//                    }
//                }
//            });



            String currentUser = FirebaseAuth.getInstance().getCurrentUser().getUid();

            DocumentReference docRef = db.collection("users")
                    .document(currentUser);


            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
//                            String petNameStr = document.getData().get("pet").toString();
                            Object petName = document.getString("pet");
                            Log.d(TAG, "DocumentSnapshot data: " + document.getData());

                            // Initializing a String Array
                            String[] pets = new String[]{
                                    (String) petName,
                                    "New Pet"
                            };

                            final List<String> petsList = new ArrayList<>(Arrays.asList(pets));

                            // Initializing an ArrayAdapter
                            final ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(
                                    SignInActivity.this,R.layout.spinner_item,petsList);

                            spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_item);
                            spinner.setAdapter(spinnerArrayAdapter);


                        } else {
                            Log.d(TAG, "No such document");
                        }
                    } else {
                        Log.d(TAG, "get failed with ", task.getException());
                    }
                }
            });




            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view,
                                           int position, long id) {
                    Log.v("pet", (String) parent.getItemAtPosition(position));
                    mPet = (String) parent.getItemAtPosition(position);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });

            btnSelect.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String mPet = spinner.getSelectedItem().toString();

                    if (mPet.equals("New Pet")) {
                        Intent intent = new Intent(SignInActivity.this, AddNewPetActivity.class);
                        startActivity(intent);
                    } else {
                        Intent intent = new Intent(v.getContext(), MainActivity.class);
                        intent.putExtra("petName", mPet);
                        startActivityForResult(intent, SELECT_PET_REQUEST);
                    }
                }
            });
        }
    }

    public void signOut(View view) {
        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(SignInActivity.this, "You've been signed out", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);
            if (resultCode == RESULT_OK) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                final String currentUserId = user.getUid();
                String name = user.getDisplayName();

                // Create user map to add to User collection
                Map<String, String> userEntry = new HashMap<>();
                userEntry.put("userId", currentUserId);
                userEntry.put("name", name);

                collectionReference.document(currentUserId)
                        .set(userEntry)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d(TAG, "DocumentSnapshot successfully written!");
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "Error writing document", e);
                            }
                        });
                Log.d(this.getClass().getName(), "This user signed in with " + response.getProviderType());
                updateUi();
                // ...
            } else {
                updateUi();
            }
        }
    }

    public void signIn(View view) {
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .build(),
                RC_SIGN_IN);
    }
}
