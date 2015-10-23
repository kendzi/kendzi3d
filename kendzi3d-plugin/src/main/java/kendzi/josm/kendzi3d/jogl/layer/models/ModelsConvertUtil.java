package kendzi.josm.kendzi3d.jogl.layer.models;

import javax.vecmath.Vector3d;

import org.openstreetmap.josm.actions.search.SearchCompiler;
import org.openstreetmap.josm.actions.search.SearchCompiler.Match;

import generated.NodeModel;
import generated.WayNodeModel;
import kendzi.kendzi3d.expressions.Context;
import kendzi.kendzi3d.expressions.ExpressiongBuilder;
import kendzi.kendzi3d.expressions.expression.Expression;
import kendzi.kendzi3d.resource.inter.ResourceService;
import kendzi.kendzi3d.resource.inter.ResourceUtil;
import kendzi.util.StringUtil;

public class ModelsConvertUtil {

    public static NodeModelConf convert(final NodeModel nodeModel) throws Exception {

        NodeModelConf pm = new NodeModelConf();
        pm.setModel(nodeModel.getModel());
        pm.setModelParameter(nodeModel.getModelParameter());
        pm.setMatcher(compileMatch(nodeModel.getMatcher()));

        if (nodeModel.getTranslateX() != null && nodeModel.getTranslateY() != null && nodeModel.getTranslateZ() != null) {

            pm.setTranslate(new Expression() {
                @Override
                public Object evaluate(Context context) {

                    return new Vector3d(nodeModel.getTranslateX(), nodeModel.getTranslateY(), nodeModel.getTranslateZ());
                }
            });
        } else {
            pm.setTranslate(ExpressiongBuilder.build(nodeModel.getTranslate()));
        }

        String scale = nodeModel.getScale();

        pm.setScale(ExpressiongBuilder.build(scale));

        pm.setDirection(ExpressiongBuilder.build(nodeModel.getDirection()));

        return pm;
    }

    public static WayNodeModelConf convert(WayNodeModel wayNodeModel) throws Exception {

        WayNodeModelConf pm = new WayNodeModelConf();

        pm.setModel(wayNodeModel.getModel());
        pm.setModelParameter(wayNodeModel.getModelParameter());
        pm.setMatcher(compileMatch(wayNodeModel.getMatcher()));
        pm.setFilter(compileMatch(wayNodeModel.getFilter()));

        pm.setTranslate(ExpressiongBuilder.build(wayNodeModel.getTranslate()));

        pm.setScale(ExpressiongBuilder.build(wayNodeModel.getScale()));

        pm.setDirection(ExpressiongBuilder.build(wayNodeModel.getDirection()));

        pm.setOffset(ExpressiongBuilder.build(wayNodeModel.getOffset()));

        return pm;
    }

    private static Match compileMatch(String match) throws Exception {
        try {
            return SearchCompiler.compile(match);
        } catch (Exception e) {
            throw new Exception("can't compile expression for: " + match, e);
        }
    }

    public static String reciveModelPath(String fileUrl, String configurationFile) {

        if (StringUtil.isBlankOrNull(fileUrl)) {
            return fileUrl;
        }

        if (configurationFile.startsWith(ResourceService.PLUGIN_FILE_PREFIX)) {
            // if configuration is stored inside plug-in, all resources should
            // be taken from plug-in jar root
            return ResourceService.PLUGIN_FILE_PREFIX + "/" + removeRoot(fileUrl);
        }

        String directory = ResourceUtil.getUrlDrectory(configurationFile);

        if (StringUtil.isBlankOrNull(directory)) {
            return fileUrl;
        }

        fileUrl = removeRoot(fileUrl);

        return directory + "/" + fileUrl;
    }

    private static String removeRoot(String fileName) {
        if (StringUtil.isBlankOrNull(fileName)) {
            return fileName;
        }
        fileName = fileName.trim();
        if (fileName.startsWith("\\") || fileName.startsWith("/")) {
            return fileName.substring(1);
        }
        return fileName;
    }

}
