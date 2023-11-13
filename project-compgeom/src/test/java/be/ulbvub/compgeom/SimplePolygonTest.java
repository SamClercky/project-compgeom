package be.ulbvub.compgeom;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import processing.core.PVector;

import java.text.DecimalFormat;
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

}
