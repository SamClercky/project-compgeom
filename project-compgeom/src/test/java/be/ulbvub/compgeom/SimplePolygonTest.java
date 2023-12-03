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

	private SimplePolygon nonConvexRectangle;

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
		this.nonConvexRectangle = new SimplePolygon(new ArrayList<>(Arrays.asList(
				new PVector(0, 0),
				new PVector(0, 7),
				new PVector(9, 7),
				//Notch
				new PVector(10, 6),
				new PVector(11, 7),
				new PVector(15, 7),
				//Notch
				new PVector(16, 6),
				new PVector(16, 7),
				new PVector(22, 7),
				//Notch
				new PVector(22, 6),
				new PVector(23, 7),
				new PVector(25, 7),
				new PVector(25, 0),
				new PVector(20, 0),
				//Notch
				new PVector(19, 1),
				new PVector(18, 0),
				new PVector(11, 0),
				//Notch
				new PVector(10, 1),
				new PVector(9, 0),
				new PVector(3, 0),
				//Notch
				new PVector(3, 1),
				new PVector(2, 0)
		)));
	}

	@Test
	void given_a_polygon_then_split_returns_valid_polygons() {
		final var rightPolygonA = nonConvexRectangle.clone();
		final var leftPolygonA = rightPolygonA.split(new PVector(10, 6), new PVector(10, 1));

		final var rightPolygonB = nonConvexRectangle.clone();
		final var leftPolygonB = rightPolygonB.split(new PVector(9, 7), new PVector(3, 1));

		assertEquals(new ArrayList<>(List.of(
				new PVector(10, 6),
				new PVector(11, 7),
				new PVector(15, 7),
				//Notch
				new PVector(16, 6),
				new PVector(16, 7),
				new PVector(22, 7),
				//Notch
				new PVector(22, 6),
				new PVector(23, 7),
				new PVector(25, 7),
				new PVector(25, 0),
				new PVector(20, 0),
				//Notch
				new PVector(19, 1),
				new PVector(18, 0),
				new PVector(11, 0),
				//Notch
				new PVector(10, 1))
				), leftPolygonA.points()
		);
		assertEquals(new ArrayList<>(List.of(
				new PVector(0, 0),
				new PVector(0, 7),
				new PVector(9, 7),
				//Notch
				new PVector(10, 6),
				new PVector(10, 1),
				new PVector(9, 0),
				new PVector(3, 0),
				//Notch
				new PVector(3, 1),
				new PVector(2, 0))
				), rightPolygonA.points()
		);

		assertEquals(new ArrayList<>(List.of(
						new PVector(9, 7),
						new PVector(10, 6),
						new PVector(11, 7),
						new PVector(15, 7),
						//Notch
						new PVector(16, 6),
						new PVector(16, 7),
						new PVector(22, 7),
						//Notch
						new PVector(22, 6),
						new PVector(23, 7),
						new PVector(25, 7),
						new PVector(25, 0),
						new PVector(20, 0),
						//Notch
						new PVector(19, 1),
						new PVector(18, 0),
						new PVector(11, 0),
						//Notch
						new PVector(10, 1),
						new PVector(9, 0),
						new PVector(3, 0),
						new PVector(3, 1))
				), leftPolygonB.points()
		);
		assertEquals(new ArrayList<>(List.of(
						new PVector(0, 0),
						new PVector(0, 7),
						new PVector(9, 7),
						new PVector(3, 1),
						new PVector(2, 0))
				), rightPolygonB.points()
		);
	}

	@Test
	void given_polygon_then_get_notch_range_returns_the_actual_notch_range() {
		final List<PVector> expectedRange = new ArrayList<>(List.of(
				new PVector(2, 2),
				new PVector(2, 0)
		));
		final List<PVector> expectedRangeNonConvexRectangle1 = new ArrayList<>(List.of(
				new PVector(9, 7)
		));
		final List<PVector> expectedRangeNonConvexRectangle2 = new ArrayList<>(List.of(
				new PVector(25, 7)
		));
		final List<PVector> flagPolygonNotchRange = flagPolygon.getNotchRange(new PVector(1, 1), 1);
		System.out.println("----");
		final List<PVector> nonConvexRectangleNotchRange1 = nonConvexRectangle.getNotchRange(new PVector(3, 1), 6);

		//final List<PVector> nonConvexRectangleNotchRange2 = nonConvexRectangle.getNotchRange(new PVector(19, 1), 6);

		assertEquals(expectedRange, flagPolygonNotchRange);
		assertEquals(expectedRangeNonConvexRectangle1, nonConvexRectangleNotchRange1);
		//assertEquals(expectedRangeNonConvexRectangle2, nonConvexRectangleNotchRange2);
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
		assertTrue(nonConvexRectangle.isInside(new PVector(3, 1), new PVector(0, 0)));

		assertFalse(flagPolygon.exists(new PVector(0, 0), new PVector(0, 2)));
	}

	@Test
	void given_2_points_of_P_then_isInside_returns_correct_value() {
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
	}

	@Test
	void given_2_segments_then_do_intersect_returns_correct_value() {
		final PVector[] segmentA = new PVector[] {new PVector(0, 10), new PVector(5, 0)};
		final PVector[] segmentB = new PVector[] {new PVector(2.5f, 5), new PVector(7.5f, 5)};

		final PVector[] segmentC = new PVector[] {new PVector(0, 0), new PVector(1, 1)};
		final PVector[] segmentD = new PVector[] {new PVector(1.5f, 1.5f), new PVector(10, 1.5f)};

		assertTrue(SimplePolygon.doIntersect(segmentA, segmentB));
		assertFalse(SimplePolygon.doIntersect(segmentC, segmentD));
	}

	@Test
	void given_2_intersecting_segments_then_get_intersection_returns_correct_coordinates() {
		final PVector[] segmentA = new PVector[] {new PVector(0, 0), new PVector(3, 3)};
		final PVector[] segmentB = new PVector[] {new PVector(0, 3), new PVector(3, 0)};

		assertEquals(Optional.of(new PVector(1.5f, 1.5f)), SimplePolygon.getIntersection(segmentA, segmentB));
	}

	@Test
	void given_1_point_of_P_and_another_point_not_in_P_then_isInside_returns_correct_value() {
		//Check inside is true
		assertTrue(flagPolygon.isInside(new PVector(0, 0), new PVector(1, 0.5f)));
		assertTrue(flagPolygon.isInside(new PVector(0, 0), new PVector(1.5f, 1)));
		assertTrue(flagPolygon.isInside(new PVector(1, 1), new PVector(1, 2)));

		assertFalse(flagPolygon.isInside(new PVector(0, 0), new PVector(-1, 1)));
		assertFalse(flagPolygon.isInside(new PVector(0, 0), new PVector(2, 5f)));
		assertFalse(flagPolygon.isInside(new PVector(0, 0), new PVector(1, 1.5f)));
	}

	@Test
	void given_a_point_isInside_returns_true_if_given_point_is_inside_P() {
		assertTrue(flagPolygon.isInside(new PVector(0, 0)));
		assertTrue(flagPolygon.isInside(new PVector(0.5f, 0.5f)));
		assertTrue(flagPolygon.isInside(new PVector(1.5f, 1)));
		assertTrue(flagPolygon.isInside(new PVector(1.5f, 1.5f)));
		assertTrue(flagPolygon.isInside(new PVector(1, 0)));

		assertFalse(flagPolygon.isInside(new PVector(-1, 1)));
		assertFalse(flagPolygon.isInside(new PVector(3, 1)));
		assertFalse(flagPolygon.isInside(new PVector(-1, 0)));
	}

}
