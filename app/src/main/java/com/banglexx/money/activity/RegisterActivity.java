package com.banglexx.money.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Patterns;
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
        setContentView(binding.getRoot());
        setupListener();
        setupPasswordWatcher();
        setupEmailWatcher();
    }

    @Override
    protected void onStart() {
        super.onStart();
        showProgress(false);
    }

    private void setupListener() {
        binding.buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isRequired()) {
                    String password = binding.editPassword.getText().toString();
                    String email = binding.editEmail.getText().toString();
                    if (!isValidEmail(email)) {
                        showMessage("Email tidak valid");
                    } else if (password.length() < 6) {
                        showMessage("Password harus terdiri dari minimal 6 karakter");
                    } else {
                        showProgress(true);
                        api.register(
                                binding.editName.getText().toString(),
                                email,
                                password
                        ).enqueue(new Callback<SubmitResponse>() {
                            @Override
                            public void onResponse(Call<SubmitResponse> call, Response<SubmitResponse> response) {
                                showProgress(false);
                                if (response.isSuccessful()) {
                                    showMessage("Berhasil Membuat Akun");
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
                    }
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

    private void setupPasswordWatcher() {
        binding.editPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Tidak perlu melakukan apa pun di sini
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() < 6) {
                    binding.editPassword.setError("Password harus terdiri dari minimal 6 karakter");
                } else {
                    binding.editPassword.setError(null); // Hapus error jika panjang password cukup
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Tidak perlu melakukan apa pun di sini
            }
        });
    }

    private void setupEmailWatcher() {
        binding.editEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Tidak perlu melakukan apa pun di sini
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!isValidEmail(s.toString())) {
                    binding.editEmail.setError("Format email tidak valid");
                } else {
                    binding.editEmail.setError(null); // Hapus error jika format email valid
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Tidak perlu melakukan apa pun di sini
            }
        });
    }

    private boolean isValidEmail(CharSequence email) {
        return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private Boolean isRequired() {
        return (
                binding.editName.getText() != null && binding.editEmail.getText() != null &&
                        binding.editPassword.getText() != null
        );
    }

    private void showMessage(String message) {
        Toast.makeText(RegisterActivity.this, message, Toast.LENGTH_SHORT).show();
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
