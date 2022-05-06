package com.openclassrooms.go4lunch.ui.listview;

import android.Manifest;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

import com.openclassrooms.go4lunch.databinding.FragmentListviewBinding;
import com.openclassrooms.go4lunch.manager.UserManager;
import com.openclassrooms.go4lunch.ui.ViewModelFactory;

import org.jetbrains.annotations.NotNull;

public class ListviewFragment extends Fragment {

    private ListviewViewModel mListviewViewModel;
    private FragmentListviewBinding mBinding;

    private ListviewAdapter mAdapter;

    private OnRestaurantClickedListener mListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        setHasOptionsMenu(true);

        mListviewViewModel = new ViewModelProvider(this, ViewModelFactory.getInstance()).get(ListviewViewModel.class);

        mBinding = FragmentListviewBinding.inflate(inflater, container, false);

        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        ActivityCompat.requestPermissions(requireActivity(),
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);
        initRecyclerview();
        initObserver();
    }

    @Override
    public void onAttach(@NonNull @NotNull Context context) {
        super.onAttach(context);
        mListener = (OnRestaurantClickedListener) context;
    }

    @Override
    public void onResume() {
        super.onResume();
        mListviewViewModel.refresh();

//        UserManager userManager = UserManager.getInstance();
//        userManager.listenDocument();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBinding = null;
    }

    @Override
    public void onPrepareOptionsMenu(@NonNull @NotNull Menu menu) {
        super.onPrepareOptionsMenu(menu);
        setMenuVisibility(true);
    }

    private void initRecyclerview() {
        mAdapter = new ListviewAdapter(mListener);
        RecyclerView recyclerView = mBinding.fragmentListviewRecyclerview;
        recyclerView.setAdapter(mAdapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL));
    }

    private void initObserver() {
        mListviewViewModel.getLocation().observe(getViewLifecycleOwner(),
                location -> mListviewViewModel.executeGetNearbyPlace(location));

        mListviewViewModel.getNearbyPlaceLiveData().observe(getViewLifecycleOwner(),
                listviewViewStates -> mAdapter.submitList(listviewViewStates));
    }
}
