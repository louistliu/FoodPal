package client.scenes;

import client.Main;
import client.utils.Config;
import client.utils.ConfigService;
import client.utils.Endpoint;
import client.utils.FlagUtils;
import client.utils.PrintRecipe;
import client.utils.ResponseHandler;
import client.utils.ServerSockets;
import com.google.inject.Inject;
import commons.Ingredient;
import commons.Recipe;
import commons.RecipeIngredient;
import commons.RecipeList;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.NodeOrientation;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Pair;

/**
 * The Class that handles the logic of the main screen.
 */
public class MainScreenCtrl {

    private final ServerSockets serverRecipes;
    private final ConfigService configService;
    RecipeList listOfRecipes = new RecipeList();
    private String selectedLanguage = "English";
    private ObservableList<Recipe> observableRecipes;
    private ObservableList<RecipeIngredient> observableIngredients;
    private Recipe selectedRecipe;

    private List<Recipe> allRecipes = new ArrayList<>();
    private String currentSearchQuery = "";
    private boolean showingFavoritesOnly = false;

    @FXML
    private AnchorPane rightPane;
    @FXML
    private ListView<Recipe> recipeListView;
    @FXML
    private ListView<RecipeIngredient> ingredientListView;
    @FXML
    private ListView<String> instructionListView;
    @FXML
    private TextField searchRecipesField;
    @FXML
    private ComboBox<String> languageComboBox;
    @FXML
    private TextField recipeNameField;
    @FXML
    private TextArea recipeDescriptionField;
    @FXML
    private Label ingredientsLabel;
    @FXML
    private Label instructionsLabel;
    @FXML
    private ToggleButton switchMenuButton;
    @FXML
    private Button saveButton;
    @FXML
    private Button addButton;
    @FXML
    private Button deleteButton;
    @FXML
    private Button addInstructionButton;
    @FXML
    private Button deleteInstructionButton;
    @FXML
    private Button addIngredientButton;
    @FXML
    private Button deleteIngredientButton;
    @FXML
    private Button duplicateButton;
    @FXML
    private Button favoritesButton;
    @FXML
    private Button allButton;
    @FXML
    private Button printButton;
    @FXML
    private Image dutchFlag;
    @FXML
    private Image englishFlag;
    @FXML
    private Image frenchFlag;
    @FXML
    private Image arabicFlag;
    private final LanguageController languageController;

    @FXML
    private Button favoriteButton;

    /**
     * Constructs the MainScreenCtrl, injecting the scene controller.
     *
     * @param m The main application controller for scene transitions.
     */
    @Inject
    public MainScreenCtrl(MainCtrl m, ServerSockets server, ConfigService configService) {
        this.serverRecipes = server;
        this.configService = configService;
        this.languageController = m.getLanguageController();
    }

