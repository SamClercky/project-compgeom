package be.ulbvub.compgeom;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import processing.core.PVector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class NotchFinderTest {

	//A flag-shaped polygon
	private SimplePolygon flagPolygon;

	@BeforeEach
	void setup() {
		//The (1, 1) point is a reflex point
		this.flagPolygon = new SimplePolygon(new ArrayList<>(Arrays.asList(
				new PVector(0, 0),
				//reflex point
				new PVector(1, 1),
				new PVector(0, 2),
				new PVector(2, 2),
				new PVector(2, 0)
		)));
	}

	@Test
	void given_flag_polygon_then_only_point_1_1_is_a_notch() {
		final var notchFinder = new NotchFinder(flagPolygon);
		final List<PVector> notches = notchFinder.findNotches().getNotches();
		final List<PVector> expected = new ArrayList<>(List.of(new PVector(1, 1)));

		Assertions.assertEquals(expected, notches);
	}

}
