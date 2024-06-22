package com.banglexx.money.activity;

import androidx.core.view.ViewCompat;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.banglexx.money.R;
import com.banglexx.money.adapter.CategoryAdapter;
import com.banglexx.money.databinding.ActivityCreateBinding;
import com.banglexx.money.model.CategoryResponse;
import com.banglexx.money.model.SubmitResponse;
import com.banglexx.money.retrofit.ApiEndpoint;
import com.banglexx.money.retrofit.ApiService;


import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateActivity extends BaseActivity {
    private final String TAG = CreateActivity.class.getSimpleName();

    private ActivityCreateBinding binding;
    private final ApiEndpoint api = ApiService.endpoint();
    private CategoryAdapter categoryAdapter;
    private List<CategoryResponse.Data> categories = new ArrayList<>();
    private Integer userId;
    private String categoryId = "";
    private String type = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCreateBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        userId = getIntent().getIntExtra("user_id", 0);

        setupView();
        setupRecyclerView();
        setupListener();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }

    @Override
    protected void onStart() {
        super.onStart();
        getListCategory();
    }

    private void setupView(){
        getSupportActionBar().hide();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void setupRecyclerView(){
        categoryAdapter = new CategoryAdapter(CreateActivity.this, categories, new CategoryAdapter.AdapterListener() {
            @Override
            public void onClick(CategoryResponse.Data result) {
                categoryId = result.getId();
            }
        });
        binding.listCategory.setAdapter( categoryAdapter );
    }

    private void setupListener(){
        binding.buttonIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.buttonIn.setTextColor(getResources().getColor(R.color.white));
                ViewCompat.setBackgroundTintList(
                        binding.buttonIn, ColorStateList.valueOf(getResources().getColor(R.color.teal_700))
                );
                binding.buttonOut.setTextColor(getResources().getColor(R.color.teal_200));
                ViewCompat.setBackgroundTintList(
                        binding.buttonOut, ColorStateList.valueOf(getResources().getColor(R.color.white))
                );
                type = "IN";
            }
        });
        binding.buttonOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.buttonOut.setTextColor(getResources().getColor(R.color.white));
                ViewCompat.setBackgroundTintList(
                        binding.buttonOut, ColorStateList.valueOf(getResources().getColor(R.color.teal_700))
                );
                binding.buttonIn.setTextColor(getResources().getColor(R.color.teal_200));
                ViewCompat.setBackgroundTintList(
                        binding.buttonIn, ColorStateList.valueOf(getResources().getColor(R.color.white))
                );
                type = "OUT";
            }
        });
        binding.buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isRequired()) {
                    binding.buttonSave.setEnabled( false );
                    api.transaction(
                            userId,
                            Integer.parseInt( categoryId ),
                            binding.editDescription.getText().toString(),
                            Long.parseLong( binding.editAmount.getText().toString() ),
                            type
                    ).enqueue(new Callback<SubmitResponse>() {
                        @Override
                        public void onResponse(Call<SubmitResponse> call, Response<SubmitResponse> response) {
                            binding.buttonSave.setEnabled( true );
                            if (response.isSuccessful()) {
                                Toast.makeText(getApplicationContext(), "Transaksi berhasil disimpan!",
                                        Toast.LENGTH_SHORT).show();
                                onBackPressed();
                            }
                        }

                        @Override
                        public void onFailure(Call<SubmitResponse> call, Throwable t) {
                            binding.buttonSave.setEnabled( true );
                        }
                    });
                }
            }
        });
    }

    private void getListCategory(){
        api.listCategory().enqueue(new Callback<CategoryResponse>() {
            @Override
            public void onResponse(Call<CategoryResponse> call, Response<CategoryResponse> response) {
                if (response.isSuccessful()) {
                    categories = response.body().getData();
                    categoryAdapter.setData( categories );
                }
            }

            @Override
            public void onFailure(Call<CategoryResponse> call, Throwable t) {

            }
        });
    }

    private Boolean isRequired() {
        if (type.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Tentukan tipe transaksi masuk atau keluar",
                    Toast.LENGTH_SHORT).show();
            return false;
        } else if (categoryId.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Kategori transaksi tidak boleh kosong",
                    Toast.LENGTH_SHORT).show();
            return false;
        } else if (binding.editAmount.getText().toString().isEmpty()) {
            binding.editAmount.setError("Masukkan jumlah transaksi");
            return false;
        } else if (binding.editDescription.getText().toString().isEmpty()) {
            binding.editDescription.setError("Masukkan keterangan transaksi");
            return false;
        }

        return true;
    }
}