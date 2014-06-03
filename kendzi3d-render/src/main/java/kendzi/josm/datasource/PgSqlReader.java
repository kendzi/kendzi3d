package kendzi.josm.datasource;

import static org.openstreetmap.josm.tools.I18n.*;

import java.sql.Array;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.stream.XMLStreamException;

import kendzi.math.geometry.bbox.Bbox2d;

import org.apache.log4j.Logger;
import org.openstreetmap.josm.data.coor.LatLon;
import org.openstreetmap.josm.data.osm.DataSet;
import org.openstreetmap.josm.data.osm.Node;
import org.openstreetmap.josm.data.osm.NodeData;
import org.openstreetmap.josm.data.osm.OsmPrimitiveType;
import org.openstreetmap.josm.data.osm.PrimitiveData;
import org.openstreetmap.josm.data.osm.Relation;
import org.openstreetmap.josm.data.osm.RelationData;
import org.openstreetmap.josm.data.osm.RelationMemberData;
import org.openstreetmap.josm.data.osm.User;
import org.openstreetmap.josm.data.osm.Way;
import org.openstreetmap.josm.data.osm.WayData;
import org.openstreetmap.josm.gui.progress.NullProgressMonitor;
import org.openstreetmap.josm.gui.progress.ProgressMonitor;
import org.openstreetmap.josm.io.AbstractReader;
import org.openstreetmap.josm.io.IllegalDataException;
import org.openstreetmap.josm.tools.CheckParameterUtil;
import org.postgis.PGgeometry;

public class PgSqlReader extends AbstractReader {

    /** Log. */
    private static final Logger log = Logger.getLogger(PgSqlReader.class);

    HashSet<Long> nodesIds = new HashSet<Long>();

    public static DataSet parseDataSet(Connection connection, Bbox2d bbox, ProgressMonitor progressMonitor)
            throws IllegalDataException {
        return new PgSqlReader().doParseDataSet(connection, bbox, progressMonitor);
    }

    protected DataSet doParseDataSet(Connection connection, Bbox2d bbox, ProgressMonitor progressMonitor)
            throws IllegalDataException {
        if (progressMonitor == null) {
            progressMonitor = NullProgressMonitor.INSTANCE;
        }
        CheckParameterUtil.ensureParameterNotNull(connection, "source");
        try {
            progressMonitor.beginTask(tr("Prepare OSM data...", 2));
            progressMonitor.indeterminateSubTask(tr("Parsing OSM data..."));

            // InputStreamReader ir = UTFInputStreamReader.create(source,
            // "UTF-8");
            // XMLStreamReader parser =
            // XMLInputFactory.newInstance().createXMLStreamReader(ir);
            // setConnection(source);
            parse(connection, bbox);
            progressMonitor.worked(1);

            progressMonitor.indeterminateSubTask(tr("Preparing data set..."));
            prepareDataSet();
            progressMonitor.worked(1);

            // // iterate over registered postprocessors and give them each a
            // chance
            // // to modify the dataset we have just loaded.
            // if (postprocessors != null) {
            // for (OsmServerReadPostprocessor pp : postprocessors) {
            // pp.postprocessDataSet(getDataSet(), progressMonitor);
            // }
            // }
            return getDataSet();
        } catch (IllegalDataException e) {
            throw e;
            // } catch(OsmParsingException e) {
            // throw new IllegalDataException(e.getMessage(), e);
            // } catch(XMLStreamException e) {
            // String msg = e.getMessage();
            // Pattern p = Pattern.compile("Message: (.+)");
            // Matcher m = p.matcher(msg);
            // if (m.find()) {
            // msg = m.group(1);
            // }
            // if (e.getLocation() != null) {
            // throw new IllegalDataException(tr("Line {0} column {1}: ",
            // e.getLocation().getLineNumber(),
            // e.getLocation().getColumnNumber()) + msg, e);
            // } else {
            // throw new IllegalDataException(msg, e);
            // }
        } catch (Exception e) {
            throw new IllegalDataException(e);
        } finally {
            progressMonitor.finishTask();
        }
    }

