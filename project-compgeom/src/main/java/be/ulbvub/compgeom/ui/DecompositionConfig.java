package be.ulbvub.compgeom.ui;

import be.ulbvub.compgeom.Polygon;
import processing.core.PVector;

import java.util.Objects;

public abstract class DecompositionConfig {

    public static final class SlabConfig extends DecompositionConfig {
        private final PVector direction;
        private final Polygon polygon;

        public SlabConfig(PVector direction, Polygon polygon) {
            this.direction = direction;
            this.polygon = polygon;
        }

        public PVector direction() {
            return direction;
        }

        public Polygon polygon() {
            return polygon;
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
        private final Polygon polygon;

        public TriangulationConfig(Polygon polygon) {
            this.polygon = polygon;
        }

        public Polygon polygon() {
            return polygon;
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
    }

    public static final class KdConfig extends DecompositionConfig {
        private final Polygon polygon;

        public KdConfig(Polygon polygon) {
            this.polygon = polygon;
        }

        public Polygon polygon() {
            return polygon;
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

    }

    public static final class GreedyConfig extends DecompositionConfig {
        private final Polygon polygon;

        public GreedyConfig(Polygon polygon) {
            this.polygon = polygon;
        }

        public Polygon polygon() {
            return polygon;
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
