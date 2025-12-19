package client.scenes;

import javafx.application.Platform;
import javafx.scene.control.TextField;
import javafx.scene.control.MenuButton;
import javafx.scene.control.ChoiceBox;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;

public class AddIngredientScreenCtrlTest {

    private AddIngredientScreenCtrl ctrl;


    // Needed for the UI components to exist
    @BeforeAll
    static void initializeToolKit() {
        try {
            Platform.startup(() -> {
            });
        } catch (IllegalStateException e) {
        }
    }

    @BeforeEach
    void setup() throws Exception {
        ctrl = new AddIngredientScreenCtrl();

        setPrivateField(ctrl, "amountTextField", new TextField());
        setPrivateField(ctrl, "unitMenuButton", new MenuButton());
        setPrivateField(ctrl, "ingredientChoiceBox", new ChoiceBox<String>());
    }

    @Test
    @SuppressWarnings("unchecked")
    void testHandleOkWithValidInput() throws Exception {
        ((TextField) getPrivateField(ctrl, "amountTextField")).setText("450");
        ((MenuButton) getPrivateField(ctrl, "unitMenuButton")).setText("grams");
        ((ChoiceBox<String>) getPrivateField(ctrl, "ingredientChoiceBox")).setValue("pepper");

        ctrl.handleOk();

        assertEquals("450 grams pepper", ctrl.getResult());
    }

    @Test
    @SuppressWarnings("unchecked")
    void equalsNullWhenMissingAmount() throws Exception {
        ((TextField) getPrivateField(ctrl, "amountTextField")).setText("");
        ((MenuButton) getPrivateField(ctrl, "unitMenuButton")).setText("grams");
        ((ChoiceBox<String>) getPrivateField(ctrl, "ingredientChoiceBox")).setValue("pepper");

        ctrl.handleOk();

        assertNull(ctrl.getResult());
    }

    @Test
    void testHandleCancel() {
        ctrl.handleCancel();
        assertNull(ctrl.getResult());
    }

    // These methods are here to bypass private access
    private void setPrivateField(Object obj, String fieldName, Object value) throws Exception {
        Field field = obj.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(obj, value);
    }

    private Object getPrivateField(Object obj, String fieldName) throws Exception {
        Field field = obj.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        return field.get(obj);
    }
}