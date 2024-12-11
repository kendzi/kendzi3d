package kendzi.kendzi3d.expressions.functions;

import kendzi.kendzi3d.expressions.Context;

public interface Function<T> {
    T eval(Context context, double... e);
}