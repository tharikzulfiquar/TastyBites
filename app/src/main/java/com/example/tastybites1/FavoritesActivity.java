package com.example.tastybites1;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tastybites1.data.FavoritesDbHelper;
import com.example.tastybites1.network.SpoonacularApi;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class FavoritesActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    ProgressBar progressBar;
    FavoriteAdapter adapter;
    List<Recipe> recipes = new ArrayList<>();
    FavoritesDbHelper dbHelper;
    SpoonacularApi api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);  // Tweaked: Use correct layout

        recyclerView = findViewById(R.id.recyclerFavorites);
        progressBar = findViewById(R.id.progressBar);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        dbHelper = new FavoritesDbHelper(this);
        adapter = new FavoriteAdapter(FavoritesActivity.this, recipes, dbHelper);  // Tweaked: Context update
        recyclerView.setAdapter(adapter);

        api = new SpoonacularApi(this);

        // Seed sample IDs with local titles if database empty
        List<Recipe> samples = new ArrayList<>();
        samples.add(new Recipe(716429, "Pasta with Garlic, Scallions, Cauliflower & Breadcrumbs", 45, null));  // Tweaked: Added minutes
        samples.add(new Recipe(715538, "What to make for dinner tonight?? Bruschetta Style Pork & Pasta", 30, null));
        samples.add(new Recipe(716406, "Asparagus and Pea Risotto", 40, null));
        samples.add(new Recipe(782601, "Red Kidney Bean Jambalaya", 50, null));
        dbHelper.seedSamplesIfEmpty(samples);

        loadFavoritesFromIds();
    }

    private void loadFavoritesFromIds() {
        progressBar.setVisibility(View.VISIBLE);
        List<Integer> ids = dbHelper.getAllFavoriteIds();
        final Map<Integer, String> titleMap = dbHelper.getFavoriteTitlesMap();
        if (ids.isEmpty()) {
            recipes.clear();
            adapter.notifyDataSetChanged();
            progressBar.setVisibility(View.GONE);
            Toast.makeText(this, "No favorites yet", Toast.LENGTH_SHORT).show();
            return;
        }

        api.fetchRecipesByIds(ids, new SpoonacularApi.RecipesCallback() {
            @Override
            public void onSuccess(List<Recipe> list) {
                recipes.clear();
                for (Recipe r : list) {
                    String dbTitle = titleMap.get(r.getId());
                    recipes.add(new Recipe(r.getId(), dbTitle != null ? dbTitle : r.getTitle(), r.getReadyInMinutes(), r.getImage()));
                }
                adapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onError(Throwable error) {
                // FIXED: Fallback to DB data with dummy image
                recipes.clear();
                List<Recipe> dbRecipes = dbHelper.getAllFavorites();
                for (Recipe r : dbRecipes) {
                    String image = r.getImage();
                    if (image == null || image.isEmpty()) {
                        image = "https://via.placeholder.com/150?text=Recipe+Image"; // Dummy
                    }
                    recipes.add(new Recipe(r.getId(), r.getTitle(), r.getReadyInMinutes(), image));
                }
                adapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
                Toast.makeText(FavoritesActivity.this, "Using local data (API quota reached)", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh favorites list when returning to this screen
        loadFavoritesFromIds();
    }
}