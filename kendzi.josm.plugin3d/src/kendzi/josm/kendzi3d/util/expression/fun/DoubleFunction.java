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

public class DoubleFunction implements SimpleFunction {

    CompileContext context;

    private Double num;

    public DoubleFunction(CompileContext pContext, Double pDouble) {
        this.num = pDouble;
        this.context = pContext;
    }

    @Override
    public Double eval(Context context) {

        return this.num;
    }

    @Override
    public CompileContext getContext() {
        return this.context;
    }

}
