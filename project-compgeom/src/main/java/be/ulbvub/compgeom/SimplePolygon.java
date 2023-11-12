package be.ulbvub.compgeom;

import processing.core.PVector;

import java.util.*;
import java.util.stream.Collectors;

/**
 * A very simple polygon implementation for pure algorithmic purpose.
 * <p>
 * The points must be given in CLOCKWISE order. i.e. each point in the list is linked to the next one in the list.
 * <p>
 * The points of the polygon. Each point in the list is assumed to be linked with the next and the previous one,
 * forming an edge of the polygon.
 *
 * @param points the points of the polygon
 */
public record SimplePolygon(List<PVector> points) {

	private PVector getNext(final int index) {
		if(points.isEmpty()) {
			return null;
		}
		if(index + 1 >= points.size()) {
			return points.get(0);
		}

		return points.get(index + 1);
	}

	private PVector getPrevious(final int index) {
		if(points.isEmpty()) {
			return null;
		}
		if(index - 1 < 0) {
			return points.get(points.size() - 1);
		}

		return points.get(index - 1);
	}

	private SimplePolygon split(final PVector a, final PVector b) {
		final List<PVector> subPolygonPoints = new ArrayList<>();
		boolean buildingSubPolygon = false;

		for (final PVector point : points) {
			//As soon as the first point a is visited, start building the sub polygon
			if(point.equals(a)) {
				buildingSubPolygon = true;
			}
			//Add the point only if the sub polygon is being built
			if(buildingSubPolygon) {
				subPolygonPoints.add(point.copy());
			}
			//Stop looping as soon as the b point is visited
			if(point.equals(b)) {
				break;
			}
		}
		//Remove all sub polygon points from this instance, effectively splitting it into this object and this method's returned object
		for (final PVector subPolygonPoint : subPolygonPoints) {
			//Keep the common edge
			if(!(subPolygonPoint.equals(a) || subPolygonPoint.equals(b))) {
				this.points.remove(subPolygonPoint);
			}
		}

		return new SimplePolygon(subPolygonPoints);
	}

	/**
	 * Get the notch range R(v) of the given notch v.
	 *
	 * @param notch the notch, referred as 'v' in the paper
	 * @param nP    the number of notches in P
	 * @return a set of points making for the range of the notch
	 */
	public Set<PVector> getNotchRange(final PVector notch, final int nP) {
		final Set<PVector> range = new HashSet<>();

		//Immediately return the empty set if size < 3
		if(size() < 3) {
			return range;
		}
		//Iterate over this instance's vertices
		for (final PVector u : points) {
			final int uIndex = points.indexOf(u);

			//Do not choose a vertex incident to the given notch or equal to the notch
			if(getNext(uIndex).equals(notch) || getPrevious(uIndex).equals(notch) || u.equals(notch)) {
				continue;
			}
			final SimplePolygon subPolygonA = clone();
			final SimplePolygon subPolygonB = subPolygonA.split(notch, u);

			final List<PVector> subPolygonANotches = new NotchFinder(subPolygonA).findNotches().getNotches();
			final List<PVector> subPolygonBNotches = new NotchFinder(subPolygonB).findNotches().getNotches();

			//If the subdivision of P, using u, into PA and PB reduces the number of notches -> add u in the range
			if(subPolygonANotches.size() + subPolygonBNotches.size() == nP - 1) {
				range.add(u);
			}
		}

		return range;
	}

	public int size() {
		return points.size();
	}


	@Override
	public SimplePolygon clone() {
		return new SimplePolygon(points.stream().map(PVector::copy).collect(Collectors.toCollection(LinkedList::new)));
	}

}
