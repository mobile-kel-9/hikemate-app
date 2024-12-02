package com.example.hikemate;

import static com.example.hikemate.utils.DateUtils.formatToIso8601;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.example.hikemate.model.RegisterRequest;
import com.example.hikemate.model.RegisterResponse;
import com.example.hikemate.network.AuthApi;
import com.example.hikemate.network.RetrofitClient;
import com.google.android.material.textfield.TextInputEditText;

import java.io.IOException;
import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    private TextInputEditText nameInput, emailInput, passwordInput, dobInput, countryInput;
    private Button submitButton;
    private TextView registerText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.page_register);

        nameInput = findViewById(R.id.name_input);
        emailInput = findViewById(R.id.email_input);
        passwordInput = findViewById(R.id.password_input);
        dobInput = findViewById(R.id.dateofbirth_input);
        countryInput = findViewById(R.id.countryregion_input);
        submitButton = findViewById(R.id.submit_button);
        registerText = findViewById(R.id.register_text);

        dobInput.setOnClickListener(v -> showDatePickerDialog());

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleRegister();
            }
        });

        registerText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void showDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                RegisterActivity.this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    String formattedDate = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
                    dobInput.setText(formattedDate);
                },
                year,
                month,
                day
        );

        datePickerDialog.show();
    }

    private void handleRegister() {
//        String name = "Duljekkk";
//        String email = "duljek29@gmail.com";
//        String password = "lorem";
//        String birth_date = "2024-12-10";
//        String country = "Indonesia";
        String name = nameInput.getText().toString().trim();
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString();
        String birth_date = dobInput.getText().toString().trim();
        String country = countryInput.getText().toString().trim();

        if (name.isEmpty() || email.isEmpty() || password.isEmpty() || birth_date.isEmpty() || country.isEmpty()) {
            Toast.makeText(this, "Please fill out all fields", Toast.LENGTH_SHORT).show();
            return;
        }

//        Log.d("RegisterActivity", "Date: " + _birth_date);

//        String birth_date = formatToIso8601(_birth_date);
//        if (birth_date == null) {
//            Toast.makeText(this, "Invalid date format", Toast.LENGTH_SHORT).show();
//            return;
//        }

        Log.d("RegisterActivity", "Name: " + name);
        Log.d("RegisterActivity", "Email: " + email);
        Log.d("RegisterActivity", "Password: " + password);
        Log.d("RegisterActivity", "DOB: " + birth_date);
        Log.d("RegisterActivity", "Country: " + country);


        RegisterRequest registerRequest = new RegisterRequest(name, email, password, birth_date, country);

        Log.d("RegisterActivity", "RegisterRequest: " + registerRequest.toString());

        AuthApi authApi = RetrofitClient.getInstance().create(AuthApi.class);
        Call<RegisterResponse> call = authApi.register(registerRequest);

        call.enqueue(new Callback<RegisterResponse>() {
            @Override
            public void onResponse(Call<RegisterResponse> call, Response<RegisterResponse> response) {
                Log.d("RegisterActivity", "Response Code: " + response.code());
                Log.d("RegisterActivity", "Response Message: " + response.message());

                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(RegisterActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    try {
                        if (response.errorBody() != null) {
                            String errorBody = response.errorBody().string();
                            Log.d("RegisterActivity", "Error Body: " + errorBody);
                            Toast.makeText(RegisterActivity.this, "Error: " + errorBody, Toast.LENGTH_SHORT).show();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(RegisterActivity.this, "Failed to read error response", Toast.LENGTH_SHORT).show();
                    }
                    Toast.makeText(RegisterActivity.this, "Registration failed: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<RegisterResponse> call, Throwable t) {
                Log.d("RegisterActivity", "Error: " + t.getMessage());
                Toast.makeText(RegisterActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}

