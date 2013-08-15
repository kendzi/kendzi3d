package kendzi.kendzi3d.models.library.messages;

import java.beans.Beans;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class Messages {
    ////////////////////////////////////////////////////////////////////////////
    //
    // Constructor
    //
    ////////////////////////////////////////////////////////////////////////////
    private Messages() {
        // do not instantiate
    }
    ////////////////////////////////////////////////////////////////////////////
    //
    // Bundle access
    //
    ////////////////////////////////////////////////////////////////////////////
    private static final String BUNDLE_NAME = "kendzi.kendzi3d.models.library.messages.messages"; //$NON-NLS-1$
    private static final ResourceBundle RESOURCE_BUNDLE = loadBundle();
    private static ResourceBundle loadBundle() {
        return ResourceBundle.getBundle(BUNDLE_NAME);
    }
    ////////////////////////////////////////////////////////////////////////////
    //
    // Strings access
    //
    ////////////////////////////////////////////////////////////////////////////
    public static String getString(String key) {
        try {
            ResourceBundle bundle = Beans.isDesignTime() ? loadBundle() : RESOURCE_BUNDLE;
            return bundle.getString(key);
        } catch (MissingResourceException e) {
            return "!" + key + "!";
        }
    }
}
