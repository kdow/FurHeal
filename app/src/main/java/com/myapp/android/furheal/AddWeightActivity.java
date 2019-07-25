package com.myapp.android.furheal;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.myapp.android.furheal.model.WeightEntry;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class AddWeightActivity extends AppCompatActivity {

    private static final String TAG = "AddWeightDetail";

    TextView date;
    DatePickerDialog datePickerDialog;
    int year;
    int month;
    int dayOfMonth;
    Calendar calendar;

    private View selectDate;

    EditText mEditText;
    String mWeightDate;
    Button mSaveButton;
    String mWeightUnit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_weight);

        initViews();

        mWeightUnit = getResources().getStringArray(R.array.weight_options_array)[0];

        Spinner spin = (Spinner) findViewById(R.id.add_weights_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.weight_options_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        spin.setAdapter(adapter);

        spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                Log.v("item", (String) parent.getItemAtPosition(position));
                mWeightUnit = (String) parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        selectDate = findViewById(R.id.weight_date);
        date = findViewById(R.id.tvDate);

        calendar = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("MM-dd-yyyy");
        String currentDate = df.getDateInstance().format(calendar.getTime());
        mWeightDate = currentDate;
//        try {
//            mWeightDate = df.parse(currentDate);
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
        final Button buttonDate = findViewById(R.id.weight_date);
        buttonDate.setText(currentDate);

        selectDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                year = calendar.get(Calendar.YEAR);
                month = calendar.get(Calendar.MONTH);
                dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
                datePickerDialog = new DatePickerDialog(AddWeightActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                                Calendar c = Calendar.getInstance();
                                c.set(Calendar.YEAR, year);
                                c.set(Calendar.MONTH, month);
                                c.set(Calendar.DAY_OF_MONTH, day);
                                mWeightDate = DateFormat.getDateInstance().format(c.getTime());
                                buttonDate.setText(DateFormat.getDateInstance().format(c.getTime()));
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
        final String weight = mEditText.getText().toString();
        final String weightUnit = mWeightUnit;
        final String weightDate = mWeightDate;
//        final WeightEntry newWeight = new WeightEntry(weight);

        Map<String, Object> weightEntry = new HashMap<>();
        weightEntry.put("weight", weight);
        weightEntry.put("unit", weightUnit);
        weightEntry.put("date", weightDate);

        final Context context = getApplicationContext();
        final CharSequence text = "Weight added!";
        final int duration = Toast.LENGTH_SHORT;
        String currentUser = FirebaseAuth.getInstance().getCurrentUser().getUid();

        db.collection("users").document(currentUser)
                .collection("weights")
            .add(weightEntry)
            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                @Override
                public void onSuccess(DocumentReference documentReference) {
                    Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.get());
                    mEditText.setText("");
                    Toast.makeText(context, text, duration).show();
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("weight", weight);
                    resultIntent.putExtra("unit", weightUnit);
                    resultIntent.putExtra("date", weightDate);
                    setResult(RESULT_OK, resultIntent);
//                    WeightActivity.updateWeightLog(weightDate, weight, weightUnit);
                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.w(TAG, "Error adding document", e);
                }
            });
    }
}
