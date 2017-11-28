/*
 * This software is provided "AS IS" without a warranty of any kind. You use it
 * on your own risk and responsibility!!! This file is shared under BSD v3
 * license. See readme.txt and BSD3 file for details.
 */

package kendzi.josm.kendzi3d.action;

import static org.openstreetmap.josm.tools.I18n.*;

import java.awt.event.ActionEvent;

import org.openstreetmap.josm.actions.JosmAction;
import org.openstreetmap.josm.data.coor.EastNorth;
import org.openstreetmap.josm.gui.MainApplication;

import com.google.inject.Inject;

import kendzi.jogl.camera.Camera;
import kendzi.jogl.camera.SimpleMoveAnimator;
import kendzi.josm.kendzi3d.data.perspective.Perspective3D;
import kendzi.josm.kendzi3d.data.perspective.Perspective3dProvider;

/**
 * Move camera action.
 *
 * @author Tomasz Kędziora (Kendzi)
 *
 */
public class MoveCameraAction extends JosmAction {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    @Inject
    private Perspective3dProvider perspective3dProvider;

    @Inject
    private SimpleMoveAnimator simpleMoveAnimator;

    /**
     * Constructor.
     *
     */

    public MoveCameraAction() {
        super(tr("Move camera"), "1306318146_build__24", tr("Move camera"), null, false);
    }

    @Override
    public void actionPerformed(ActionEvent pE) {

        EastNorth mapCenter = MainApplication.getMap().mapView.getCenter();
        Perspective3D perspective = perspective3dProvider.getPerspective3d();

        double x = perspective.calcX(mapCenter.getX());
        double y = perspective.calcY(mapCenter.getY());

        simpleMoveAnimator.getPoint().x = x;
        simpleMoveAnimator.getPoint().y = Camera.CAM_HEIGHT;
        simpleMoveAnimator.getPoint().z = -y;
    }

}
