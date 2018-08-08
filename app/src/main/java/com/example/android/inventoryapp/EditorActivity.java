package com.example.android.inventoryapp;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.android.inventoryapp.data.ProductContract.ProductEntry;

import com.example.android.inventoryapp.data.ProductDbHelper;

public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private EditText mNameEditText;

    private EditText mPriceEditText;

    private EditText mQuantityEditText;

    private EditText mSupplierNameEditText;

    private EditText mSupplierPhoneEditText;

    private Uri currentProductUri;

    private boolean productHasChanged;

    private static final int EXISTING_PET_LOADER = 0;

    private View.OnTouchListener touchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            productHasChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        currentProductUri = getIntent().getData();
        if (currentProductUri == null) {
            setTitle(R.string.editor_activity_title_new_book);
            invalidateOptionsMenu();
        } else {
            setTitle(R.string.editor_activity_title_edit_book);
            getLoaderManager().initLoader(EXISTING_PET_LOADER, null, this);
        }

        setContentView(R.layout.activity_editor);
        mNameEditText = findViewById(R.id.edit_product_name);
        mPriceEditText = findViewById(R.id.edit_product_price);
        mQuantityEditText = findViewById(R.id.edit_product_quantity);
        mSupplierNameEditText = findViewById(R.id.edit_supplier_name);
        mSupplierPhoneEditText = findViewById(R.id.edit_supplier_phone);

        mNameEditText.setOnTouchListener(touchListener);
        mPriceEditText.setOnTouchListener(touchListener);
        mQuantityEditText.setOnTouchListener(touchListener);
        mSupplierPhoneEditText.setOnTouchListener(touchListener);
        mSupplierNameEditText.setOnTouchListener(touchListener);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (currentProductUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                saveProduct();
                return true;
            case R.id.action_delete:
                showDeleteConfirmationDialog();
                return true;
            case android.R.id.home:
                if (!productHasChanged) {
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                }
                DialogInterface.OnClickListener discardButtonClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    }
                };
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    private void saveProduct() {
        String name = mNameEditText.getText().toString().trim();
        String price = mPriceEditText.getText().toString().trim();
        String quantityString = mQuantityEditText.getText().toString().trim();
        String supplierName = mSupplierNameEditText.getText().toString().trim();
        String supplierPhone = mSupplierPhoneEditText.getText().toString().trim();
        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(supplierName) || TextUtils.isEmpty(price) || TextUtils.isEmpty(supplierName) || TextUtils.isEmpty(supplierPhone)) {
            Toast.makeText(this, R.string.editor_insert_product_required_fields, Toast.LENGTH_SHORT).show();
            return;
        } else {

            int quantity = 0;
            if (!TextUtils.isEmpty(quantityString)) {
                quantity = Integer.parseInt(quantityString);
            }

            ContentValues values = new ContentValues();
            values.put(ProductEntry.COLUMN_PRODUCT_NAME, name);
            values.put(ProductEntry.COLUMN_PRODUCT_PRICE, price);
            values.put(ProductEntry.COLUMN_PRODUCT_QUANTITY, quantity);
            values.put(ProductEntry.COLUMN_SUPPLIER_NAME, supplierName);
            values.put(ProductEntry.COLUMN_SUPPLIER_PHONE_NUMBER, supplierPhone);

            if (currentProductUri == null) {
                Uri newUri = getContentResolver().insert(ProductEntry.CONTENT_URI, values);

                if (newUri == null) {
                    Toast.makeText(this, getString(R.string.editor_insert_product_failed), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, getString(R.string.editor_insert_product_successful), Toast.LENGTH_SHORT).show();
                }
            } else {
                int updatedRows = getContentResolver().update(currentProductUri, values, null, null);
                if (updatedRows != 0) {
                    Toast.makeText(this, getString(R.string.editor_update_product_successful), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, getString(R.string.editor_update_product_failed), Toast.LENGTH_SHORT).show();
                }
            }
            finish();
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                ProductEntry._ID,
                ProductEntry.COLUMN_PRODUCT_NAME,
                ProductEntry.COLUMN_PRODUCT_QUANTITY,
                ProductEntry.COLUMN_PRODUCT_PRICE,
                ProductEntry.COLUMN_SUPPLIER_NAME,
                ProductEntry.COLUMN_SUPPLIER_PHONE_NUMBER
        };
        return new CursorLoader(this, currentProductUri, projection, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor.moveToFirst()) {
            String name = cursor.getString(cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_NAME));
            mNameEditText.setText(name);

            int quantity = cursor.getInt(cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_QUANTITY));
            mQuantityEditText.setText(String.valueOf(quantity));

            Double price = cursor.getDouble(cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_PRICE));
            mPriceEditText.setText(String.valueOf(price));

            String supplierName = cursor.getString(cursor.getColumnIndex(ProductEntry.COLUMN_SUPPLIER_NAME));
            mSupplierNameEditText.setText(supplierName);

            String supplierPhone = cursor.getString(cursor.getColumnIndex(ProductEntry.COLUMN_SUPPLIER_PHONE_NUMBER));
            mSupplierPhoneEditText.setText(supplierPhone);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mNameEditText.setText(null);
        mQuantityEditText.setText(null);
        mPriceEditText.setText(null);
        mSupplierNameEditText.setText(null);
        mSupplierPhoneEditText.setText(null);
    }

    private void showUnsavedChangesDialog(DialogInterface.OnClickListener discardButtonClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public void onBackPressed() {
        if (!productHasChanged) {
            super.onBackPressed();
            return;
        }
        DialogInterface.OnClickListener discardButtonClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        };
        showUnsavedChangesDialog(discardButtonClickListener);
    }

    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deleteProduct();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void deleteProduct() {
        if (currentProductUri != null) {
            int deletedRow = getContentResolver().delete(currentProductUri, null, null);
            if (deletedRow != 0) {
                Toast.makeText(this, getString(R.string.editor_delete_product_successful), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.editor_delete_product_failed), Toast.LENGTH_SHORT).show();
            }
        }
        setResult(RESULT_OK);
        finish();
    }
}
