package com.example.android.inventoryapp;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.android.inventoryapp.data.ProductContract.ProductEntry;

import static android.R.attr.name;

public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int PRODUCT_LOADER = 0;
    private EditText mNameEditText;
    private EditText mDescriptionEditText;
    private EditText mPriceEditText;
    private EditText mQuantityEditText;
    private EditText mProviderNameEditText;
    private EditText mProviderPhoneEditText;

    private ImageButton mMinusPriceImageButton;
    private ImageButton mPlusPriceImageButton;
    private ImageButton mMinusQuantityImageButton;
    private ImageButton mPlusQuantityImageButton;
    private Button mOrderMoreButton;

    private Uri mCurrentProductUri;
    private boolean mProductHasChanged = false;

    private static final int priceField = 0;
    private static final int quantityField = 1;

    private static final int minusOne = -1;
    private static final int plusOne = 1;

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

        mMinusPriceImageButton = (ImageButton) findViewById(R.id.minus_price_button);
        mPlusPriceImageButton = (ImageButton) findViewById(R.id.plus_price_button);
        mMinusQuantityImageButton = (ImageButton) findViewById(R.id.minus_quantity_button);
        mPlusQuantityImageButton = (ImageButton) findViewById(R.id.plus_quantity_button);
        mOrderMoreButton = (Button) findViewById(R.id.order_more_button);

        mNameEditText.setOnTouchListener(mTouchListener);
        mDescriptionEditText.setOnTouchListener(mTouchListener);
        mPriceEditText.setOnTouchListener(mTouchListener);
        mQuantityEditText.setOnTouchListener(mTouchListener);
        mProviderNameEditText.setOnTouchListener(mTouchListener);
        mProviderPhoneEditText.setOnTouchListener(mTouchListener);
        mMinusPriceImageButton.setOnTouchListener(mTouchListener);
        mPlusPriceImageButton.setOnTouchListener(mTouchListener);
        mMinusQuantityImageButton.setOnTouchListener(mTouchListener);
        mPlusQuantityImageButton.setOnTouchListener(mTouchListener);

        if (currentProductUri == null) {
            setTitle(getString(R.string.editor_activity_title_add_product));
            invalidateOptionsMenu();
            mOrderMoreButton.setVisibility(View.GONE);
        } else {
            setTitle(getString(R.string.editor_activity_title_edit_product));
            mCurrentProductUri = currentProductUri;
            // Kick off loader
            getLoaderManager().initLoader(PRODUCT_LOADER, null, this);
        }

        // Now we add an onClickListener to each add and substract buttons to modify the corresponding fields
        mMinusPriceImageButton.setOnClickListener(imageButtonClickListener(priceField, minusOne));
        mPlusPriceImageButton.setOnClickListener(imageButtonClickListener(priceField, plusOne));
        mMinusQuantityImageButton.setOnClickListener(imageButtonClickListener(quantityField, minusOne));
        mPlusQuantityImageButton.setOnClickListener(imageButtonClickListener(quantityField, plusOne));

        mOrderMoreButton.setOnClickListener(orderMoreButtonClickListener());

        mProviderPhoneEditText.addTextChangedListener(phoneNumberTextWatcher());
    }

    private View.OnClickListener imageButtonClickListener(final int field, final int action) {
        final EditText objectField;
        switch (field) {
            case priceField:
                objectField = mPriceEditText;
                break;
            case quantityField:
                objectField = mQuantityEditText;
                break;
            default:
                objectField = mPriceEditText;
        }

        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int currentValue = !objectField.getText().toString().isEmpty() ? Integer.parseInt(objectField.getText().toString()) : 0;
                currentValue += action;
                objectField.setText(Integer.toString(currentValue));
            }
        };
    }

    private View.OnClickListener orderMoreButtonClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phoneNumber = mProviderPhoneEditText.getText().toString();
                if (!phoneNumber.isEmpty()) {
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse("tel:" + phoneNumber));
                    if (intent.resolveActivity(getPackageManager()) != null) {
                        startActivity(intent);
                    }
                } else {
                    Toast.makeText(EditorActivity.this, "The provider\'s phone needs to be filled in", Toast.LENGTH_SHORT).show();
                }
            }
        };
    }

    private TextWatcher phoneNumberTextWatcher(){
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String phoneNumber = mProviderPhoneEditText.getText().toString();
                if (!phoneNumber.isEmpty()) {
                    mOrderMoreButton.setBackgroundColor(EditorActivity.this.getColor(R.color.colorEditorAccent));
                } else {
                    mOrderMoreButton.setBackgroundColor(EditorActivity.this.getColor(R.color.non_clickable));
                }
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String phoneNumber = mProviderPhoneEditText.getText().toString();
                if (!phoneNumber.isEmpty()) {
                    mOrderMoreButton.setBackgroundColor(EditorActivity.this.getColor(R.color.colorEditorAccent));
                } else {
                    mOrderMoreButton.setBackgroundColor(EditorActivity.this.getColor(R.color.non_clickable));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                String phoneNumber = mProviderPhoneEditText.getText().toString();
                if (!phoneNumber.isEmpty()) {
                    mOrderMoreButton.setBackgroundColor(EditorActivity.this.getColor(R.color.colorEditorAccent));
                } else {
                    mOrderMoreButton.setBackgroundColor(EditorActivity.this.getColor(R.color.non_clickable));
                }
            }
        };
    }

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

        String providerName = !mProviderNameEditText.getText().toString().trim().isEmpty() ? mProviderNameEditText.getText().toString().trim() : null;
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

    private void deleteProduct() {
        int deletedLines = getContentResolver().delete(mCurrentProductUri, null, null);
        if (deletedLines > 0) {
            Toast.makeText(this, getString(R.string.editor_deleted_done),
                    Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, getString(R.string.editor_deleted_none),
                    Toast.LENGTH_SHORT).show();
        }
        finish();
    }

    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Keep editing" button, so dismiss the dialog
                // and continue editing the product.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the postivie and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the product.
                deleteProduct();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the product.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public void onBackPressed() {
        // If the product hasn't changed, continue with handling back button press
        if (!mProductHasChanged) {
            super.onBackPressed();
            return;
        }

        // Otherwise if there are unsaved changes, setup a dialog to warn the user.
        // Create a click listener to handle the user confirming that changes should be discarded.
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // User clicked "Discard" button, close the current activity.
                        finish();
                    }
                };

        // Show dialog that there are unsaved changes
        showUnsavedChangesDialog(discardButtonClickListener);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        // Define a projection that specifies the columns of the table we're interested in.
        String[] projection = {
                ProductEntry._ID,
                ProductEntry.COLUMN_PRODUCT_NAME,
                ProductEntry.COLUMN_PRODUCT_DESCRIPTION,
                ProductEntry.COLUMN_PRODUCT_PRICE,
                ProductEntry.COLUMN_PRODUCT_QUANTITY,
                ProductEntry.COLUMN_PROVIDER_NAME,
                ProductEntry.COLUMN_PROVIDER_PHONE};

        return new CursorLoader(this,
                mCurrentProductUri,
                projection,
                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        // Proceed with moving to the first row of the cursor and reading data from it
        // (This should be the only row in the cursor)
        if (data.moveToFirst()) {

            int nameColumn = data.getColumnIndexOrThrow(ProductEntry.COLUMN_PRODUCT_NAME);
            int descriptionColumn = data.getColumnIndexOrThrow(ProductEntry.COLUMN_PRODUCT_DESCRIPTION);
            int priceColumn = data.getColumnIndexOrThrow(ProductEntry.COLUMN_PRODUCT_PRICE);
            int quantityColumn = data.getColumnIndexOrThrow(ProductEntry.COLUMN_PRODUCT_QUANTITY);
            int providerNameColumn = data.getColumnIndexOrThrow(ProductEntry.COLUMN_PROVIDER_NAME);
            int providerPhoneColumn = data.getColumnIndexOrThrow(ProductEntry.COLUMN_PROVIDER_PHONE);

            String nameText = data.getString(nameColumn);
            String descriptionText = data.getString(descriptionColumn);
            String priceText = Integer.toString(data.getInt(priceColumn));
            String quantityText = Integer.toString(data.getInt(quantityColumn));
            String providerNameText = data.getString(providerNameColumn);
            String providerPhoneText = data.getString(providerPhoneColumn);

            // Here we handle the data received in the Cursor
            mNameEditText.setText(nameText);
            mDescriptionEditText.setText(descriptionText);
            mPriceEditText.setText(priceText);
            mQuantityEditText.setText(quantityText);
            mProviderNameEditText.setText(providerNameText);
            mProviderPhoneEditText.setText(providerPhoneText);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mNameEditText.setText("");
        mDescriptionEditText.setText("");
        mPriceEditText.setText("");
        mQuantityEditText.setText("");
        mProviderNameEditText.setText("");
        mProviderPhoneEditText.setText("");
    }
}
