package com.openclassrooms.go4lunch.ui.detail;

import android.app.Application;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.openclassrooms.go4lunch.MainApplication;
import com.openclassrooms.go4lunch.R;
import com.openclassrooms.go4lunch.databinding.ItemWorkmateBinding;
import com.openclassrooms.go4lunch.model.User;

import org.jetbrains.annotations.NotNull;

public class RestaurantDetailAdapter extends ListAdapter<User, RestaurantDetailAdapter.RestaurantDetailViewHolder> {

    private ItemWorkmateBinding mBinding;
    private final Application mApplication = MainApplication.getApplication();

    protected RestaurantDetailAdapter() {
        super(diffCallback);
    }

    private static final DiffUtil.ItemCallback<User> diffCallback = new DiffUtil.ItemCallback<User>() {
        @Override
        public boolean areItemsTheSame(@NonNull @NotNull User oldItem, @NonNull @NotNull User newItem) {
            return oldItem.getUid().equals(newItem.getUid());
        }

        @Override
        public boolean areContentsTheSame(@NonNull @NotNull User oldItem, @NonNull @NotNull User newItem) {
            return oldItem.equals(newItem);
        }
    };

    @NonNull
    @NotNull
    @Override
    public RestaurantDetailViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        mBinding = ItemWorkmateBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new RestaurantDetailViewHolder(mBinding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull RestaurantDetailViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    public class RestaurantDetailViewHolder extends RecyclerView.ViewHolder {

        private final ImageView mImageView;
        private final TextView mTextView;

        public RestaurantDetailViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            mImageView = mBinding.itemWorkmateImage;
            mTextView = mBinding.itemWorkmateName;
        }

        public void bind(User item) {
            Glide.with(mApplication)
                    .load(item.getUrlPicture())
                    .apply(RequestOptions.circleCropTransform())
                    .into(mImageView);

            mTextView.setText(mApplication.getString(R.string.item_interested_workmate, item.getUsername().split(" ")[1]));
        }
    }
}
