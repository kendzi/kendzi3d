package kendzi.kendzi3d.expressions.functions;

import kendzi.kendzi3d.expressions.Context;

public interface Function  {
    Object eval(Context context, double ... e);
}