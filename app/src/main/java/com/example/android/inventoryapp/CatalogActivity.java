package com.example.android.inventoryapp;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.example.android.inventoryapp.data.ProductContract.ProductEntry;

import com.example.android.inventoryapp.data.ProductContract;
import com.example.android.inventoryapp.data.ProductCursorAdapter;
import com.example.android.inventoryapp.data.ProductDbHelper;

public class CatalogActivity extends AppCompatActivity {

    private ProductCursorAdapter cursorAdapter;

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
        ListView listView = findViewById(R.id.list_view);
        cursorAdapter = new ProductCursorAdapter(this, null);
        listView.setAdapter(cursorAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        displayDatabaseInfo();
    }

    private void displayDatabaseInfo() {
        String[] projection = { ProductEntry._ID, ProductEntry.COLUMN_PRODUCT_NAME, ProductEntry.COLUMN_PRODUCT_PRICE, ProductEntry.COLUMN_PRODUCT_QUANTITY};
        Cursor cursor = getContentResolver().query(ProductEntry.CONTENT_URI, projection, null, null, null);
        cursorAdapter.swapCursor(cursor);
    }
}
