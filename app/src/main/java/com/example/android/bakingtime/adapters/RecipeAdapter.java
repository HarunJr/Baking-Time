package com.example.android.bakingtime.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.android.bakingtime.databinding.ItemRecipeBinding;
import com.example.android.bakingtime.fragments.RecipeFragment;
import com.example.android.bakingtime.models.Recipe;

/**
 * Created by HARUN on 9/3/2017.
 */

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder> {
    private static final String LOG_TAG = RecipeAdapter.class.getSimpleName();
    private static final int TYPE_ITEM = 0;
    private Cursor mCursor;
    private Recipe recipe;
    final private RecipeAdapterOnClickHandler mClickHandler;

    public RecipeAdapter(Context context, RecipeAdapterOnClickHandler clickHandler){
        this.mClickHandler = clickHandler;
    }

    @Override
    public RecipeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        ItemRecipeBinding recipeBinding = ItemRecipeBinding.inflate(layoutInflater, parent, false);
//        view.setFocusable(true);
        return new RecipeViewHolder(recipeBinding);
    }

    @Override
    public void onBindViewHolder(RecipeViewHolder holder, int position) {
        recipe = getRecipeData(position);
        holder.bind(recipe);
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


    public class RecipeViewHolder extends ViewHolder implements View.OnClickListener{
        final ItemRecipeBinding binding;

        public RecipeViewHolder(ItemRecipeBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

            itemView.setOnClickListener(this);
            Log.w(LOG_TAG, "RecipeViewHolder: ");
        }

        public void bind(Recipe recipe) {
            binding.tvItemRecipe.setText(recipe.getName());
            binding.executePendingBindings();
            Log.w(LOG_TAG, "bind: " + recipe.getName());
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            recipe = getRecipeData(adapterPosition);

            Log.w(LOG_TAG, "onClick: "+ recipe.getName());
            mClickHandler.onClick(recipe, this);

        }
    }

    private Recipe getRecipeData(int position) {
        mCursor.moveToPosition(position);

        int id = mCursor.getInt(RecipeFragment.COL_ID);
        String recipe_name = mCursor.getString(RecipeFragment.COL_NAME);

        return new Recipe(id, recipe_name);
    }

    public interface RecipeAdapterOnClickHandler {
        void onClick(Recipe recipe, RecipeViewHolder vh);
    }

}
