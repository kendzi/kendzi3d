package kendzi.kendzi3d.expressions;

import javax.vecmath.Vector2d;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import kendzi.kendzi3d.expressions.exeption.ExpressionExeption;
import kendzi.kendzi3d.expressions.expression.Expression;
import kendzi.kendzi3d.expressions.functions.HeightFunction;
import kendzi.kendzi3d.expressions.functions.WayNodeDirectionFunction;

import org.openstreetmap.josm.Main;
import org.openstreetmap.josm.data.Preferences;
import org.openstreetmap.josm.data.osm.Way;

/**
 * Unit test for ExpressiongBuilder.
 */
public class OsmFunctionTest extends TestCase {
    /**
     * Create the test case
     * 
     * @param testName name of the test case
     */
    public OsmFunctionTest(String testName) {
        super(testName);
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite() {
        return new TestSuite(OsmFunctionTest.class);
    }


    public void testExpression1() throws ExpressionExeption {
        Context c = new Context();
        c.getVariables().put("bisector", new Vector2d(1, 1));
        c.registerFunction(new WayNodeDirectionFunction());

        Expression build = ExpressiongBuilder.build("wayNodeDirection() +10");
        Object value = build.evaluate(c);
        assertEquals(90d + 45d + 10d, value);
    }

    public void testExpression2() throws ExpressionExeption {
        Main.pref = new Preferences();

        Context c = new Context();
        Way w = new Way();
        w.put("height", "10");

        c.getVariables().put("osm", w);
        c.registerFunction(new HeightFunction());

        Expression build = ExpressiongBuilder.build("height(5)");
        Object value = build.evaluate(c);
        assertEquals(10d, value);
    }

    public void testExpression3() throws ExpressionExeption {
        Main.pref = new Preferences();

        Context c = new Context();
        Way w = new Way();

        c.getVariables().put("osm", w);
        c.registerFunction(new HeightFunction());

        Expression build = ExpressiongBuilder.build("height(5)");
        Object value = build.evaluate(c);
        assertEquals(5d, value);
    }

    public void testExpression4() throws ExpressionExeption {
        Main.pref = new Preferences();

        Context c = new Context();
        Way w = new Way();

        c.getVariables().put("osm", w);
        c.registerFunction(new HeightFunction());

        Expression build = ExpressiongBuilder.build("height(5) + 1");
        Object value = build.evaluate(c);
        assertEquals(6d, value);
    }

    public void testExpression5() throws ExpressionExeption {
        Main.pref = new Preferences();

        Context c = new Context();
        Way w = new Way();

        c.getVariables().put("osm", w);
        c.registerFunction(new HeightFunction());

        Expression build = ExpressiongBuilder.build("0.5 * height(5)");
        Object value = build.evaluate(c);
        assertEquals(2.5d, value);
    }

}