    private void parse(Connection conn, Bbox2d bbox) {
        try {

            ResultSet selectNodes = LoadFromPgSnapsnot.selectNodes(conn, bbox);

            int nCount = 0;
            while (selectNodes.next()) {

                parseNode(selectNodes);

                nCount++;
            }

            if (log.isTraceEnabled()) {
                log.trace("loaded nodes: " + nCount);
            }

            ResultSet selectWays = LoadFromPgSnapsnot.selectWays(conn, bbox);

            int wCount = 0;
            while (selectWays.next()) {

                Way loadNode = parseWay(selectWays);

                wCount++;
            }

            if (log.isTraceEnabled()) {
                log.trace("loaded ways: " + wCount);
            }

            // load missing nodes

            List<Long> nodesToLoad = new ArrayList<Long>();
            for (Collection<Long> nodes : this.ways.values()) {
                for (Long nodeId : nodes) {
                    if (!this.nodesIds.contains(nodeId)) {
                        nodesToLoad.add(nodeId);
                    }

                }
            }

            ResultSet selectNodesById = LoadFromPgSnapsnot.selectNodesById(conn, nodesToLoad);
            ;

            int nwCount = 0;
            while (selectNodesById.next()) {

                parseNode(selectNodesById);

                nwCount++;
            }

            if (log.isTraceEnabled()) {
                log.trace("loaded nodes from ways: " + nwCount);
            }

            parseRelationsForMembers(conn, nodesIds, ways.keySet(), null);

            // parseNode(null);

        } catch (XMLStreamException e) {
            log.error("error parsing data from db", e);
        } catch (SQLException e) {
            log.error("error parsing data from db", e);
        }

    }

    protected Relation parseRelation(ResultSet rs) throws SQLException {
        RelationData rd = new RelationData();
        readCommon(rs, rd);
        Relation r = new Relation(rd.getId(), rd.getVersion());
        r.setVisible(rd.isVisible());
        r.load(rd);
        this.externalIdMap.put(rd.getPrimitiveId(), r);

        LoadFromPgSnapsnot.injectTags(rs, r);

        // Collection<RelationMemberData> members = new
        // ArrayList<RelationMemberData>();
        // while (true) {
        // int event = parser.next();
        // if (event == XMLStreamConstants.START_ELEMENT) {
        // if (parser.getLocalName().equals("member")) {
        // members.add(parseRelationMember(r));
        // } else if (parser.getLocalName().equals("tag")) {
        // parseTag(r);
        // } else {
        // parseUnknown();
        // }
        // } else if (event == XMLStreamConstants.END_ELEMENT) {
        // break;
        // }
        // }
        // if (r.isDeleted() && members.size() > 0) {
        // System.out.println(tr("Deleted relation {0} contains members",
        // r.getUniqueId()));
        // members = new ArrayList<RelationMemberData>();
        // }
        // relations.put(rd.getUniqueId(), members);
        return r;
    }

    private void parseRelationsForMembers(Connection conn, Set<Long> nodesIds, Set<Long> waysIds, Set<Long> relationIds)
            throws SQLException {

        List<Long> relationsIds = LoadFromPgSnapsnot.selectRelationsIdsForMembers(conn, nodesIds, waysIds, relationIds);

        int i = 0;
        while (relationsIds.size() > 0) {
            i++;
            if (i > 20) {
                throw new RuntimeException("to deep relations child: " + i);
            }

            ResultSet relationsRS = LoadFromPgSnapsnot.selectRelations(conn, relationsIds);
            while (relationsRS.next()) {
                parseRelation(relationsRS);
            }
            List<RelationMember> rmList = new ArrayList<RelationMember>();
            ResultSet relationsMembersRS = LoadFromPgSnapsnot.selectRelationMembers(conn, relationsIds);
            while (relationsMembersRS.next()) {
                rmList.add(parseRelationMember(relationsMembersRS));
            }

            List<Long> childRelations = parseRelationMembers(rmList);

            relationsIds = childRelations;
        }

        // load children relations!
    }

    private int getMaxSequence(List<RelationMember> relationMemberList) {
        int max = -1;
        for (RelationMember rm : relationMemberList) {
            if (rm.getSequenceId() > max) {
                max = rm.getSequenceId();
            }
        }
        return max;
    }

    private List<Long> parseRelationMembers(List<RelationMember> relationMemberList) {

        Set<Long> childRelationIdsSet = new HashSet<Long>();

        Map<Long, List<RelationMember>> relationsData = new HashMap<Long, List<RelationMember>>();
        for (RelationMember rm : relationMemberList) {

            List<RelationMember> index = relationsData.get(rm.getRelationId());
            if (index == null) {
                index = new ArrayList<PgSqlReader.RelationMember>();
                relationsData.put(rm.getRelationId(), index);
            }
            index.add(rm);
        }

        for (Long relationId : relationsData.keySet()) {
            List<RelationMember> rmList = relationsData.get(relationId);

            int maxSequence = getMaxSequence(rmList) + 1;

            if (maxSequence > rmList.size()) {
                throw new RuntimeException("there are some problems with relation members. Expected: " + maxSequence
                        + " members but found: " + rmList.size());
            }

            ArrayList<RelationMemberData> members = new ArrayList<RelationMemberData>(maxSequence);

            for (int i = 0; i < maxSequence; i++) {
                members.add(null);
            }

            for (RelationMember rm : rmList) {
                members.set(rm.getSequenceId(), rm.getRelationMemberData());

                RelationMemberData memberData = rm.getRelationMemberData();
                if (OsmPrimitiveType.RELATION.equals(memberData.getType())) {
                    if (this.relations.get(memberData.getMemberId()) == null) {
                        // child relation. Need to load
                        childRelationIdsSet.add(memberData.getMemberId());
                    }
                }
            }

            // if (r.isDeleted() && members.size() > 0) {
            // System.out.println(tr("Deleted relation {0} contains members",
            // r.getUniqueId()));
            // members = new ArrayList<RelationMemberData>();
            // }
            this.relations.put(relationId, members);

        }

        return new ArrayList<Long>(childRelationIdsSet);
    }

