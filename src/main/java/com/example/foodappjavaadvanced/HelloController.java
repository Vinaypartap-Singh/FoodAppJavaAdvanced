package com.example.foodappjavaadvanced;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.control.Alert.AlertType;
import javafx.concurrent.Task;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.Scene;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class HelloController {
    @FXML
    private GridPane mealGrid;

    @FXML
    private Label gridTitle;

    @FXML
    private TextField searchField;

    private static final String API_URL = "https://www.themealdb.com/api/json/v1/1/search.php?s=";

    @FXML
    public void initialize() {
        fetchMeals("chicken"); // Initial fetch with "chicken"
    }

    @FXML
    private void handleSearch() {
        String searchQuery = searchField.getText().trim();
        if (!searchQuery.isEmpty()) {
            fetchMeals(searchQuery);
        }
    }

    @FXML
    private void handleQuickSearch(javafx.event.ActionEvent event) {
        Button clickedButton = (Button) event.getSource();
        String quickSearchQuery = clickedButton.getText();
        fetchMeals(quickSearchQuery);
    }

    private void fetchMeals(String ingredient) {
        Task<Void> fetchTask = new Task<>() {
            @Override
            protected Void call() throws Exception {
                try {
                    String urlString = API_URL + ingredient;

                    URL url = new URL(urlString);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(5000);
                    connection.setReadTimeout(5000);

                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }

                    Gson gson = new Gson();
                    JsonObject jsonResponse = gson.fromJson(response.toString(), JsonObject.class);
                    JsonArray meals = jsonResponse.getAsJsonArray("meals");

                    if (meals != null) {
                        Platform.runLater(() -> {
                            gridTitle.setText("Meals containing: " + ingredient.substring(0, 1).toUpperCase() + ingredient.substring(1));
                            mealGrid.getChildren().clear();
                            for (int i = 0; i < meals.size(); i++) {
                                JsonObject meal = meals.get(i).getAsJsonObject();
                                String mealName = meal.get("strMeal").getAsString();
                                String imageUrl = meal.get("strMealThumb").getAsString();
                                String mealId = meal.get("idMeal").getAsString();

                                VBox mealCard = createMealCard(mealName, imageUrl, mealId);
                                int column = i % 4;
                                int row = i / 4;
                                mealGrid.add(mealCard, column, row);
                            }
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }
        };

        new Thread(fetchTask).start();
    }

    private VBox createMealCard(String mealName, String imageUrl, String mealId) {
        VBox mealCard = new VBox(10);
        mealCard.setAlignment(Pos.CENTER);
        mealCard.setStyle("-fx-padding: 10; -fx-border-color: #ccc; -fx-border-width: 1; -fx-border-radius: 5; -fx-background-radius: 5;");

        ImageView imageView = new ImageView(new Image(imageUrl));
        imageView.setFitWidth(200);
        imageView.setFitHeight(200);
        imageView.setPreserveRatio(true);
        imageView.setStyle("-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.3), 10, 0, 0, 5);");

        Label nameLabel = new Label(mealName);
        nameLabel.setStyle("-fx-font-size: 16; -fx-font-weight: bold; -fx-text-alignment: CENTER;");

        Button likeButton = new Button("Like ❤️");
        likeButton.setStyle("-fx-background-color: #ff6b6b; -fx-text-fill: white; -fx-padding: 5 10; -fx-border-radius: 5; -fx-background-radius: 5;");
        likeButton.setOnAction(event -> System.out.println("Liked: " + mealName));

        mealCard.getChildren().addAll(nameLabel, imageView, likeButton);

        // Add click event to the meal card to show more details
        mealCard.setOnMouseClicked(event -> showMealDetails(mealId));

        return mealCard;
    }


    private void showMealDetails(String mealId) {
        Task<Void> fetchTask = new Task<>() {
            @Override
            protected Void call() throws Exception {
                try {
                    String urlString = "https://www.themealdb.com/api/json/v1/1/lookup.php?i=" + mealId;

                    URL url = new URL(urlString);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(5000);
                    connection.setReadTimeout(5000);

                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }

                    Gson gson = new Gson();
                    JsonObject jsonResponse = gson.fromJson(response.toString(), JsonObject.class);
                    JsonArray meals = jsonResponse.getAsJsonArray("meals");
                    if (meals != null && meals.size() > 0) {
                        JsonObject meal = meals.get(0).getAsJsonObject();
                        String mealName = meal.get("strMeal").getAsString();
                        String mealImage = meal.get("strMealThumb").getAsString();
                        String instructions = meal.get("strInstructions").getAsString();
                        String ingredients = getMealIngredients(meal);

                        Platform.runLater(() -> {
                            // Create a styled popup window
                            Stage popupStage = new Stage();
                            popupStage.initModality(Modality.APPLICATION_MODAL);
                            popupStage.setTitle(mealName);

                            // Use VBox for meal details
                            VBox vbox = new VBox(10);
                            vbox.setAlignment(Pos.CENTER);
                            vbox.setStyle("-fx-background-color: white; -fx-padding: 20; -fx-border-radius: 10; -fx-background-radius: 10;");

                            // Meal image
                            ImageView mealImageView = new ImageView(new Image(mealImage));
                            mealImageView.setFitWidth(300);
                            mealImageView.setFitHeight(300);
                            mealImageView.setPreserveRatio(true);

                            // Instructions label
                            Label instructionsLabel = new Label("Instructions: \n" + instructions);
                            instructionsLabel.setStyle("-fx-font-size: 14; -fx-text-fill: #333333; -fx-padding: 10; -fx-text-alignment: LEFT; -fx-line-spacing: 6;");

                            // Ingredients label
                            Label ingredientsLabel = new Label("Ingredients: \n" + ingredients);
                            ingredientsLabel.setStyle("-fx-font-size: 14; -fx-text-fill: #333333; -fx-padding: 10; -fx-text-alignment: LEFT; -fx-line-spacing: 6;");

                            // Close button
                            Button closeButton = new Button("Close");
                            closeButton.setStyle("-fx-background-color: #ff6b6b; -fx-text-fill: white; -fx-padding: 5 10; -fx-border-radius: 5; -fx-background-radius: 5;");
                            closeButton.setOnAction(e -> popupStage.close());

                            // Wrap content in ScrollPane for scrolling
                            ScrollPane scrollPane = new ScrollPane(vbox);
                            scrollPane.setFitToWidth(true); // Make sure content fits width of the ScrollPane
                            scrollPane.setFitToHeight(true); // Make sure content fits height of the ScrollPane

                            // Add all content to VBox
                            vbox.getChildren().addAll(mealImageView, instructionsLabel, ingredientsLabel, closeButton);

                            // Create the scene and show the popup stage
                            Scene scene = new Scene(scrollPane, 400, 600);
                            popupStage.setScene(scene);
                            popupStage.show();
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }
        };

        new Thread(fetchTask).start();
    }



    private String getMealIngredients(JsonObject meal) {
        StringBuilder ingredients = new StringBuilder();
        for (int i = 1; i <= 20; i++) {
            String ingredientKey = "strIngredient" + i;
            String ingredient = meal.get(ingredientKey).getAsString();
            if (!ingredient.isEmpty()) {
                ingredients.append(ingredient).append("\n");
            }
        }
        return ingredients.toString();
    }
}
