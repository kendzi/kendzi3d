package kendzi.kendzi3d.render.dataset;

import kendzi.josm.datasource.Kendzi3dPostgisDataSource;
import kendzi.kendzi3d.render.conf.RenderDataSourceConf;
import kendzi.kendzi3d.render.conf.RenderDataSourceConf.InputSource;

public class DataSetProviderFactory {

    public static DataSetProvider loadConf(RenderDataSourceConf conf) {

        if (InputSource.FILE.equals(conf.getInputSource())) {
            return new FileDataProvider(conf.getFileUrl());
        } else if (InputSource.PGSQL.equals(conf.getInputSource())) {

            Kendzi3dPostgisDataSource ds = new Kendzi3dPostgisDataSource();

            ds.setUrl(conf.getJdbcUrl());
            ds.setUsername(conf.getJdbcUsername());
            ds.setPassword(conf.getJdbcPassword());

            return new PGSQLDataProvider(ds);
        }
        throw new RuntimeException("bad Data Source Provider type: " + conf.getInputSource());
    }
}
