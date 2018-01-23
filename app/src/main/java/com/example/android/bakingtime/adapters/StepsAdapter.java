package com.example.android.bakingtime.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.android.bakingtime.R;
import com.example.android.bakingtime.databinding.ItemStepsBinding;
import com.example.android.bakingtime.fragments.DetailFragment;
import com.example.android.bakingtime.models.Steps;

/**
 * Created by HARUN on 9/5/2017.
 */

public class StepsAdapter extends RecyclerView.Adapter<StepsAdapter.StepsViewHolder> {
    private static final String LOG_TAG = StepsAdapter.class.getSimpleName();
    private static final int TYPE_ITEM = 2;
    private Cursor mCursor;
    private final Context mContext;
    final private StepsAdapterOnClickHandler mClickHandler;
    private int selectedPosition = 0;
    private Steps steps;

    public StepsAdapter(Context context, StepsAdapterOnClickHandler clickHandler) {
        this.mClickHandler = clickHandler;
        this.mContext = context;
    }

    @Override
    public StepsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        ItemStepsBinding stepsBinding = ItemStepsBinding.inflate(layoutInflater, parent, false);

//        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_steps, parent, false);
//        view.setFocusable(true);
        return new StepsViewHolder(stepsBinding);
    }

    @Override
    public void onBindViewHolder(StepsViewHolder holder, int position) {
        steps = getSteps(position);
        holder.bind(steps, position);
    }

    @Override
    public int getItemCount() {
        if (null == mCursor) return 0;

        Log.w(LOG_TAG, "getItemCount: " + mCursor.getCount());
        return mCursor.getCount();
    }

    @Override
    public int getItemViewType(int position) {
        Log.w(LOG_TAG, "getItemViewType: " + position);
        return TYPE_ITEM;
    }

    public void swapCursor(Cursor newCursor) {
        mCursor = newCursor;
        notifyDataSetChanged();
//        mEmptyView.setVisibility(getItemCount() == 0 ? View.VISIBLE : View.GONE);
    }

    public class StepsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        final ItemStepsBinding binding;
//        private final TextView textView;

        public StepsViewHolder(ItemStepsBinding itemStepsBinding) {
            super(itemStepsBinding.getRoot());
            this.binding = itemStepsBinding;

//            textView = (TextView) itemView.findViewById(R.id.tv_item_steps);
            itemView.setOnClickListener(this);
            Log.w(LOG_TAG, "StepsViewHolder: ");
        }

        void bind(Steps steps, int position) {
//            steps.setSize(getItemCount());
//            Log.w(LOG_TAG, "bind stepsCount: "+getItemCount());

            if (selectedPosition == position){
                binding.tvItemSteps.setTextColor(ContextCompat.getColor(mContext, R.color.colorWhite));
                itemView.setBackgroundColor(ContextCompat.getColor(mContext, R.color.colorAccent));
//                textView.setTextColor(ContextCompat.getColor(mContext, R.color.colorWhite));
            }else {
                itemView.setBackgroundColor(ContextCompat.getColor(mContext, R.color.colorWhite));
            }

            Log.w(LOG_TAG, "bind: " + steps.getShortDescription());
            String stepsText = String.format(mContext.getResources().getString(R.string.steps), steps.getId(), steps.getShortDescription());
            binding.tvItemSteps.setText(stepsText);
        }


        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            selectedPosition = adapterPosition;
            notifyDataSetChanged();

            steps = getSteps(adapterPosition);
            steps.setSize(getItemCount());
            Log.w(LOG_TAG, "onClick: "+ steps.getShortDescription());
            mClickHandler.onClick(steps, this);
        }

    }

    private Steps getSteps(int position) {
        mCursor.moveToPosition(position);

        int id = mCursor.getInt(DetailFragment.COL_STEP_ID);
        String shortDesc = mCursor.getString(DetailFragment.COL_SHORT_DESC);
        String description = mCursor.getString(DetailFragment.COL_DESC);
        String videoUrl = mCursor.getString(DetailFragment.COL_VIDEO_URL);
        String thumbnailUrl = mCursor.getString(DetailFragment.COL_THUMBNAIL_URL);

        return new Steps(id, shortDesc, description, videoUrl, thumbnailUrl);
    }


    public interface StepsAdapterOnClickHandler {
        void onClick(Steps steps, StepsViewHolder vh);
    }

}

