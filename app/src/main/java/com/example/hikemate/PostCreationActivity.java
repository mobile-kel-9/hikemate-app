package com.example.hikemate;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.example.hikemate.model.PostOriginalResponse;
import com.example.hikemate.model.PostResponse;
import com.example.hikemate.network.PostsApi;
import com.example.hikemate.network.RetrofitClient;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PostCreationActivity extends AppCompatActivity {
    private EditText editTextTitle, editTextContent, editTextPlace;
    private TextView textViewFileName;
    private Uri selectedFileUri;
    private Uri cameraImageUri;
    private static final int REQUEST_TAKE_PHOTO = 2;
    private static final int REQUEST_CHOOSE_FILE = 1;
    private static final int REQUEST_CAMERA_PERMISSION = 1;
    private static int CHOOSE_FILE_FLAG = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_creation);

        editTextTitle = findViewById(R.id.editTextTitle);
        editTextContent = findViewById(R.id.editTextContent);
        editTextPlace = findViewById(R.id.editTextPlace);
        textViewFileName = findViewById(R.id.textViewFileName);

        findViewById(R.id.buttonChooseFile).setOnClickListener(v -> chooseFile());
        findViewById(R.id.buttonTakePicture).setOnClickListener(v -> takePicture());
        findViewById(R.id.buttonSubmitPost).setOnClickListener(v -> submitPost());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                takePicture();
            } else {
                Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void chooseFile() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(Intent.createChooser(intent, "Select a file"), REQUEST_CHOOSE_FILE);
        CHOOSE_FILE_FLAG = 1;
    }

    private void takePicture() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
        } else {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                File photoFile = null;
                try {
                    photoFile = createImageFile();
                } catch (IOException ex) {
                    Log.d("PostCreation", ex.toString());
                    Toast.makeText(PostCreationActivity.this, "Failed to take a picture", Toast.LENGTH_SHORT).show();
                }
                // Continue only if the File was successfully created
                if (photoFile != null) {
                    cameraImageUri = FileProvider.getUriForFile(this, "com.example.hikemate.fileprovider", photoFile);
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, cameraImageUri);
                    takePictureIntent.setFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                    startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
                }
            }
        }
    }

    private String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            if (cursor != null) {
                try {
                    int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    if (nameIndex != -1 && cursor.moveToFirst()) {
                        result = cursor.getString(nameIndex);
                    }
                } finally {
                    cursor.close();
                }
            }
        }

        // If the scheme is 'file', get the file name directly
        if (result == null) {
            result = uri.getLastPathSegment();
        }

        return result;
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        if (storageDir == null) {
            throw new IOException("External storage is not available");
        }

        File image = File.createTempFile(imageFileName, ".jpg", storageDir);

        if (image == null) {
            throw new IOException("Failed to create image file");
        }
        return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        Log.d("PostCreation", "Activity result: " + " " + requestCode + " " + resultCode + " " + data);
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CHOOSE_FILE && resultCode == RESULT_OK) {
            if (data != null) {
                selectedFileUri = data.getData();
                if (selectedFileUri != null) {
                    String fileName = getFileName(selectedFileUri);
                    textViewFileName.setText(fileName);
                }
                Log.d("PostCreation", "Selected file URI: " + selectedFileUri.toString());
                Log.d("PostCreation", "Last path segment: " + selectedFileUri.getLastPathSegment());
            }
            else {
                Log.d("PostCreation", "File tidak masuk");
            }
        } else if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            String fileName = cameraImageUri.getLastPathSegment();
            textViewFileName.setText(fileName);
            selectedFileUri = cameraImageUri;
        }
    }

    private void submitPost() {
        String title = editTextTitle.getText().toString();
        String content = editTextContent.getText().toString();
        String place = editTextPlace.getText().toString();

        if (title.isEmpty() || content.isEmpty() || place.isEmpty() || selectedFileUri == null) {
            Toast.makeText(this, "Please fill all fields and choose a file", Toast.LENGTH_SHORT).show();
            return;
        }

        Log.d("SubmitPost", "CHOOSE_FILE_FLAG: " + CHOOSE_FILE_FLAG);

        MultipartBody.Part filePart;

        if (CHOOSE_FILE_FLAG == 1) {
            filePart = createFilePart("file", selectedFileUri);
        }
        else {
            filePart = prepareFilePart("file", selectedFileUri);
        }

        RequestBody titlePart = RequestBody.create(MediaType.parse("text/plain"), title);
        RequestBody contentPart = RequestBody.create(MediaType.parse("text/plain"), content);
        RequestBody placePart = RequestBody.create(MediaType.parse("text/plain"), place);

        String token = getAccessTokenFromSharedPreferences(); // Replace with your token retrieval logic
        PostsApi api = RetrofitClient.getInstance().create(PostsApi.class);
        Call<PostOriginalResponse> call = api.insertPost("Bearer " + token, titlePart, contentPart, placePart, filePart);

        call.enqueue(new Callback<PostOriginalResponse>() {
            @Override
            public void onResponse(Call<PostOriginalResponse> call, Response<PostOriginalResponse> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(PostCreationActivity.this, "Post created successfully", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Log.d("PostCreation", response.toString());
                    Toast.makeText(PostCreationActivity.this, "Failed to create post", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<PostOriginalResponse> call, Throwable t) {
                Log.d("PostCreation", t.toString());
                Toast.makeText(PostCreationActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private MultipartBody.Part createFilePart(String partName, Uri fileUri) {
        File file = new File(fileUri.getPath());
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        return MultipartBody.Part.createFormData(partName, file.getName(), requestFile);
    }

    private MultipartBody.Part prepareFilePart(String partName, Uri fileUri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(fileUri);
            File file = new File(getCacheDir(), "temp_image.jpg"); // Temporary file in cache directory

            // Write the input stream to the file
            FileOutputStream outputStream = new FileOutputStream(file);
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            inputStream.close();
            outputStream.close();

            RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), file);
            return MultipartBody.Part.createFormData(partName, file.getName(), requestFile);
        } catch (IOException e) {
            e.printStackTrace();
            return null; // Handle the error appropriately
        }
    }

    private String getAccessTokenFromSharedPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences("AppPreferences", MODE_PRIVATE);
        return sharedPreferences.getString("accessToken", "");
    }
}
