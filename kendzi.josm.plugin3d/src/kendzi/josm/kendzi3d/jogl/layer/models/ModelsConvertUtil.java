package kendzi.josm.kendzi3d.jogl.layer.models;

import generated.NodeModel;
import generated.WayNodeModel;

import javax.vecmath.Vector3d;

import kendzi.josm.kendzi3d.util.expression.DoubleContext;
import kendzi.josm.kendzi3d.util.expression.SimpleDoubleExpressionParser;
import kendzi.josm.kendzi3d.util.expression.Vector3dContext;

import org.openstreetmap.josm.actions.search.SearchCompiler;

public class ModelsConvertUtil {

    public static NodeModelConf convert(NodeModel nodeModel) throws Exception {

        NodeModelConf pm = new NodeModelConf();
        pm.setModel(nodeModel.getModel());
        pm.setMatcher(SearchCompiler.compile(nodeModel.getMatcher(), false, false));

        pm.setTranslate(new Vector3d(nodeModel.getTranslateX(), nodeModel.getTranslateY(), nodeModel.getTranslateZ()));

        String scale = nodeModel.getScale();

        pm.setScale(SimpleDoubleExpressionParser.<Double>compile(scale, new DoubleContext()));

        Double direction = 0d;
        try {
            direction = (Double.parseDouble(nodeModel.getDirection()));
        } catch (Exception e) {
            //
        }
        if (direction == null) {
            direction = 0d;
        }

        pm.setDirection(direction);
        return pm;
    }

    public static WayNodeModelConf convert(WayNodeModel wayNodeModel) throws Exception {

        WayNodeModelConf pm = new WayNodeModelConf();

        pm.setModel(wayNodeModel.getModel());
        pm.setMatcher(SearchCompiler.compile(wayNodeModel.getMatcher(), false, false));
        pm.setFilter(SearchCompiler.compile(wayNodeModel.getFilter(), false, false));

        DoubleContext pContext = new DoubleContext();
        Vector3dContext vContext = new Vector3dContext();

        pm.setTranslate(SimpleDoubleExpressionParser.<Vector3d>compile(wayNodeModel.getTranslate(), vContext));

        pm.setScale(SimpleDoubleExpressionParser.<Double>compile(wayNodeModel.getScale(), pContext));

        pm.setDirection(SimpleDoubleExpressionParser.<Double>compile(wayNodeModel.getDirection(), pContext));

        pm.setOffset(SimpleDoubleExpressionParser.<Double>compile(wayNodeModel.getOffset(), vContext));

        return pm;
    }
}
