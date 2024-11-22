package com.example.foodappjavaadvanced;

import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.concurrent.Task;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class HelloController {
    @FXML
    private ListView<String> mealListView;

    private static final String API_URL = "https://www.themealdb.com/api/json/v1/1/search.php?s=";

    @FXML
    public void initialize() {
        // Fetch meals data on initialization with "chicken" as the default query
        fetchMeals("chicken");
    }

    // Fetch meals from TheMealDB API
    private void fetchMeals(String ingredient) {
        Task<Void> fetchTask = new Task<>() {
            @Override
            protected Void call() throws Exception {
                try {
                    // Construct the API URL
                    String urlString = API_URL + ingredient;

                    // Make the HTTP request
                    URL url = new URL(urlString);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(5000);
                    connection.setReadTimeout(5000);

                    // Read the response
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }

                    // Parse the JSON response
                    Gson gson = new Gson();
                    JsonObject jsonResponse = gson.fromJson(response.toString(), JsonObject.class);
                    JsonArray meals = jsonResponse.getAsJsonArray("meals");

                    // Populate ListView on the JavaFX Application Thread
                    if (meals != null) {
                        mealListView.getItems().clear();
                        for (int i = 0; i < meals.size(); i++) {
                            JsonObject meal = meals.get(i).getAsJsonObject();
                            String mealName = meal.get("strMeal").getAsString();
                            mealListView.getItems().add(mealName);
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }
        };

        // Start the background task
        new Thread(fetchTask).start();
    }
}
