package kendzi.josm.kendzi3d.context;

import java.util.HashMap;
import java.util.Map;

public class ApplicationContext {
//    ApplicationContext context = null;
//    public static ApplicationContext  getContext() {
//        return context
//    }

    Map<String, Object> bean = new HashMap<String, Object>();

    public void addBean(String pName, Object pBean) {
        this.bean.put(pName, pBean);
    }

    public Object getBean(String pName) {
        return this.bean.get(pName);
    }
}
