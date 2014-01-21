/*
 * This software is provided "AS IS" without a warranty of any kind.
 * You use it on your own risk and responsibility!!!
 *
 * This file is shared under BSD v3 license.
 * See readme.txt and BSD3 file for details.
 *
 */


package kendzi.josm.kendzi3d.util.expression;

import java.util.Set;

import kendzi.josm.kendzi3d.util.expression.fun.FunctionBuilder;

/**
 * Functions names.
 *
 * @author Tomasz Kędziora (kendzi)
 *
 */
public interface CompileContext extends FunctionBuilder {

    /** Functions in context.
     * @return function names
     */
    Set<String> getFunctionsNames();
}
