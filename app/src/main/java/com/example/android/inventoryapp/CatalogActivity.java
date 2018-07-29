package com.example.android.inventoryapp;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.android.inventoryapp.data.ProductContract.ProductEntry;

import com.example.android.inventoryapp.data.ProductContract;
import com.example.android.inventoryapp.data.ProductDbHelper;

public class CatalogActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        displayDatabaseInfo();
    }

    private void displayDatabaseInfo() {
        ProductDbHelper mDbHelper = new ProductDbHelper(this);

        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        String[] projection = { ProductEntry.COLUMN_PRODUCT_NAME, ProductEntry.COLUMN_PRODUCT_PRICE, ProductEntry.COLUMN_PRODUCT_QUANTITY, ProductEntry.COLUMN_SUPPLIER_NAME, ProductEntry.COLUMN_SUPPLIER_PHONE_NUMBER};
        Cursor cursor = db.query(ProductEntry.TABLE_NAME, projection, null, null, null, null, null);

        TextView displayView = findViewById(R.id.text_view_books);

        try {
            displayView.setText(
                    ProductEntry.COLUMN_PRODUCT_NAME + " - "
                    + ProductEntry.COLUMN_PRODUCT_PRICE + " - "
                    + ProductEntry.COLUMN_PRODUCT_QUANTITY + " - "
                    + ProductEntry.COLUMN_SUPPLIER_NAME + " - "
                    + ProductEntry.COLUMN_SUPPLIER_PHONE_NUMBER + "\n");

            while (cursor.moveToNext()) {
                String name = cursor.getString(cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_NAME));
                Double price = cursor.getDouble(cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_PRICE));
                int quantity = cursor.getInt(cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_QUANTITY));
                String supplierName = cursor.getString(cursor.getColumnIndex(ProductEntry.COLUMN_SUPPLIER_NAME));
                String supplierPhone = cursor.getString(cursor.getColumnIndex(ProductEntry.COLUMN_SUPPLIER_PHONE_NUMBER));

                displayView.append("\n" + name + " - " + price + " " + getString(R.string.unit_product_price) +  " - " + quantity + " " + getString(R.string.unit_product_quantity) +  " - " + " - " + supplierName + " - " + supplierPhone);
            }
        } finally {
            cursor.close();
        }
    }
}