    // private RelationMemberData parseRelationMembers_Ups(Set<Long> nodesIds,
    // Set<Long> waysIds, Set<Long> relationIds) throws SQLException {
    // ResultSet selectRelationsMembers = findRelationMember(nodesIds, waysIds,
    // relationIds);
    //
    // Set<Long> relationIdsSet = new HashSet<Long>();
    //
    // Set<Long> relationMembersIdsSet = new HashSet<Long>();
    //
    // Map<Long, List<RelationMember>> relationsData = new HashMap<Long,
    // List<RelationMember>>();
    // int rCount = 0;
    // while (selectRelationsMembers.next()) {
    //
    // RelationMember rm = parseRelationMember(selectRelationsMembers);
    //
    // List<RelationMember> index = relationsData.get(rm.getRelationId());
    // if (index == null) {
    // index = new ArrayList<PgSqlReader.RelationMember>();
    // relationsData.put(rm.getRelationId(), index);
    // }
    // index.add(rm);
    //
    // rCount++;
    // }
    //
    //
    // for (Long relationId : relationsData.keySet()) {
    // List<RelationMember> rmList = relationsData.get(relationId);
    //
    // int maxSequence = getMaxSequence(rmList);
    //
    // if (maxSequence > rmList.size()) {
    // throw new RuntimeException( x6dre �e �� )
    // }
    //
    // ArrayList<RelationMemberData> members = new
    // ArrayList<RelationMemberData>(maxSequence);
    //
    // for (RelationMember rm : rmList) {
    // members.get(rm.getSequenceId(), )
    // }
    //
    // }
    //
    //
    // if (r.isDeleted() && members.size() > 0) {
    // System.out.println(tr("Deleted relation {0} contains members",
    // r.getUniqueId()));
    // members = new ArrayList<RelationMemberData>();
    // }
    // relations.put(rd.getUniqueId(), members);
    // }

    // private ResultSet findRelationMember(Set<Long> nodesIds2,
    // Set<Long> waysIds, Set<Long> relationIds) {
    // // TODO Auto-generated method stub
    // return null;
    // }

    private RelationMember parseRelationMember(ResultSet rs) throws SQLException {

        OsmPrimitiveType type = null;

        long id = rs.getLong("member_id");
        String typeStr = rs.getString("member_type");
        if ("N".equals(typeStr)) {
            type = OsmPrimitiveType.NODE;
        } else if ("W".equals(typeStr)) {
            type = OsmPrimitiveType.WAY;
        } else if ("R".equals(typeStr)) {
            type = OsmPrimitiveType.RELATION;
        }
        if (type == null) {
            throw new RuntimeException("unknown relation type: " + typeStr + " for memeber_id: " + id);
        }
        // throwException(tr("Missing attribute ''type'' on member {0} in relation {1}.",
        // Long.toString(id), Long.toString(r.getUniqueId())));

        String role = rs.getString("member_role");

        long relationId = rs.getLong("relation_id");
        int sequenceId = rs.getInt("sequence_id");

        return new RelationMember(relationId, sequenceId, new RelationMemberData(role, type, id));
    }

    private static class RelationMember {
        long relationId;
        int sequenceId;
        RelationMemberData relationMemberData;

        public RelationMember(long relationId, int sequenceId, RelationMemberData relationMemberData) {
            super();
            this.relationId = relationId;
            this.sequenceId = sequenceId;
            this.relationMemberData = relationMemberData;
        }

        /**
         * @return the relationId
         */
        public long getRelationId() {
            return this.relationId;
        }

        /**
         * @param relationId
         *            the relationId to set
         */
        public void setRelationId(long relationId) {
            this.relationId = relationId;
        }

        /**
         * @return the sequenceId
         */
        public int getSequenceId() {
            return this.sequenceId;
        }

        /**
         * @param sequenceId
         *            the sequenceId to set
         */
        public void setSequenceId(int sequenceId) {
            this.sequenceId = sequenceId;
        }

