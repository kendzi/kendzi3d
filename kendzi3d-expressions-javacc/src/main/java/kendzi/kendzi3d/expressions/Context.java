package kendzi.kendzi3d.expressions;

import java.util.HashMap;
import java.util.Map;

import kendzi.kendzi3d.expressions.functions.Function;
import kendzi.kendzi3d.expressions.functions.NamedFunction;

public class Context {
    private Map<String, Object> variables = new HashMap<String, Object>();
    private Map<String, Function> functions = new HashMap<String, Function>();
    /**
     * @return the variables
     */
    public Map<String, Object> getVariables() {
        return variables;
    }
    /**
     * @param variables the variables to set
     */
    public void setVariables(Map<String, Object> variables) {
        this.variables = variables;
    }
    /**
     * @return the functions
     */
    public Map<String, Function> getFunctions() {
        return functions;
    }
    /**
     * @param functions the functions to set
     */
    public void setFunctions(Map<String, Function> functions) {
        this.functions = functions;
    }

    public void registerFunction(NamedFunction function) {
        this.functions.put(function.functionName(), function);
    }

    @SuppressWarnings("unchecked")
    public static <W> W getRequiredContextVariable(String variableName, Context context, Class<W> class1) {
        Object obj = context.getVariables().get(variableName);
        if (obj == null) {
            throw new RuntimeException(String.format("can't take required variable %s of type %s from context", variableName, class1));

        }
        if (!class1.isAssignableFrom(obj.getClass())) {
            throw new RuntimeException(String.format("can't take required variable %s of type %s from context, wrong class", variableName, class1));
        }

        return (W) obj;
    }

}
