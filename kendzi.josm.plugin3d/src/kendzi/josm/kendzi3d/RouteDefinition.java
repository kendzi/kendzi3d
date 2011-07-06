package kendzi.josm.kendzi3d;

import java.awt.Color;

import org.openstreetmap.josm.actions.search.SearchCompiler;
import org.openstreetmap.josm.actions.search.SearchCompiler.Match;
import org.openstreetmap.josm.actions.search.SearchCompiler.ParseError;
import org.openstreetmap.josm.data.osm.OsmPrimitive;

public class RouteDefinition {

    private final Color color;
    private final String matchString;
    private Match match;
    private final int index;

    public RouteDefinition(int index, Color color, String expression) {
        this.color = color;
        this.matchString = expression;
        this.index = index;
        try {
            match = SearchCompiler.compile(expression, false, false);
        } catch (ParseError e) {
            match = new SearchCompiler.Never();
            e.printStackTrace();
        }
    }

    public boolean matches(OsmPrimitive primitive) {
        return match.match(primitive);
    }

    public Color getColor() {
        return color;
    }

    public int getIndex() {
        return index;
    }

    @Override
    public String toString() {
        return color.toString() + " " + matchString;
    }

}
