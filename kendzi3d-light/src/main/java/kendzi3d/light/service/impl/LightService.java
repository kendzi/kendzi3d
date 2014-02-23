/*
 * This software is provided "AS IS" without a warranty of any kind. You use it
 * on your own risk and responsibility!!! This file is shared under BSD v3
 * license. See readme.txt and BSD3 file for details.
 */
package kendzi3d.light.service.impl;

import javax.inject.Inject;
import javax.vecmath.Vector3d;

import kendzi.math.geometry.point.TransformationMatrix3d;
import kendzi3d.light.dao.LightDao;
import kendzi3d.light.dto.LightConfiguration;
import kendzi3d.light.service.LightRenderService;
import kendzi3d.light.service.LightStorageService;

import org.ejml.simple.SimpleMatrix;

/**
 * Store light configuration, cache data for rendering.
 * 
 * @author Tomasz Kedziora (Kendzi)
 * 
 */
public class LightService implements LightRenderService, LightStorageService {

    private LightDao lightDao;

    private float[] diffuseLightColor = new float[4];

    private float[] ambientLightColor = new float[4];

    private float[] lightPosition = new float[4];

    @Inject
    public LightService(LightDao lightDao) {
        this.lightDao = lightDao;

        init();
    }

    @Override
    public float[] getAmbientLightColor() {
        return ambientLightColor;
    }

    @Override
    public float[] getDiffuseLightColor() {
        return diffuseLightColor;
    }

    @Override
    public float[] getLightPosition() {
        return lightPosition;
    }

    @Override
    public LightConfiguration load() {
        return lightDao.load();
    }

    @Override
    public LightConfiguration loadDefault() {
        return new LightConfiguration();
    }

    @Override
    public void save(LightConfiguration lightLocation) {
        saveRenderConfiguration(lightLocation);
        lightDao.save(lightLocation);
    }

    protected void saveRenderConfiguration(LightConfiguration lightLocation) {
        lightLocation.getAmbientColor().getColorComponents(ambientLightColor);
        lightLocation.getDiffuseColor().getColorComponents(diffuseLightColor);

        Vector3d vector = new Vector3d(0, 0, -1);

        SimpleMatrix directionMatric = TransformationMatrix3d.rotYA(Math.toRadians(-lightLocation.getDirection()));
        SimpleMatrix angleMatric = TransformationMatrix3d.rotXA(Math.toRadians(lightLocation.getAngle()));

        SimpleMatrix matrix = directionMatric.mult(angleMatric);

        Vector3d position = TransformationMatrix3d.transform(vector, matrix);

        lightPosition[0] = (float) position.x;
        lightPosition[1] = (float) position.y;
        lightPosition[2] = (float) position.z;
        lightPosition[3] = 0f;
    }

    private void init() {
        LightConfiguration light = lightDao.load();
        if (light == null) {
            light = loadDefault();
        }

        saveRenderConfiguration(light);
    }

}
