package be.ulbvub.compgeom.chazelle;

import be.ulbvub.compgeom.SimplePolygon;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import processing.core.PVector;

import java.util.ArrayList;
import java.util.Arrays;

public class PatternTest {

	private SimplePolygon nonConvexRectangle;

	@BeforeEach
	void setup() {
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
	void given_polygon_and_xk_patterns_then_can_split_returns_correct_value() {
		final var x3Pattern = new Pattern.X3Pattern(new PVector(16, 6), new PVector(22, 6), new PVector(19, 1));
		final var x2Pattern = new Pattern.X2Pattern(new PVector(10, 6), new PVector(10, 1));

		Assertions.assertTrue(x3Pattern.canSplit(nonConvexRectangle));
		Assertions.assertTrue(x2Pattern.canSplit(nonConvexRectangle));
	}

}
