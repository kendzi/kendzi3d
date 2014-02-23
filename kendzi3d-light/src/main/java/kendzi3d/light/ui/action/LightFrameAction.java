/*
 * This software is provided "AS IS" without a warranty of any kind. You use it
 * on your own risk and responsibility!!! This file is shared under BSD v3
 * license. See readme.txt and BSD3 file for details.
 */
package kendzi3d.light.ui.action;

import java.awt.Color;
import java.awt.EventQueue;

import javax.inject.Inject;
import javax.swing.JColorChooser;

import kendzi.josm.kendzi3d.ui.validate.ValidateUtil;
import kendzi.util.ParseUtil;
import kendzi3d.light.dao.MemoryLightDao;
import kendzi3d.light.dto.LightConfiguration;
import kendzi3d.light.service.impl.LightService;
import kendzi3d.light.ui.LightFrame;

/**
 * Light frame actions.
 * 
 * @author Tomasz Kedziora (Kendzi)
 * 
 */
public class LightFrameAction extends LightFrame {

    private static final long serialVersionUID = 1L;

    private LightService lightService;

    @Inject
    public LightFrameAction(LightService lightService) {
        this.lightService = lightService;

        load();
    }

    private void load() {
        LightConfiguration light = lightService.load();

        if (light == null) {
            light = lightService.loadDefault();
        }

        fillForm(light);
    }

    private void fillForm(LightConfiguration light) {
        getDirectionText().setText("" + light.getDirection());
        getAngleText().setText("" + light.getAngle());

        getLblAmbientColor().setBackground(light.getAmbientColor());
        getLblDiffuseColor().setBackground(light.getDiffuseColor());
    }

    @Override
    protected void onDefault() {

        LightConfiguration light = lightService.loadDefault();

        fillForm(light);
    }

    @Override
    protected void onOk() {

        String directionText = getDirectionText().getText();
        String angleText = getAngleText().getText();

        Color ambientColor = getLblAmbientColor().getBackground();
        Color diffuseColor = getLblDiffuseColor().getBackground();

        Double direction = ParseUtil.parseDouble(directionText);
        Double angle = ParseUtil.parseDouble(angleText);

        if (validate(direction, angle)) {
            lightService.save(new LightConfiguration(direction, angle, ambientColor, diffuseColor));
            // dispose();
        }
    }

    private boolean validate(Double direction, Double angle) {
        boolean directionError = direction == null || direction < 0 || direction > 360;
        boolean angleError = angle == null || angle < 0 || angle > 90;

        ValidateUtil.setComponentError(getDirectionText(), directionError);
        ValidateUtil.setComponentError(getAngleText(), angleError);

        return !(directionError || angleError);
    }

    @Override
    protected void onSelectAmbientColor() {
        Color color = JColorChooser.showDialog(this, "Select color", getLblAmbientColor().getBackground());
        getLblAmbientColor().setBackground(color);
    }

    @Override
    protected void onSelectDiffuseColor() {
        Color color = JColorChooser.showDialog(this, "Select color", getLblDiffuseColor().getBackground());
        getLblDiffuseColor().setBackground(color);
    }

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    LightFrame frame = new LightFrameAction(new kendzi3d.light.service.impl.LightService(new MemoryLightDao()));
                    frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
