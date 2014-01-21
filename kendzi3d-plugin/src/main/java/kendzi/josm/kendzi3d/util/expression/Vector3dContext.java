/*
 * This software is provided "AS IS" without a warranty of any kind.
 * You use it on your own risk and responsibility!!!
 *
 * This file is shared under BSD v3 license.
 * See readme.txt and BSD3 file for details.
 *
 */

package kendzi.josm.kendzi3d.util.expression;

import java.util.HashSet;
import java.util.Set;

import kendzi.josm.kendzi3d.util.expression.fun.FunctionBuilder;
import kendzi.josm.kendzi3d.util.expression.fun.SimpleFunction;
import kendzi.josm.kendzi3d.util.expression.fun.VectorFunction;

/**
 * Context for node.
 *
 * @author Tomasz Kędziora (kendzi)
 *
 */
public class Vector3dContext implements CompileContext, FunctionBuilder {

    @Override
    public Set<String> getFunctionsNames() {
        Set<String> fun = new HashSet<String>();
        fun.add("vector");
        return fun;
    }

    @Override
    public SimpleFunction build(String pName, String[] args) {
        if ("vector".equals(pName)) {
            return new VectorFunction(this, args);
        }
        return null;
    }
}
