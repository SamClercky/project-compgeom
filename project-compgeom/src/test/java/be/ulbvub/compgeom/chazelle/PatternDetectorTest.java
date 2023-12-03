package be.ulbvub.compgeom.chazelle;

import be.ulbvub.compgeom.SimplePolygon;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import processing.core.PVector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PatternDetectorTest {

	//A flag-shaped polygon
	private SimplePolygon flagPolygon;

	private SimplePolygon complexNonConvexShape;

	private SimplePolygon bigNonConvexShape;

	private SimplePolygon nonConvexRectangle;

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
				new PVector(9, 6),
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
				new PVector(5, 0),
				//Notch
				new PVector(5, 1),
				new PVector(4, 0)
		)));
	}

	@Test
	void test() {
		System.out.println(new NotchFinder(nonConvexRectangle).findNotches().getNotches());
		final var patternDetector = new PatternDetector(nonConvexRectangle);
		patternDetector.detectPatterns();
		System.out.println("Data:");
		patternDetector.displaySegments();
		System.out.println("Splitting using patterns");
		final List<SimplePolygon> splitPolygons = patternDetector.partition();

		for(final SimplePolygon subPolygon : splitPolygons) {
			System.out.println("split: " + subPolygon.points());
		}
	}

}
