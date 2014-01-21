/*
 * This software is provided "AS IS" without a warranty of any kind.
 * You use it on your own risk and responsibility!!!
 *
 * This file is shared under BSD v3 license.
 * See readme.txt and BSD3 file for details.
 *
 */

package kendzi.josm.kendzi3d.util.expression.fun;

import kendzi.josm.kendzi3d.util.expression.CompileContext;
import kendzi.josm.kendzi3d.util.expression.Context;

public class DoubleFunction extends AbstractSimpleFunction<Double> {

    public DoubleFunction(CompileContext context, String [] args) {
        super(context, args);
    }

    @Override
    public Double eval(Context context) {
        return getRequiredArgument(0, Double.class);
    }

    @Override
    public String getDefaultName() {
        return "double_fun";
    }

    @Override
    public CompileContext getContext() {
        return null;
    }

}
