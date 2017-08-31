package com.example.android.inventoryapp.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Vicuko on 31/8/17.
 */

public final class ProductContract {

    public static final String CONTENT_AUTHORITY = "com.example.android.inventoryapp";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_PRODUCTS = "products";

    public static abstract class ProductEntry implements BaseColumns {

        public static final String TABLE_NAME = "products";

        /**
         * The content URI to access the product data in the provider
         */
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_PRODUCTS);

        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_PRODUCT_NAME = "name";
        public static final String COLUMN_PRODUCT_DESCRIPTION = "description";
        public static final String COLUMN_PRODUCT_PRICE = "price";
        public static final String COLUMN_PRODUCT_QUANTITY = "quantity";
        public static final String COLUMN_PROVIDER_NAME = "provider_name";
        public static final String COLUMN_PROVIDER_PHONE = "provider_phone";

        /*
        Initial price to be shown for new products
         */
        public static final int PRICE_INITIAL = 0;

        public static boolean isValidPrice(int price) {
            if (price >= 0) {
                return true;
            }
            return false;
        }

        public static boolean isValidQuantity(int quantity) {
            if (quantity >= 0) {
                return true;
            }
            return false;
        }
    }

}
