package com.openclassrooms.go4lunch.ui.listview;

import android.app.Application;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.openclassrooms.go4lunch.MainApplication;
import com.openclassrooms.go4lunch.R;
import com.openclassrooms.go4lunch.databinding.ItemListViewBinding;

import org.jetbrains.annotations.NotNull;

public class ListviewAdapter extends ListAdapter<ListviewViewState, ListviewAdapter.ListviewViewHolder> {

    @NonNull
    private final OnRestaurantClickedListener mListener;

    private ItemListViewBinding mBinding;
    private final Application mApplication = MainApplication.getApplication();

    public ListviewAdapter(@NonNull OnRestaurantClickedListener listener) {
        super(diffCallback);
        mListener = listener;
    }

    private static final DiffUtil.ItemCallback<ListviewViewState> diffCallback = new DiffUtil.ItemCallback<ListviewViewState>() {
        @Override
        public boolean areItemsTheSame(@NonNull @NotNull ListviewViewState oldItem, @NonNull @NotNull ListviewViewState newItem) {
            return oldItem.getName().equals(newItem.getName());
        }

        @Override
        public boolean areContentsTheSame(@NonNull @NotNull ListviewViewState oldItem, @NonNull @NotNull ListviewViewState newItem) {
            return oldItem.equals(newItem);
        }
    };

    @NonNull
    @NotNull
    @Override
    public ListviewViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        mBinding = ItemListViewBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ListviewViewHolder(mBinding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ListviewViewHolder holder, int position) {
        holder.bind(getItem(position), mListener);
    }

    public class ListviewViewHolder extends RecyclerView.ViewHolder {

        private final ImageView mImageView;
        private final TextView mNameTextView;
        private final TextView mAddressTextView;
        private final TextView mOpenTextView;
        private final TextView mDistanceTextView;
        private final TextView mWorkmateTextView;
        private final RatingBar mRatingBar;

        private final Resources mResources = MainApplication.getApplication().getResources();

        public ListviewViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            mImageView = mBinding.itemListViewImage;
            mNameTextView = mBinding.itemListViewTitle;
            mAddressTextView = mBinding.itemListViewAddress;
            mOpenTextView = mBinding.itemListViewOpeningTime;
            mDistanceTextView = mBinding.itemListViewDistance;
            mWorkmateTextView = mBinding.itemListViewColleagueInterestedNumber;
            mRatingBar = mBinding.itemListViewRatingBar;
        }

        public void bind(ListviewViewState item, OnRestaurantClickedListener listener) {
            Glide.with(mApplication)
                    .load(item.getPhoto())
                    .into(mImageView);

            mNameTextView.setText(item.getName());
            mAddressTextView.setText(item.getAddress());

            if (item.isOpen().equals(MainApplication.getApplication().getResources().getString(R.string.opening_hour_opened))) {
                mOpenTextView.setTextColor(mResources.getColor(R.color.opened));
                mOpenTextView.setTypeface(mOpenTextView.getTypeface(), Typeface.NORMAL);
            } else if (item.isOpen().equals(MainApplication.getApplication().getResources().getString(R.string.opening_hour_closed))) {
                mOpenTextView.setTextColor(mResources.getColor(R.color.closed));
                mOpenTextView.setTypeface(mOpenTextView.getTypeface(), Typeface.BOLD);
            } else {
                mOpenTextView.setTextColor(mResources.getColor(R.color.grey));
                mOpenTextView.setTypeface(mOpenTextView.getTypeface(), Typeface.ITALIC);
            }
            mOpenTextView.setText(item.isOpen());

            mDistanceTextView.setText(item.getDistance());
            mWorkmateTextView.setText(item.getWorkmatesInterested());
            if (item.getRate() == 0) {
                mRatingBar.setVisibility(View.INVISIBLE);
            } else {
                mRatingBar.setVisibility(View.VISIBLE);
                mRatingBar.setRating(item.getRate());
                mRatingBar.setNumStars((int) Math.ceil(item.getRate()));
            }
            itemView.setOnClickListener(v ->
                    listener.onRestaurantClicked(item.getPlaceId()));
        }
    }
}
