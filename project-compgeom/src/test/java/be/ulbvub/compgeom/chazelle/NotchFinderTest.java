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

	private SimplePolygon flagPolygonWithReflexPointAsFirstPoint;

	private SimplePolygon complexNonConvexShape;

	private SimplePolygon convexTriangleWithColinearPoints;

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
		this.flagPolygonWithReflexPointAsFirstPoint = new SimplePolygon(new ArrayList<>(Arrays.asList(
				//reflex point
				new PVector(1, 1),
				new PVector(0, 2),
				new PVector(2, 2),
				new PVector(2, 0),
				new PVector(0, 0)
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
		this.convexTriangleWithColinearPoints = new SimplePolygon(new ArrayList<>(Arrays.asList(
				new PVector(0, 0),
				new PVector(0, 7),
				new PVector(9, 7),
				new PVector(3, 1),
				new PVector(2, 0))
		));
	}

	@Test
	void given_polygon_then_notch_finder_retrieves_valid_points() {
		final var notchFinderA = new NotchFinder(flagPolygon);
		final List<PVector> notchesA = notchFinderA.findNotches().getNotches();
		final List<PVector> expectedA = List.of(new PVector(1, 1));

		final var notchFinderB = new NotchFinder(complexNonConvexShape);
		final List<PVector> notchesB = notchFinderB.findNotches().getNotches();
		final List<PVector> expectedB = List.of(new PVector(3, 5),
				new PVector(6, 9),
				new PVector(7, 4),
				new PVector(4, 2));

		//Check a full convex polygon with co linear points
		final var notchFinderC = new NotchFinder(convexTriangleWithColinearPoints);
		final List<PVector> notchesC = notchFinderC.findNotches().getNotches();
		//No notches should be found
		final List<PVector> expectedC = new ArrayList<>();

		final var notchFinderD = new NotchFinder(flagPolygonWithReflexPointAsFirstPoint);
		final List<PVector> notchesD = notchFinderD.findNotches().getNotches();
		final List<PVector> expectedD = new ArrayList<>(List.of(new PVector(1, 1)));

		Assertions.assertEquals(expectedA, notchesA);
		Assertions.assertEquals(expectedB, notchesB);
		Assertions.assertEquals(expectedC, notchesC);
		Assertions.assertEquals(expectedD, notchesD);
	}

}
