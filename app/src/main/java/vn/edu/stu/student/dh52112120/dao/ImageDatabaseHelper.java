package vn.edu.stu.student.dh52112120.dao;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class ImageDatabaseHelper extends SQLiteOpenHelper {
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "ImageDB";

    // Table Name
    private static final String TABLE_IMAGES = "images";

    // Table Columns
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_IMAGE = "image";

    public ImageDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Create table
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_IMAGES_TABLE = "CREATE TABLE " + TABLE_IMAGES + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_NAME + " TEXT,"
                + KEY_IMAGE + " BLOB)";
        db.execSQL(CREATE_IMAGES_TABLE);
    }

    // Upgrade database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_IMAGES);

        // Create tables again
        onCreate(db);
    }

    // Method to convert Bitmap to byte array
    private byte[] getBitmapAsByteArray(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, outputStream);
        return outputStream.toByteArray();
    }

    // Method to convert byte array back to Bitmap
    private Bitmap getByteArrayAsBitmap(byte[] byteArray) {
        return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
    }

    // Add image to database
    public long addImage(String name, Bitmap image) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, name);
        values.put(KEY_IMAGE, getBitmapAsByteArray(image));

        // Inserting Row
        long id = db.insert(TABLE_IMAGES, null, values);
        db.close(); // Closing database connection

        return id;
    }

    // Get image by name
    public Bitmap getImageByName(String name) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_IMAGES,
                new String[]{KEY_IMAGE},
                KEY_NAME + "=?",
                new String[]{name},
                null, null, null, null);

        Bitmap image = null;
        if (cursor != null && cursor.moveToFirst()) {
            byte[] imageBlob = cursor.getBlob(cursor.getColumnIndex(KEY_IMAGE));
            image = getByteArrayAsBitmap(imageBlob);
            cursor.close();
        }

        db.close();
        return image;
    }

    // Delete image by name
    public void deleteImage(String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_IMAGES, KEY_NAME + " = ?", new String[]{name});
        db.close();
    }

    // Update image
    public int updateImage(String name, Bitmap newImage) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_IMAGE, getBitmapAsByteArray(newImage));

        // Updating row
        return db.update(TABLE_IMAGES,
                values,
                KEY_NAME + " = ?",
                new String[]{name});
    }
    public List<Bitmap> getAllImages() {
        List<Bitmap> images = new ArrayList<>();

        // Select All Query
        String selectQuery = "SELECT * FROM " + TABLE_IMAGES;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // Looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                // Get the image blob
                byte[] imageBlob = cursor.getBlob(
                        cursor.getColumnIndex(KEY_IMAGE)
                );

                // Convert blob to bitmap
                Bitmap image = getByteArrayAsBitmap(imageBlob);

                // Add to list
                images.add(image);
            } while (cursor.moveToNext());
        }

        // Close cursor and database
        cursor.close();
        db.close();

        return images;
    }

    // Optional: Method to get image names as well if needed
    public List<String> getAllImageNames() {
        List<String> imageNames = new ArrayList<>();

        String selectQuery = "SELECT " + KEY_NAME + " FROM " + TABLE_IMAGES;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                imageNames.add(cursor.getString(
                        cursor.getColumnIndex(KEY_NAME)
                ));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return imageNames;
    }
}
