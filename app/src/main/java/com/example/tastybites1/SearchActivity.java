package com.example.tastybites1;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {

    private EditText etSearch;
    private Button btnSearch;
    private ProgressBar progressBar;
    private RecyclerView rvRecipes;
    private RecipeAdapter adapter;
    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        etSearch = findViewById(R.id.etSearch);
        btnSearch = findViewById(R.id.btnSearch);
        progressBar = findViewById(R.id.progressBar);
        rvRecipes = findViewById(R.id.rvRecipes);

        // Setup RecyclerView
        adapter = new RecipeAdapter(this);
        rvRecipes.setLayoutManager(new LinearLayoutManager(this));
        rvRecipes.setAdapter(adapter);

        // Volley
        requestQueue = Volley.newRequestQueue(this);

        btnSearch.setOnClickListener(v -> {
            String query = etSearch.getText().toString().trim();
            if (!query.isEmpty()) {
                searchRecipes(query);
            } else {
                Toast.makeText(this, "Enter an ingredient", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void searchRecipes(String query) {
        progressBar.setVisibility(View.VISIBLE);
        String url = "https://api.spoonacular.com/recipes/complexSearch" +
                "?query=" + query +
                "&apiKey=" + Constants.SPOONACULAR_API_KEY +
                "&number=10";

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    progressBar.setVisibility(View.GONE);
                    try {
                        JSONArray results = response.getJSONArray("results");
                        List<Recipe> recipes = new ArrayList<>();
                        for (int i = 0; i < results.length(); i++) {
                            JSONObject obj = results.getJSONObject(i);
                            int id = obj.getInt("id");
                            String title = obj.getString("title");
                            String image = obj.getString("image");
                            int minutes = obj.optInt("readyInMinutes", 30);
                            recipes.add(new Recipe(id, title, minutes, image));
                        }
                        adapter.setRecipes(recipes);
                    } catch (JSONException e) {
                        showDummyData();
                    }
                },
                error -> {
                    progressBar.setVisibility(View.GONE);
                    showDummyData();
                });

        requestQueue.add(request);
    }

    private void showDummyData() {
        List<Recipe> dummy = new ArrayList<>();
        dummy.add(new Recipe(123, "Chicken Curry (Dummy)", 45, "https://via.placeholder.com/150"));
        dummy.add(new Recipe(456, "Pasta Carbonara (Dummy)", 20, "https://via.placeholder.com/150"));
        adapter.setRecipes(dummy);
        Toast.makeText(this, "Using dummy data (no internet)", Toast.LENGTH_LONG).show();
    }
}