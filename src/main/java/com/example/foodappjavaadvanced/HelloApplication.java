package com.example.foodappjavaadvanced;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import javafx.concurrent.Task;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonArray;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class HelloApplication extends Application {

    private static final String API_URL = "https://www.themealdb.com/api/json/v1/1/search.php?s="; // URL for searching meals by name

    @Override
    public void start(Stage stage) throws Exception {
        // Create a ListView to display the meal names
        ListView<String> mealListView = new ListView<>();

        // Fetch meals data and populate ListView in a background task
        fetchMeals("chicken", mealListView); // Example search query: chicken

        // Set up the scene and stage
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 320, 240);
        stage.setTitle("Food App");
        stage.setScene(scene);
        stage.show();
    }

    // Fetch meals from TheMealDB API
    public void fetchMeals(String ingredient, ListView<String> mealListView) {
        // Create a background task to fetch and process the meals
        Task<Void> fetchTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                try {
                    // Construct the API URL with the ingredient (e.g., "chicken")
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

                    // Parse the JSON response using Gson
                    Gson gson = new Gson();
                    JsonObject jsonResponse = gson.fromJson(response.toString(), JsonObject.class);
                    JsonArray meals = jsonResponse.getAsJsonArray("meals");

                    // Populate ListView with meal names on the JavaFX Application Thread
                    if (meals != null) {
                        // This is done on the JavaFX Application Thread
                        mealListView.getItems().clear();
                        for (int i = 0; i < meals.size(); i++) {
                            JsonObject meal = meals.get(i).getAsJsonObject();
                            String mealName = meal.get("strMeal").getAsString(); // Extract meal name
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

    public static void main(String[] args) {
        launch();
    }
}
