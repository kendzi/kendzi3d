package kendzi.kendzi3d.expressions.functions;

import kendzi.kendzi3d.expressions.Context;

public interface Function  {
    double eval(Context context, double ... e);
}