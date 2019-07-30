package com.myapp.android.furheal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class EditFoodActivity extends AppCompatActivity {

    private static final String TAG = "EditFoodDetail";

    TextView date;
    DatePickerDialog datePickerDialog;
    int year;
    int month;
    int dayOfMonth;
    Calendar calendar;

    private View selectDate;
    private View endDate;

    EditText mEditText;
    String mFoodDate;
    String mEndDate;
    Button mSaveButton;

    private EditText editText;
    private Button foodDate;
    private Button endingDate;

    String foodId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_food);

        initViews();

        editText = findViewById(R.id.editText);
        foodDate = findViewById(R.id.food_date);
        endingDate = findViewById(R.id.end_date);

        Intent intent= getIntent();
        Bundle bundle = intent.getExtras();

        foodId = null;

        if (bundle != null)
        {
            foodId = (String) bundle.get("docId");
        }

        String currentUser = FirebaseAuth.getInstance().getCurrentUser().getUid();

        DocumentReference docRef = db.collection("users").document(currentUser)
                .collection("food").document(foodId);

        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        editText.setText(editText.getText() + document.get("food").toString());
                        foodDate.setText(foodDate.getText() + document.get("startDate").toString());
                        if (document.get("endDate") != null) {
                            endingDate.setText(endingDate.getText() + document.get("endDate").toString());
                        }
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });

        selectDate = findViewById(R.id.food_date);
        endDate = findViewById(R.id.end_date);
        date = findViewById(R.id.tvDate);

        calendar = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("MM-dd-yyyy");
        String currentDate = df.getDateInstance().format(calendar.getTime());
        mFoodDate = currentDate;

        final Button buttonDate = findViewById(R.id.food_date);

        selectDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                year = calendar.get(Calendar.YEAR);
                month = calendar.get(Calendar.MONTH);
                dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
                datePickerDialog = new DatePickerDialog(EditFoodActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                                Calendar c = Calendar.getInstance();
                                c.set(Calendar.YEAR, year);
                                c.set(Calendar.MONTH, month);
                                c.set(Calendar.DAY_OF_MONTH, day);
                                mFoodDate = DateFormat.getDateInstance().format(c.getTime());
                                buttonDate.setText(DateFormat.getDateInstance().format(c.getTime()));
                            }
                        }, year, month, dayOfMonth);
                datePickerDialog.getDatePicker();
                datePickerDialog.show();
            }
        });

        final Button endDate = findViewById(R.id.end_date);

        endDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                year = calendar.get(Calendar.YEAR);
                month = calendar.get(Calendar.MONTH);
                dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
                datePickerDialog = new DatePickerDialog(EditFoodActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                                Calendar c = Calendar.getInstance();
                                c.set(Calendar.YEAR, year);
                                c.set(Calendar.MONTH, month);
                                c.set(Calendar.DAY_OF_MONTH, day);
                                mEndDate = DateFormat.getDateInstance().format(c.getTime());
                                endDate.setText(DateFormat.getDateInstance().format(c.getTime()));
                            }
                        }, year, month, dayOfMonth);
                datePickerDialog.getDatePicker();
                datePickerDialog.show();
            }
        });
    }

    private void initViews() {
        mEditText = findViewById(R.id.editText);

        mSaveButton = findViewById(R.id.saveButton);
        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSaveButtonClicked();
            }
        });
    }

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    private void onSaveButtonClicked() {
        final String food = mEditText.getText().toString();
        final String foodDate = mFoodDate;
        final String endDate = mEndDate;

        Map<String, Object> foodEntry = new HashMap<>();
        foodEntry.put("food", food);
        foodEntry.put("startDate", foodDate);
        foodEntry.put("endDate", endDate);

        final Context context = getApplicationContext();
        final CharSequence text = "Food updated!";
        final int duration = Toast.LENGTH_SHORT;
        String currentUser = FirebaseAuth.getInstance().getCurrentUser().getUid();

        DocumentReference foodRef = db.collection("users")
                .document(currentUser)
                .collection("food")
                .document(foodId);

        foodRef
                .update(foodEntry)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully updated!");
                        Toast.makeText(context, text, duration).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error updating document", e);
                    }
                });
    }

    public void goHome(View view) {
        Intent intent = new Intent(EditFoodActivity.this, MainActivity.class);
        startActivity(intent);
    }
}
