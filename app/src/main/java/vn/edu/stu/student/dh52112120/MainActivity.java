package vn.edu.stu.student.dh52112120;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.IOException;

import vn.edu.stu.student.dh52112120.dao.ImageDatabaseHelper;

public class MainActivity extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int CAMERA_CAPTURE_REQUEST = 2;

    // UI Components
    private ImageView imageView;
    private Button btnSelectFromGallery;
    private Button btnCaptureFromCamera;
    private Button btnSaveImage;
    // Selected image bitmap
    private Bitmap selectedImageBitmap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        addControls();
        addEvents();
    }

    private void addControls() {
        // Initialize UI components
        imageView = findViewById(R.id.imageView);
        btnSelectFromGallery = findViewById(R.id.btnSelectFromGallery);
        btnCaptureFromCamera = findViewById(R.id.btnCaptureFromCamera);
        btnSaveImage = findViewById(R.id.btnSaveImage);
    }

    private void addEvents() {
        btnSelectFromGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });
        btnCaptureFromCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCamera();
            }
        });
        btnSaveImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveImage();
            }
        });

    }
    // Method to open gallery for image selection
    private void openGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST);
    }

    // Method to open camera for image capture
    private void openCamera() {
//        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        startActivityForResult(cameraIntent, CAMERA_CAPTURE_REQUEST);
        Intent intent = new Intent(MainActivity.this, MainActivity2.class);
        startActivity(intent);
    }

    // Handle the result of image selection or capture
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == PICK_IMAGE_REQUEST && data != null) {
                // Image selected from gallery
                Uri selectedImageUri = data.getData();
                try {
                    selectedImageBitmap = MediaStore.Images.Media.getBitmap(
                            this.getContentResolver(), selectedImageUri);
                    imageView.setImageBitmap(selectedImageBitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show();
                }
            } else if (requestCode == CAMERA_CAPTURE_REQUEST && data != null) {
                // Image captured from camera
                selectedImageBitmap = (Bitmap) data.getExtras().get("data");
                imageView.setImageBitmap(selectedImageBitmap);
            }
        }
    }

    // Method to save the selected image
    private void saveImage() {
        if (selectedImageBitmap == null) {
            Toast.makeText(this, "No image selected", Toast.LENGTH_SHORT).show();
            return;
        }

        // Use the previously created ImageDatabaseHelper to save the image
        ImageDatabaseHelper dbHelper = new ImageDatabaseHelper(this);

        // Save image with a unique name (you can modify this logic)
        long imageId = dbHelper.addImage("image_" + System.currentTimeMillis(), selectedImageBitmap);

        if (imageId != -1) {
            Toast.makeText(this, "Image saved successfully", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Failed to save image", Toast.LENGTH_SHORT).show();
        }
    }
//    private void requestPermissions() {
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
//                != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this,
//                    new String[]{Manifest.permission.CAMERA,
//                            Manifest.permission.READ_EXTERNAL_STORAGE,
//                            Manifest.permission.WRITE_EXTERNAL_STORAGE},
//                    PERMISSION_REQUEST_CODE);
//        }
//    }
}