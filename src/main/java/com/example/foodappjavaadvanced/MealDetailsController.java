package com.example.foodappjavaadvanced;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.control.ScrollPane;

public class MealDetailsController {

    @FXML
    private ImageView mealImageView;

    @FXML
    private Label instructionsLabel;

    @FXML
    private Label ingredientsLabel;

    @FXML
    private ScrollPane scrollPane; // Reference to ScrollPane, if needed

    // Method to set meal details
    public void setMealDetails(String instructions, String ingredients, String imageUrl) {
        // Set the text for the instructions and ingredients labels
        instructionsLabel.setText(instructions);
        ingredientsLabel.setText(ingredients);

        // Load the image from the URL
        try {
            Image image = new Image(imageUrl); // Load image from URL
            mealImageView.setImage(image);
        } catch (Exception e) {
            e.printStackTrace(); // Handle image loading errors
            mealImageView.setImage(null); // Clear image if loading fails
        }
    }
}
