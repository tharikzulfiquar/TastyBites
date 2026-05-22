package com.example.tastybites1.network;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.tastybites1.Constants;
import com.example.tastybites1.Recipe;

import org.json.JSONArray;
import org.json.JSONObject;
import com.android.volley.toolbox.JsonArrayRequest;

import java.util.ArrayList;
import java.util.List;

// Acknowledged: Adapted from Spoonacular docs[](https://spoonacular.com/food-api/docs#Get-Recipe-Information-Bulk) and Lab 9 (Volley)
public class SpoonacularApi {

    private RequestQueue queue;

    public SpoonacularApi(Context context) {
        queue = Volley.newRequestQueue(context);
    }

    public interface RecipesCallback {
        void onSuccess(List<Recipe> list);
        void onError(Throwable error);
    }

    public void fetchRecipesByIds(List<Integer> ids, RecipesCallback callback) {
        if (ids.isEmpty()) {
            callback.onSuccess(new ArrayList<>());
            return;
        }

        String idStr = android.text.TextUtils.join(",", ids);
        String url = "https://api.spoonacular.com/recipes/informationBulk?ids=" + idStr + "&apiKey=" + Constants.SPOONACULAR_API_KEY;

        // Use JsonArrayRequest because response is a JSON array
        com.android.volley.toolbox.JsonArrayRequest request = new com.android.volley.toolbox.JsonArrayRequest(
                Request.Method.GET, url, null,
                response -> {
                    try {
                        List<Recipe> recipes = new ArrayList<>();
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject obj = response.getJSONObject(i);
                            int id = obj.getInt("id");
                            String title = obj.getString("title");
                            String image = obj.optString("image", "https://via.placeholder.com/150?text=No+Image");
                            int minutes = obj.getInt("readyInMinutes");
                            recipes.add(new Recipe(id, title, minutes, image));
                        }
                        callback.onSuccess(recipes);
                    } catch (Exception e) {
                        callback.onError(e);
                    }
                },
                error -> callback.onError(error)
        );
        queue.add(request);
    }
}