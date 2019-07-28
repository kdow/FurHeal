package com.myapp.android.furheal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Date;

public class SymptomsActivity extends AppCompatActivity {
    private static final String TAG = "SymptomDetail";
    static final int ADD_SYMPTOM_REQUEST = 1;
    static final int SELECT_SYMPTOM_REQUEST = 2;

    static LinearLayout linearLayout;
    static TextView textView;

    private Date mDate;

    static FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_symptoms);

        linearLayout = (LinearLayout) findViewById(R.id.symptomLog);

        String currentUser = FirebaseAuth.getInstance().getCurrentUser().getUid();

        CollectionReference docRef = db.collection("users")
                .document(currentUser).collection("symptoms");

        docRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {

                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Log.d(TAG, document.getId() + " => " + document.getData());
                        final String id = document.getId();
                        final LinearLayout linearLayout = findViewById(R.id.symptomLog);
                        final TextView textView = new TextView( SymptomsActivity.this);
                        textView.setTextSize(22);
                        textView.setPadding(0,8,0,8);
                        String fullDate = document.getData().get("symptom").toString();
                        textView.setText(fullDate);
                        if (linearLayout != null) {
                            linearLayout.addView(textView);
                        }
                        textView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent myIntent = new Intent(SymptomsActivity.this, SymptomActivity.class);
                                myIntent.putExtra("docId", id);
                                startActivityForResult(myIntent, SELECT_SYMPTOM_REQUEST);
                            }
                        });
                    }
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });

        System.out.println(docRef);

        Button mAddSymptomButton = (Button) findViewById(R.id.add_symptom);

        mAddSymptomButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent( SymptomsActivity.this, AddSymptomActivity.class);
                startActivityForResult(intent, ADD_SYMPTOM_REQUEST);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ADD_SYMPTOM_REQUEST) {
            if (resultCode == RESULT_OK) {
                Bundle symptomData = data.getExtras();
                updateSymptomLog(symptomData.getCharSequence("symptom").toString());
            }
        }
    }

    public void updateSymptomLog(String symptom) {
        linearLayout = (LinearLayout) findViewById(R.id.symptomLog);
        textView = new TextView(SymptomsActivity.this);
        String fullInfo = symptom;
        textView.setText(fullInfo);
        if (linearLayout != null) {
            linearLayout.addView(textView);
        }
    }

    public void goHome(View view) {
        Intent intent = new Intent(SymptomsActivity.this, MainActivity.class);
        startActivity(intent);
    }
}
