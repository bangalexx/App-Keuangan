package com.banglexx.money.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.banglexx.money.R;

import com.banglexx.money.activity.HomeActivity;
import com.banglexx.money.activity.LoginActivity;
import com.banglexx.money.databinding.FragmentProfileBinding;
import com.banglexx.money.retrofit.ApiEndpoint;
import com.banglexx.money.retrofit.ApiService;
import com.banglexx.money.sharedpreferences.PreferencesManager;
import com.banglexx.money.util.FormatUtil;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    private final ApiEndpoint api = ApiService.endpoint();
    private Integer userId = 0;
    private PreferencesManager pref;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        pref = new PreferencesManager(getContext());
        setupView();
        setupListener();
    }

    @Override
    public void onStart() {
        super.onStart();
        binding.imageAvatar.setImageResource( pref.getInt("pref_user_avatar") );
    }

    private void setupView(){
        binding.textName.setText( pref.getString("pref_user_name") );
        binding.textBalance.setText( getActivity().getIntent().getStringExtra("balance") );
        binding.textEmail.setText( pref.getString("pref_user_email") );
        binding.textDate.setText( FormatUtil.date( pref.getString("pref_user_date") ) );
    }

    private void setupListener(){
        binding.imageAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(ProfileFragment.this)
                        .navigate(R.id.action_profileFragment_to_avatarFragment);
            }
        });
        binding.cardLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pref.clear();
                Toast.makeText(getContext(), "Keluar", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(requireActivity(), LoginActivity.class));
                HomeActivity.instance.finish();
                requireActivity().finish();
            }
        });
    }
}