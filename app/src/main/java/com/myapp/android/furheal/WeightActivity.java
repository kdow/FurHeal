package com.myapp.android.furheal;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;

public class WeightActivity extends AppCompatActivity {

    private Date mDate;

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weight);

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
        startActivity(intent);
    }

    public Date getDate() {
        return mDate;
    }

    public void setDate(Date date) {
        mDate = date;
    }
}
