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

public class MedsActivity extends AppCompatActivity {
    private static final String TAG = "MedsDetail";
    static final int ADD_MEDS_REQUEST = 1;
    static final int SELECT_MED_REQUEST = 2;

    static LinearLayout linearLayout;
    static TextView textView;

    private Date mDate;

    static FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meds);

        linearLayout = (LinearLayout) findViewById(R.id.medsLog);

        String currentUser = FirebaseAuth.getInstance().getCurrentUser().getUid();

        CollectionReference docRef = db.collection("users")
                .document(currentUser).collection("medications");

        docRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {

                    for (final QueryDocumentSnapshot document : task.getResult()) {
                        Log.d(TAG, document.getId() + " => " + document.getData());
                        final String id = document.getId();
                        final LinearLayout linearLayout = findViewById(R.id.medsLog);
                        final TextView textView = new TextView(MedsActivity.this);
                        textView.setTextSize(16);
                        final String medication = document.getData().get("medication").toString();
                        textView.setText(medication);
                        if (linearLayout != null) {
                            linearLayout.addView(textView);
                        }
                        textView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent myIntent = new Intent(MedsActivity.this, MedActivity.class);
                                myIntent.putExtra("docId", id);
                                startActivityForResult(myIntent, SELECT_MED_REQUEST);
                            }
                        });
                    }

                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });

        System.out.println(docRef);

        Button mAddMedButton = (Button) findViewById(R.id.add_med);

        mAddMedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MedsActivity.this, AddMedActivity.class);
                startActivityForResult(intent, ADD_MEDS_REQUEST);
            }
        });



    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ADD_MEDS_REQUEST) {
            if (resultCode == RESULT_OK) {
                Bundle medData = data.getExtras();
                updateMedsLog(medData.getCharSequence("medication").toString());
            }
        }
    }

    public void updateMedsLog(String medication) {
        linearLayout = (LinearLayout) findViewById(R.id.medsLog);
        textView = new TextView(MedsActivity.this);
        String fullInfo = medication;
        textView.setText(fullInfo);
        if (linearLayout != null) {
            linearLayout.addView(textView);
        }
    }



    public void goHome(View view) {
        Intent intent = new Intent(MedsActivity.this, MainActivity.class);
        startActivity(intent);
    }
}
