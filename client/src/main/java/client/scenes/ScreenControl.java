package client.scenes;

import java.util.List;
import javafx.stage.Stage;

/**
 * Default class for handling interscene logic.
 */
public abstract class ScreenControl {
    protected Stage stage;
    protected List<Object> initialParams;

    /**
     * Sets the stage for this scene, necessary for closing the window.
     *
     * @param stage The modal stage.
     */
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    /**
     * Function that should be executed after the default scene setup
     * and before displaying to user.
     *
     * @param params Parameters passed in during init
     */
    public void init(Object... params) {
        initialParams = List.of(params);
    }
}
