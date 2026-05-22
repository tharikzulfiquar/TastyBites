package com.example.tastybites1;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import java.util.List;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder> {

    private Context context;
    private List<Recipe> recipeList;

    public RecipeAdapter(Context context) {
        this.context = context;
        this.recipeList = new ArrayList<>();
    }

    public void setRecipes(List<Recipe> recipes) {
        this.recipeList = recipes;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_recipe, parent, false);
        return new RecipeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeViewHolder holder, int position) {
        Recipe recipe = recipeList.get(position);
        holder.tvTitle.setText(recipe.getTitle());
        holder.tvTime.setText(recipe.getReadyInMinutes() + " mins");

        // Load image with Picasso
        Picasso.get()
                .load(recipe.getImage())
                .placeholder(R.drawable.ic_cooking)
                .error(R.drawable.ic_cooking)
                .into(holder.ivRecipe);

        // Click → go to Detail
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, DetailActivity.class);
            intent.putExtra(Constants.EXTRA_RECIPE_ID, recipe.getId());
            intent.putExtra(Constants.EXTRA_RECIPE_TITLE, recipe.getTitle());
            intent.putExtra(Constants.EXTRA_RECIPE_IMAGE, recipe.getImage());
            intent.putExtra(Constants.EXTRA_RECIPE_MINUTES, recipe.getReadyInMinutes());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return recipeList.size();
    }

    static class RecipeViewHolder extends RecyclerView.ViewHolder {
        ImageView ivRecipe;
        TextView tvTitle, tvTime;

        public RecipeViewHolder(@NonNull View itemView) {
            super(itemView);
            ivRecipe = itemView.findViewById(R.id.ivRecipe);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvTime = itemView.findViewById(R.id.tvTime);
        }
    }
}