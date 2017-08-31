package com.example.android.inventoryapp;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.android.inventoryapp.data.ProductContract.ProductEntry;

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

        mNameEditText = (EditText) findViewById(R.id.edit_product_name);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        // If this is a new prdduct, hide the "Delete" menu item.
        if (mCurrentProductUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mProductHasChanged = true;
            return false;
        }
    };

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save:
                saveProduct();
                finish();
                return true;
            // Respond to a click on the "Delete" menu option
            case R.id.action_delete:
                // Show a dialog that notifies the users they are going to delete a product
                showDeleteConfirmationDialog();
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                // If the product hasn't changed, continue with navigating up to parent activity
                // which is the {@link CatalogActivity}.
                if (!mProductHasChanged) {
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    return true;
                }

                // Otherwise setup a dialog to warn the user.
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // User clicked "Discard" button, navigate to parent activity.
                                NavUtils.navigateUpFromSameTask(EditorActivity.this);
                            }
                        };

                // Show a dialog that notifies the user they have unsaved changes
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void saveProduct() {
        String productName = !mNameEditText.getText().toString().trim().isEmpty() ? mNameEditText.getText().toString().trim() : null;
        String productDescription = !mDescriptionEditText.getText().toString().trim().isEmpty() ? mDescriptionEditText.getText().toString().trim() : "";

        String priceString = !mPriceEditText.getText().toString().trim().isEmpty() ? mPriceEditText.getText().toString().trim() : "0";
        int productPrice = Integer.parseInt(priceString);

        String quantityString = !mQuantityEditText.getText().toString().trim().isEmpty() ? mQuantityEditText.getText().toString().trim() : "0";
        int productQuantity = Integer.parseInt(quantityString);

        String providerName = !mProviderNameEditText.getText().toString().trim().isEmpty() ? mProviderPhoneEditText.getText().toString().trim() : null;
        String providerPhone = !mProviderPhoneEditText.getText().toString().trim().isEmpty() ? mProviderPhoneEditText.getText().toString().trim() : null;

        ContentValues values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, productName);
        values.put(ProductEntry.COLUMN_PRODUCT_DESCRIPTION, productDescription);
        values.put(ProductEntry.COLUMN_PRODUCT_PRICE, productPrice);
        values.put(ProductEntry.COLUMN_PRODUCT_QUANTITY, productQuantity);
        values.put(ProductEntry.COLUMN_PROVIDER_NAME, providerName);
        values.put(ProductEntry.COLUMN_PROVIDER_PHONE, providerPhone);

        if (!(productName == null &&
                productDescription == "" &&
                productPrice == ProductEntry.PRICE_INITIAL &&
                productQuantity == 0 &&
                providerName == null &&
                providerPhone == null)) {

            if (mCurrentProductUri == null) {
                Uri newUri = getContentResolver().insert(ProductEntry.CONTENT_URI, values);
                if (newUri == null) {
                    // If the new content URI is null, then there was an error with insertion.
                    Toast.makeText(this, getString(R.string.editor_insert_product_failed),
                            Toast.LENGTH_SHORT).show();
                } else {
                    // Otherwise, the insertion was successful and we can display a toast.
                    Toast.makeText(this, getString(R.string.editor_insert_product_successful),
                            Toast.LENGTH_SHORT).show();
                }
            } else {
                int updatedLines = getContentResolver().update(mCurrentProductUri, values, null, null);
                if (updatedLines > 0) {
                    Toast.makeText(this, getString(R.string.editor_update_done),
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, getString(R.string.editor_update_none),
                            Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

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
