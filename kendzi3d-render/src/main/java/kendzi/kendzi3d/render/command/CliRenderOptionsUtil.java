package kendzi.kendzi3d.render.command;

import java.util.ArrayList;
import java.util.List;

import kendzi.josm.kendzi3d.util.StringUtil;
import kendzi.kendzi3d.render.conf.RenderDataSourceConf.InputSource;

public class CliRenderOptionsUtil {

    public static List<String> validate(CliRenderOptions o) {

        List<String> ret = new ArrayList<String>();
        if (InputSource.FILE.equals(o.getInputSource())) {
            if (StringUtil.isBlankOrNull(o.getFileUrl())) {
                ret.add("for file input source, setup file location by parameter --file.url");
            }

        } else if (InputSource.PGSQL.equals(o.getInputSource())) {
            if (o.isDbUrl()) {
                ret.add("for PGSQL input source, setup db url by parameter --db.url");
            }
            if (o.isDbUsername()) {
                ret.add("for PGSQL input source, setup db username by parameter --db.username");
            }
            if (o.isDbPassword()) {
                ret.add("for PGSQL input source, setup db password by parameter --db.password");
            }
        }

        if (!o.isOviewBbox() && !o.isOviewTile()) {
            ret.add("chose location for render by option --oview.bbox or --oview.tile");
        }

        return ret;
    }
}
