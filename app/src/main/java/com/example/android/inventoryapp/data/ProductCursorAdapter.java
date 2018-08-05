package com.example.android.inventoryapp.data;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;
import com.example.android.inventoryapp.data.ProductContract.ProductEntry;
import com.example.android.inventoryapp.R;

public class ProductCursorAdapter extends CursorAdapter {
    public ProductCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    @Override
    public void bindView(View view, final Context context, final Cursor cursor) {
        TextView textViewName = view.findViewById(R.id.name);
        final TextView textViewQuantity = view.findViewById(R.id.quantity);
        TextView textViewPrice = view.findViewById(R.id.price);

        String name = cursor.getString(cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_NAME));
        int quantity = cursor.getInt(cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_QUANTITY));
        Double price = cursor.getDouble(cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_PRICE));

        Button buttonSale = view.findViewById(R.id.button_sale);
        buttonSale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int currentQuatity = cursor.getInt(cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_QUANTITY));
                long id = cursor.getLong(cursor.getColumnIndex(ProductEntry._ID));
                Uri productUri = ContentUris.withAppendedId(ProductEntry.CONTENT_URI, id);
                ContentValues values = new ContentValues();
                values.put(ProductEntry.COLUMN_PRODUCT_QUANTITY, currentQuatity - 1);
                context.getContentResolver().update(productUri, values, null, null);
            }
        });

        textViewName.setText(name);
        textViewQuantity.setText(String.valueOf(quantity) + " in stock");
        textViewPrice.setText("$"+ String.valueOf(price));

    }
}
