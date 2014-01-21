package kendzi.josm.kendzi3d.dao;

import kendzi.josm.kendzi3d.dto.PointModelDTO;

public interface PointModelDao {

    PointModelDTO findAll();

    void save(PointModelDTO pPointModelDTO);

}
