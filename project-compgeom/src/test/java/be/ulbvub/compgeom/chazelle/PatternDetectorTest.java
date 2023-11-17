package be.ulbvub.compgeom.chazelle;

import be.ulbvub.compgeom.SimplePolygon;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import processing.core.PVector;

import java.util.ArrayList;
import java.util.Arrays;

public class PatternDetectorTest {

	//A flag-shaped polygon
	private SimplePolygon flagPolygon;

	private SimplePolygon complexNonConvexShape;

	private SimplePolygon bigNonConvexShape;

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
	void test() {
		System.out.println(new NotchFinder(bigNonConvexShape).findNotches().getNotches());
		final var patternDetector = new PatternDetector(bigNonConvexShape);
		patternDetector.detectPatterns();
		System.out.println("Data:");

		for(final PatternDetector.PatternData data : patternDetector.getPatternData()) {
			System.out.println(data.toString());
		}
	}

}
