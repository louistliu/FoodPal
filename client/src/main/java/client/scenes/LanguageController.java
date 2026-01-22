package client.scenes;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * class for the language controller.
 */
public class LanguageController {

    /**
     * Active language bundle.
     * Default language is English.
     */
    private ResourceBundle bundle =
            ResourceBundle.getBundle("Languages.messages", Locale.ENGLISH);

    /**
     * Loads a language by messages code (e.g. "en", "nl", "fr").
     *
     * @param language language code
     */
    public void loadLanguage(String language) {
        Locale locale = new Locale(language);
        bundle = ResourceBundle.getBundle("Languages.messages", locale);
    }

    /**
     * get method for UI translation keys.
     *
     * @param key messages key (e.g. "saveButton.text")
     * @return translated text
     */
    public String get(String key) {
        return bundle.getString(key);
    }

    public String getSaveText() {
        return get("saveButton.text");
    }

    public String getDeleteText() {
        return get("deleteButton.text");
    }

}