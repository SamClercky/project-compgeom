package be.ulbvub.compgeom.chazelle;

import be.ulbvub.compgeom.SimplePolygon;
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

	private SimplePolygon complexNonConvexShape;

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
		this.complexNonConvexShape = new SimplePolygon(new ArrayList<>(Arrays.asList(
				new PVector(3, 0),
				new PVector(0, 5),
				//Reflex point
				new PVector(3, 5),
				new PVector(1, 10),
				new PVector(4, 12),
				//Reflex point
				new PVector(6, 9),
				new PVector(8, 11),
				new PVector(10, 9),
				//Reflex point
				new PVector(7, 4),
				new PVector(9, 3),
				new PVector(5, 0),
				//Reflex point
				new PVector(4, 2)
		)));
	}

	@Test
	void given_flag_polygon_then_only_point_1_1_is_a_notch() {
		final var notchFinder = new NotchFinder(flagPolygon);
		final List<PVector> notches = notchFinder.findNotches().getNotches();
		final List<PVector> expected = List.of(new PVector(1, 1));

		Assertions.assertEquals(expected, notches);
	}

	@Test
	void given_complex_non_convex_polygon_then_valid_notches_are_returned() {
		final var notchFinder = new NotchFinder(complexNonConvexShape);
		final List<PVector> notches = notchFinder.findNotches().getNotches();
		final List<PVector> expected = List.of(new PVector(3, 5),
				new PVector(6, 9),
				new PVector(7, 4),
				new PVector(4, 2));

		Assertions.assertEquals(expected, notches);
	}

}
