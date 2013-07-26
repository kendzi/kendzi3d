package kendzi.util.index;

import java.util.ArrayList;
import java.util.List;

public class SimplifyIndex<T> {
    private List<T> data;
    private List<T> sdata;
    private List<Integer> index;
    private List<Integer> sindex;
    private List<Integer> reindex;

    public SimplifyIndex(List<T> data, List<Integer> index) {
        this.data = data;
        this.index = index;
    }

    public void simple() {

        reindex = new ArrayList<Integer>(index.size());
        sindex = new ArrayList<Integer>(index.size());
        sdata = new ArrayList<T>();

        for (Integer i : index) {
            sdata.add(data.get(i));
            sindex.add(sdata.size()-1);
            //			reindex.add()
        }

    }

    public List<T> getSdata() {
        return sdata;
    }

    public List<Integer> getSindex() {
        return sindex;
    }

}
