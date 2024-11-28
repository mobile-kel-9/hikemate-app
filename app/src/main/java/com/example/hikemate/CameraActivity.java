package com.example.hikemate;

import android.Manifest;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.hikemate.R;

public class CameraActivity extends AppCompatActivity {

    private static final int CAMERA_PERMISSION_REQUEST_CODE = 100;
    private FrameLayout cameraPreview;
    private ImageView imageViewGallery;
    private ImageView imageViewCamera;
    private ImageView imageViewFlash;
    private Camera camera;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.page_camera);

        cameraPreview = findViewById(R.id.camera_preview);
        imageViewGallery = findViewById(R.id.imageViewGallery);
        imageViewCamera = findViewById(R.id.imageViewCamera);
        imageViewFlash = findViewById(R.id.imageViewFlash);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST_CODE);
        } else {
            startCamera();
        }

        imageViewGallery.setOnClickListener(v -> {
            Toast.makeText(this, "Gallery clicked", Toast.LENGTH_SHORT).show();
        });

        imageViewCamera.setOnClickListener(v -> {
            Toast.makeText(this, "Camera clicked", Toast.LENGTH_SHORT).show();
        });

        imageViewFlash.setOnClickListener(v -> {
            Toast.makeText(this, "Flash clicked", Toast.LENGTH_SHORT).show();
        });
    }

    private void startCamera() {
        camera = Camera.open();
        CameraPreview preview = new CameraPreview(this, camera);
        cameraPreview.addView(preview);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startCamera();
            } else {
                Toast.makeText(this, "Camera permission is required", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (camera != null) {
            camera.release();
            camera = null;
        }
    }
}