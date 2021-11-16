package com.openclassrooms.go4lunch.ui.listview;

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

import com.openclassrooms.go4lunch.databinding.FragmentListviewBinding;

public class ListviewFragment extends Fragment {

    private ListviewViewModel mListviewViewModel;
    private FragmentListviewBinding mBinding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        mListviewViewModel = new ViewModelProvider(this).get(ListviewViewModel.class);

        mBinding = FragmentListviewBinding.inflate(inflater, container, false);
        View root = mBinding.getRoot();

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBinding = null;
    }
}
