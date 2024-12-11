/*
 * This software is provided "AS IS" without a warranty of any kind.
 * You use it on your own risk and responsibility!!!
 *
 * This file is shared under BSD v3 license.
 * See readme.txt and BSD3 file for details.
 *
 */

package kendzi.josm.kendzi3d.action;

import static org.openstreetmap.josm.tools.I18n.tr;

import com.google.inject.Inject;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.xml.bind.JAXBException;

import kendzi.jogl.texture.library.TextureLibraryStorageService;
import kendzi.jogl.texture.library.UrlTextureLibrary;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openstreetmap.josm.actions.JosmAction;

/**
 * Texture filter toggle action.
 *
 * @author Tomasz Kędziora (Kendzi)
 *
 */
public class LoadTextureLibraryAction extends JosmAction {

    /** Log. */
    private static final Logger log = LogManager.getLogger(LoadTextureLibraryAction.class);

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    /**
     * Texture library service.
     */
    private final TextureLibraryStorageService textureLibraryStorageService;

    final JFileChooser fc = new JFileChooser();

    /**
     * Constructor of wiki texture loader toggle action.
     * 
     * @param pWikiTextureLoaderService
     *            wiki texture loader service
     */
    @Inject
    public LoadTextureLibraryAction(TextureLibraryStorageService TextureLibraryStorageService) {
        super(tr("Load texture library from file"), "1323558253_wikipedia-icon_24", tr("Load texture library from file"), null,
                false);

        this.textureLibraryStorageService = TextureLibraryStorageService;
    }

    /**
     *
     */
    public void loadTextureLibraryFromFile() {
        List<String> errors = null;
        String timestamp = null;
        try {
            int returnVal = this.fc.showOpenDialog(null);

            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File file = this.fc.getSelectedFile();
                // This is where a real application would open the file.
                log.info("Opening: " + file.getName());

                UrlTextureLibrary urlTextureLibrary = new UrlTextureLibrary();
                urlTextureLibrary.setUrl(file.toURI().toURL());

                boolean overwrite = showOverwriteDialog();

                urlTextureLibrary.setOverwrite(overwrite);

                this.textureLibraryStorageService.loadUserFile(urlTextureLibrary);
            } else {
                log.info("Open command cancelled by user.");
            }

            //// LoadRet load = this.textureLibraryStorageService.load();
            // errors = load.getErrors();
            // timestamp = load.getTimestamp();

        } catch (JAXBException | IOException e) {
            log.error(e, e);
            showError(e);
        }

        // if (errors != null && !errors.isEmpty()) {
        //
        // StringBuffer sb = new StringBuffer();
        // for (String err: errors ) {
        // sb.append(err);
        // sb.append("\n");
        // }
        //
        // JOptionPane.showMessageDialog(null,
        // tr("Error downloding textures from urls: ") + "\n" + sb,
        // "Error",
        // JOptionPane.ERROR_MESSAGE);
        // } else {
        // JOptionPane.showMessageDialog(null,
        // tr("Downloded textures from wiki timestamp: " + timestamp + " to path: ") +
        // "\n"
        // + this.wikiTextureLoaderService.getTexturesPath() ,
        // "Info",
        // JOptionPane.INFORMATION_MESSAGE);
        // }
    }

    /**
     * @return
     */
    public boolean showOverwriteDialog() {
        int n = showNegativeConfirmDialog(null, "Overwrite values", "Overwrite values");
        boolean overwrite = n == JOptionPane.YES_OPTION;
        return overwrite;
    }

    private void showError(Exception e) {
        // custom title, error icon
        JOptionPane.showMessageDialog(null, "Error loading textures definitions from file: " + e.getMessage(), "Error",
                JOptionPane.ERROR_MESSAGE);
    }

    @Override
    public void actionPerformed(ActionEvent pE) {
        loadTextureLibraryFromFile();
    }

    @Override
    protected void updateEnabledState() {
        // setEnabled(Main.map != null && Main.main.getEditLayer() != null);
    }

    public static int showNegativeConfirmDialog(Component parentComponent, Object message, String title) {
        List<Object> options = new ArrayList<>();
        Object defaultOption;

        options.add(UIManager.getString("OptionPane.yesButtonText"));
        options.add(UIManager.getString("OptionPane.noButtonText"));
        defaultOption = UIManager.getString("OptionPane.noButtonText");

        return JOptionPane.showOptionDialog(parentComponent, message, title, JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE, null, options.toArray(), defaultOption);
    }

}
