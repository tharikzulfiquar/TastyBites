package com.example.tastybites1;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;
import com.example.tastybites1.data.FavoritesDbHelper;

public class DetailActivity extends AppCompatActivity {

    private ImageView ivDetailImage;
    private TextView tvDetailTitle, tvDetailTime;
    private Button btnGetNutrition, btnSaveFavorite;
    private RequestQueue requestQueue;


    private int recipeId;
    private int recipeMinutes;
    private String recipeTitle, recipeImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        // Find views
        ivDetailImage = findViewById(R.id.ivDetailImage);
        tvDetailTitle = findViewById(R.id.tvDetailTitle);
        tvDetailTime = findViewById(R.id.tvDetailTime);
        btnGetNutrition = findViewById(R.id.btnGetNutrition);
        btnSaveFavorite = findViewById(R.id.btnSaveFavorite);

        // Volley
        requestQueue = Volley.newRequestQueue(this);

        // Get data from Search
        recipeId = getIntent().getIntExtra(Constants.EXTRA_RECIPE_ID, -1);
        recipeTitle = getIntent().getStringExtra(Constants.EXTRA_RECIPE_TITLE);
        recipeImage = getIntent().getStringExtra(Constants.EXTRA_RECIPE_IMAGE);
        recipeMinutes = getIntent().getIntExtra(Constants.EXTRA_RECIPE_MINUTES, 30);

        // Set UI
        tvDetailTitle.setText(recipeTitle);
        tvDetailTime.setText(recipeMinutes + " mins"); // fallback
        Picasso.get().load(recipeImage).into(ivDetailImage);

        // Buttons
        btnGetNutrition.setOnClickListener(v -> getNutrition());
        btnSaveFavorite.setOnClickListener(v -> saveToFavorites());
    }

    private void getNutrition() {
        String query = recipeTitle;
        String encodedQuery = query.replace(" ", "%20");
        String url = "https://api.nal.usda.gov/fdc/v1/foods/search?query=" + encodedQuery + "&pageSize=1&api_key=DEMO_KEY";

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        JSONArray foods = response.getJSONArray("foods");
                        if (foods.length() > 0) {
                            JSONObject food = foods.getJSONObject(0);
                            JSONArray nutrients = food.getJSONArray("foodNutrients");

                            int calories = 0;
                            double protein = 0.0;
                            double fat = 0.0;

                            for (int i = 0; i < nutrients.length(); i++) {
                                JSONObject nutrient = nutrients.getJSONObject(i);
                                String name = nutrient.getString("nutrientName");

                                if (name.contains("Energy") && name.contains("kcal")) {
                                    calories = nutrient.getInt("value");
                                } else if (name.contains("Protein")) {
                                    protein = nutrient.getDouble("value");
                                } else if (name.contains("Total lipid (fat)")) {
                                    fat = nutrient.getDouble("value");
                                }
                            }

                            showNutritionDialog(calories, protein, fat);
                        } else {
                            showDummyNutrition();
                        }
                    } catch (Exception e) {
                        showDummyNutrition();
                    }
                },
                error -> showDummyNutrition()
        );

        requestQueue.add(request);
    }

    private void showNutritionDialog(int calories, double protein, double fat) {
        new AlertDialog.Builder(this)
                .setTitle("Nutrition Facts")
                .setMessage(
                        "Calories: " + calories + " kcal\n" +
                                "Protein: " + String.format("%.1f", protein) + "g\n" +
                                "Fat: " + String.format("%.1f", fat) + "g"
                )
                .setPositiveButton("OK", null)
                .show();
    }

    private void showDummyNutrition() {
        new AlertDialog.Builder(this)
                .setTitle("Nutrition (Dummy)")
                .setMessage("Calories: 250 kcal\nProtein: 20g\nFat: 10g")
                .setPositiveButton("OK", null)
                .show();
    }

    private void saveToFavorites() {
        FavoritesDbHelper dbHelper = new FavoritesDbHelper(this);
        Recipe recipe = new Recipe(recipeId, recipeTitle, recipeMinutes, recipeImage);
        long result = dbHelper.addFavorite(recipe);
        if (result > 0) {
            Toast.makeText(this, "Added to favorites", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Already in favorites", Toast.LENGTH_SHORT).show();
        }
    }

}