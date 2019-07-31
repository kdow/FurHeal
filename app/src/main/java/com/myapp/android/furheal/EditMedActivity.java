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
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class EditMedActivity extends AppCompatActivity {


    private static final String TAG = "EditMedDetail";

    TextView date;
    DatePickerDialog datePickerDialog;
    int year;
    int month;
    int dayOfMonth;
    Calendar calendar;

    private View selectDate;
    private View selectEndDate;

    EditText mEditText;
    EditText mEditDoseText;
    String mMedDate;
    String mMedEndDate;
    String mMedUnit;
    Button mSaveButton;

    private EditText editText;
    private EditText editDosageText;
    private Button medDate;
    private Button endingDate;

    String medId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_med);

        initViews();

        editText = findViewById(R.id.editText);
        editDosageText = findViewById(R.id.editDosageText);
        medDate = findViewById(R.id.med_date);
        endingDate = findViewById(R.id.end_date);

        mMedUnit = getResources().getStringArray(R.array.med_options_array)[0];

        final Spinner spin = (Spinner) findViewById(R.id.add_med_spinner);
        final ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.med_options_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        spin.setAdapter(adapter);

        spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                Log.v("item", (String) parent.getItemAtPosition(position));
                mMedUnit = (String) parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });


        Intent intent= getIntent();
        Bundle bundle = intent.getExtras();
        medId = null;

        if (bundle != null)
        {
            medId = (String) bundle.get("docId");
        }

        String currentUser = FirebaseAuth.getInstance().getCurrentUser().getUid();

        DocumentReference docRef = db.collection("users").document(currentUser)
                .collection("medications").document(medId);

        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        if (document.get("medication") != null) {
                            editText.setText(editText.getText() + document.get("medication").toString());
                        }
                        if (document.get("dosage") != null) {
                            editDosageText.setText(editDosageText.getText() + document.get("dosage").toString());
                        }
                        if (document.get("unit") != null) {
                            spin.setSelection(adapter.getPosition("unit"));
                        }
                        if (document.get("startDate") != null) {
                            medDate.setText(medDate.getText() + document.get("startDate").toString());
                        }
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


        selectDate = findViewById(R.id.med_date);
        selectEndDate = findViewById(R.id.end_date);

        calendar = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("MM-dd-yyyy");
        String currentDate = df.getDateInstance().format(calendar.getTime());
        mMedDate = currentDate;

        final Button buttonDate = findViewById(R.id.med_date);

        selectDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                year = calendar.get(Calendar.YEAR);
                month = calendar.get(Calendar.MONTH);
                dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
                datePickerDialog = new DatePickerDialog(EditMedActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                                Calendar c = Calendar.getInstance();
                                c.set(Calendar.YEAR, year);
                                c.set(Calendar.MONTH, month);
                                c.set(Calendar.DAY_OF_MONTH, day);
                                mMedDate = DateFormat.getDateInstance().format(c.getTime());
                                buttonDate.setText(DateFormat.getDateInstance().format(c.getTime()));
                            }
                        }, year, month, dayOfMonth);
                datePickerDialog.getDatePicker();
                datePickerDialog.show();
            }
        });


        final Button endDate = findViewById(R.id.end_date);

        selectEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                year = calendar.get(Calendar.YEAR);
                month = calendar.get(Calendar.MONTH);
                dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
                datePickerDialog = new DatePickerDialog(EditMedActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                                Calendar c = Calendar.getInstance();
                                c.set(Calendar.YEAR, year);
                                c.set(Calendar.MONTH, month);
                                c.set(Calendar.DAY_OF_MONTH, day);
                                mMedEndDate = DateFormat.getDateInstance().format(c.getTime());
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
        mEditDoseText = findViewById(R.id.editDosageText);

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
        final String med = mEditText.getText().toString();
        final String dose = mEditDoseText.getText().toString();
        final String medUnit = mMedUnit;
        final String medDate = mMedDate;
        final String endDate = mMedEndDate;

        Map<String, Object> medEntry = new HashMap<>();
        medEntry.put("medication", med);
        medEntry.put("dosage", dose);
        medEntry.put("unit", medUnit);
        medEntry.put("startDate", medDate);
        medEntry.put("endDate", endDate);

        final Context context = getApplicationContext();
        final CharSequence text = "Medication added!";
        final int duration = Toast.LENGTH_SHORT;
        String currentUser = FirebaseAuth.getInstance().getCurrentUser().getUid();



        DocumentReference medRef = db.collection("users")
                .document(currentUser)
                .collection("medications")
                .document(medId);

        medRef
                .update(medEntry)
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


//        db.collection("users").document(currentUser)
//                .collection("medications")
//                .add(medEntry)
//                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
//                    @Override
//                    public void onSuccess(DocumentReference documentReference) {
//                        Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.get());
//                        mEditText.setText("");
//                        mEditDoseText.setText("");
//                        Toast.makeText(context, text, duration).show();
//                        Intent resultIntent = new Intent();
//                        resultIntent.putExtra("medication", med);
//                        setResult(RESULT_OK, resultIntent);
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        Log.w(TAG, "Error adding document", e);
//                    }
//                });
    }

    public void goToMeds(View view) {
        Intent intent = new Intent(EditMedActivity.this, MedsActivity.class);
        startActivity(intent);
    }

    public void goHome(View view) {
        Intent intent = new Intent(EditMedActivity.this, MainActivity.class);
        startActivity(intent);
    }
}
