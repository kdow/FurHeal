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

public class FoodsActivity extends AppCompatActivity {

    private static final String TAG = "FoodDetail";
    static final int ADD_FOOD_REQUEST = 1;
    static final int SELECT_FOOD_REQUEST = 2;

    static LinearLayout linearLayout;
    static TextView textView;

    private Date mDate;

    static FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_foods);

        linearLayout = (LinearLayout) findViewById(R.id.foodLog);

        String currentUser = FirebaseAuth.getInstance().getCurrentUser().getUid();

        CollectionReference docRef = db.collection("users")
                .document(currentUser).collection("food");

        docRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {

                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Log.d(TAG, document.getId() + " => " + document.getData());
                        final String id = document.getId();
                        final LinearLayout linearLayout = findViewById(R.id.foodLog);
                        final TextView textView = new TextView(FoodsActivity.this);
                        textView.setTextSize(22);
                        textView.setPadding(0,8,0,8);
                        String food = document.getData().get("food").toString();
                        textView.setText(food);
                        if (linearLayout != null) {
                            linearLayout.addView(textView);
                        }
                        textView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent myIntent = new Intent(FoodsActivity.this, FoodActivity.class);
                                myIntent.putExtra("docId", id);
                                startActivityForResult(myIntent, SELECT_FOOD_REQUEST);
                            }
                        });
                    }
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });

        System.out.println(docRef);

        Button mAddFoodButton = (Button) findViewById(R.id.add_food);

        mAddFoodButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FoodsActivity.this, AddFoodActivity.class);
                startActivityForResult(intent, ADD_FOOD_REQUEST);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ADD_FOOD_REQUEST) {
            if (resultCode == RESULT_OK) {
                Bundle foodData = data.getExtras();
                updateFoodLog(foodData.getCharSequence("food").toString());
            }
        }
    }

    public void updateFoodLog(String food) {
        linearLayout = (LinearLayout) findViewById(R.id.foodLog);
        textView = new TextView(FoodsActivity.this);
        String fullInfo = food;
        textView.setText(fullInfo);
        if (linearLayout != null) {
            linearLayout.addView(textView);
        }
    }

    public void goHome(View view) {
        Intent intent = new Intent(FoodsActivity.this, MainActivity.class);
        startActivity(intent);
    }
}
