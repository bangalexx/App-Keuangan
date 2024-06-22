package com.banglexx.money.activity;

import androidx.core.view.ViewCompat;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.banglexx.money.R;
import com.banglexx.money.adapter.CategoryAdapter;
import com.banglexx.money.databinding.ActivityCreateBinding;
import com.banglexx.money.model.CategoryResponse;
import com.banglexx.money.model.SubmitResponse;
import com.banglexx.money.model.TransactionRequest;
import com.banglexx.money.model.TransactionResponse;
import com.banglexx.money.retrofit.ApiEndpoint;
import com.banglexx.money.retrofit.ApiService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UpdateActivity extends BaseActivity {

    private ActivityCreateBinding binding;
    TransactionResponse.Data transaction;
    private final ApiEndpoint api = ApiService.endpoint();
    private CategoryAdapter categoryAdapter;
    private List<CategoryResponse.Data> categories = new ArrayList<>();
    private Integer categoryId = 0;
    private String type = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCreateBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        transaction = (TransactionResponse.Data) getIntent().getSerializableExtra("transaction");
        Log.e("intentTransaction", transaction.toString());

        setupView();
        setupRecyclerView();
        setupListener();
        getListCategory();
    }

    private void setupView(){
        switch (transaction.getType()) {
            case "IN": type = "IN"; buttonType(binding.buttonIn);
                break;
            case "OUT": type = "OUT"; buttonType(binding.buttonOut);
                break;
        }
        binding.editDescription.setText(transaction.getDescription());
        binding.editAmount.setText(transaction.getAmount().toString());
        binding.buttonSave.setText("Simpan perubahan");
    }

    private void setupRecyclerView(){
        categoryAdapter = new CategoryAdapter(UpdateActivity.this, categories, new CategoryAdapter.AdapterListener() {
            @Override
            public void onClick(CategoryResponse.Data result) {
                categoryId = Integer.parseInt(result.getId());
            }
        });
        binding.listCategory.setAdapter( categoryAdapter );
    }

    private void buttonType(MaterialButton buttonSelected){
        List<MaterialButton> buttonList = new ArrayList<>();
        buttonList.add( binding.buttonIn );
        buttonList.add( binding.buttonOut );
        for (MaterialButton button : buttonList) {
            button.setTextColor(getResources().getColor(R.color.teal_200));
            ViewCompat.setBackgroundTintList( button, ColorStateList.valueOf(getResources().getColor(R.color.white)) );
        }
        buttonSelected.setTextColor(getResources().getColor(R.color.white));
        ViewCompat.setBackgroundTintList(
                buttonSelected, ColorStateList.valueOf(getResources().getColor(R.color.teal_700))
        );
    }

    private void setupListener(){
        binding.buttonIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonType( (MaterialButton) view );
                type = "IN";
            }
        });
        binding.buttonOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonType( (MaterialButton) view );
                type = "OUT";
            }
        });
        binding.buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateTransaction(
                        new TransactionRequest(
                                transaction.getId().toString(),
                                getIntent().getIntExtra("user_id", 0),
                                categoryId,
                                type,
                                Integer.parseInt(binding.editAmount.getText().toString()),
                                binding.editDescription.getText().toString()
                        )
                );
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
                    for (CategoryResponse.Data category: categories) {
                        if (category.getName().contains( transaction.getCategory() )) {
                            categoryAdapter.setButtonList(category);
                            categoryId = Integer.parseInt(category.getId());
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<CategoryResponse> call, Throwable t) {

            }
        });
    }

    private void updateTransaction(TransactionRequest transactionRequest){
        api.transaction(transactionRequest).enqueue(new Callback<SubmitResponse>() {
            @Override
            public void onResponse(Call<SubmitResponse> call, Response<SubmitResponse> response) {
                if (response.isSuccessful()) {
                    SubmitResponse submitResponse = response.body();
                    Toast.makeText(UpdateActivity.this, submitResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
            @Override
            public void onFailure(Call<SubmitResponse> call, Throwable t) {

            }
        });
    }
}