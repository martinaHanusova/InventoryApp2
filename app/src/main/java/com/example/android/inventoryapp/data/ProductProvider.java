package com.example.android.inventoryapp.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.android.inventoryapp.data.ProductContract.ProductEntry;


public class ProductProvider extends ContentProvider {

    private ProductDbHelper dbHelper;

    private static final int PRODUCTS = 100;
    private static final int PRODUCT_ID = 101;

    public static final String LOG_TAG = ProductProvider.class.getSimpleName();

    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        uriMatcher.addURI(ProductContract.CONTENT_AUTHORITY, ProductContract.PATH_PRODUCTS, PRODUCTS);
        uriMatcher.addURI(ProductContract.CONTENT_AUTHORITY, ProductContract.PATH_PRODUCTS + "/#", PRODUCT_ID);
    }

    @Override
    public boolean onCreate() {
        dbHelper = new ProductDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        SQLiteDatabase database = dbHelper.getReadableDatabase();

        Cursor cursor;

        final int match = uriMatcher.match(uri);
        switch (match) {
            case PRODUCTS:
                cursor = database.query(ProductEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case PRODUCT_ID:
                selection = ProductEntry._ID + "=?";
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};
                cursor = database.query(ProductEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            default: throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        final int match = uriMatcher.match(uri);
        switch (match) {
            case PRODUCTS:
                return ProductEntry.CONTENT_LIST_TYPE;
            case PRODUCT_ID:
                return ProductEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " witch match " + match);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        final int match = uriMatcher.match(uri);
        switch (match) {
            case PRODUCTS:
                return insertProduct(uri, values);
            default:
                throw  new IllegalArgumentException("Insertion is not supported for \" + uri");

        }
    }

    private Uri insertProduct(Uri uri, ContentValues values) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        String name = values.getAsString(ProductEntry.COLUMN_PRODUCT_NAME);
        if (name == null) {
            throw new IllegalArgumentException("Product requires a name");
        }
        Double price = values.getAsDouble(ProductEntry.COLUMN_PRODUCT_PRICE);
        if (price == null && price < 0) {
            throw new IllegalArgumentException("Product requires a valid price");
        }
        Integer quantity = values.getAsInteger(ProductEntry.COLUMN_PRODUCT_QUANTITY);
        if (quantity == null && quantity < 0) {
            throw new IllegalArgumentException("Product requires a valid quantity");
        }
        String supplierName = values.getAsString(ProductEntry.COLUMN_SUPPLIER_NAME);
        if (supplierName == null) {
            throw new IllegalArgumentException("Product requires a supplier's name");
        }
        String supplierPhone = values.getAsString(ProductEntry.COLUMN_SUPPLIER_PHONE_NUMBER);
        if (supplierPhone == null && supplierPhone.length() < 4 && supplierPhone.length() > 15) {
            throw new IllegalArgumentException("Product requires a valid supplier's phone");
        }
        long id = db.insert(ProductEntry.TABLE_NAME, null, values);
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for" + uri);
            return null;
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        final int match = uriMatcher.match(uri);
        int rowsDeleted;
        switch (match) {
            case PRODUCTS:
                rowsDeleted = db.delete(ProductEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case PRODUCT_ID:
                selection = ProductEntry._ID + "=?";
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = db.delete(ProductEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        final int match = uriMatcher.match(uri);
        switch (match) {
            case PRODUCTS:
                return updateProduct(uri, values, selection, selectionArgs);
            case PRODUCT_ID:
                selection = ProductEntry._ID + "=?";
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};
                return updateProduct(uri, values, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    private int updateProduct(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        if (values.containsKey(ProductEntry.COLUMN_PRODUCT_NAME)) {
            String name = values.getAsString(ProductEntry.COLUMN_PRODUCT_NAME);
            if (name == null) {
                throw new IllegalArgumentException("Product requires a name");
            }
        }
        if (values.containsKey(ProductEntry.COLUMN_PRODUCT_PRICE)) {
            Double price = values.getAsDouble(ProductEntry.COLUMN_PRODUCT_PRICE);
            if (price == null && price < 0) {
                throw new IllegalArgumentException("Product requires a valid price");
            }
        }
        if (values.containsKey(ProductEntry.COLUMN_PRODUCT_QUANTITY)) {
            Integer quantity = values.getAsInteger(ProductEntry.COLUMN_PRODUCT_QUANTITY);
            if (quantity == null && quantity < 0) {
                throw new IllegalArgumentException("Product requires a valid quantity");
            }
        }
        if (values.containsKey(ProductEntry.COLUMN_SUPPLIER_NAME)) {
            String supplierName = values.getAsString(ProductEntry.COLUMN_SUPPLIER_NAME);
            if (supplierName == null) {
                throw new IllegalArgumentException("Product requires a supplier's name");
            }
        }
        if (values.containsKey(ProductEntry.COLUMN_SUPPLIER_PHONE_NUMBER)) {
            String supplierPhone = values.getAsString(ProductEntry.COLUMN_SUPPLIER_PHONE_NUMBER);
            if (supplierPhone == null && supplierPhone.length() < 4 && supplierPhone.length() > 15) {
                throw new IllegalArgumentException("Product requires a valid supplier's phone");
            }
        }
        if (values.size() == 0) {
            return 0;
        }
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int rowsUpdated =  db.update(ProductEntry.TABLE_NAME, values, selection, selectionArgs);
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }
}
