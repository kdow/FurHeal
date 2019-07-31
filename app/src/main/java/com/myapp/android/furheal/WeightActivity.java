package com.myapp.android.furheal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class WeightActivity extends AppCompatActivity {
    private static final String TAG = "WeightDetail";
    static final int EDIT_WEIGHT_REQUEST = 2;

    private TextView value;
    private TextView date;

    String weight;

    static FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weight);

        value = findViewById(R.id.value);
        date = findViewById(R.id.date);

        Intent intent= getIntent();
        Bundle bundle = intent.getExtras();

        weight = null;

        if (bundle != null)
        {
            weight = (String) bundle.get("docId");
        }

        String currentUser = FirebaseAuth.getInstance().getCurrentUser().getUid();

        DocumentReference docRef = db.collection("users").document(currentUser)
                .collection("weights").document(weight);

        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        value.setText(value.getText() + document.get("weight").toString()
                            + " " + document.get("unit"));
                        date.setText(date.getText() + document.get("date").toString());
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }

    public void goToEdit(View view) {
        Intent intent = new Intent(WeightActivity.this, EditWeightActivity.class);
        intent.putExtra("docId", weight);
        startActivityForResult(intent, EDIT_WEIGHT_REQUEST);
    }

    public void goToWeights(View view) {
        Intent intent = new Intent(WeightActivity.this, WeightsActivity.class);
        startActivity(intent);
    }

    public void goHome(View view) {
        Intent intent = new Intent(WeightActivity.this, MainActivity.class);
        startActivity(intent);
    }
}
