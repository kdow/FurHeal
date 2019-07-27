package com.myapp.android.furheal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class AddSymptomActivity extends AppCompatActivity {
    private static final String TAG = "AddSymptomDetail";

    TextView date;
    DatePickerDialog datePickerDialog;
    int year;
    int month;
    int dayOfMonth;
    Calendar calendar;

    private View selectDate;
    private View endDate;

    EditText mEditText;
    String mSymptomDate;
    String mSymptomEndDate;
    Button mSaveButton;
    String mSeverity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_symptom);

        initViews();

        mSeverity = getResources().getStringArray(R.array.severity_array)[0];

        Spinner spin = (Spinner) findViewById(R.id.severity_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.severity_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        spin.setAdapter(adapter);

        spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                Log.v("item", (String) parent.getItemAtPosition(position));
                mSeverity = (String) parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        selectDate = findViewById(R.id.symptom_date);
        endDate = findViewById(R.id.end_date);

        calendar = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("MM-dd-yyyy");
        String currentDate = df.getDateInstance().format(calendar.getTime());
        mSymptomDate = currentDate;

        final Button buttonDate = findViewById(R.id.symptom_date);

        selectDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                year = calendar.get(Calendar.YEAR);
                month = calendar.get(Calendar.MONTH);
                dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
                datePickerDialog = new DatePickerDialog(AddSymptomActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                                Calendar c = Calendar.getInstance();
                                c.set(Calendar.YEAR, year);
                                c.set(Calendar.MONTH, month);
                                c.set(Calendar.DAY_OF_MONTH, day);
                                mSymptomDate = DateFormat.getDateInstance().format(c.getTime());
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
                datePickerDialog = new DatePickerDialog(AddSymptomActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                                Calendar c = Calendar.getInstance();
                                c.set(Calendar.YEAR, year);
                                c.set(Calendar.MONTH, month);
                                c.set(Calendar.DAY_OF_MONTH, day);
                                mSymptomEndDate = DateFormat.getDateInstance().format(c.getTime());
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
        final String symptom = mEditText.getText().toString();
        final String symptomDate = mSymptomDate;
        final String symptomEndDate = mSymptomEndDate;
        final String severity = mSeverity;

        Map<String, Object> medEntry = new HashMap<>();
        medEntry.put("symptom", symptom);
        medEntry.put("severity", severity);
        medEntry.put("startDate", symptomDate);
        medEntry.put("endDate", symptomEndDate);

        final Context context = getApplicationContext();
        final CharSequence text = "Symptom added!";
        final int duration = Toast.LENGTH_SHORT;
        String currentUser = FirebaseAuth.getInstance().getCurrentUser().getUid();

        db.collection("users").document(currentUser)
                .collection("symptoms")
                .add(medEntry)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.get());
                        mEditText.setText("");
                        Toast.makeText(context, text, duration).show();
                        Intent resultIntent = new Intent();
                        resultIntent.putExtra("symptom", symptom);
                        setResult(RESULT_OK, resultIntent);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                    }
                });
    }

    public void goHome(View view) {
        Intent intent = new Intent(AddSymptomActivity.this, MainActivity.class);
        startActivity(intent);
    }
}
