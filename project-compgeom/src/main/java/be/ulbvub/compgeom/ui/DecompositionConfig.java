package be.ulbvub.compgeom.ui;

import be.ulbvub.compgeom.Polygon;
import be.ulbvub.compgeom.kd.KdDecomposition;
import be.ulbvub.compgeom.slab.SlabDecomposition;
import be.ulbvub.compgeom.triangles.TriangleDecomposition;
import be.ulbvub.compgeom.utils.DoublyConnectedEdgeList;
import processing.core.PVector;

import java.util.Objects;

public abstract class DecompositionConfig {

    public abstract DoublyConnectedEdgeList decompose();

    public abstract Polygon polygon();

    public abstract void setPolygon(Polygon polygon);

    public static final class SlabConfig extends DecompositionConfig {
        private final PVector direction;
        private Polygon polygon;

        public SlabConfig(PVector direction, Polygon polygon) {
            this.direction = direction;
            this.polygon = polygon;
        }

        public PVector direction() {
            return direction;
        }

        @Override
        public Polygon polygon() {
            return polygon;
        }

        @Override
        public void setPolygon(Polygon polygon) {
            this.polygon = polygon;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) return true;
            if (obj == null || obj.getClass() != this.getClass()) return false;
            var that = (SlabConfig) obj;
            return Objects.equals(this.direction, that.direction) &&
                    Objects.equals(this.polygon, that.polygon);
        }

        @Override
        public DoublyConnectedEdgeList decompose() {
            final var algorithm = new SlabDecomposition(direction, polygon);
            algorithm.buildEventQueue();
            algorithm.run();
            return algorithm.getDecomposition();
        }

        @Override
        public int hashCode() {
            return Objects.hash(direction, polygon);
        }

        @Override
        public String toString() {
            return "SlabConfig[" +
                    "direction=" + direction + ", " +
                    "polygon=" + polygon + ']';
        }
    }

    public static final class TriangulationConfig extends DecompositionConfig {
        private Polygon polygon;

        public TriangulationConfig(Polygon polygon) {
            this.polygon = polygon;
        }

        @Override
        public Polygon polygon() {
            return polygon;
        }

        @Override
        public void setPolygon(Polygon polygon) {
            this.polygon = polygon;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) return true;
            if (obj == null || obj.getClass() != this.getClass()) return false;
            var that = (TriangulationConfig) obj;
            return Objects.equals(this.polygon, that.polygon);
        }

        @Override
        public int hashCode() {
            return Objects.hash(polygon);
        }

        @Override
        public String toString() {
            return "TriangulationConfig[" +
                    "polygon=" + polygon + ']';
        }

        @Override
        public DoublyConnectedEdgeList decompose() {
            return TriangleDecomposition.decompose(polygon, false);
        }
    }

    public static final class KdConfig extends DecompositionConfig {
        private Polygon polygon;

        public KdConfig(Polygon polygon) {
            this.polygon = polygon;
        }

        public Polygon polygon() {
            return polygon;
        }

        @Override
        public void setPolygon(Polygon polygon) {
            this.polygon = polygon;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) return true;
            if (obj == null || obj.getClass() != this.getClass()) return false;
            var that = (KdConfig) obj;
            return Objects.equals(this.polygon, that.polygon);
        }

        @Override
        public int hashCode() {
            return Objects.hash(polygon);
        }

        @Override
        public String toString() {
            return "KdConfig[" +
                    "polygon=" + polygon + ']';
        }

        @Override
        public DoublyConnectedEdgeList decompose() {
            final var algorithm = new KdDecomposition(polygon);
            algorithm.run();
            return algorithm.getDecomposition();
        }
    }

    public static final class GreedyConfig extends DecompositionConfig {
        private Polygon polygon;

        public GreedyConfig(Polygon polygon) {
            this.polygon = polygon;
        }

        @Override
        public DoublyConnectedEdgeList decompose() {
            return TriangleDecomposition.decompose(polygon, true);
        }

        @Override
        public Polygon polygon() {
            return polygon;
        }

        @Override
        public void setPolygon(Polygon polygon) {
            this.polygon = polygon;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) return true;
            if (obj == null || obj.getClass() != this.getClass()) return false;
            var that = (GreedyConfig) obj;
            return Objects.equals(this.polygon, that.polygon);
        }

        @Override
        public int hashCode() {
            return Objects.hash(polygon);
        }

        @Override
        public String toString() {
            return "GreedyConfig[" +
                    "polygon=" + polygon + ']';
        }


    }

}
