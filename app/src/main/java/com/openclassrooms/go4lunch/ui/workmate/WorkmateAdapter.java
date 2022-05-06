package com.openclassrooms.go4lunch.ui.workmate;

import android.app.Application;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.openclassrooms.go4lunch.MainApplication;
import com.openclassrooms.go4lunch.R;
import com.openclassrooms.go4lunch.databinding.ItemWorkmateBinding;
import com.openclassrooms.go4lunch.model.User;

import org.jetbrains.annotations.NotNull;

public class WorkmateAdapter extends FirestoreRecyclerAdapter<User, WorkmateAdapter.WorkmateViewHolder> {

    @NonNull
    private final OnWorkmateClickedListener mListener;

    private ItemWorkmateBinding mBinding;
    private final Application mApplication = MainApplication.getApplication();

    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     * @param listener
     */
    public WorkmateAdapter(@NonNull @NotNull FirestoreRecyclerOptions<User> options, @NonNull OnWorkmateClickedListener listener) {
        super(options);
        mListener = listener;
    }

    @Override
    protected void onBindViewHolder(@NonNull @NotNull WorkmateViewHolder holder, int position, @NonNull @NotNull User model) {
        holder.bind(model, mListener);
    }

    @NonNull
    @NotNull
    @Override
    public WorkmateViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        mBinding = ItemWorkmateBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new WorkmateViewHolder(mBinding.getRoot());
    }

    public class WorkmateViewHolder extends RecyclerView.ViewHolder {

        private final ImageView mImageView;
        private final TextView mTextView;

        public WorkmateViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            mImageView = mBinding.itemWorkmateImage;
            mTextView = mBinding.itemWorkmateName;
        }

        public void bind(User user, OnWorkmateClickedListener listener) {
            Glide.with(mApplication)
                    .load(user.getUrlPicture())
                    .apply(RequestOptions.circleCropTransform())
                    .into(mImageView);

            String name = user.getUsername().split(" ")[1];
            if (TextUtils.isEmpty(user.getChosenRestaurant().getPlaceId())) {
                mTextView.setText(mApplication.getString(R.string.workmate_not_decided, name));
            } else {
                mTextView.setText(mApplication.getString(R.string.workmate_chose_restaurant, name, user.getChosenRestaurant().getPlaceName()));
                mTextView.setTextColor(mApplication.getResources().getColor(R.color.black));
            }

            itemView.setOnClickListener(v -> listener.onWorkmateClicked(user.getChosenRestaurant().getPlaceId()));
        }
    }
}
