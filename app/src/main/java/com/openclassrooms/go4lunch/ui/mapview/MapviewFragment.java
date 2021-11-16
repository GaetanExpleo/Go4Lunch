package com.openclassrooms.go4lunch.ui.mapview;

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

import com.openclassrooms.go4lunch.databinding.FragmentMapviewBinding;

import org.w3c.dom.Text;

public class MapviewFragment extends Fragment {

    private MapviewViewModel mMapviewViewModel;
    private FragmentMapviewBinding mBinding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        mMapviewViewModel = new ViewModelProvider(this).get(MapviewViewModel.class);

        mBinding = FragmentMapviewBinding.inflate(inflater, container, false);
        View root = mBinding.getRoot();

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBinding = null;
    }
}
