package vn.edu.stu.student.dh52112120;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.List;

import vn.edu.stu.student.dh52112120.dao.ImageDatabaseHelper;

public class MainActivity2 extends AppCompatActivity {
    private GridView gridView;
    private Button btnLoadImages;

    // Database Helper
    private ImageDatabaseHelper dbHelper;

    // List to store images
    private List<Bitmap> imageList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main2);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        dbHelper = new ImageDatabaseHelper(this);

        // Initialize UI components
        gridView = findViewById(R.id.gridView);
        btnLoadImages = findViewById(R.id.btnLoadImages);

        // Initialize image list
        imageList = new ArrayList<>();

        // Load Images Button
        btnLoadImages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadImagesFromDatabase();
            }
        });
    }
    private void loadImagesFromDatabase() {
        // Clear previous images
        imageList.clear();

        // Retrieve all images from database
        try {
            // You'll need to modify ImageDatabaseHelper to include this method
            imageList = dbHelper.getAllImages();

            if (imageList.isEmpty()) {
                Toast.makeText(this, "No images found", Toast.LENGTH_SHORT).show();
                return;
            }

            // Set up GridView adapter
            ImageAdapter adapter = new ImageAdapter();
            gridView.setAdapter(adapter);

            Toast.makeText(this, imageList.size() + " images loaded", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error loading images", Toast.LENGTH_SHORT).show();
        }
    }

    // Custom GridView Adapter
    private class ImageAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return imageList.size();
        }

        @Override
        public Object getItem(int position) {
            return imageList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView imageView;

            // Reuse or create ImageView
            if (convertView == null) {
                imageView = new ImageView(MainActivity2.this);

                // Set layout parameters for GridView
                imageView.setLayoutParams(new GridView.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        300 // Fixed height of 300 pixels
                ));

                // Image display settings
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                imageView.setPadding(8, 8, 8, 8);
            } else {
                imageView = (ImageView) convertView;
            }

            // Set the image
            imageView.setImageBitmap(imageList.get(position));

            return imageView;
        }
    }
}