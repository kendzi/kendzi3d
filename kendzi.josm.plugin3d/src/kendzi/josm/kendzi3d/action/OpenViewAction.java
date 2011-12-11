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

import kendzi.josm.kendzi3d.ui.View3dGLFrame;

import org.apache.log4j.Logger;
import org.openstreetmap.josm.actions.JosmAction;

public class OpenViewAction extends JosmAction {

    /** Log. */
    private static final Logger log = Logger.getLogger(OpenViewAction.class);

    //    private final WMSInfo info;

    public OpenViewAction() {
        super(tr("3D View"), "wmsmenu", tr("Open 3D View"), null, false);
        putValue("toolbar", "3dView_run" );



        new View3dGLFrame();

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        //        WMSLayer wmsLayer = new WMSLayer(info);
        //        Main.main.addLayer(wmsLayer);
    }
};
