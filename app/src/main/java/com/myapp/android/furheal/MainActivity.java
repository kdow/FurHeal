package com.myapp.android.furheal;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


public class MainActivity extends AppCompatActivity {

    private Button mWeightButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button mWeightButton = (Button) findViewById(R.id.weight);

        mWeightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToWeight();
            }
        });

        initFirestore();
    }

    private void goToWeight() {
        Intent intent = new Intent(this, WeightActivity.class);
        startActivity(intent);
    }
}
