package kendzi.kendzi3d.tile.server.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import kendzi.kendzi3d.tile.server.dto.RenderStatus;

import org.springframework.stereotype.Service;

@Service
public class RenderStatusService {

    Vector<RenderStatus> renderStatusList = new Vector<RenderStatus>();

    public synchronized void save(RenderStatus renderStatus) {
        renderStatusList.add(renderStatus);
    }

    public synchronized List<RenderStatus> findAll() {
        List<RenderStatus> ret = new ArrayList<RenderStatus>();
        for (int i = renderStatusList.size() - 1; i >= 0; i--) {
            ret.add(renderStatusList.get(i));
        }
        return ret;
    }
}
