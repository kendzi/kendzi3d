package kendzi.kendzi3d.expressions;

import static org.junit.Assert.*;

import javax.vecmath.Vector2d;
import javax.vecmath.Vector3d;

import org.junit.Test;
import org.openstreetmap.josm.data.osm.Way;
import org.openstreetmap.josm.spi.preferences.Config;
import org.openstreetmap.josm.spi.preferences.MemoryPreferences;

import kendzi.kendzi3d.expressions.exeption.ExpressionExeption;
import kendzi.kendzi3d.expressions.expression.Expression;
import kendzi.kendzi3d.expressions.functions.HeightFunction;
import kendzi.kendzi3d.expressions.functions.Vector3dFunction;
import kendzi.kendzi3d.expressions.functions.Vector3dXFunction;
import kendzi.kendzi3d.expressions.functions.Vector3dYFunction;
import kendzi.kendzi3d.expressions.functions.Vector3dZFunction;
import kendzi.kendzi3d.expressions.functions.WayNodeDirectionFunction;

/**
 * Unit test for ExpressiongBuilder.
 */
public class OsmFunctionTest {
    {
        Config.setPreferencesInstance(new MemoryPreferences());
    }

    @Test
    public void testExpression1() throws ExpressionExeption {
        Context c = new Context();
        c.getVariables().put("bisector", new Vector2d(1, 1));
        c.registerFunction(new WayNodeDirectionFunction());

        Expression build = ExpressiongBuilder.build("wayNodeDirection() +10");
        Object value = build.evaluate(c);
        assertEquals(90d + 45d + 10d, value);
    }

    @Test
    public void testExpression2() throws ExpressionExeption {

        Context c = new Context();
        Way w = new Way();
        w.put("height", "10");

        c.getVariables().put("osm", w);
        c.registerFunction(new HeightFunction());

        Expression build = ExpressiongBuilder.build("height(5)");
        Object value = build.evaluate(c);
        assertEquals(10d, value);
    }

    @Test
    public void testExpression3() throws ExpressionExeption {

        Context c = new Context();
        Way w = new Way();

        c.getVariables().put("osm", w);
        c.registerFunction(new HeightFunction());

        Expression build = ExpressiongBuilder.build("height(5)");
        Object value = build.evaluate(c);
        assertEquals(5d, value);
    }

    @Test
    public void testExpression4() throws ExpressionExeption {

        Context c = new Context();
        Way w = new Way();

        c.getVariables().put("osm", w);
        c.registerFunction(new HeightFunction());

        Expression build = ExpressiongBuilder.build("height(5) + 1");
        Object value = build.evaluate(c);
        assertEquals(6d, value);
    }

    @Test
    public void testExpression5() throws ExpressionExeption {

        Context c = new Context();
        Way w = new Way();

        c.getVariables().put("osm", w);
        c.registerFunction(new HeightFunction());

        Expression build = ExpressiongBuilder.build("0.5 * height(5)");
        Object value = build.evaluate(c);
        assertEquals(2.5d, value);
    }

    @Test
    public void testExpression6() throws ExpressionExeption {

        Context c = new Context();

        c.registerFunction(new Vector3dFunction());

        Expression build = ExpressiongBuilder.build("vector()");
        Object value = build.evaluate(c);
        assertEquals(new Vector3d(0, 0, 0), value);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testExpression7() throws ExpressionExeption {

        Context c = new Context();

        c.registerFunction(new Vector3dFunction());

        Expression build = ExpressiongBuilder.build("vector(1)");
        Object value = build.evaluate(c);
        assertEquals(new Vector3d(0, 0, 0), value);
    }

    @Test
    public void testExpression8() throws ExpressionExeption {

        Context c = new Context();

        c.registerFunction(new Vector3dFunction());

        Expression build = ExpressiongBuilder.build("vector(1,2,3)");
        Object value = build.evaluate(c);
        assertEquals(new Vector3d(1, 2, 3), value);
    }

    @Test
    public void testExpression9() throws ExpressionExeption {

        Context c = new Context();

        c.registerFunction(new Vector3dXFunction());

        Expression build = ExpressiongBuilder.build("vectorX(11)");
        Object value = build.evaluate(c);
        assertEquals(new Vector3d(11, 0, 0), value);
    }

    @Test
    public void testExpression10() throws ExpressionExeption {

        Context c = new Context();

        c.registerFunction(new Vector3dYFunction());

        Expression build = ExpressiongBuilder.build("vectorY(11)");
        Object value = build.evaluate(c);
        assertEquals(new Vector3d(0, 11, 0), value);
    }

    @Test
    public void testExpression11() throws ExpressionExeption {

        Context c = new Context();

        c.registerFunction(new Vector3dZFunction());

        Expression build = ExpressiongBuilder.build("vectorZ(11)");
        Object value = build.evaluate(c);
        assertEquals(new Vector3d(0, 0, 11), value);
    }

    @Test()
    public void testExpression12() throws ExpressionExeption {

        Context c = new Context();

        c.registerFunction(new Vector3dFunction());

        Expression build = ExpressiongBuilder.build("vector(-12,0,0)");
        Object value = build.evaluate(c);
        assertEquals(new Vector3d(-12, 0, 0), value);
    }

}
