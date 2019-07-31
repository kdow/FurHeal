package com.myapp.android.furheal;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.firestore.FirebaseFirestore;


public class MainActivity extends AppCompatActivity {

    private Button mMedsButton;
    private Button mWeightButton;
    private Button mFoodButton;

    private FirebaseFirestore mFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button mMedsButton = (Button) findViewById(R.id.meds);
        Button mSymptomsButton = (Button) findViewById(R.id.symptoms);
        Button mWeightButton = (Button) findViewById(R.id.weight);
        Button mFoodButton = (Button) findViewById(R.id.food);
        Button mProfileButton = (Button) findViewById(R.id.profile);

        mMedsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MedsActivity.class);
                startActivity(intent);
            }
        });

        mSymptomsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SymptomsActivity.class);
                startActivity(intent);
            }
        });

        mWeightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, WeightActivity.class);
                startActivity(intent);
            }
        });

        mFoodButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, FoodsActivity.class);
                startActivity(intent);
            }
        });

        mProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SignInActivity.class);
                startActivity(intent);
            }
        });

        initFirestore();
    }


    private void initFirestore() {
        mFirestore = FirebaseFirestore.getInstance();
    }
}