        /**
         * @return the relationMemberData
         */
        public RelationMemberData getRelationMemberData() {
            return this.relationMemberData;
        }

        /**
         * @param relationMemberData
         *            the relationMemberData to set
         */
        public void setRelationMemberData(RelationMemberData relationMemberData) {
            this.relationMemberData = relationMemberData;
        }
    }

    protected Way parseWay(ResultSet rs) throws SQLException {
        WayData wd = new WayData();
        readCommon(rs, wd);
        Way w = new Way(wd.getId(), wd.getVersion());
        w.setVisible(wd.isVisible());
        w.load(wd);
        this.externalIdMap.put(wd.getPrimitiveId(), w);

        LoadFromPgSnapsnot.injectTags(rs, w);

        Collection<Long> nodeIds = loadWayNodes(rs);

        // Collection<Long> nodeIds = new ArrayList<Long>();
        // while (true) {
        // int event = parser.next();
        // if (event == XMLStreamConstants.START_ELEMENT) {
        // if (parser.getLocalName().equals("nd")) {
        // nodeIds.add(parseWayNode(w));
        // } else if (parser.getLocalName().equals("tag")) {
        // parseTag(w);
        // } else {
        // parseUnknown();
        // }
        // } else if (event == XMLStreamConstants.END_ELEMENT) {
        // break;
        // }
        // }
        // if (w.isDeleted() && nodeIds.size() > 0) {
        // System.out.println(tr("Deleted way {0} contains nodes",
        // w.getUniqueId()));
        // nodeIds = new ArrayList<Long>();
        // }
        this.ways.put(wd.getUniqueId(), nodeIds);
        return w;
    }

    private List<Long> loadWayNodes(ResultSet rs) throws SQLException {

        ArrayList<Long> ret = new ArrayList<Long>();
        Array array = rs.getArray("nodes");

        for (Long id : (Long[]) array.getArray()) {
            ret.add(id);
        }
        return ret;

    }

    protected Node parseNode(ResultSet rs) throws XMLStreamException, SQLException {
        NodeData nd = new NodeData();

        PGgeometry geom = (PGgeometry) rs.getObject("geom");
        if (geom != null) {
            org.postgis.Point point = (org.postgis.Point) geom.getGeometry();
            nd.setCoor(new LatLon(point.y, point.x));
        }

        // String lat = parser.getAttributeValue(null, "lat");
        // String lon = parser.getAttributeValue(null, "lon");
        // if (lat != null && lon != null) {
        // nd.setCoor(new LatLon(Double.parseDouble(lat),
        // Double.parseDouble(lon)));
        // }

        readCommon(rs, nd);
        Node n = new Node(nd.getId(), nd.getVersion());
        n.setVisible(nd.isVisible());
        n.load(nd);
        this.externalIdMap.put(nd.getPrimitiveId(), n);

        LoadFromPgSnapsnot.injectTags(rs, n);

        // while (true) {
        // int event = parser.next();
        // if (event == XMLStreamConstants.START_ELEMENT) {
        // if (parser.getLocalName().equals("tag")) {
        // parseTag(n);
        // } else {
        // parseUnknown();
        // }
        // } else if (event == XMLStreamConstants.END_ELEMENT) {
        // return n;
        // }
        // }
        this.nodesIds.add(nd.getUniqueId());
        return n;
    }

    /**
     * Read out the common attributes and put them into current OsmPrimitive.
     * 
     * @throws SQLException
     */
    private void readCommon(ResultSet rs, PrimitiveData current) throws SQLException {
        current.setId(rs.getLong("id"));
        if (current.getUniqueId() == 0) {
            throw new RuntimeException(tr("Illegal object with ID=0."));
        }

        current.setTimestamp(rs.getDate("tstamp"));

        // // user attribute added in 0.4 API
        // String user = parser.getAttributeValue(null, "user");
        // // uid attribute added in 0.6 API
        // String uid = parser.getAttributeValue(null, "uid");
        // current.setUser(createUser(uid, user));

        current.setUser(User.getById(rs.getInt("user_id")));

        // // visible attribute added in 0.4 API
        // String visible = parser.getAttributeValue(null, "visible");
        // if (visible != null) {

        current.setVisible(true);

        int version = rs.getInt("version");

        current.setVersion(version);

        // String action = parser.getAttributeValue(null, "action");
        // if (action == null) {
        // // do nothing
        // } else if (action.equals("delete")) {
        // current.setDeleted(true);
        // current.setModified(current.isVisible());
        // } else if (action.equals("modify")) {
        // current.setModified(true);
        // }

        current.setChangesetId(rs.getInt("changeset_id"));

    }
}
