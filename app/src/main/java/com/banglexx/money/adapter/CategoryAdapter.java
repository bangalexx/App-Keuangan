package com.banglexx.money.adapter;

import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.banglexx.money.R;
import com.banglexx.money.databinding.AdapterCategoryBinding;
import com.banglexx.money.model.CategoryResponse;

import java.util.ArrayList;
import java.util.List;

import static android.os.Looper.getMainLooper;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {

    private List<CategoryResponse.Data> results;
    private AdapterListener listener;

    List<MaterialButton> buttonList = new ArrayList<>();
    private Context context;

    public CategoryAdapter(Context context, List<CategoryResponse.Data> results, AdapterListener listener) {
        this.context    = context ;
        this.results    = results ;
        this.listener   = listener ;
    }

    @NonNull
    @Override
    public CategoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        return new ViewHolder(
                AdapterCategoryBinding
                        .inflate(LayoutInflater.from(parent.getContext()), parent, false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryAdapter.ViewHolder viewHolder, int i) {
        final CategoryResponse.Data data = results.get(i);
        viewHolder.binding.buttonCategory.setText( data.getName() );
        buttonList.add(viewHolder.binding.buttonCategory);
        viewHolder.binding.buttonCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onClick(data);
                setButtonList(viewHolder.binding.buttonCategory);
            }
        });
    }

    @Override
    public int getItemCount() {
        return results.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        AdapterCategoryBinding binding;
        public ViewHolder(AdapterCategoryBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    public void setData(List<CategoryResponse.Data> data) {
        results.clear();
        results.addAll(data);
        notifyDataSetChanged();
    }

    public interface AdapterListener {
        void onClick(CategoryResponse.Data result);
    }

    private void setButtonList(MaterialButton button){
        for( MaterialButton buttons : buttonList){
            buttons.setTextColor(context.getResources().getColor(R.color.teal_200));
            ViewCompat.setBackgroundTintList(
                    buttons, ColorStateList.valueOf(context.getResources().getColor(R.color.white))
            );
        }
        button.setTextColor(context.getResources().getColor(R.color.white));
        ViewCompat.setBackgroundTintList(
                button, ColorStateList.valueOf(context.getResources().getColor(R.color.teal_700))
        );
    }

    public void setButtonList(CategoryResponse.Data category){
        new Handler(getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                for( MaterialButton buttons : buttonList){
                    Log.e("setButtonList", "" + buttons.getText());
                    if (buttons.getText().toString().contains( category.getName() )) {
                        buttons.setTextColor(context.getResources().getColor(R.color.white));
                        ViewCompat.setBackgroundTintList(
                                buttons, ColorStateList.valueOf(context.getResources().getColor(R.color.teal_700))
                        );
                    }
                }
            }
        }, 500);
    }
}