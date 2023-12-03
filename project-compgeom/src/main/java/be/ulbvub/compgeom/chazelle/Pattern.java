package be.ulbvub.compgeom.chazelle;

import be.ulbvub.compgeom.SimplePolygon;
import processing.core.PVector;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public interface Pattern {

	/**
	 * Check if 2 patterns are similar, i.e. if the pattern involves the same points.
	 *
	 * @param pattern the other pattern
	 *
	 * @return true if similar
	 */
	boolean isSimilar(Pattern pattern);

	/**
	 * Check if this pattern can effectively split the given polygon along the edges between this instance's points.
	 *
	 * @param polygon the polygon
	 *
	 * @return true if this pattern can split the polygon
	 */
	boolean canSplit(SimplePolygon polygon);

	/**
	 * Split the polygon along the edges of this instance's points.
	 *
	 * @param polygon the polygon to split
	 *
	 * @return the resulting polygons
	 */
	List<SimplePolygon> split(SimplePolygon polygon);

	class X2Pattern implements Pattern {

		private final PVector i;

		private final PVector j;

		public X2Pattern(final PVector i, final PVector j) {
			this.i = i;
			this.j = j;
		}

		@Override
		public List<SimplePolygon> split(final SimplePolygon polygon) {
			final List<SimplePolygon> polygons = new ArrayList<>();

			if(!canSplit(polygon)) {
				throw new IllegalArgumentException("Cannot split the given polygon along " + i + "; " + j + " edges");
			}
			final var split1 = polygon.clone();
			final var split2 = split1.split(i, j);

			polygons.add(split1);
			polygons.add(split2);

			return polygons;
		}

		@Override
		public boolean canSplit(final SimplePolygon polygon) {
			return polygon.points().contains(i) && polygon.points().contains(j)
					&& !polygon.getEdges().contains(new PVector[]{i, j}) && !polygon.getEdges().contains(new PVector[]{j, i});
		}

		@Override
		public boolean isSimilar(final Pattern pattern) {
			if(!(pattern instanceof X2Pattern x2Pattern)) {
				return false;
			}

			return (this.i.equals(x2Pattern.i) || this.i.equals(x2Pattern.j))
					&& (this.j.equals(x2Pattern.i) || this.j.equals(x2Pattern.j))
					&& !x2Pattern.i.equals(x2Pattern.j);
		}

	}

	class X3Pattern implements Pattern {

		private final PVector i;

		private final PVector j;

		private final PVector k;

		public X3Pattern(final PVector i, final PVector j, final PVector k) {
			this.i = i;
			this.j = j;
			this.k = k;
		}

		private List<SimplePolygon> recursiveSplit(final SimplePolygon polygon, final X2Pattern x2Pattern) {
			if(x2Pattern.canSplit(polygon)) {
				return x2Pattern.split(polygon);
			} else {
				return Collections.emptyList();
			}
		}

		@Override
		public List<SimplePolygon> split(final SimplePolygon polygon) {
			final List<SimplePolygon> polygons = new ArrayList<>();
			final var ijPattern = new X2Pattern(i, j);
			final var jkPattern = new X2Pattern(j, k);
			final var kiPattern = new X2Pattern(k, i);

			if(!ijPattern.canSplit(polygon) || !jkPattern.canSplit(polygon) || !kiPattern.canSplit(polygon)) {
				throw new IllegalArgumentException("Cannot split the given polygon along " + i + "; " + j + "; " + k + " edges");
			}
			//Clone the original polygon
			var polygonToSplit = polygon.clone();
			//split the original polygon into 2 parts along ij
			System.out.println("Splitting " + polygonToSplit.points() + " along " + i + "; " + j);
			List<SimplePolygon> splitPolygon = ijPattern.split(polygonToSplit);
			splitPolygon.forEach(p -> System.out.println("p: " + p.points()));
			//finalSubPolygon points always points to a sub polygon that cannot be further split in 2
			SimplePolygon finalSubPolygon;

			//Check which one of the 2 new splits cannot be split further
			//If the first split cannot be split by either jk or ki, then that first split is final
			if(!jkPattern.canSplit(splitPolygon.get(0)) && !kiPattern.canSplit(splitPolygon.get(0))) {
				finalSubPolygon = splitPolygon.get(0);
				System.out.println("0 is final: " + finalSubPolygon.points());
				//Now, the polygon to split is the other element of the previous split
				polygonToSplit = splitPolygon.get(1);
			} else {
				//Otherwise, the other part is the final first split
				finalSubPolygon = splitPolygon.get(1);
				System.out.println("0 is final: " + finalSubPolygon.points());
				//Now, the polygon to split is the other element of the previous split
				polygonToSplit = splitPolygon.get(0);
			}
			//At that point, we can add the first final sub polygon
			polygons.add(finalSubPolygon);
			System.out.println("Splitting: " + polygonToSplit.points() + " along " + j + "; " + k);
			//Split on jk
			splitPolygon = jkPattern.split(polygonToSplit);
			splitPolygon.forEach(p -> System.out.println("p: " + p.points()));

			//If the first split from the jk split cannot be split further, then that split is final
			if(!kiPattern.canSplit(splitPolygon.get(0))) {
				finalSubPolygon = splitPolygon.get(0);
				System.out.println("0 is final: " + finalSubPolygon.points());
				polygonToSplit = splitPolygon.get(1);
			} else {
				finalSubPolygon = splitPolygon.get(1);
				System.out.println("1 is final: " + finalSubPolygon.points());
				polygonToSplit = splitPolygon.get(0);
			}
			//At that point, we can add the second final sub polygon
			polygons.add(finalSubPolygon);
			System.out.println("Splitting: " + polygonToSplit.points() + " along " + k + "; " + i);
			//Lastly, split the non-final sub polygon into the 2 last (final) parts
			splitPolygon = kiPattern.split(polygonToSplit);
			System.out.println("Split into: ");
			splitPolygon.forEach(p -> System.out.println("p: " + p.points()));
			//And add both those parts in the list
			polygons.addAll(splitPolygon);

			return polygons;
		}

		@Override
		public boolean canSplit(final SimplePolygon polygon) {
			return polygon.points().contains(i) && polygon.points().contains(j) && polygon.points().contains(k)
					&& !polygon.getEdges().contains(new PVector[]{i, j}) && !polygon.getEdges().contains(new PVector[]{j, i})
					&& !polygon.getEdges().contains(new PVector[]{j, k}) && !polygon.getEdges().contains(new PVector[]{k, j})
					&& !polygon.getEdges().contains(new PVector[]{k, i}) && !polygon.getEdges().contains(new PVector[]{i, k});
		}

		@Override
		public boolean isSimilar(final Pattern pattern) {
			if(!(pattern instanceof X3Pattern x3Pattern)) {
				return false;
			}

			return (this.i.equals(x3Pattern.i) || this.i.equals(x3Pattern.j) || this.i.equals(x3Pattern.k))
					&& (this.j.equals(x3Pattern.i) || this.j.equals(x3Pattern.j) || this.j.equals(x3Pattern.k))
					&& (this.k.equals(x3Pattern.i) || this.k.equals(x3Pattern.j) || this.k.equals(x3Pattern.k))
					&& !(x3Pattern.i.equals(x3Pattern.j) || x3Pattern.i.equals(x3Pattern.k) || x3Pattern.j.equals(x3Pattern.k));
		}

	}

}
