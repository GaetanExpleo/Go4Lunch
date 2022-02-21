package com.openclassrooms.go4lunch.ui.workmate;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;
import com.openclassrooms.go4lunch.databinding.FragmentWorkmateBinding;
import com.openclassrooms.go4lunch.model.User;
import com.openclassrooms.go4lunch.ui.ViewModelFactory;

import org.jetbrains.annotations.NotNull;

public class WorkmateFragment extends Fragment {

    private WorkmateViewModel mWorkmateViewModel;
    private FragmentWorkmateBinding mBinding;

    private OnWorkmateClickedListener mListener;

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        mWorkmateViewModel = new ViewModelProvider(this, ViewModelFactory.getInstance()).get(WorkmateViewModel.class);

        mBinding = FragmentWorkmateBinding.inflate(inflater, container, false);

        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        this.initRecyclerView();
    }

    @Override
    public void onAttach(@NonNull @NotNull Context context) {
        super.onAttach(context);
        mListener = (OnWorkmateClickedListener) context;
    }

    @Override
    public void onPrepareOptionsMenu(@NonNull @NotNull Menu menu) {
        super.onPrepareOptionsMenu(menu);
        setMenuVisibility(false);
    }

    private void initRecyclerView() {
        RecyclerView recyclerView = mBinding.fragmentWorkmateRecyclerview;
        WorkmateAdapter adapter = new WorkmateAdapter(
                generateOptionsForAdapter(mWorkmateViewModel.getAllWorkmates()), mListener);
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL));
    }

    private FirestoreRecyclerOptions<User> generateOptionsForAdapter(Query allWorkmates) {
        return new FirestoreRecyclerOptions.Builder<User>()
                .setQuery(allWorkmates, User.class)
                .setLifecycleOwner(getViewLifecycleOwner())
                .build();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBinding = null;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull @NotNull Menu menu, @NonNull @NotNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }
}
