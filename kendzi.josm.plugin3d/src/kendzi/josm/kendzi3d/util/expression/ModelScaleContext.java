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
import kendzi.josm.kendzi3d.util.expression.fun.NormalHeightFunction;
import kendzi.josm.kendzi3d.util.expression.fun.ScaleHeightFunction;
import kendzi.josm.kendzi3d.util.expression.fun.SimpleFunction;

/**
 * Context for height.
 *
 * @author Tomasz Kędziora (kendzi)
 *
 */
public class ModelScaleContext implements CompileContext, FunctionBuilder {

    @Override
    public Set<String> getFunctionsNames() {
        Set<String> fun = new HashSet<String>();
        fun.add("scaleHeight");
        fun.add("normalHeight");
        return fun;
    }

    @Override
    public SimpleFunction build(String pName, String[] args) {
        if ("scaleHeight".equals(pName)) {
            return new ScaleHeightFunction(this, args);
        } else if ("normalHeight".equals(pName)) {
            return new NormalHeightFunction(this, args);
        }
        return null;
    }
}
