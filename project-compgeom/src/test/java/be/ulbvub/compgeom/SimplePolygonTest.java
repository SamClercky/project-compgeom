package be.ulbvub.compgeom;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import processing.core.PVector;

import java.text.DecimalFormat;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class SimplePolygonTest {

	//A flag-shaped polygon
	private SimplePolygon flagPolygon;

	private SimplePolygon bigNonConvexShape;

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
		this.bigNonConvexShape = new SimplePolygon(new ArrayList<>(Arrays.asList(
				new PVector(6, 0),
				//Reflex point
				new PVector(6, 2),
				new PVector(4, 1),
				//Reflex point
				new PVector(4, 5),
				new PVector(2, 3),
				new PVector(0, 6),
				//Reflex point
				new PVector(4, 7),
				//Reflex point
				new PVector(4, 8),
				new PVector(0, 8),
				//Reflex point
				new PVector(3, 11),
				new PVector(2, 14),
				//Reflex point
				new PVector(6, 12),
				new PVector(9, 15),
				//Reflex point
				new PVector(11, 10),
				new PVector(14, 12),
				new PVector(12, 5),
				//Reflex point
				new PVector(9, 7),
				new PVector(10, 1)
		)));
	}

	@Test
	void given_polygon_then_get_notch_range_returns_the_actual_notch_range() {
		final List<PVector> expectedRange = new ArrayList<>(Set.of(
				new PVector(2, 2),
				new PVector(2, 0)
		));
		final List<PVector> flagPolygonNotchRange = flagPolygon.getNotchRange(new PVector(1, 1), 1);

		assertEquals(expectedRange, flagPolygonNotchRange);
	}

	@Test
	void given_3_points_then_oriented_angle_returns_valid_result() {
		//8 digits decimal precision
		final var format = new DecimalFormat("#.########");

		final double angle1 = SimplePolygon.orientedAngle(new PVector(0, 1),
				new PVector(0, 0),
				new PVector(1, 0));
		final double angle2 = SimplePolygon.orientedAngle(new PVector((float) Math.cos(Math.PI/4), (float) Math.sin(Math.PI / 4)),
				new PVector(0, 0),
				new PVector(1, 0));
		final double angle3 = SimplePolygon.orientedAngle(new PVector((float) Math.cos(Math.PI/6), (float) Math.sin(Math.PI / 6)),
				new PVector(0, 0),
				new PVector(1, 0));

		assertEquals(format.format(Math.PI/2), format.format(angle1));
		assertEquals(format.format(Math.PI/4), format.format(angle2));
		assertEquals(format.format(Math.PI/6), format.format(angle3));
	}

	@Test
	void given_2_points_then_within_polygon_returns_correct_value() {
		assertTrue(flagPolygon.exists(new PVector(0, 0), new PVector(2, 0)));
		assertTrue(flagPolygon.exists(new PVector(1, 1), new PVector(0, 2)));
		assertFalse(flagPolygon.exists(new PVector(0, 0), new PVector(0, 2)));
	}

	@Test
	void given_2_points_of_P_then_is_inside_returns_correct_value() {
		//Check inside is true
		assertTrue(flagPolygon.isInside(new PVector(1, 1), new PVector(2, 2)));
		assertTrue(flagPolygon.isInside(new PVector(2, 2), new PVector(1, 1)));
		//Check outside is false
		assertFalse(flagPolygon.isInside(new PVector(0, 0), new PVector(0, 2)));
		assertFalse(flagPolygon.isInside(new PVector(0, 2), new PVector(0, 0)));
		//Check for a b where the segment a b is in P
		assertTrue(flagPolygon.isInside(new PVector(1, 1), new PVector(0, 0)));
		assertTrue(flagPolygon.isInside(new PVector(0, 0), new PVector(1, 1)));

		//Check for a more complex polygon
		assertFalse(bigNonConvexShape.isInside(new PVector(4, 5), new PVector(0, 8)));
		assertFalse(bigNonConvexShape.isInside(new PVector(0, 8), new PVector(4, 5)));
		assertFalse(bigNonConvexShape.isInside(new PVector(4, 5), new PVector(3, 11)));
		assertFalse(bigNonConvexShape.isInside(new PVector(3, 11), new PVector(4, 5)));

		assertTrue(bigNonConvexShape.isInside(new PVector(9, 7), new PVector(6, 12)));
		assertTrue(bigNonConvexShape.isInside(new PVector(6, 12), new PVector(9, 7)));

		//Check that isInside throws when dealing with non-existing points of P
		assertThrows(IllegalArgumentException.class, () -> flagPolygon.isInside(new PVector(15, -4), new PVector(2, 2)));
	}

	@Test
	void given_2_intersecting_segments_then_get_intersection_returns_correct_coordinates() {
		final PVector[] segmentA = new PVector[] {new PVector(0, 0), new PVector(3, 3)};
		final PVector[] segmentB = new PVector[] {new PVector(0, 3), new PVector(3, 0)};

		assertEquals(Optional.of(new PVector(1.5f, 1.5f)), SimplePolygon.getIntersection(segmentA, segmentB));
	}

	@Test
	void intersection_test() {
		final PVector p1 = new PVector(3, 11);
		final PVector q1 = new PVector(6, 2);
		final PVector p2 = new PVector(0, 8);
		final PVector q2 = new PVector(4, 8);
		final PVector q22 = new PVector(3.99f, 8);

		assertTrue(SimplePolygon.doIntersect(p1, q1, p2, q2));
		assertFalse(SimplePolygon.doIntersect(p1, q1, p2, q22));
	}

}
