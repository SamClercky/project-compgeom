package be.ulbvub.compgeom;

import be.ulbvub.compgeom.chazelle.NotchFinder;
import processing.core.PVector;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
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
			throw new IllegalArgumentException("points cannot be empty.");
		}
		if(index + 1 >= points.size()) {
			return points.get(0);
		}

		return points.get(index + 1);
	}

	private PVector getPrevious(final int index) {
		if(points.isEmpty()) {
			throw new IllegalArgumentException("points cannot be empty.");
		}
		if(index - 1 < 0) {
			return points.get(points.size() - 1);
		}

		return points.get(index - 1);
	}

	/**
	 * Split this instance along the points ab. This method returns the left part polygon of the split.
	 * This instance becomes the right part polygon of the split.
	 * Both resulting polygons share the ab edge.
	 *
	 * @param a the first extremity of the cut
	 * @param b the second extremity of the cut
	 *
	 * @throws IllegalArgumentException if one of the points do not belong to the polygon
	 *
	 * @return the left part polygon of the split.
	 */
	public SimplePolygon split(final PVector a, final PVector b) {
		final List<PVector> subPolygonPoints = new ArrayList<>();
		boolean buildingSubPolygon = false;

		if(!points.contains(a) || !points.contains(b)) {
			throw new IllegalArgumentException("Both points must belong to the polygon.");
		}

		for (final PVector point : points) {
			//As soon as the first point a is visited, start building the sub polygon
			if(point.equals(a) || point.equals(b)) {

				//If the process was currently building, the last point must still be added in the polygon
				if(buildingSubPolygon) {
					subPolygonPoints.add(point.copy());
				}
				//Switch the value;
				buildingSubPolygon = !buildingSubPolygon;
			}
			//Add the point only if the sub polygon is being built
			if(buildingSubPolygon) {
				subPolygonPoints.add(point.copy());
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
	 * Get the notch range R(v) of the given notch v, i.e. the points u where splitting this polygon along uv reduces the
	 * number of notches in the resulting polygons.
	 *
	 * @param notch the notch, referred as 'v' in the paper
	 * @param nP    the number of notches in P
	 * @return a list of points, in clockwise order, making for the range of the notch
	 */
	public List<PVector> getNotchRange(final PVector notch, final int nP) {
		final List<PVector> range = new ArrayList<>();

		//Immediately return the empty set if size < 4 as no notch can exist in a polygon < 4
		if(size() < 4) {
			return range;
		}
		int i = points.indexOf(notch);
		final PVector after = getNext(i);
		final PVector before = getPrevious(i);
		PVector u = getNext(i);

		//Visit all other points until we hit the notch
		while(!u.equals(notch)) {
			i = points.indexOf(u);

			//Do not choose a vertex incident to the given notch or equal to the notch nor an edge not fully inside
			if(u.equals(after) || u.equals(before) || !isInside(notch, u)) {
				u = getNext(i);
				continue;
			}
			final SimplePolygon subPolygonA = this.clone();
			final SimplePolygon subPolygonB = subPolygonA.split(notch, u);

			final List<PVector> subPolygonANotches = new NotchFinder(subPolygonA).findNotches().getNotches();
			final List<PVector> subPolygonBNotches = new NotchFinder(subPolygonB).findNotches().getNotches();

			//If the subdivision of P, using u, into PA and PB reduces the number of notches -> add u in the range
			if(subPolygonANotches.size() + subPolygonBNotches.size() == nP - 1) {
				range.add(u);
			}

			u = getNext(i);
		}

		return range;
	}

	public List<PVector[]> getEdges() {
		final List<PVector[]> edges = new ArrayList<>();

		for(int i = 0; i < points().size(); i++) {
			edges.add(new PVector[]{points.get(i), getNext(i)});
		}

		return edges;
	}

	/**
	 * @return the number of points making this polygon instance
	 */
	public int size() {
		return points.size();
	}

	/**
	 * Check if the segment a b exists within this instance.
	 *
	 * @param a a segment extremity
	 * @param b a segment extremity
	 * @return true if a b exists within this instance
	 */
	public boolean exists(final PVector a, final PVector b) {
		//Check if both points are in the polygon
		if(points.contains(a) && points.contains(b)) {
			final int index = points.indexOf(a);

			//Check if a is consecutive with b
			return getNext(index).equals(b) || getPrevious(index).equals(b);
		}

		return false;
	}

	private boolean isAtoBInside(final PVector a, final PVector b) {
		//Check for notches clockwise
		final List<PVector> notches = new NotchFinder(this).findNotches().getNotches();

		//No notches mean the polygon is convex, hence a b is always inside with a and b points of P.
		if(notches.isEmpty()) {
			return true;
		}
		boolean inBetween = false;

		for(final PVector point : points) {
			if(inBetween && notches.contains(point)) {
				//Check if left turn
				if(getTurnDirection(a, point, b) > 0) {
					return false;
				}
			}
			if(point.equals(a)) {
				inBetween = true;
			}
			if(point.equals(b)) {
				break;
			}
		}

		return true;
	}

	/**
	 * Check whether the point v is inside this polygon.
	 *
	 * @param v the point
	 *
	 * @return true if v is inside tis polygon
	 */
	public boolean isInside(final PVector v) {
		if(points.contains(v)) {
			return true;
		}
		final var vPrime = new PVector(v.x + 100_000, v.y);
		final PVector[] ray = new PVector[] {v, vPrime};
		int counter = 0;
		int intersectionsWithP = 0;

		for(final PVector[] edge : getEdges()) {
			if(doIntersect(edge, ray)) {
				//Check if co-linear
				if(getTurnDirection(edge[0], v, edge[1]) == 0) {
					return onSegment(edge[0], edge[1], v);
				}
				final Optional<PVector> intersection = getIntersection(edge, ray);

				//If this evaluates to true, the counter will be incremented twice for the same coordinates
				if(intersection.isPresent() && points.contains(intersection.get())) {
					intersectionsWithP++;
				}
				counter++;
			}
		}

		//Return true if the number of effective intersection is odd
		return (counter - (intersectionsWithP / 2)) % 2 == 1;
	}

	/**
	 * Check whether the segment a b, where both a and b are points of the polygon, is inside this polygon.
	 *
	 * @param a an endpoint
	 * @param b an endpoint
	 * @return true if a b is inside this polygon's instance
	 */
	public boolean isInside(final PVector a, final PVector b) {
		//If the segment a b is a segment of this polygon, then a b is inside
		if(exists(a, b)) {
			return true;
		}
		if(points.contains(a) && points.contains(b)) {
			//Check for any intersection occurring somewhere else than one of the endpoints of the segment to check
			for(final PVector[] edge : getEdges()) {
				if(getIntersection(edge, new PVector[]{a, b})
						.map(intersection -> !(intersection.x == a.x && intersection.y == a.y) && !(intersection.x == b.x && intersection.y == b.y))
						//No intersection -> false
						.orElse(false))
				{
					//If an intersection is found, the segment cannot be fully inside P
					return false;
				}
			}
			//No intersections means that ab can be FULLY outside OR FULLY inside
			//=> If FULLY outside: ALL points of ab are outside. If FULLY inside: ALL points of ab are inside

			//Take a point in the middle of the segment ab (any other point in ]ab[ would work):
			final var middlePoint = new PVector((b.x + a.x) / 2, (b.y + a.y) / 2);

			return isInside(middlePoint);

		} else if(points.contains(a) || points.contains(b)) {
			final PVector v = points.contains(a) ? a : b;
			final PVector m = points.contains(a) ? b : a;

			//If m is not inside P, then vm cannot be inside P
			if(!isInside(m)) {
				return false;
			}
			//If m is inside P, check if vm has intersects with edges, excluding endpoints
			for(final PVector[] edge : getEdges()) {
				if(getIntersection(edge, new PVector[] {v, m})
						//Check if the intersection coordinate is not a point of P nor the intersection occurs at m
						.map(intersection -> !(intersection.x == v.x && intersection.y == v.y) && !(intersection.x == m.x && intersection.y == m.y))
						//If no intersection found, then return false (i.e. no intersection found yet)
						.orElse(false)) {

					//Then ab (= vm) is not in P
					return false;
				}
			}

			return true;

		} else {
			throw new IllegalArgumentException("One of the endpoints must be points of this polygon's instance");
		}
	}

	public boolean isInside(final PVector[] segment) {
		return isInside(segment[0], segment[1]);
	}

	@Override
	public SimplePolygon clone() {
		return new SimplePolygon(points.stream().map(PVector::copy).collect(Collectors.toCollection(LinkedList::new)));
	}

	/**
	 * Compute the angle formed by the points a, b and c where b is the angle point.
	 *
	 * @param a point a
	 * @param b point b, the angle point
	 * @param c point c
	 *
	 * @return the angle in rad
	 */
	public static double orientedAngle(final PVector a, final PVector b, final PVector c) {
		//length b -- a
		final double lengthBA = length(b, a);
		//length b -- c
		final double lengthBC = length(b, c);
		//length a -- c
		final double lengthAC = length(a, c);

		//arc cos((BA² + BC² - AC²) - (2.BA.BC))
		return Math.acos((Math.pow(lengthBA, 2) + Math.pow(lengthBC, 2) - Math.pow(lengthAC, 2)) / (2 * lengthBA * lengthBC));
	}

	/**
	 * Compute the length between the points a and b.
	 *
	 * @param a the point a
	 * @param b the point b
	 *
	 * @return the length between a and b
	 */
	public static double length(final PVector a, final PVector b) {
		//sqrt( (a.x - b.x)² + (a.y - b.y)² )
		return Math.sqrt(Math.pow(a.x - b.x, 2) + Math.pow(a.y - b.y, 2));
	}

	/**
	 * Get the turn direction from l to s, using the common point as the angle point.
	 *
	 * @param l the first edge
	 * @param s the second edge
	 *
	 * @return a positive value for a left turn, a negative value for a right turn and 0 otherwise
	 */
	public static int getTurnDirection(final PVector[] l, final PVector[] s) {
		if(l[0] == s[0]) {
			return getTurnDirection(l[1], l[0], s[1]);
		}
		if(l[0] == s[1]) {
			return getTurnDirection(l[1], l[0], s[0]);
		}
		if(l[1] == s[0]) {
			return getTurnDirection(l[0], l[1], s[1]);
		}
		if(l[1] == s[1]) {
			return getTurnDirection(l[0], l[1], s[0]);
		}

		throw new IllegalArgumentException("l and s must share exactly one common vertex");
	}

	/**
	 * Get the turn direction (left or right). The method assumes the vertices are considered in clockwise order, i.e.
	 * a left turn indicates a reflex point.
	 *
	 * @param a the first point
	 * @param b the second point (the angle point)
	 * @param c the third point
	 *
	 * @return a positive value for a left turn, a negative value for a right turn and 0 otherwise
	 */
	public static int getTurnDirection(final PVector a, final PVector b, final PVector c) {
		//(x2 - x1) * (y3 - y1) - (y2 - y1) * (x3 - x1)
		final double value = (b.x - a.x) * (c.y - a.y) - (b.y - a.y) * (c.x - a.x);

		if(value > 0) {
			return 1;
		} else if(value < 0) {
			return -1;
		} else {
			return 0;
		}
	}

	/**
	 * Check if the point v lies on the segment ab.
	 *
	 * @param a the edge endpoint
	 * @param b the edge other endpoint
	 * @param v the point to test
	 *
	 * @return true if v lies on [a, b]
	 */
	private static boolean onSegment(final PVector a, final PVector b, final PVector v) {
		return v.x <= Math.max(a.x, b.x)
				&& v.x >= Math.min(a.x, b.x)
				&& v.y <= Math.max(a.y, b.y)
				&& v.y >= Math.min(a.y, b.y);
	}

	private static boolean onSegment(final PVector l[], final PVector v) {
		return onSegment(l[0], l[1], v);
	}

	/**
	 * Check if the segments p1-p2 and p3-p4 intersect.
	 *
	 * @param p1
	 * @param p2
	 * @param p3
	 * @param p4
	 *
	 * @return true if the segments intersect
	 */
	public static boolean doIntersect(final PVector p1, final PVector p2, final PVector p3, final PVector p4) {
		final double orientation1 = SimplePolygon.getTurnDirection(p3, p4, p1);
		final double orientation2 = SimplePolygon.getTurnDirection(p3, p4, p2);
		final double orientation3 = SimplePolygon.getTurnDirection(p1, p2, p3);
		final double orientation4 = SimplePolygon.getTurnDirection(p1, p2, p4);

		if(( (orientation1 > 0 && orientation2 < 0) || (orientation1 < 0 && orientation2 > 0) )
				&& ( (orientation3 > 0 && orientation4 < 0) || (orientation3 < 0 && orientation4 > 0) )) {
			return true;
		}
		if(orientation1 == 0 && onSegment(p3, p4, p1)) {
			return true;
		}
		if(orientation2 == 0 && onSegment(p3, p4, p2)) {
			return true;
		}
		if(orientation3 == 0 && onSegment(p1, p2, p3)) {
			return true;
		}
		if(orientation4 == 0 && onSegment(p1, p2, p4)) {
			return true;
		}

		return false;
	}

	public static boolean doIntersect(final PVector[] l, final PVector[] s) {
		return doIntersect(l[0], l[1], s[0], s[1]);
	}

	public static Optional<PVector> getIntersection(final PVector[] l, final PVector[] s) {
		if(!doIntersect(l[0], l[1], s[0], s[1])) {
			return Optional.empty();
		}
		if(l[0].equals(s[0])) {
			return Optional.of(l[0]);
		}
		if(l[0].equals(s[1])) {
			return Optional.of(l[0]);
		}
		if(l[1].equals(s[0])) {
			return Optional.of(l[1]);
		}
		if(l[1].equals(s[1])) {
			return Optional.of(l[1]);
		}
		//Special case: one of the edges is vertical (i.e. x = constant. no y)
		final float lSlope = (l[1].y - l[0].y) / (l[1].x - l[0].x);
		final float sSlope = (s[1].y - s[0].y) / (s[1].x - s[0].x);

		final float lOriginY = l[0].y - (lSlope * l[0].x);
		final float sOriginY = s[0].y - (sSlope * s[0].x);
		//Compute x coordinate of intersection:
		float x = (sOriginY - lOriginY) / (lSlope - sSlope);
		//Compute y coordinate of intersection:
		float y = lSlope * x + lOriginY;

		if(l[0].x == l[1].x) {
			//i.e l is vertical -> x-coordinate of intersection is x of l
			y = sSlope * l[0].x + sOriginY;
			x = l[0].x;
		}
		if(s[0].x == s[1].x) {
			//i.e s is vertical -> x-coordinate of intersection is x of s
			y = lSlope * s[0].x + lOriginY;
			x = s[0].x;
		}
		x = new BigDecimal(Float.toString(x)).setScale(6, RoundingMode.HALF_DOWN).floatValue();
		y = new BigDecimal(Float.toString(y)).setScale(6, RoundingMode.HALF_DOWN).floatValue();

		return Optional.of(new PVector(x, y));
	}

	/*
	 //Special case: one of the edges is vertical (i.e. x = constant. no y)
		final var lSlope = (BigDecimal.valueOf(l[1].x)).subtract(BigDecimal.valueOf(l[0].x)).floatValue() == 0 ? null :
				BigDecimal.valueOf(l[1].y).subtract(BigDecimal.valueOf(l[0].y))
				.divide((BigDecimal.valueOf(l[1].x)).subtract(BigDecimal.valueOf(l[0].x)), RoundingMode.DOWN);

		final var sSlope = (BigDecimal.valueOf(s[1].x)).subtract(BigDecimal.valueOf(s[0].x)).floatValue() == 0 ? null :
				BigDecimal.valueOf(s[1].y).subtract(BigDecimal.valueOf(s[0].y))
				.divide((BigDecimal.valueOf(s[1].x)).subtract(BigDecimal.valueOf(s[0].x)), RoundingMode.DOWN);

		final var lOriginY = lSlope == null ? null :
				BigDecimal.valueOf(l[0].y).subtract( lSlope.multiply(BigDecimal.valueOf(l[0].x)) );
		final var sOriginY = sSlope == null ? null :
				BigDecimal.valueOf(s[0].y).subtract( sSlope.multiply(BigDecimal.valueOf(s[0].x)) );

		if(l[0].x == l[1].x) {
			//i.e l is vertical -> x-coordinate of intersection is x of l
			var y = sSlope.multiply(BigDecimal.valueOf(l[0].x)).add(sOriginY);
			var x = BigDecimal.valueOf(l[0].x);

			return Optional.of(new PVector(x.floatValue(), y.floatValue()));
		}
		if(s[0].x == s[1].x) {
			//i.e s is vertical -> x-coordinate of intersection is x of s
			var y = lSlope.multiply(BigDecimal.valueOf(s[0].x)).add(lOriginY);
			var x = BigDecimal.valueOf(s[0].x);

			return Optional.of(new PVector(x.floatValue(), y.floatValue()));
		}

		//Compute x coordinate of intersection:
		var x = new BigDecimal(Float.toString((sOriginY.floatValue() - lOriginY.floatValue()) / (lSlope.floatValue() - sSlope.floatValue())));
		//Compute y coordinate of intersection:
		var y = new BigDecimal(Float.toString(lSlope.floatValue() * x.floatValue() + lOriginY.floatValue()));

		return Optional.of(new PVector(x.floatValue(), y.floatValue()));
	 */

}
