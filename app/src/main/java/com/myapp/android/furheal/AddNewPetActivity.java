package com.myapp.android.furheal;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class AddNewPetActivity extends AppCompatActivity {

    EditText mEditText;
    Button mSaveButton;
    static final int SELECT_PET_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_pet);

        mEditText = findViewById(R.id.name_input);

        mSaveButton = findViewById(R.id.save_pet);
        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSaveButtonClicked();
            }
        });
    }

    private void onSaveButtonClicked() {
        final String name = mEditText.getText().toString();

        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("petName", name);
        startActivityForResult(intent, SELECT_PET_REQUEST);
    }
}
