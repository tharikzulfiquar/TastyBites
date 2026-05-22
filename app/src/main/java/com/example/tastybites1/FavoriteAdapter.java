package com.example.tastybites1;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tastybites1.data.FavoritesDbHelper;
import com.squareup.picasso.Picasso;  // Tweaked: Use Picasso

import java.util.List;

public class FavoriteAdapter extends RecyclerView.Adapter<FavoriteAdapter.FavViewHolder> {

    private final Context context;
    private final List<Recipe> recipeList;
    private final FavoritesDbHelper db;

    public FavoriteAdapter(Context context, List<Recipe> recipeList, FavoritesDbHelper db) {
        this.context = context;
        this.recipeList = recipeList;
        this.db = db;
    }

    @NonNull
    @Override
    public FavViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_favorite, parent, false);
        return new FavViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FavViewHolder holder, int position) {
        Recipe item = recipeList.get(position);
        holder.tvRecipeTitle.setText(item.getTitle());
        holder.tvRecipeTime.setText(item.getReadyInMinutes() + " mins");  // Tweaked: Show minutes

        // Tweaked: Use Picasso instead of Glide
        Picasso.get()
                .load(item.getImage())
                .placeholder(R.drawable.ic_cooking)  // Use your cooking icon
                .error(R.drawable.ic_cooking)        // Show on error
                .into(holder.ivRecipe);

        holder.itemView.setOnClickListener(v ->
                Toast.makeText(context, "Selected: " + item.getTitle(), Toast.LENGTH_SHORT).show()
        );

        holder.btnRemove.setOnClickListener(v -> {
            db.removeFavorite(item.getId());
            int pos = holder.getBindingAdapterPosition();
            if (pos != RecyclerView.NO_POSITION) {
                recipeList.remove(pos);
                notifyItemRemoved(pos);
            }
            Toast.makeText(context, "Removed from favorites", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public int getItemCount() {
        return recipeList.size();
    }

    public static class FavViewHolder extends RecyclerView.ViewHolder {
        ImageView ivRecipe;
        TextView tvRecipeTitle, tvRecipeTime;  // Tweaked: tvRecipeTime
        ImageButton btnRemove;

        public FavViewHolder(@NonNull View itemView) {
            super(itemView);
            ivRecipe = itemView.findViewById(R.id.ivRecipe);
            tvRecipeTitle = itemView.findViewById(R.id.tvRecipeTitle);
            tvRecipeTime = itemView.findViewById(R.id.tvRecipeTime);  // Tweaked
            btnRemove = itemView.findViewById(R.id.btnRemove);
        }
    }
}