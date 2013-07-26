package kendzi.util.index;

import java.lang.reflect.Array;


public class SimplifyIndexArray<T> {
    private T[] data;
    private T[] sdata;
    private int[] index;
    private int[] sindex;

    public SimplifyIndexArray(T [] data, int [] index) {
        this.data = data;
        this.index = index;
    }

    public static <E> SimplifyIndexArray<E> simple(E [] data, int [] index, Class<E> c) {
        SimplifyIndexArray<E> simplifyIndexArray = new SimplifyIndexArray<E>(data, index);
        simplifyIndexArray.simple(c);
        return simplifyIndexArray;
    }

    @SuppressWarnings("unchecked")
    public void simple(Class<T> c) {

        this.sindex = new int[this.index.length];

        this.sdata = (T[]) Array.newInstance(c, this.index.length);

        for (int j = 0; j < this.index.length; j++) {
            Integer i  = this.index[j];

            this.sdata[j] = this.data[i];
            this.sindex[j] = j;
        }
    }

    public T[] getSdata() {
        return this.sdata;
    }

    public int[] getSindex() {
        return this.sindex;
    }

}