    /**
     * Initializes the controller, sets up the initial recipe list,
     * language options, and adds selection listeners.
     */
    public void initialize() {
        languageChoiceBox.setItems(FXCollections.observableArrayList("English", "Dutch", "German"));
        if (languageChoiceBox.getItems().contains(configService.getConfig().getLanguage())) {
            selectedLanguage = configService.getConfig().getLanguage();
        }
        languageChoiceBox.getSelectionModel().select(selectedLanguage);
        languageChoiceBox.setItems(FXCollections.observableArrayList(
                "English", "Dutch", "German"));
        languageChoiceBox.getSelectionModel().selectFirst();
        languageComboBox.setItems(FXCollections.observableArrayList(
                "English", "Dutch", "French", "Arabic"));
        languageComboBox.getSelectionModel().selectFirst();

        refreshUIText();
        FlagUtils.loadFlags(languageComboBox, 0);


        if (recipeDescriptionField != null) {
            recipeDescriptionField.setWrapText(true);
        }

        // --- Cell Factory and Listeners ---
        recipeListView.setCellFactory(lv -> new ListCell<>() {
            public void updateItem(Recipe recipe, boolean empty) {
                super.updateItem(recipe, empty);
                setText(empty ? null : recipe != null ? recipe.getName() : null);
            }
        });

        ingredientListView.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(RecipeIngredient item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setContextMenu(null);
                } else {
                    setText(item.getIngredient().getName() + " " + item.getAmount() + " "
                            + item.getUnit());

                    ContextMenu cm = new ContextMenu();
                    MenuItem editItem = new MenuItem(languageController.get("menu.edit"));
                    editItem.setOnAction(event -> editIngredient(item));
                    cm.getItems().add(editItem);
                    setContextMenu(cm);
                }
            }
        });

        // This listener will fire immediately if data is bound, triggering
        // showRecipeDetails
        recipeListView.getSelectionModel().selectedItemProperty()
              .addListener(this::onSelectedRecipeChanged);

        setupInstructionDragAndDrop();

        recipeNameField.setOnMouseExited(e -> saveRecipeTitle());
        recipeNameField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue) {
                saveRecipeTitle();
            }
        });
        recipeDescriptionField.setOnMouseExited(e -> saveRecipeDescription());
        recipeDescriptionField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue) {
                saveRecipeDescription();
            }
        });


        System.out.println("FoodPal Main Screen UI initialized.");
        rightPane.setVisible(false);

        observableRecipes = FXCollections.observableList(new ArrayList<>());
        recipeListView.setItems(observableRecipes);
        observableIngredients = FXCollections.observableArrayList();
        ingredientListView.setItems(observableIngredients);

        serverRecipes.subscribe(Endpoint.RECIPE_FETCH,
              new ResponseHandler<List<Recipe>>(this::onUpdateRecipeList) {
              });

        serverRecipes.subscribe(Endpoint.RECIPE_CREATE,
              new ResponseHandler<Recipe>(this::onAddRecipe) {
              });
        serverRecipes.subscribe(Endpoint.RECIPE_USER_CREATE,
              new ResponseHandler<Recipe>(this::onCreateUserRecipe) {
              });
        serverRecipes.subscribe(Endpoint.RECIPE_UPDATE,
              new ResponseHandler<Recipe>(this::onUpdateRecipe) {
              });
        serverRecipes.subscribe(Endpoint.RECIPE_DELETE,
              new ResponseHandler<Recipe>(this::onDeleteRecipe) {
              });
        serverRecipes.subscribe(Endpoint.ERROR,
              new ResponseHandler<Throwable>(ErrorScreenCtrl::onError) {
              });

        searchRecipesField.textProperty().addListener((obs, oldVal, newVal) -> {
            this.currentSearchQuery = newVal.trim().toLowerCase();
            refreshListView();
        });

        searchRecipesField.setOnKeyPressed(event -> {
            if (event.getCode() == javafx.scene.input.KeyCode.ESCAPE) {
                searchRecipesField.clear();
                recipeListView.requestFocus();
            }
        });
    }

    /**
     * Callback method triggered when the server broadcasts a newly created
     * user-specific recipe.
     *
     * @param recipe The newly created recipe.
     */
    private void onCreateUserRecipe(Recipe recipe) {
        Platform.runLater(() -> {
            selectRecipe(recipe);
        });
    }

    /**
     * Callback method triggered when the full recipe list is received from the server.
     * Updates the master list and refreshes the current UI view.
     *
     * @param recipes The full list of recipes from the server.
     */
    private void onUpdateRecipeList(List<Recipe> recipes) {

        System.out.print("RECIPES ARRIVED");
        Platform.runLater(() -> {
            allRecipes.clear();
            allRecipes.addAll(recipes);
            refreshListView();
        });
    }

    /**
     * Handles real-time search field updates and the Escape key shortcut.
     */
    public void setupSearchField() {
        searchRecipesField.textProperty()
              .addListener((obs, oldVal, newVal) -> {
                  this.currentSearchQuery = newVal.trim().toLowerCase();
                  refreshListView();
              });

        searchRecipesField.setOnKeyPressed(event -> {
            if (event.getCode() == javafx.scene.input.KeyCode.ESCAPE) {
                searchRecipesField.clear();
                recipeListView.requestFocus();
            }
        });
    }

    private void onAddRecipe(Recipe recipe) {
        Platform.runLater(() -> {
            if (!allRecipes.contains(recipe)) {
                allRecipes.add(recipe);
            }
            if (observableRecipes.contains(recipe)) {
                return;
            }
            refreshListView();
        });
    }

    /**
     * Callback method triggered when a recipe is deleted from the server.
     * Removes the recipe from both the master list and the UI list.
     *
     * @param recipe The recipe to be removed.
     */
    private void onDeleteRecipe(Recipe recipe) {
        Platform.runLater(() -> {
            allRecipes.removeIf(r -> r.getId() == recipe.getId());

            observableRecipes.remove(recipe);
            recipeListView.refresh();
        });
    }

    /**
     * Callback method triggered when an existing recipe is updated on the server.
     * Synchronizes the master list and updates the UI if the recipe is currently visible.
     *
     * @param recipe The updated recipe data.
     */
    private void onUpdateRecipe(Recipe recipe) {
        Platform.runLater(() -> {
            for (int i = 0; i < allRecipes.size(); i++) {
                if (allRecipes.get(i).getId() == recipe.getId()) {
                    allRecipes.set(i, recipe);
                    break;
                }
            }

            var recipes = observableRecipes.stream()
                    .filter(x -> x.getId() == recipe.getId()).toList();

            if (recipes.isEmpty()) {
                refreshListView();
                return;
            }

            int ind = observableRecipes.indexOf(recipes.getLast());

            if (selectedRecipe == null || recipe.getId() != selectedRecipe.getId()) {
                observableRecipes.remove(ind);
                observableRecipes.add(ind, recipe);
                recipeListView.refresh();
                return;
            }

            safeSwapRecipe(ind, recipe);

            selectRecipe(recipe);
        });
    }

    /**
     * Swaps the recipe with the new one without triggering onSelectedRecipeChanged.
     *
     * @param index  index of recipe in observable Recipes
     * @param recipe recipe to swap to
     */
    private void safeSwapRecipe(int index, Recipe recipe) {
        recipeListView.getSelectionModel().selectedItemProperty()
              .removeListener(this::onSelectedRecipeChanged);
        observableRecipes.set(index, recipe);
        recipeListView.refresh();
        recipeListView.getSelectionModel().selectedItemProperty()
              .addListener(this::onSelectedRecipeChanged);
    }

    private void onSelectedRecipeChanged(ObservableValue<? extends Recipe> obs, Recipe oldRecipe,
                                         Recipe newRecipe) {
        saveRecipeTitle();
        saveRecipeDescription();
        showRecipeDetails(newRecipe);
    }

    /**
     * Selects a recipe in the list view and sets it as the currently selected recipe.
     * If the recipe is not in the current observable list, it is added before selection.
     *
     * @param recipe The recipe to be selected.
     */
    private void selectRecipe(Recipe recipe) {
        if (!observableRecipes.contains(recipe)) {
            observableRecipes.add(recipe);
            recipeListView.refresh();
        }

        recipeListView.getSelectionModel().select(recipe);
        selectedRecipe = recipe;
    }

    private void saveRecipeTitle() {
        if (selectedRecipe == null || recipeNameField.getText().trim().isEmpty()) {
            return;
        }
        if (recipeNameField.getText().equals(selectedRecipe.getName())) {
            return;
        }
        saveRecipe();
    }

    private void saveRecipeDescription() {
        if (selectedRecipe == null || recipeDescriptionField.getText().trim().isEmpty()) {
            return;
        }
        if (recipeDescriptionField.getText().equals(selectedRecipe.getDescription())) {
            return;
        }
        saveRecipe();
    }

    /**
     * Configures the cell factory for the instruction list view to enable
     * custom rendering and edit handling.
     */
    private void setupInstructionDragAndDrop() {
        instructionListView.setCellFactory(
              param -> new InstructionListCell(this::saveRecipe, this::editInstructionHandler));
    }

    /**
     * Handles the logic for editing an existing instruction.
     *
     * @param instruction The original instruction text.
     * @return An Optional containing the updated instruction text if edited
     */
    private Optional<String> editInstructionHandler(String instruction) {
        AddInstructionScreenCtrl control =
              launchModal(AddInstructionScreenCtrl.class, "AddInstructionScreen.fxml",
                    "Edit Instruction", instruction);

        if (control == null) {
            ErrorScreenCtrl.showError(languageController.get("error.failedEditInstruction"));
            return Optional.empty();
        }
        return control.getResult() == null ? Optional.empty() :
              Optional.of(control.getResult());
    }

    /**
     * Updates the text areas to show the details of the selected recipe.
     */
    private void showRecipeDetails(Recipe recipe) {
        this.selectedRecipe = recipe;

        if (recipe == null) {
            recipeNameField.clear();
            recipeDescriptionField.clear();

            if (ingredientListView != null) {
                ingredientListView.getItems().clear();
            }
            if (instructionListView != null) {
                instructionListView.getItems().clear();
            }
            rightPane.setVisible(false);
            return;
        }
        rightPane.setVisible(true);

        if (!recipeNameField.getText().equals(recipe.getName())) {
            recipeNameField.setText(recipe.getName());
        }
        if (!recipeDescriptionField.getText().equals(recipe.getDescription())) {
            recipeDescriptionField.setText(recipe.getDescription());
        }

        updateFavoriteButtonText(recipe);

        // Refresh Instructions View
        if (instructionListView != null) {
            instructionListView.getItems().clear();
            if (recipe.getInstructions() != null) {
                instructionListView.getItems().addAll(recipe.getInstructions());
            }
        }

        // Refresh Ingredients View (NOT IMPLEMENTED YET)
        if (ingredientListView != null) {
            ingredientListView.getItems().clear();
            if (recipe.getIngredients() != null) {
                observableIngredients.addAll(recipe.getIngredients());
            }
        }
    }

    /**
     * Saves the changes made in the text fields to the selected Recipe object.
     * Linked to the Save button in FXML.
     */
    public void saveRecipe() {
        if (selectedRecipe == null) {
            return;
        }

        String newName = recipeNameField.getText();
        String newDescription = recipeDescriptionField.getText();

        selectedRecipe.setName(newName);
        selectedRecipe.setDescription(newDescription);

        if (instructionListView != null) {
            List<String> currentInstructions = new ArrayList<>(instructionListView.getItems());
            selectedRecipe.setInstructions(currentInstructions);
        }

        // if recipe is already created - update recipe otherwise save recipe,
        // update the list of recipes once server sends response
        if (observableRecipes.contains(selectedRecipe)) {
            serverRecipes.send(Endpoint.RECIPE_UPDATE, selectedRecipe);
        } else {
            serverRecipes.send(Endpoint.RECIPE_CREATE, selectedRecipe);
        }

        System.out.println("Saved changes for: " + newName);
    }

    /**
     * Finds the lowest available integer for a given naming pattern.
     */
    public int findNextId(String prefix) {
        Set<Integer> takenNumbers = new HashSet<>();

        for (Recipe r : observableRecipes) {
            String name = r.getName();

            if (name.startsWith(prefix)) {
                try {
                    String numberPart = name.substring(prefix.length()).trim();
                    int number = Integer.parseInt(numberPart);
                    takenNumbers.add(number);
                } catch (NumberFormatException e) {
                    // Ignore names that don't match pattern
                }
            }
        }
        int i = 1;
        while (takenNumbers.contains(i)) {
            i++;
        }
        return i;
    }

    /**
     * Creates a new recipe with a unique name, and sends it to the server.
     */
    public void addRecipe() {
        String newName = "Recipe " + findNextId("Recipe ");
        serverRecipes.send(Endpoint.RECIPE_CREATE, new Recipe(newName));

        System.out.println("Added new recipe: " + newName);
    }

    /**
     * Handles the deletion of the currently selected recipe.
     */
    public void deleteRecipe() {
        Recipe selected = recipeListView.getSelectionModel().getSelectedItem();

        if (selected == null) {
            ErrorScreenCtrl.showError(languageController.get("error.noRecipeSelected"));
            return;
        }
        serverRecipes.send(Endpoint.RECIPE_DELETE, selected);
        System.out.println("Deleted recipe: " + selected.getName());
    }

    /**
     * Handles the duplication (cloning) of the currently selected recipe.
     */
    public void duplicateRecipe() {
        if (selectedRecipe == null) {
            return;
        }
        List<RecipeIngredient> clonedIngredients = new ArrayList<>();
        if (selectedRecipe.getIngredients() != null) {
            selectedRecipe.getIngredients().forEach(r -> clonedIngredients.add(r.copy()));
        }

        List<String> clonedInstructions = new ArrayList<>();
        if (selectedRecipe.getInstructions() != null) {
            clonedInstructions.addAll(selectedRecipe.getInstructions());
        }

        String recipeName = selectedRecipe.getName();
        System.out.println("RECIPE: " + recipeName);

        Recipe newRecipe =
              new Recipe(selectedRecipe.getName() + " Clone " + findNextId(recipeName + " Clone"),
                    selectedRecipe.getDescription(), clonedIngredients, clonedInstructions);

        serverRecipes.send(Endpoint.RECIPE_CREATE, newRecipe);

        System.out.println("Added new recipe: " + newRecipe.getName());
    }

    /**
     * Toggles the favorite status of the selected recipe locally.
     * Does not send an update to the server.
     */
    @FXML
    public void toggleFavorite() {
        if (selectedRecipe == null) {
            return;
        }

        List<Long> favoriteIds = configService.getConfig().getFavoriteRecipeIds();
        long currentId = selectedRecipe.getId();

        if (favoriteIds.contains(currentId)) {
            favoriteIds.remove(currentId);
            System.out.println("Recipe '" + selectedRecipe.getName() + "' removed from favorites.");
        } else {
            favoriteIds.add(currentId);
            System.out.println("Recipe '" + selectedRecipe.getName() + "' added to favorites.");
        }

        configService.persistConfig();
        updateFavoriteButtonText(selectedRecipe);
        refreshListView();

    }

    /**
     * Updates the text of the singular favoriteButton.
     */
    private void updateFavoriteButtonText(Recipe recipe) {
        if (recipe == null) {
            return;
        }
        boolean isFavorite = configService.getConfig().getFavoriteRecipeIds()
              .contains(recipe.getId());
        favoriteButton.setText(isFavorite ? "Unfavorite" : "Favorite");
    }

    /**
     * Refreshes the list view by applying filters across names,
     * descriptions, ingredients, and instructions.
     */
    private void refreshListView() {
        List<Long> favoriteIds = configService.getConfig().getFavoriteRecipeIds();

        List<Recipe> filteredList = allRecipes.stream()
                .filter(recipe -> {
                    boolean matchesFavorite = !showingFavoritesOnly
                            || favoriteIds.contains(recipe.getId());
                    if (!matchesFavorite) {
                        return false;
                    }

                    if (currentSearchQuery.isEmpty()) {
                        return true;
                    }

                    // Aggregating all searchable content for this recipe
                    StringBuilder searchableContent = new StringBuilder();
                    searchableContent.append(recipe.getName()).append(" ");
                    searchableContent.append(recipe.getDescription()).append(" ");

                    String[] keywords = currentSearchQuery.split("\\s+");

                    // Append ingredient names
                    for (RecipeIngredient ri : recipe.getIngredients()) {
                        searchableContent.append(ri.getIngredient().getName()).append(" ");
                    }

                    // Append instruction steps
                    for (String instruction : recipe.getInstructions()) {
                        searchableContent.append(instruction).append(" ");
                    }

                    String finalSearchString = searchableContent.toString().toLowerCase();

                    for (String keyword : keywords) {
                        if (!finalSearchString.contains(keyword.toLowerCase())) {
                            return false;
                        }
                    }
                    return true;
                })
                .toList();

        observableRecipes.setAll(filteredList);
        recipeListView.refresh();
    }

    /**
     * Helper method to launch a modal window.
     *
     * @param defaultParams parameters that should be passed to the screen every time it launches.
     */
    private <T extends ScreenControl> T launchModal(Class<T> controllerClass, String fxmlFileName,
                                                    String title,
                                                    Object... defaultParams) {
        try {
            Stage modalStage = new Stage();

            // Set owner to block main window interactions
            if (recipeListView.getScene() != null) {
                modalStage.initOwner(recipeListView.getScene().getWindow());
            }

            modalStage.initModality(Modality.WINDOW_MODAL);
            modalStage.setTitle(title);
            Pair<T, Parent> pair =
                  Main.getFxml().load(controllerClass, "client", "scenes", fxmlFileName);
            modalStage.setScene(new Scene(pair.getValue()));
            Parent root = pair.getValue();
            // Pass the stage to the controller so it can close itself
            pair.getKey().setStage(modalStage);
            pair.getKey().init(defaultParams);

            modalStage.showAndWait();
            return pair.getKey();
        } catch (Exception e) {
            System.err.println("Failed to load modal '" + title + "': " + e.getMessage());
            return null;
        }
    }

    /**
     * Handles the click of the 'Add Ingredient' button.
     */
    public void addIngredient() {
        if (selectedRecipe == null) {
            ErrorScreenCtrl.showError(languageController.get("error.noRecipeAddIngredient"));
            return;
        }

        AddIngredientScreenCtrl controller =
                launchModal(AddIngredientScreenCtrl.class, "AddIngredientScreen.fxml",
                        languageController.get("title.addIngredient"));


        if (controller != null && controller.getResult() != null) {
            RecipeIngredient newIngredient = controller.getResult();
            if (containsIngredient(selectedRecipe, newIngredient.getIngredient())) {
                String pattern = languageController.get("error.ingredientAlreadyExists");
                String name = newIngredient.getIngredient().getName();

                ErrorScreenCtrl.showError(String.format(pattern, name));
                return;
            }
            System.out.println("Ingredient added: " + newIngredient.getIngredient().getName());
            selectedRecipe.getIngredients().add(newIngredient);
            serverRecipes.send(Endpoint.RECIPE_UPDATE, selectedRecipe);
        }
    }

    /**
     * Handles the edit ingredient function.
     *
     * @param item - The recipe ingredient to be edited.
     */
    private void editIngredient(RecipeIngredient item) {
        if (selectedRecipe == null || item == null) {
            return;
        }

        AddIngredientScreenCtrl controller = launchModal(
                AddIngredientScreenCtrl.class,
                "AddIngredientScreen.fxml",
                "Edit Ingredient",
                item
        );

        if (controller != null && controller.getResult() != null) {
            RecipeIngredient updatedItem = controller.getResult();

            if (!item.getIngredient().getName().equals(updatedItem.getIngredient().getName())
                  && containsIngredient(selectedRecipe, updatedItem.getIngredient())) {
                String pattern = languageController.get("error.ingredientAlreadyExists");
                String name = updatedItem.getIngredient().getName();

                ErrorScreenCtrl.showError(String.format(pattern, name));
                return;
            }

            int index = selectedRecipe.getIngredients().indexOf(item);
            if (index != -1) {
                selectedRecipe.getIngredients().set(index, updatedItem);
                int obsIndex = observableIngredients.indexOf(item);
                if (obsIndex != -1) {
                    observableIngredients.set(obsIndex, updatedItem);
                    saveRecipe();
                } else {
                    observableIngredients.setAll(selectedRecipe.getIngredients());
                }
                ingredientListView.refresh();
            }
        }
    }

    /**
     * Checks if a specific recipe already contains an ingredient with the same name.
     *
     * @param recipe The recipe to check.
     * @param ingredient The ingredient to look for.
     * @return True if an ingredient with the same name exists, false otherwise.
     */
    private boolean containsIngredient(Recipe recipe, Ingredient ingredient) {
        return recipe.getIngredients().stream()
              .anyMatch(i -> i.getIngredient().getName().equals(ingredient.getName()));
    }

    /**
     * Adds an instruction using the modal.
     */
    public void addInstruction() {
        if (selectedRecipe == null) {
            ErrorScreenCtrl.showError(languageController.get("error.noRecipeAddInstruction"));
            return;
        }


        AddInstructionScreenCtrl controller =
                launchModal(AddInstructionScreenCtrl.class, "AddInstructionScreen.fxml",
                        languageController.get("title.addInstruction"));

        if (controller != null) {
            String instructionText = controller.getResult();

            if (instructionText != null && !instructionText.isEmpty()) {
                System.out.println("Instruction added: " + instructionText);

                instructionListView.getItems().add(instructionText);
                saveRecipe();
            }
        }
    }

    /**
     * Deletes an ingredient.
     */
    public void deleteIngredient() {
        RecipeIngredient selected = ingredientListView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            selectedRecipe.removeIngredient(selected);
            ingredientListView.getItems().remove(selected);
            saveRecipe();
        }
    }

    /**
     * Deletes an instruction.
     */
    public void deleteInstruction() {
        String selected = instructionListView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            instructionListView.getItems().remove(selected);
            selectedRecipe.getInstructions().remove(selected);
            saveRecipe();
        }
    }

    /**
     * Handles the request to download a printable version of the recipe.
     */
    public void printRecipe() {

        if (selectedRecipe == null) {
            return;
        }

        PrintRecipe.exportRecipe(printButton.getScene().getWindow(),
                selectedRecipe, languageController);

        System.out.println("Exported recipe: " + selectedRecipe.getName());
    }

    /**
     * Handles switching the recipe list view to show only favorite recipes.
     */
    @FXML
    public void showFavorites() {
        System.out.println("Switching view to show only favorite recipes.");
        this.showingFavoritesOnly = true;
        refreshListView();
    }

    /**
     * Handles switching the recipe list view to show all recipes available on the
     * server.
     */
    @FXML
    public void showAllRecipes() {
        System.out.println("Switching view to show all recipes.");
        this.showingFavoritesOnly = false;
        refreshListView();
    }

    /**
     * provides the actions when user changes language.
     */
    public void onLanguageChange() {
        selectedLanguage = languageChoiceBox.getValue();
        configService.persistConfig();
        System.out.println("Language switched to: " + languageChoiceBox.getValue());

        String language = languageComboBox.getValue();

        switch (selected) {
            case "Dutch" -> switchLanguage("nl");
            case "German" -> switchLanguage("de");
            case "French" -> switchLanguage("fr");
            case "Arabic" -> switchLanguage("ar");
            default -> switchLanguage("en");
        }

    }

    /**
     * switches the language of the UI.
     *
     * @param languageCode language Code
     */
    public void switchLanguage(String languageCode) {
        languageController.loadLanguage(languageCode);

        // Get the root element of your current scene
        Parent root = saveButton.getScene().getRoot();

        if ("ar".equals(languageCode)) {
            // Flip the entire UI for Arabic
            root.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
        } else {
            // Keep it standard for English, Dutch, French
            root.setNodeOrientation(NodeOrientation.LEFT_TO_RIGHT);
        }

        refreshUIText();
    }

    /**
     * refreshes the UI with the new language.
     */
    public void refreshUIText() {

        ingredientsLabel.setText(languageController.get("label.ingredients"));
        instructionsLabel.setText(languageController.get("label.instructions"));
        saveButton.setText(languageController.get("button.save"));
        duplicateButton.setText(languageController.get("button.duplicate"));
        printButton.setText(languageController.get("button.print"));

        favoritesButton.setText(languageController.get("button.favorites"));
        allButton.setText(languageController.get("button.allRecipes"));

        recipeNameField.setPromptText(languageController.get("label.recipeNamePrompt"));
        recipeDescriptionField.setPromptText(languageController.get("label.descriptionPrompt"));
        searchRecipesField.setPromptText(languageController.get("label.searchPrompt"));
        if (saveButton != null && saveButton.getScene() != null) {
            Stage stage = (Stage) saveButton.getScene().getWindow();
            stage.setTitle(languageController.get("app.title"));
        }
        recipeListView.refresh();

    }

}



