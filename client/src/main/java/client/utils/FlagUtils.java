package client.utils;

import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * class for loading flags and imageView.
 */
public class FlagUtils {

    private static final Image englishFlag = load("/flags/English_Flag.png");
    private static final Image dutchFlag   = load("/flags/Dutch_Flag.png");
    private static final Image frenchFlag  = load("/flags/French_Flag.png");
    private static final Image arabicFlag  = load("/flags/Arabic_Flag.png");
    private static final Image turkishFlag  = load("/flags/Turkey_Flag.png");

    private static Image load(String path) {
        var url = FlagUtils.class.getResource(path);
        if (url == null) {
            System.err.println("Could not find resource: " + path);
            return null; // or a placeholder image
        }
        return new Image(url.toExternalForm());
    }

    /**
     * method to load all the flags.
     *
     * @param defaultIndex the language that is selected
     */
    public static void loadFlags(ComboBox<String> comboBox, int defaultIndex) {
        System.out.println(FlagUtils.class.getResource("/flags/english.png"));
        comboBox.getItems().clear();
        comboBox.getItems().addAll("English", "Dutch", "French", "Arabic", "Turkish");

        class FlagCell extends ListCell<String> {
            private final ImageView imageView = new ImageView();

            {
                imageView.setFitWidth(25);
                imageView.setFitHeight(18);
            }

            /**
             * This method switches the flag when selecting another language.
             *
             * @param item item that needs to be updated (flag).
             * @param empty boolean value to handle ghost cells.
             */
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    setText(item);

                    switch (item) {
                        case "Dutch" -> imageView.setImage(dutchFlag);
                        case "French" -> imageView.setImage(frenchFlag);
                        case "Arabic" -> imageView.setImage(arabicFlag);
                        case "Turkish" -> imageView.setImage(turkishFlag);

                        default -> imageView.setImage(englishFlag);
                    }

                    setGraphic(imageView);
                }
            }
        }

        comboBox.setCellFactory(lv -> new FlagCell());
        comboBox.setButtonCell(new FlagCell());

        comboBox.getSelectionModel().select(defaultIndex);
    }

}
