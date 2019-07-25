package com.myapp.android.furheal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class AddNewPetActivity extends AppCompatActivity {
    private static final String TAG = "AddNewPetDetail";

    EditText mEditText;
    Button mSaveButton;
    static final int SELECT_PET_REQUEST = 1;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference collectionReference = db.collection("users");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_pet);

        mEditText = findViewById(R.id.name_input);

        mSaveButton = findViewById(R.id.save_pet);
        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSaveButtonClicked();
            }
        });
    }

    private void onSaveButtonClicked() {
        final String name = mEditText.getText().toString();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final String currentUserId = user.getUid();



        Map<String, String> petEntry = new HashMap<>();
        petEntry.put("pet", name);

        collectionReference.document(currentUserId)
                .set(petEntry, SetOptions.merge())
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

        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("petName", name);
        startActivityForResult(intent, SELECT_PET_REQUEST);
    }
}
