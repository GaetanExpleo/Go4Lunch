package com.openclassrooms.go4lunch.ui.workmate;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.openclassrooms.go4lunch.databinding.FragmentWorkmateBinding;

public class WorkmateFragment extends Fragment {

    private WorkmateViewModel mWorkmateViewModel;
    private FragmentWorkmateBinding mBinding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        mWorkmateViewModel = new ViewModelProvider(this).get(WorkmateViewModel.class);

        mBinding = FragmentWorkmateBinding.inflate(inflater, container, false);
        View root = mBinding.getRoot();


        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBinding = null;
    }
}
