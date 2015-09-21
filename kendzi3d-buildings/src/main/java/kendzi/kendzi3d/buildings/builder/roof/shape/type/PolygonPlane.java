package kendzi.kendzi3d.buildings.builder.roof.shape.type;

import kendzi.math.geometry.Plane3d;
import kendzi.math.geometry.polygon.MultiPolygonList2d;

public class PolygonPlane {

        private MultiPolygonList2d polygon;

        private Plane3d plane;

        public PolygonPlane(MultiPolygonList2d polygon, Plane3d plane) {
            super();
            this.polygon = polygon;
            this.plane = plane;
        }

        /**
         * @return the polygon
         */
        public MultiPolygonList2d getPolygon() {
            return polygon;
        }
        /**
         * @param polygon the polygon to set
         */
        public void setPolygon(MultiPolygonList2d polygon) {
            this.polygon = polygon;
        }
        /**
         * @return the plane
         */
        public Plane3d getPlane() {
            return plane;
        }
        /**
         * @param plane the plane to set
         */
        public void setPlane(Plane3d plane) {
            this.plane = plane;
        }


    }