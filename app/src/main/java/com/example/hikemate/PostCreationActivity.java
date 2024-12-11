package com.example.hikemate;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.hikemate.model.PostOriginalResponse;
import com.example.hikemate.model.PostResponse;
import com.example.hikemate.network.PostsApi;
import com.example.hikemate.network.RetrofitClient;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_creation);

        editTextTitle = findViewById(R.id.editTextTitle);
        editTextContent = findViewById(R.id.editTextContent);
        editTextPlace = findViewById(R.id.editTextPlace);
        textViewFileName = findViewById(R.id.textViewFileName);

        findViewById(R.id.buttonChooseFile).setOnClickListener(v -> chooseFile());
        findViewById(R.id.buttonSubmitPost).setOnClickListener(v -> submitPost());
    }

    private void chooseFile() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        startActivityForResult(Intent.createChooser(intent, "Choose File"), 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            selectedFileUri = data.getData();
            String fileName = selectedFileUri.getLastPathSegment();
            textViewFileName.setText(fileName);
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

//        MultipartBody.Part filePart = createFilePart("file", selectedFileUri);
        MultipartBody.Part filePart = prepareFilePart("file", selectedFileUri);

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
            // Get the actual file path from the content URI
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

            // Create RequestBody from the file
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
