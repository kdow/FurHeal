package com.myapp.android.furheal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.myapp.android.furheal.model.Pet;

import java.util.Arrays;
import java.util.Date;

public class WeightActivity extends AppCompatActivity {

    private static final String TAG = "WeightDetail";
    static final int ADD_WEIGHT_REQUEST = 1;

    static LinearLayout linearLayout;
    static TextView textView;

    private Date mDate;

    static FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weight);

        linearLayout = (LinearLayout) findViewById(R.id.weightLog);

        String currentUser = FirebaseAuth.getInstance().getCurrentUser().getUid();

        CollectionReference docRef = db.collection("users")
                .document(currentUser).collection("weights");

        docRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Pet p = new Pet(document.getId());
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                final LinearLayout linearLayout = findViewById(R.id.weightLog);
                                final TextView textView = new TextView(WeightActivity.this);
                                textView.setTextSize(16);
                                String fullDate = document.getData().get("weight").toString()
                                        + " " + document.getData().get("unit").toString() + " "
                                        + document.getData().get("date").toString();
                                textView.setText(fullDate);
                                if (linearLayout != null) {
                                    linearLayout.addView(textView);
                                }
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
        });

        System.out.println(docRef);

        Button mAddWeightButton = (Button) findViewById(R.id.add_weight);

        mAddWeightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToAddWeight();
            }
        });
    }

    private void goToAddWeight() {
        Intent intent = new Intent(this, AddWeightActivity.class);
        startActivityForResult(intent, ADD_WEIGHT_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ADD_WEIGHT_REQUEST) {
            if (resultCode == RESULT_OK) {
                Bundle weightData = data.getExtras();
                updateWeightLog(weightData.getCharSequence("date").toString(),
                        weightData.getCharSequence("weight").toString(),
                        weightData.getCharSequence("unit").toString());
            }
        }
    }

    public void updateWeightLog(String date, String weight, String unit) {
        linearLayout = (LinearLayout) findViewById(R.id.weightLog);
        textView = new TextView(WeightActivity.this);
        String fullInfo = weight + " " + unit + " " + date;
        textView.setText(fullInfo);
        if (linearLayout != null) {
            linearLayout.addView(textView);
        }
    }

    public Date getDate() {
        return mDate;
    }

    public void setDate(Date date) {
        mDate = date;
    }

    public void goHome(View view) {
        Intent intent = new Intent(WeightActivity.this, MainActivity.class);
        startActivity(intent);
    }
}
