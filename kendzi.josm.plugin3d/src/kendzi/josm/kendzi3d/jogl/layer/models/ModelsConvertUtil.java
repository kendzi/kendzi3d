package kendzi.josm.kendzi3d.jogl.layer.models;

import generated.NodeModel;
import generated.WayNodeModel;

import javax.vecmath.Vector3d;

import kendzi.kendzi3d.expressions.ExpressiongBuilder;

import org.openstreetmap.josm.actions.search.SearchCompiler;

public class ModelsConvertUtil {

    public static NodeModelConf convert(NodeModel nodeModel) throws Exception {

        NodeModelConf pm = new NodeModelConf();
        pm.setModel(nodeModel.getModel());
        pm.setModelParameter(nodeModel.getModelParameter());
        pm.setMatcher(SearchCompiler.compile(nodeModel.getMatcher(), false, false));

        pm.setTranslate(new Vector3d(nodeModel.getTranslateX(), nodeModel.getTranslateY(), nodeModel.getTranslateZ()));

        String scale = nodeModel.getScale();

        pm.setScale(ExpressiongBuilder.build(scale));

        pm.setDirection(ExpressiongBuilder.build(nodeModel.getDirection()));

        return pm;
    }

    public static WayNodeModelConf convert(WayNodeModel wayNodeModel) throws Exception {

        WayNodeModelConf pm = new WayNodeModelConf();

        pm.setModel(wayNodeModel.getModel());
        pm.setModelParameter(wayNodeModel.getModelParameter());
        pm.setMatcher(SearchCompiler.compile(wayNodeModel.getMatcher(), false, false));
        pm.setFilter(SearchCompiler.compile(wayNodeModel.getFilter(), false, false));

        pm.setTranslate(ExpressiongBuilder.build(wayNodeModel.getTranslate()));

        pm.setScale(ExpressiongBuilder.build(wayNodeModel.getScale()));

        pm.setDirection(ExpressiongBuilder.build(wayNodeModel.getDirection()));

        pm.setOffset(ExpressiongBuilder.build(wayNodeModel.getOffset()));

        return pm;
    }
}
