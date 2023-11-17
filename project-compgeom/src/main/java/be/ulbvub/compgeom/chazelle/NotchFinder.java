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
		//The first index acts as the last index for the last angle
		PVector firstVertex = null;
		final List<PVector> angleQueue = new ArrayList<>();

		if(polygon.size() < 3) {
			return this;
		}
		for(final PVector vertex : polygon.points()) {
			angleQueue.add(vertex);

			if(firstVertex == null) {
				firstVertex = vertex;
			}
			//Check if an angle must be checked
			if(angleQueue.size() >= 3) {
				//Check if it's a left turn
				if(SimplePolygon.getTurnDirection(angleQueue.get(0), angleQueue.get(1), angleQueue.get(2)) > 0) {
					//Put the middle point in the notches list
					notches.add(angleQueue.get(1));
				}
				//Remove the tail, allowing for the next angle to be checked
				angleQueue.remove(0);
			}
		}
		if(SimplePolygon.getTurnDirection(angleQueue.get(0), angleQueue.get(1), firstVertex) > 0) {
			//Put the middle point in the notches list
			notches.add(angleQueue.get(1));
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
