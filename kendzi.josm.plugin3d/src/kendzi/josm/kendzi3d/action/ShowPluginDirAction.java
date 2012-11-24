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

import java.awt.event.ActionEvent;

import javax.swing.JOptionPane;
import javax.swing.JTextArea;

import kendzi.josm.kendzi3d.module.binding.Kendzi3dPluginDirectory;

import org.apache.log4j.Logger;
import org.openstreetmap.josm.actions.JosmAction;

import com.google.inject.Inject;

/**
 * Texture filter toggle action.
 *
 * @author Tomasz Kędziora (Kendzi)
 *
 */
public class ShowPluginDirAction extends JosmAction {

    /** Log. */
    private static final Logger log = Logger.getLogger(ShowPluginDirAction.class);

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    /**
     * Plugin directory.
     */
    @Inject @Kendzi3dPluginDirectory
    private String pluginDir;

    /**
     * Constructor.
     */
    @Inject
    public ShowPluginDirAction() {
        super(
                tr("Show plugin directory"),
                "1323558253_wikipedia-icon_24",
                tr("Show plugin directory"),
                null,
                false
        );
    }

    private void showPluginDirectory() {

        JTextArea area = new JTextArea("Plugin directory: \n" + this.pluginDir);
        area.setEditable(false);

        JOptionPane.showMessageDialog(null,
                area,
                "Directory",
                JOptionPane.INFORMATION_MESSAGE);
    }

    @Override
    public void actionPerformed(ActionEvent pE) {
        showPluginDirectory();
    }

    @Override
    protected void updateEnabledState() {
//        setEnabled(Main.map != null && Main.main.getEditLayer() != null);
    }
}
