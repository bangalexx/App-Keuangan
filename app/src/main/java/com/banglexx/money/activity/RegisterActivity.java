package com.banglexx.money.activity;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.banglexx.money.R;
import com.banglexx.money.databinding.ActivityRegisterBinding;
import com.banglexx.money.model.SubmitResponse;
import com.banglexx.money.retrofit.ApiEndpoint;
import com.banglexx.money.retrofit.ApiService;
import com.banglexx.money.retrofit.ErrorUtil;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends BaseActivity {

    private ActivityRegisterBinding binding;
    private final ApiEndpoint api = ApiService.endpoint();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
//        setContentView(R.layout.activity_register);
        setContentView(binding.getRoot());
        setupListener();
    }

    @Override
    protected void onStart() {
        super.onStart();
        showProgress(false);
    }

    private void setupListener(){
        binding.buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isRequired()) {
                    showProgress(true);
                    api.register(
                            binding.editName.getText().toString(),
                            binding.editEmail.getText().toString(),
                            binding.editPassword.getText().toString()
                    ).enqueue(new Callback<SubmitResponse>() {
                        @Override
                        public void onResponse(Call<SubmitResponse> call, Response<SubmitResponse> response) {
                            showProgress(false);
                            if (response.isSuccessful()) {
                                showMessage("Register Successfully!");
                                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                                finish();
                            } else {
                                showMessage(ErrorUtil.getMessage(response));
                            }
                        }
                        @Override
                        public void onFailure(Call<SubmitResponse> call, Throwable t) {
                            showProgress(false);
                        }
                    });
                } else {
                    showMessage("Isi data dengan benar");
                }
            }
        });
        binding.textLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                finish();
            }
        });
    }

    private Boolean isRequired() {
        return (
                binding.editName.getText() != null && binding.editEmail.getText() != null &&
                        binding.editPassword.getText() != null
        );
    }

    private void showMessage(String message) {
        Toast.makeText( RegisterActivity.this, message, Toast.LENGTH_SHORT ).show();
    }

    private void showProgress(Boolean progress) {
        if (progress) {
            binding.progress.setVisibility(View.VISIBLE);
            binding.buttonRegister.setEnabled(false);
        } else {
            binding.progress.setVisibility(View.GONE);
            binding.buttonRegister.setEnabled(true);
        }
    }
}