package com.example.android.inventoryapp;

import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;

public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int PRODUCT_LOADER = 0;
    private EditText mNameEditText;
    private EditText mDescriptionEditText;
    private EditText mPriceEditText;
    private EditText mQuantityEditText;
    private EditText mProviderNameEditText;
    private EditText mProviderPhoneEditText;

    private Uri mCurrentProductUri;
    private boolean mProductHasChanged = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        Intent intent = getIntent();
        Uri currentProductUri = intent.getData();

        mNameEditText = (EditText) findViewById(R.id.edit_product_name;
        mDescriptionEditText = (EditText) findViewById(R.id.edit_product_description);
        mPriceEditText = (EditText) findViewById(R.id.edit_product_price);
        mQuantityEditText = (EditText) findViewById(R.id.edit_product_quantity);
        mProviderNameEditText = (EditText) findViewById(R.id.edit_provider_name);
        mProviderPhoneEditText = (EditText) findViewById(R.id.edit_provider_phone);

        mNameEditText.setOnTouchListener(mTouchListener);
        mDescriptionEditText.setOnTouchListener(mTouchListener);
        mPriceEditText.setOnTouchListener(mTouchListener);
        mQuantityEditText.setOnTouchListener(mTouchListener);
        mProviderNameEditText.setOnTouchListener(mTouchListener);
        mProviderPhoneEditText.setOnTouchListener(mTouchListener);

        if (currentProductUri == null) {
            setTitle(getString(R.string.editor_activity_title_add_product));
            invalidateOptionsMenu();
        } else {
            setTitle(getString(R.string.editor_activity_title_edit_product));
            mCurrentProductUri = currentProductUri;
            // Kick off loader
            getLoaderManager().initLoader(PRODUCT_LOADER, null, this);
        }
    }

    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mProductHasChanged = true;
            return false;
        }
    };

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
