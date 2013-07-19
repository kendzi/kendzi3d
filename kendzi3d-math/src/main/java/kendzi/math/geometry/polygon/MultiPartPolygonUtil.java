package kendzi.math.geometry.polygon;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MultiPartPolygonUtil {

    /** Try to connect all edges in the input set to closed polygons. Vertex and Edges are compared using <B>equal</B> method!
     * Polygon are presented in form of ordered list of edges. Edges required reversion are marked with reverted flag.
     * This method try to find all possible smallest polygons in input list.
     *
     * @param in
     * @return
     */
    public static <E,V> List<List<EdgeOut<E, V>>> connect(List<Edge<E, V>> in) {

        List<Edge<E, V>> all = in;

        Map<Vertex<V>, List<Connection<E, V>>> connections = new HashMap<Vertex<V>, List<Connection<E, V>>>();

        validate(in);

        for (Edge<E, V> e : in) {
            addVertex(e.v1, e.v2, e, connections, false);
            addVertex(e.v2, e.v1, e, connections, true);
        }

        while (removeVertexWithOneEdge(connections)) {}

        Set<Edge<E,V>> used = new HashSet<Edge<E,V>>();
        markUsedForNotConnected(all, connections, used);

        List<List<EdgeOut<E, V>>>  ret = findClosedPolygon(all, connections, used);

        return ret;
    }

    private static <E, V> void validate(List<Edge<E, V>> in) {
        for (Edge<E, V> edge : in) {
            if (edge.getData() == null) {
                throw new IllegalArgumentException("edge can't have empty data!");
            }

            if (edge.getV1() == null) {
                throw new IllegalArgumentException("edge can't have empty v1!");
            }

            if (edge.getV1().getData() == null) {
                throw new IllegalArgumentException("vertex v1 can't have empty data!");
            }

            if (edge.getV2() == null) {
                throw new IllegalArgumentException("edge can't have empty v2!");
            }

            if (edge.getV2().getData() == null) {
                throw new IllegalArgumentException("vertex v2 can't have empty data!");
            }
        }
    }

    private static <E, V> void markUsedForNotConnected(
            List<Edge<E, V>> all,
            Map<Vertex<V>, List<Connection<E, V>>> connections,
            Set<Edge<E, V>> used) {

        for (Edge<E, V> e : all) {

            if (connections.get(e.v1) == null) {
                used.add(e);
                continue;
            }

            if (connections.get(e.v2) == null) {
                used.add(e);
                continue;
            }
        }
    }

    private static <E, V> List<List<EdgeOut<E, V>>> findClosedPolygon(
            List<Edge<E, V>> all,
            Map<Vertex<V>, List<Connection<E, V>>> connections,
            Set<Edge<E, V>> used) {

        List<List<EdgeOut<E, V>>> ret = new ArrayList<List<EdgeOut<E, V>>>();
        for (Edge<E, V> e : all) {
            if (used.contains(e)) {
                continue;
            }
            used.add(e);

            if (connections.get(e.v1).size() < 2) throw new RuntimeException();
            List<EdgeOut<E, V>> polygon = searchGraph(e, connections);
            if (polygon != null) {
                ret.add(polygon);

                for(EdgeOut<E, V> eo : polygon) {
                    used.add(eo.getEdge());
                }
            }
        }
        return ret;
    }


    private static <E, V> List<EdgeOut<E, V>> searchGraph(
//            Vertex<V> startVertex,
            Edge<E,V> startEdge,
            Map<Vertex<V>, List<Connection<E, V>>> connections) {

        List<ConnectionWithParent<E, V>> toProcess = new ArrayList<ConnectionWithParent<E, V>>();
        Set<Connection<E, V>> processed = new HashSet<Connection<E, V>>();
        List<ConnectionWithParent<E, V>> processedOrder = new ArrayList<ConnectionWithParent<E, V>>();
        // key: current vertex, value: paretn vertex
        Map<Connection<E, V>, Connection<E, V>> parentList = new HashMap<Connection<E, V>, Connection<E, V>>();

//        processVertex(startVertex, toProcess, processed, connections);

        Vertex<V> startVertex = startEdge.getV1();

        for(Connection<E, V> connection : connections.get(startVertex)) {
            if (connection.reverted == false && connection.getEdge().equals(startEdge)) {
                parentList.put(connection, null);
                toProcess.add(new ConnectionWithParent<E, V>(connection, null));
            }
        }

        if (toProcess.size() == 0 ) throw new RuntimeException();

        while (toProcess.size() > 0) {
            ConnectionWithParent<E, V> current = toProcess.remove(0);
            Connection<E, V> process = current.getConnection();

            Vertex<V> nextVertex = process.getEnd();

            if (processed.contains(nextVertex)) {
                continue;
            }

            processed.add(process);
//            processed.add(nextVertex);
            processedOrder.add(current);


            if (startVertex.equals(nextVertex)) {

                return findReturnPath(current /*nextVertex, processedOrder*/);
            }

            for(Connection<E, V> connection : connections.get(nextVertex)) {

                if (processed.contains(connection)) {
                    continue;
                }

                if (connection.getEdge().equals(process.getEdge())) {
                    // two way connections. so wi drop currentyly processed
                    continue;
                }

                parentList.put(connection, process);
                toProcess.add(new ConnectionWithParent<E, V>(connection, current));
            }
        }

        return null;
    }

    static class ConnectionWithParent<E, V> {
        private Connection<E, V> connection;
        private ConnectionWithParent<E, V> parent;



        public ConnectionWithParent(Connection<E, V> connection, ConnectionWithParent<E, V> parent) {
            super();
            this.connection = connection;
            this.parent = parent;
        }
        /**
         * @return the connection
         */
        public Connection<E, V> getConnection() {
            return this.connection;
        }
        /**
         * @param connection the connection to set
         */
        public void setConnection(Connection<E, V> connection) {
            this.connection = connection;
        }
        /**
         * @return the parent
         */
        public ConnectionWithParent<E, V> getParent() {
            return this.parent;
        }
        /**
         * @param parent the parent to set
         */
        public void setParent(ConnectionWithParent<E, V> parent) {
            this.parent = parent;
        }

    }

    private static <E, V> List<EdgeOut<E, V>> findReturnPath(
            ConnectionWithParent<E, V> current) {
//            Vertex<V> endVertex,
//            List<ConnectionWithParent<E, V>> processedOrder) {

        List<EdgeOut<E, V>> ret = new ArrayList<EdgeOut<E, V>>();

        ConnectionWithParent<E, V> last = current;//processedOrder.get(processedOrder.size() - 1);
        ret.add(new EdgeOut<E, V>(last.connection.getEdge(),last.connection.isReverted()));

        while ((last = last.getParent()) != null) {

            ret.add(new EdgeOut<E, V>(last.connection.getEdge(),last.connection.isReverted()));
        }

        return revers(ret);

//        List<EdgeOut<E, V>> ret = new ArrayList<EdgeOut<E, V>>();
//
//        Vertex<V> currentVertex = endVertex;
//        ConnectionWithParent<E, V> last = processedOrder.get(processedOrder.size() - 1);
//        ret.add(new EdgeOut<E, V>(last.connection.getEdge(),last.connection.isReverted()));
//
//        Connection<E,V> parent = last.getParent();
//
//
//        for (int i = processedOrder.size() -2; i >= 0; i--) {
//            ConnectionWithParent<E, V> connectionWithParent = processedOrder.get(i);
//
//            if (connectionWithParent.getConnection().equals(parent)) {
//                ret.add(new EdgeOut<E, V>(connectionWithParent.connection.getEdge(),connectionWithParent.connection.isReverted()));
//                parent = connectionWithParent.getParent();
//            }
//
//            if (parent == null) {
//                break;
//            }
//        }




//        for (int i = processedOrder.size() -1; i >= 0; i--) {
//            Connection<E, V> connection = processedOrder.get(i).getConnection();
//
//            if (connection.getEnd().equals(currentVertex)) {
//                currentVertex = connection.getBegin();
//                ret.add(new EdgeOut<E, V>(connection.getEdge(), connection.isReverted()));
//            }
//        }
//        return ret;
    }

    private static <E,V> List<EdgeOut<E, V>> revers(List<EdgeOut<E, V>> in) {
        List<EdgeOut<E, V>> ret = new ArrayList<EdgeOut<E, V>>();
        for (int i = in.size() - 1; i >= 0; i--) {
            ret.add(in.get(i));
        }
        return ret;
    }

    static class Connection<E, V> {
        private Vertex<V> begin;
        private Edge<E, V> edge;
        private Vertex<V> end;
        private boolean reverted;



        public Connection(Vertex<V> begin, Vertex<V> end, Edge<E, V> edge, boolean reverted) {
            super();
            this.begin = begin;
            this.end = end;
            this.edge = edge;
            this.reverted = reverted;
        }
        /**
         * @return the begin
         */
        public Vertex<V> getBegin() {
            return this.begin;
        }
        /**
         * @param begin the begin to set
         */
        public void setBegin(Vertex<V> begin) {
            this.begin = begin;
        }
        /**
         * @return the edge
         */
        public Edge<E, V> getEdge() {
            return this.edge;
        }
        /**
         * @param edge the edge to set
         */
        public void setEdge(Edge<E, V> edge) {
            this.edge = edge;
        }
        /**
         * @return the end
         */
        public Vertex<V> getEnd() {
            return this.end;
        }
        /**
         * @param end the end to set
         */
        public void setEnd(Vertex<V> end) {
            this.end = end;
        }
        /**
         * @return the reverted
         */
        public boolean isReverted() {
            return this.reverted;
        }
        /**
         * @param reverted the reverted to set
         */
        public void setReverted(boolean reverted) {
            this.reverted = reverted;
        }
        /**
         * {@inheritDoc}
         *
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString() {
            return "Connection [begin=" + this.begin + ", end=" + this.end + ", reverted=" + this.reverted + ", edge=" + this.edge + "]";
        }
        /**
         * {@inheritDoc}
         *
         * @see java.lang.Object#hashCode()
         */
        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((this.begin == null) ? 0 : this.begin.hashCode());
            result = prime * result + ((this.edge == null) ? 0 : this.edge.hashCode());
            result = prime * result + ((this.end == null) ? 0 : this.end.hashCode());
            result = prime * result + (this.reverted ? 1231 : 1237);
            return result;
        }
        /**
         * {@inheritDoc}
         *
         * @see java.lang.Object#equals(java.lang.Object)
         */
        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            Connection other = (Connection) obj;
            if (this.begin == null) {
                if (other.begin != null)
                    return false;
            } else if (!this.begin.equals(other.begin))
                return false;
            if (this.edge == null) {
                if (other.edge != null)
                    return false;
            } else if (!this.edge.equals(other.edge))
                return false;
            if (this.end == null) {
                if (other.end != null)
                    return false;
            } else if (!this.end.equals(other.end))
                return false;
            if (this.reverted != other.reverted)
                return false;
            return true;
        }
    }


    public static class EdgeOut<E, V> {

        private Edge<E,V> edge;
        private boolean reverted;

        /**
         * @param edge
         * @param reverts
         */
        public EdgeOut(Edge<E,V> edge, boolean reverted) {
            super();
            this.edge = edge;
            this.reverted = reverted;
        }

        /**
         * @return the reverted
         */
        public boolean isReverted() {
            return this.reverted;
        }
        /**
         * @param reverted the reverted to set
         */
        public void setReverted(boolean reverted) {
            this.reverted = reverted;
        }
        /**
         * @return the edge
         */
        public Edge<E, V> getEdge() {
            return this.edge;
        }
        /**
         * @param edge the edge to set
         */
        public void setEdge(Edge<E, V> edge) {
            this.edge = edge;
        }

        /**
         * {@inheritDoc}
         *
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString() {
            return "EdgeOut [reverted=" + this.reverted + ", edge=" + this.edge + "]";
        }


    }

//    class VertexConnection {
//        Edge e;
//        Vertex end;
//    }

    private static <E,V> boolean removeVertexWithOneEdge(
            Map<Vertex<V>, List<Connection<E, V>>> connections) {

        boolean removed = false;


        for (Vertex<V> vBegin : new HashSet<Vertex<V>>(connections.keySet())) {

            List<Connection<E, V>> list = connections.get(vBegin);
            if (list.size() == 1) {
                Vertex<V> vEnd = list.get(0).getEnd();
                removed = true;
                connections.remove(vBegin);

                int removedEnd = 0;
                List<Connection<E, V>> listEnd = connections.get(vEnd);
                for (int i = listEnd.size() - 1; i >=0; i--) {
                    Connection<E, V> connection = listEnd.get(i);

                    if (vEnd.equals(connection.getBegin())
                            && vBegin.equals(connection.getEnd())){
                        listEnd.remove(connection);
                        removedEnd++;
                    }
                }
                if (removedEnd > 1) {
                    throw new RuntimeException();
                }
            }
        }

        return removed;
    }

    private static <V, E> void addVertex(
            Vertex<V> begin,
            Vertex<V> end,
            Edge<E, V> edge,
            Map<Vertex<V>, List<Connection<E, V>>> connections,
            boolean reverted) {

        List<Connection<E, V>> list = connections.get(begin);
        if (list == null) {
            list = new ArrayList<Connection<E, V>>();
            connections.put(begin, list);
        }

        list.add(new Connection<E, V>(begin, end, edge, reverted));
    }

    /**
     *
     * @author Tomasz Kędziora (Kendzi)
     * @param <V>
     */
    public static class Vertex<V> {

        V data;

        public Vertex(V data) {
            this.data = data;
        }

        /**
         * @return the data
         */
        public V getData() {
            return this.data;
        }

        /**
         * @param data the data to set
         */
        public void setData(V data) {
            this.data = data;
        }


        /**
         * {@inheritDoc}
         *
         * @see java.lang.Object#hashCode()
         */
        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((this.data == null) ? 0 : this.data.hashCode());
            return result;
        }

        /**
         * {@inheritDoc}
         *
         * @see java.lang.Object#equals(java.lang.Object)
         */
        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            Vertex<V> other = (Vertex<V>) obj;
            if (this.data == null) {
                if (other.data != null)
                    return false;
            } else if (!this.data.equals(other.data))
                return false;

            return true;
        }

        /**
         * {@inheritDoc}
         *
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString() {
            String hash = Integer.toHexString(System.identityHashCode(this));
            return "Vertex(" + hash + ") [data=" + this.data + "]";
        }
    }

    /**
     *
     * @author Tomasz Kędziora (Kendzi)
     * @param <E>
     * @param <V>
     */
    public static class Edge<E, V> {
        private Vertex<V> v1;
        private Vertex<V> v2;

        E data;

        /**
         * @param v1
         * @param v2
         * @param data
         */
        public Edge(Vertex<V> v1, Vertex<V> v2, E data) {
            super();
            this.v1 = v1;
            this.v2 = v2;
            this.data = data;
        }

        /**
         * @return the data
         */
        public E getData() {
            return this.data;
        }

        /**
         * @param data the data to set
         */
        public void setData(E data) {
            this.data = data;
        }

        /**
         * @return the v1
         */
        public Vertex<V> getV1() {
            return this.v1;
        }

        /**
         * @param v1 the v1 to set
         */
        public void setV1(Vertex<V> v1) {
            this.v1 = v1;
        }

        /**
         * @return the v2
         */
        public Vertex<V> getV2() {
            return this.v2;
        }

        /**
         * @param v2 the v2 to set
         */
        public void setV2(Vertex<V> v2) {
            this.v2 = v2;
        }

        /**
         * {@inheritDoc}
         *
         * @see java.lang.Object#hashCode()
         */
        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((this.data == null) ? 0 : this.data.hashCode());
            result = prime * result + ((this.v1 == null) ? 0 : this.v1.hashCode());
            result = prime * result + ((this.v2 == null) ? 0 : this.v2.hashCode());
            return result;
        }

        /**
         * {@inheritDoc}
         *
         * @see java.lang.Object#equals(java.lang.Object)
         */
        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            Edge other = (Edge) obj;
            if (this.data == null) {
                if (other.data != null)
                    return false;
            } else if (!this.data.equals(other.data))
                return false;
            if (this.v1 == null) {
                if (other.v1 != null)
                    return false;
            } else if (!this.v1.equals(other.v1))
                return false;
            if (this.v2 == null) {
                if (other.v2 != null)
                    return false;
            } else if (!this.v2.equals(other.v2))
                return false;
            return true;
        }

        /**
         * {@inheritDoc}
         *
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString() {

            String hash = Integer.toHexString(System.identityHashCode(this));

            return "Edge("+hash+") [v1=" + this.v1 + ", v2=" + this.v2 + ", data=" + this.data + "]";
        }




    }

}
