package com.example.android.bakingtime.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.example.android.bakingtime.R;
import com.example.android.bakingtime.databinding.ItemIngredientBinding;
import com.example.android.bakingtime.fragments.DetailFragment;
import com.example.android.bakingtime.models.Ingredients;

/**
 * Created by HARUN on 9/4/2017.
 */

public class IngredientsAdapter extends RecyclerView.Adapter<IngredientsAdapter.IngredientViewHolder> {
    private static final String LOG_TAG = IngredientsAdapter.class.getSimpleName();
    private static final int TYPE_ITEM = 1;
    private Cursor mCursor;
    private final Context mContext;

    public IngredientsAdapter(Context context) {
        this.mContext = context;
    }

    @Override
    public IngredientViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        ItemIngredientBinding ingredientBinding = ItemIngredientBinding.inflate(layoutInflater, parent, false);

//        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_steps, parent, false);
//        view.setFocusable(true);
        return new IngredientViewHolder(ingredientBinding);
    }

    @Override
    public void onBindViewHolder(IngredientViewHolder holder, int position) {
        Ingredients ingredients = getIngredients(position);
        holder.bind(ingredients);
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

    public class IngredientViewHolder extends RecyclerView.ViewHolder {
        final ItemIngredientBinding binding;

        public IngredientViewHolder(ItemIngredientBinding ingredientBinding) {
            super(ingredientBinding.getRoot());
            this.binding = ingredientBinding;
            Log.w(LOG_TAG, "IngredientViewHolder: ");
        }

        public void bind(Ingredients ingredients) {
            if (ingredients != null) {
                Log.w(LOG_TAG, "bind: " + ingredients.getIngredient());
                String ingredientText = String.format(mContext.getResources().getString(R.string.ingredients), ingredients.getQuantity(), ingredients.getMeasure(), ingredients.getIngredient());
//                binding.tvItemIngredient.setText(ingredients.getQuantity()+" "+ingredients.getMeasure()+" "+ingredients.getIngredient());
                binding.tvItemIngredient.setText(ingredientText);
            }
        }

    }

    private Ingredients getIngredients(int position) {
        mCursor.moveToPosition(position);

        int quantity = mCursor.getInt(DetailFragment.COL_QUANTITY);
        String measure = mCursor.getString(DetailFragment.COL_MEASURE);
        String ingredient = mCursor.getString(DetailFragment.COL_INGREDIENT);

        return new Ingredients(quantity, measure, ingredient);
    }

}
