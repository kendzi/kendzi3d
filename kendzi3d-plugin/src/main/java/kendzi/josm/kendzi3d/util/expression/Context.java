/*
 * This software is provided "AS IS" without a warranty of any kind.
 * You use it on your own risk and responsibility!!!
 *
 * This file is shared under BSD v3 license.
 * See readme.txt and BSD3 file for details.
 *
 */

package kendzi.josm.kendzi3d.util.expression;

import java.util.HashMap;
import java.util.Map;

/**
 * Conetxt with variables values.
 *
 * @author Tomasz Kędziora (kendzi)
 *
 */
public class Context {

    private Map<String, Object> variables = new HashMap<String, Object>();

    /**
     * @return variables
     */
    public Map<String, Object> getVariables() {
        return this.variables;
    }

    /** Add variable.
     * @param pName variable name
     * @param pValue variable value
     */
    public void putVariable(String pName, Object pValue) {
        this.variables.put(pName, pValue);
    }

    /** Get variable.
     * @param pName variable name
     * @return variable value
     */
    public Object getVariable(String pName) {
        return this.variables.get(pName);
    }
}
