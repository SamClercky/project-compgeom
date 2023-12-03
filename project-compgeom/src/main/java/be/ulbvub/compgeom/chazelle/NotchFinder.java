package be.ulbvub.compgeom.chazelle;

import be.ulbvub.compgeom.SimplePolygon;
import processing.core.PVector;

import java.util.*;

/**
 * The NotchFinder class finds all the notches of a polygon. It executes in linear time.
 */
public class NotchFinder {

	private final SimplePolygon polygon;

	private final List<PVector> notches = new ArrayList<>();

	/**
	 * @param polygon the polygon from which the notches will be found
	 */
	public NotchFinder(final SimplePolygon polygon) {
		this.polygon = polygon;
	}

	/**
	 * Find all the notches of this instance's polygon, that is, all the points forming a reflex angle with its neighbours.
	 * The notches are considered in clockwise order.
	 *
	 * @return this
	 */
	public NotchFinder findNotches() {
		if(polygon.size() < 3) {
			return this;
		}
		final int size = polygon.size();

		//Iterate for all points and get the edges surrounding each of them
		for(int i = 0; i < size; i++) {
			final PVector[] edge1 = new PVector[] {polygon.points().get(i == 0 ? size-1 : i-1),
					polygon.points().get(i)};
			final PVector[] edge2 = new PVector[] {polygon.points().get(i), polygon.points().get((i+1) % size)};

			//Check for a left turn
			if(SimplePolygon.getTurnDirection(edge1, edge2) < 0) {
				//Put the middle point in the notches list
				notches.add(polygon.points().get(i));
			}
		}

		return this;
	}

	/**
	 * Get the notches formed by the polygon if this instance. Always returns an empty list if the findNotches method
	 * isn't called first.
	 *
	 * @return a list of points
	 */
	public List<PVector> getNotches() {
		return notches;
	}

}
