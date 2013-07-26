package kendzi.util.index;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

public class SimplifyIndexTest {

    @Test
    public void test() {

        List<Integer> data = new ArrayList<Integer>();
        data.add(1);
        data.add(2);
        data.add(3);
        data.add(4);
        data.add(5);

        List<Integer> index = new ArrayList<Integer>();
        index.add(2);
        index.add(4);

        SimplifyIndex<Integer> si = new SimplifyIndex<Integer>(data, index);
        si.simple();

        Assert.assertEquals(2, si.getSdata().size());
        Assert.assertEquals(2, si.getSindex().size());

        Assert.assertEquals(3, (int) si.getSdata().get(0));
        Assert.assertEquals(5, (int) si.getSdata().get(1));

        Assert.assertEquals(0, (int) si.getSindex().get(0));
        Assert.assertEquals(1, (int) si.getSindex().get(1));

        //		System.out.println("data");
        //		print(si.getSdata());
        //		System.out.println("index");
        //		print(si.getSindex());


    }

    private static void print(List<Integer> sdata) {
        for (Object o : sdata) {
            System.out.println(o);
        }

    }

}
