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

public class SymptomActivity extends AppCompatActivity {
    private static final String TAG = "SymptomDetail";
    static final int EDIT_SYMPTOM_REQUEST = 2;

    private TextView symptomName;
    private TextView severity;
    private TextView startDate;
    private TextView endDate;

    String symptom;

    static FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_symptom);

        symptomName = findViewById(R.id.symptomName);
        severity = findViewById(R.id.severity);
        startDate = findViewById(R.id.startDate);
        endDate = findViewById(R.id.endDate);

        Intent intent= getIntent();
        Bundle bundle = intent.getExtras();

        symptom = null;

        if (bundle != null)
        {
            symptom = (String) bundle.get("docId");
        }

        String currentUser = FirebaseAuth.getInstance().getCurrentUser().getUid();

        DocumentReference docRef = db.collection("users").document(currentUser)
                .collection("symptoms").document(symptom);

        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        symptomName.setText(symptomName.getText() + document.get("symptom").toString());
                        if (document.get("severity") != null) {
                            severity.setText(severity.getText() + document.get("severity").toString());
                        }
                        if (document.get("startDate") != null) {
                            startDate.setText(startDate.getText() + document.get("startDate").toString());
                        }
                        if (document.get("endDate") != null) {
                            endDate.setText(endDate.getText() + document.get("endDate").toString());
                        }
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
        Intent intent = new Intent(SymptomActivity.this, EditSymptomActivity.class);
        intent.putExtra("docId", symptom);
        startActivityForResult(intent, EDIT_SYMPTOM_REQUEST);
    }

    public void goToSymptoms(View view) {
        Intent intent = new Intent(SymptomActivity.this, SymptomsActivity.class);
        startActivity(intent);
    }

    public void goHome(View view) {
        Intent intent = new Intent(SymptomActivity.this, SymptomsActivity.class);
        startActivity(intent);
    }
}
