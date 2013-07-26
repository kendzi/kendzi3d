package kendzi.util.index;

import org.junit.Assert;
import org.junit.Test;

public class SimplifyIndexTestArray {

    @Test
    public void test() {

        Integer[] data = new Integer[] {
                1, 2, 3, 4, 5
        };

        int[] index = new int[] {
                2, 4
        };

        SimplifyIndexArray<Integer> si = new SimplifyIndexArray<Integer>(data, index);
        si.simple(Integer.class);

        System.out.println("data");
        print(si.getSdata());
        System.out.println("index");
        print(si.getSindex());

        Assert.assertEquals(2, si.getSdata().length);
        Assert.assertEquals(2, si.getSindex().length);

        Assert.assertEquals(3, (int) si.getSdata()[0]);
        Assert.assertEquals(5, (int) si.getSdata()[1]);

        Assert.assertEquals(0, si.getSindex()[0]);
        Assert.assertEquals(1, si.getSindex()[1]);



    }

    private static void print(Integer[] sdata) {
        for (Object o : sdata) {
            System.out.println(o);
        }
    }
    private static void print(int[] sdata) {
        for (Object o : sdata) {
            System.out.println(o);
        }
    }

}
