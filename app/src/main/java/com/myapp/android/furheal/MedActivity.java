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

public class MedActivity extends AppCompatActivity {
    private static final String TAG = "MedDetail";

    private TextView medName;
    private TextView dosage;
    private TextView startDate;
    private TextView endDate;

    static FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_med);

        medName = findViewById(R.id.medName);
        dosage = findViewById(R.id.dosage);
        startDate = findViewById(R.id.startDate);
        endDate = findViewById(R.id.endDate);

        Intent intent= getIntent();
        Bundle bundle = intent.getExtras();

        String medication = null;

        if (bundle != null)
        {
            medication = (String) bundle.get("docId");
        }

        String currentUser = FirebaseAuth.getInstance().getCurrentUser().getUid();

        DocumentReference docRef = db.collection("users").document(currentUser)
                .collection("medications").document(medication);

        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        medName.setText(medName.getText() + document.get("medication").toString());
                        dosage.setText(dosage.getText() + document.get("dosage").toString() + " "
                                + document.get("unit").toString());
                        startDate.setText(startDate.getText() + document.get("startDate").toString());
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

        medName = (TextView)findViewById(R.id.medName);
//        medName.setText((medName.getText() + medication));
    }

    public void goToMeds(View view) {
        Intent intent = new Intent(MedActivity.this, MedsActivity.class);
        startActivity(intent);
    }

    public void goHome(View view) {
        Intent intent = new Intent(MedActivity.this, MainActivity.class);
        startActivity(intent);
    }
}
