package com.myapp.android.furheal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import au.com.bytecode.opencsv.CSVWriter;

public class WeightActivity extends AppCompatActivity {

    private static final String TAG = "WeightDetail";
    static final int ADD_WEIGHT_REQUEST = 1;

    static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 0;

    static LinearLayout linearLayout;
    static TextView textView;

    private Date mDate;

    static FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FileWriter mFileWriter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weight);

        linearLayout = (LinearLayout) findViewById(R.id.weightLog);

        CollectionReference docRef = db.collection("testPets")
                .document("JazzyTest").collection("weights");
        docRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
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

        Button mExportWeight = (Button) findViewById(R.id.export_weight);

        mExportWeight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(WeightActivity.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {

                    // Permission is not granted
                    if (ActivityCompat.shouldShowRequestPermissionRationale(WeightActivity.this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                        // Show an explanation to the user *asynchronously* -- don't block
                        // this thread waiting for the user's response! After the user
                        // sees the explanation, try again to request the permission.
                    } else {
                        // No explanation needed; request the permission
                        ActivityCompat.requestPermissions(WeightActivity.this,
                                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);

                        // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                        // app-defined int constant. The callback method gets the
                        // result of the request.
                    }
                } else {
                    // Permission has already been granted
                    String csv = android.os.Environment.getExternalStorageDirectory().getAbsolutePath();
                    CSVWriter writer = null;
                    try {
                        writer = new CSVWriter(new FileWriter(csv));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    List<String[]> data = new ArrayList<String[]>();
                    data.add(new String[] {"India", "New Delhi"});
                    data.add(new String[] {"United States", "Washington D.C"});
                    data.add(new String[] {"Germany", "Berlin"});

                    writer.writeAll(data);

                    try {
                        writer.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                public void onRequestPermissionsResult(int requestCode,
                String[] permissions, int[] grantResults)
                switch (requestCode) {
                    case MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE: {
                        // If request is cancelled, the result arrays are empty.
                        if (grantResults.length > 0
                                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                            // permission was granted, yay! Do the
                            // contacts-related task you need to do.
                        } else {
                            // permission denied, boo! Disable the
                            // functionality that depends on this permission.
                        }
                        return;
                    }

                    // other 'case' lines to check for other
                    // permissions this app might request.
                }


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
}
