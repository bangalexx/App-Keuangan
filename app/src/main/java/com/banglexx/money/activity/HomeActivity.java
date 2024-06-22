package com.banglexx.money.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.banglexx.money.databinding.ActivityHomeBinding;
import com.banglexx.money.databinding.HomeAvatarBinding;
import com.banglexx.money.databinding.HomeBalanceBinding;
import com.banglexx.money.model.SubmitResponse;
import com.banglexx.money.util.FormatUtil;
import com.banglexx.money.adapter.TransactionAdapter;
import com.banglexx.money.model.TransactionResponse;
import com.banglexx.money.retrofit.ApiEndpoint;
import com.banglexx.money.retrofit.ApiService;
import com.banglexx.money.sharedpreferences.PreferencesManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends BaseActivity {

    public static Activity instance;

    private ActivityHomeBinding binding;
    private HomeAvatarBinding bindingAvatar;
    private HomeBalanceBinding bindingBalance;

    private final ApiEndpoint api = ApiService.endpoint();
    private PreferencesManager pref;
    private TransactionAdapter transactionAdapter;
    private String balance = "";

    private List<TransactionResponse.Data> dataList = new ArrayList<>();
    private final Integer filterRequestCode = 123;
    private Boolean filterTransaction = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupBinding();

        instance = this;
        pref = new PreferencesManager(this);

        setupRecyclerView();
        setupListener();

        getListTransaction();
        setAvatar();
    }

    @Override
    protected void onStart() {
        super.onStart();
        setAvatar();
        if (!filterTransaction) { getListTransaction(); }
    }

    private void setupBinding(){
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        bindingAvatar = HomeAvatarBinding.inflate(getLayoutInflater());
        bindingBalance = HomeBalanceBinding.inflate(getLayoutInflater());
        bindingAvatar = binding.layoutAvatar;
        bindingBalance = binding.layoutBalance;
        setContentView(binding.getRoot());
    }

    private void setupRecyclerView(){
        transactionAdapter = new TransactionAdapter(dataList, new TransactionAdapter.AdapterListener() {
            @Override
            public void onClick(TransactionResponse.Data data) {
                startActivity(
                        new Intent(HomeActivity.this, UpdateActivity.class)
                        .putExtra("transaction", data)
                        .putExtra("user_id", pref.getInt("pref_user_id"))
                );
            }

            @Override
            public void onLongClick(TransactionResponse.Data data) {
                AlertDialog alertDialog = new AlertDialog.Builder(HomeActivity.this).create();
                alertDialog.setTitle("Hapus");
                alertDialog.setMessage("Hapus transaksi " + data.getDescription() + "?");
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Hapus",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                deleteTransaction(data.getId());
                            }
                        });
                alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Batal",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();
            }
        });

        binding.listMoney.setAdapter(transactionAdapter);
        binding.refreshList.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getListTransaction();
            }
        });
    }

    private void setupListener(){
        binding.fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(
                        new Intent(HomeActivity.this, CreateActivity.class)
                                .putExtra("user_id", pref.getInt("pref_user_id"))
                );
            }
        });
        bindingAvatar.containerAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(
                        new Intent(HomeActivity.this, ProfileActivity.class)
                                .putExtra("balance", balance)
                );
            }
        });
        bindingBalance.imageFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                filterTransaction = true;
                startActivityForResult(
                        new Intent(HomeActivity.this, FilterActivity.class),
                        filterRequestCode
                );
            }
        });
    }

    private void getListTransaction(){
        binding.refreshList.setRefreshing(true);
        api.listTransaction(pref.getInt("pref_user_id")).enqueue(new Callback<TransactionResponse>() {
            @Override
            public void onResponse(Call<TransactionResponse> call, Response<TransactionResponse> response) {
                binding.refreshList.setRefreshing(false);
                if (response.isSuccessful()) {
                    TransactionResponse transactionList = response.body();
                    Log.e("transactionList", transactionList.toString());
                    transactionAdapter.setData( transactionList.getData() );
                    setBalance( transactionList );
                    balance = "Rp " + FormatUtil.number(transactionList.getBalance());
                }
            }
            @Override
            public void onFailure(Call<TransactionResponse> call, Throwable t) {
                binding.refreshList.setRefreshing(false);
            }
        });
    }

    private void setBalance(TransactionResponse data) {
        bindingBalance.textBalance.setText( "Rp " + FormatUtil.number( data.getBalance() ));
        bindingBalance. textIn.setText( "Rp " + FormatUtil.number( data.getTotal_in() ));
        bindingBalance.textOut.setText( "Rp " + FormatUtil.number( data.getTotal_out() ));
    }

    private void setAvatar(){
        bindingAvatar.textAvatar.setText( pref.getString("pref_user_name") );
        bindingAvatar.imageAvatar.setImageResource( pref.getInt("pref_user_avatar") );
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == filterRequestCode && resultCode == Activity.RESULT_OK) {

            String dateStart =  data.getStringExtra("date_start");
            String dateEnd =  data.getStringExtra("date_end");

            binding.refreshList.setRefreshing(true);
            api.listTransactionFilter(
                    pref.getInt("pref_user_id"),
                    dateStart,
                    dateEnd
            ).enqueue(new Callback<TransactionResponse>() {
                @Override
                public void onResponse(Call<TransactionResponse> call, Response<TransactionResponse> response) {
                    binding.refreshList.setRefreshing(false);
                    filterTransaction = false;
                    if (response.isSuccessful()) {
                        TransactionResponse moneyList = response.body();
                        transactionAdapter.setData( moneyList.getData() );
                        setBalance( moneyList );
                    }
                }
                @Override
                public void onFailure(Call<TransactionResponse> call, Throwable t) {
                    binding.refreshList.setRefreshing(false);
                    filterTransaction = false;
                }
            });
        }
    }

    private void deleteTransaction(Integer id){
        binding.refreshList.setRefreshing(true);
        api.transaction(id).enqueue(new Callback<SubmitResponse>() {
            @Override
            public void onResponse(Call<SubmitResponse> call, Response<SubmitResponse> response) {
                binding.refreshList.setRefreshing(false);
                if (response.isSuccessful()) getListTransaction();
            }

            @Override
            public void onFailure(Call<SubmitResponse> call, Throwable t) {
                binding.refreshList.setRefreshing(false);
            }
        });
    }
}