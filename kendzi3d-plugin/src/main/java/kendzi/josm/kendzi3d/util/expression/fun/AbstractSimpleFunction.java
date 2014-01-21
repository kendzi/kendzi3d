package kendzi.josm.kendzi3d.util.expression.fun;

import kendzi.josm.kendzi3d.util.expression.CompileContext;
import kendzi.josm.kendzi3d.util.expression.Context;

public abstract class AbstractSimpleFunction<T> implements SimpleFunction<T> {

    String[] args;
    private CompileContext context;

    public AbstractSimpleFunction(CompileContext context2, String[] args) {
        this.args = args;
        this.context = context2;
    }

    @SuppressWarnings("unchecked")
    public <W> W getOptionalArgument(int num, Class<W> type) {
        if (args == null || args.length < num) {
            return null;
        }

        return getArgument(num, type);
    }

    /**
     * @param num
     * @param type
     * @return
     */
    private <W> W getArgument(int num, Class<W> type) {
        if (type.isAssignableFrom(Double.class)) {
            return (W) ((Object) Double.parseDouble(args[num]));
        } else if (type.isAssignableFrom(String.class)) {
            return (W) args[num];
        }
        throw new RuntimeException("uknown type of argument");
    }

    @SuppressWarnings("unchecked")
    public <W> W getRequiredArgument(int num, Class<W> type) {
        if (args == null || args.length < num) {
            throw new RuntimeException("there is no required argument: " + num + " type "+ type);
        }

        return getArgument(num, type);
    }


    public <W> W getRequiredContextVariable(String string, Context context, Class<W> class1) {
        Object obj = context.getVariable("bisector");
        if (obj == null) {
            throw new RuntimeException(String.format("can't take required variable %s of type %s from context", string, class1));

        }
        if (!class1.isAssignableFrom(obj.getClass())) {
             throw new RuntimeException(String.format("can't take required variable %s of type %s from context, wrong class", string, class1));
        }

        return (W) obj;
    }


    @Override
    public CompileContext getContext() {
        return context;
    }
}
