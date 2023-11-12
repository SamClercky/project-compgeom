package be.ulbvub.compgeom;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import processing.core.PVector;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SimplePolygonTest {

	//A flag-shaped polygon
	private SimplePolygon flagPolygon;

	@BeforeEach
	void setUp() {
		//The (1, 1) point is a reflex point
		this.flagPolygon = new SimplePolygon(new LinkedList<>(Arrays.asList(
				new PVector(0, 0),
				//reflex point
				new PVector(1, 1),
				new PVector(0, 2),
				new PVector(2, 2),
				new PVector(2, 0)
		)));
		/*this.flagPolygon = new SimplePolygon(new LinkedList<>() {
			{
				add(new PVector(0, 0));
				//reflex point
				add(new PVector(1, 1));
				add(new PVector(0, 2));
				add(new PVector(2, 2));
				add(new PVector(2, 0));
			}
		});*/
	}

	@Test
	void given_polygon_then_get_notch_range_returns_the_actual_notch_range() {
		final Set<PVector> expectedRange = new HashSet<>(Set.of(
				new PVector(2, 2),
				new PVector(2, 0)
		));
		final Set<PVector> flagPolygonNotchRange = flagPolygon.getNotchRange(new PVector(1, 1), 1);

		assertEquals(expectedRange, flagPolygonNotchRange);
	}

}
