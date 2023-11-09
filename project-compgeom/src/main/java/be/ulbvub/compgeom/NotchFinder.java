package be.ulbvub.compgeom;

import processing.core.PVector;

import java.util.*;

/**
 * The NotchFinder class finds all the notches of a polygon. It executes in linear time.
 */
public class NotchFinder {

	private final Polygon polygon;

	private final List<PVector> notches = new ArrayList<>();

	/**
	 * @param polygon the polygon from which the notches will be found
	 */
	public NotchFinder(final Polygon polygon) {
		this.polygon = polygon;
	}

	/**
	 * Find all the notches of this instance's polygon, that is, all the points forming a reflex angle with its neighbours.
	 *
	 * @return this
	 */
	public NotchFinder findNotches() {
		//The first index acts as the last index for the last angle
		PVector firstVertex = null;
		final List<PVector> angleQueue = new ArrayList<>();

		for(final Iterator<PVector> iterator = polygon.ccwIterator(); iterator.hasNext();) {
			final PVector vertex = iterator.next();
			angleQueue.add(vertex);

			if(firstVertex == null) {
				firstVertex = vertex;
			}
			//Check if an angle must be checked
			if(angleQueue.size() >= 3) {
				//Check if it's a right turn. Since we're in ccw, right turn -> reflex point
				if(getTurnDirection(angleQueue.get(0), angleQueue.get(1), angleQueue.get(2)) > 0) {
					//Put the middle point in the notches list
					notches.add(angleQueue.get(1));
				}
				//Remove the tail, allowing for the next angle to be checked
				angleQueue.remove(0);
			}
		}
		if(getTurnDirection(angleQueue.get(0), angleQueue.get(1), firstVertex) > 0) {
			//Put the middle point in the notches list
			notches.add(angleQueue.get(1));
		}

		return this;
	}

	private double getTurnDirection(final PVector a, final PVector b, final PVector c) {
		//(x2 - x1) * (y3 - y1) - (y2 - y1) * (x3 - x1)
		return (b.x - a.x) * (c.y - a.y) - (b.y - a.y) * (c.x - a.x);
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
