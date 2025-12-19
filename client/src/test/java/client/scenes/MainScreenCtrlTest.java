package client.scenes;


import commons.Recipe;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ListView;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;

class MainScreenCtrlTest {

    @BeforeAll
    static void initToolkit() {
        try {
            Platform.startup(() -> {
            });
        } catch (IllegalStateException e) {
        }
    }


    @Test
    void testFindNextId() throws Exception {
        MainScreenCtrl ctrl = new MainScreenCtrl(null, null);

        ObservableList<Recipe> fakeList = FXCollections.observableArrayList(
                new Recipe("Recipe 1", "", null, null),
                new Recipe("Recipe 2", "", null, null)
        );

        setPrivateField(ctrl, "observableRecipes", fakeList);

        int nextId = ctrl.findNextId("Recipe");

        assertEquals(3, nextId);
    }


    @Test
    void testAddRecipe() throws Exception {
        MainScreenCtrl ctrl = new MainScreenCtrl(null, null);
        ObservableList<Recipe> fakeList = FXCollections.observableArrayList();
        setPrivateField(ctrl, "observableRecipes", fakeList);

        ListView<Recipe> view = new ListView<>();
        setPrivateField(ctrl, "recipeListView", view);

        ctrl.addRecipe();

        assertEquals(1, fakeList.size());
        assertEquals("Recipe 1", fakeList.getFirst().getName());
    }

    @Test
    void testDeleteRecipe() throws Exception {
        MainScreenCtrl ctrl = new MainScreenCtrl(null, null);

        Recipe r1 = new Recipe("Recipe 1", "", null, null);
        ObservableList<Recipe> fakeList = FXCollections.observableArrayList(r1);
        setPrivateField(ctrl, "observableRecipes", fakeList);

        ListView<Recipe> mockView = new ListView<>(fakeList);
        mockView.getSelectionModel().select(r1);
        setPrivateField(ctrl, "recipeListView", mockView);

        ctrl.deleteRecipe();

        assertTrue(fakeList.isEmpty());
    }

    // These methods are here to bypass private access
    private void setPrivateField(Object obj, String fieldName, Object value) throws Exception {
        Field field = obj.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(obj, value);
    }


}

