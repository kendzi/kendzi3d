package kendzi.kendzi3d.expressions;

import java.util.HashMap;
import java.util.Map;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import kendzi.kendzi3d.expressions.exeption.ExpressionExeption;
import kendzi.kendzi3d.expressions.expression.Expression;
import kendzi.kendzi3d.expressions.functions.Function;
import kendzi.kendzi3d.expressions.functions.OneParamFunction;
import kendzi.kendzi3d.expressions.functions.ZeroParamFunction;

/**
 * Unit test for ExpressiongBuilder.
 */
public class ExpressiongBuilderTest extends TestCase {
    /**
     * Create the test case
     * 
     * @param testName name of the test case
     */
    public ExpressiongBuilderTest(String testName) {
        super(testName);
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite() {
        return new TestSuite(ExpressiongBuilderTest.class);
    }

    /**
     * Rigourous Test :-)
     */
    public void testApp() {
        assertTrue(true);
    }

    public void testExpression1() throws ExpressionExeption {
        Expression build = ExpressiongBuilder.build("1+2");
        Object value = build.evaluate(null);
        assertEquals(3d, value);
    }

    public void testExpression2() throws ExpressionExeption {
        Expression build = ExpressiongBuilder.build("(1+2)*3");
        Object value = build.evaluate(null);
        assertEquals(9d, value);
    }

    public void testExpression3() throws ExpressionExeption {
        Expression build = ExpressiongBuilder.build("1+2*3");
        Object value = build.evaluate(null);
        assertEquals(7d, value);
    }


    public void testExpression4() throws ExpressionExeption {
        Context c = new Context();
        Map<String, Object> var = new HashMap<String, Object>();
        var.put("A", 5d);
        c.setVariables(var);

        Expression build = ExpressiongBuilder.build("1+A");
        Object value = build.evaluate(c);
        assertEquals(6d, value);
    }

    //    class DoubleFunction extends ArgExpression<Double> {
    //
    //        public DoubleFunction(Class<Double> expectedParamType) {
    //            super(expectedParamType);
    //            // TODO Auto-generated constructor stub
    //        }
    //
    //    }




    interface OneArgFunction  {
        Expression build(Expression e);
    }


    public void testExpression5() throws ExpressionExeption {
        Context c = new Context();
        Map<String, Object> var = new HashMap<String, Object>();
        var.put("A", 5d);
        c.setVariables(var);


        Map<String, Function> fun = new HashMap<String, Function>();
        c.setFunctions(fun);
        fun.put("funArgPlusOne", new OneParamFunction() {

            @Override
            public double evalOneParam(Context context, double param) {
                return 1 + param;
            }
        });


        Expression build = ExpressiongBuilder.build("1+funArgPlusOne(1)");
        Object value = build.evaluate(c);
        assertEquals(3d, value);
    }

    public void testExpression6() throws ExpressionExeption {
        Context c = new Context();
        Map<String, Object> var = new HashMap<String, Object>();
        var.put("A", 5d);
        c.setVariables(var);


        Map<String, Function> fun = new HashMap<String, Function>();
        c.setFunctions(fun);
        fun.put("funTree", new ZeroParamFunction() {

            @Override
            public double evalZeroParam(Context context) {
                return 3;
            }
        });


        Expression build = ExpressiongBuilder.build("funTree()");
        Object value = build.evaluate(c);
        assertEquals(3d, value);
    }

    public void testExpression7() throws ExpressionExeption {
        Expression build = ExpressiongBuilder.build("1.1+2.2*3.3");
        Object value = build.evaluate(null);
        assertEquals(8.36d, value);
    }

}
