package com.example.android.inventoryapp;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;

public class EditorActivity extends AppCompatActivity {

    private static final int PET_LOADER = 0;
    private EditText mNameEditText;
    private EditText mDescriptionEditText;
    private EditText mPriceEditText;
    private EditText mQuantityEditText;
    private EditText mProviderNameEditText;
    private EditText mProviderPhoneEditText;

    private Uri mCurrentPetUri;
    private boolean mProductHasChanged = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);
    }
}
