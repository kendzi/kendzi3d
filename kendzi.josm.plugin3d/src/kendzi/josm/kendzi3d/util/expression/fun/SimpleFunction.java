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

/**
 * Function definition.
 *
 * @author Tomasz Kędziora (kendzi)
 *
 */
public interface SimpleFunction {

    /**
     * @return context
     */
    CompileContext getContext();

    /**
     * @param context local context
     * @return function value in context.
     */
    Double eval(Context context);

}
