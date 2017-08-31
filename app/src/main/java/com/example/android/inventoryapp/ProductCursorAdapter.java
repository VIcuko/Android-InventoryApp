package com.example.android.inventoryapp;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.inventoryapp.data.ProductContract.ProductEntry;

import static android.R.attr.id;

/**
 * Created by Vicuko on 31/8/17.
 */

public class ProductCursorAdapter extends CursorAdapter {

    public ProductCursorAdapter(Context context, Cursor c) {
        super(context, c);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    @Override
    public void bindView(View view, final Context context, final Cursor cursor) {

        TextView productName = (TextView) view.findViewById(R.id.item_product_name);
        TextView productPrice = (TextView) view.findViewById(R.id.item_product_price);
        final TextView productQuantity = (TextView) view.findViewById(R.id.item_product_quantity);

        final Button saleButton = (Button) view.findViewById(R.id.sale_button);

        // Set listener to reduce by 1 the quantity when clicked
        saleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int quantityLeft = Integer.parseInt(productQuantity.getText().toString());
                if (quantityLeft > 0) {
                    quantityLeft -= 1;
                    ContentValues values = new ContentValues();
                    values.put(ProductEntry.COLUMN_PRODUCT_QUANTITY, quantityLeft);

                    int cursor_id = cursor.getInt(cursor.getColumnIndexOrThrow(ProductEntry._ID));

                    Uri currentUri = ContentUris.withAppendedId(ProductEntry.CONTENT_URI, cursor_id);
                    int updatedLines = context.getContentResolver().update(currentUri, values, null, null);
                    if (updatedLines>0){
                        Log.i("Positive updated lines","Lines updated: "+updatedLines);
                    }
                    else{
                        Log.i("Negative updated lines","Lines updated: "+updatedLines);
                    }

                } else {
                    saleButton.setBackgroundColor(context.getColor(R.color.non_clickable));
                    Toast.makeText(context, "There aren\'t any more units left", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Extract properties from cursor
        String name = cursor.getString(cursor.getColumnIndexOrThrow(ProductEntry.COLUMN_PRODUCT_NAME));
        int price = cursor.getInt(cursor.getColumnIndexOrThrow(ProductEntry.COLUMN_PRODUCT_PRICE));
        int quantity = cursor.getInt(cursor.getColumnIndexOrThrow(ProductEntry.COLUMN_PRODUCT_QUANTITY));

        productName.setText(name);
        productQuantity.setText(Integer.toString(quantity));

        if (price == 0) {
            productPrice.setText(R.string.free_product);
        } else {
            productPrice.setText(Integer.toString(price) + " " + context.getString(R.string.currency));
        }
    }
}
