package client.scenes;

import javafx.application.Platform;
import javafx.scene.control.TextArea;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;

class AddInstructionScreenCtrlTest {


    private AddInstructionScreenCtrl ctrl;

    //needed for the UI components to exist
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
        ctrl = new AddInstructionScreenCtrl();

        setPrivateField(ctrl, "inputTextArea", new TextArea());
    }

    @Test
    void testHandleOk() throws Exception {
        TextArea input = (TextArea) getPrivateField(ctrl, "inputTextArea");
        input.setText("let him cook");

        ctrl.handleOk();

        assertEquals("let him cook", ctrl.getResult());
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
