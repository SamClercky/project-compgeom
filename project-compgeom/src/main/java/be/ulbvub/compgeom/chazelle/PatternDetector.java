package be.ulbvub.compgeom.chazelle;

import be.ulbvub.compgeom.SimplePolygon;
import processing.core.PVector;

import java.util.*;

public class PatternDetector {

	private final SimplePolygon polygon;

	private final List<PVector> notches;

	private final int nP;

	//Contains the l' and r' data of each i - j notch, where i = first index (row) and j = second index (column).
	//private final PatternData[][] segmentMatrix;

	//Link each segment to a boolean value set to true if the entirety of this segment is inside P
	private final Map<PVector[], Boolean> segmentPrimes = new HashMap<>();

	private final List<IntersectionData> intersections = new ArrayList<>();

	private final Set<Pattern> xkPatterns = new HashSet<>();

	//private final List<PatternData> dataList = new ArrayList<>();

	public PatternDetector(final SimplePolygon polygon) {
		this.polygon = polygon;
		this.notches = new NotchFinder(polygon).findNotches().getNotches();
		this.nP = notches.size();
	}

	private PVector[] makeR_ij(final PVector v_i, final PVector v_j) {
		boolean inRange = false;
		PVector bestT = null;
		double bestAngle = -1;

		for(final PVector t : notches) {
			//Check whether the notches are between a and b
			boolean equal = t.equals(v_i) || t.equals(v_j);

			if(equal) {
				inRange = !inRange;
			}
			//Only consider the notches between a and b and ignore a and b
			if(inRange && !equal) {
				double angle = SimplePolygon.orientedAngle(t, v_i, v_j);
				final int orientation = SimplePolygon.getTurnDirection(t, v_i, v_j);

				if(orientation != 0) {
					angle *= orientation;
				}
				if(angle > bestAngle) {
					bestAngle = angle;
					bestT = t;
				}
			}
		}
		if(bestT == null) {
			return new PVector[] {v_i, v_j};
		}

		return new PVector[] {v_i, bestT};
	}

	private PVector[] makeL_ik(final PVector v_i, final PVector v_k) {
		boolean inRange = false;
		PVector bestT = null;
		double bestAngle = -1;

		for(final PVector t : notches) {
			//Check whether the notches are between a and b
			boolean equal = t.equals(v_k) || t.equals(v_i);

			if(equal) {
				inRange = !inRange;
			}
			//Only consider the notches between a and b and ignore a and b
			if(inRange && !equal) {
				double angle = SimplePolygon.orientedAngle(v_k, v_i, t);
				final int orientation = SimplePolygon.getTurnDirection(v_k, v_i, t);

				if(orientation != 0) {
					angle *= orientation;
				}
				if(angle > bestAngle) {
					bestAngle = angle;
					bestT = t;
				}
			}
		}
		if(bestT == null) {
			return new PVector[] {v_i, v_k};
		}

		return new PVector[] {v_i, bestT};
	}

	public void detectPatterns() {
		buildData();
	}

	private void buildData() {
		if(notches.size() < 4) {
			return;
		}
		//For all triplet (v_i, v_j, v_k):
		for(final PVector i : notches) {
			for(final PVector j : notches) {
				for(final PVector k : notches) {
					if(i == j || i == k || j == k) {
						continue;
					}
					/*
					 * Build l_ik and r_ij
					 */
					final PVector[] l_ik = makeL_ik(i, k);
					final PVector[] r_ij = makeR_ij(i, j);
					final List<PVector> rangeV_i = polygon.getNotchRange(i, nP);

					if(rangeV_i.size() < 2) {
						System.out.println("No 2 extremities in R(v_i)");
						continue;
					}
					/*
					 * Build l'_ik and r'_ij
					 */
					final PVector[] l = {i, rangeV_i.get(0)};
					final PVector[] r = {i, rangeV_i.get(rangeV_i.size()-1)};

					//If l_ik -> l is a left turn, l' = l, otherwise l' = l_ik
					final PVector[] lPrime_ik = SimplePolygon.getTurnDirection(l_ik[1], l_ik[0], l[1]) > 0 ? l : l_ik;
					//If r -> r_ij is a left turn, r' = r, otherwise r' = r_ij
					final PVector[] rPrime_ij = SimplePolygon.getTurnDirection(r[1], r[0], r_ij[1]) > 0 ? r : r_ij;

					final int iIndex = notches.indexOf(i);
					final int jIndex = notches.indexOf(j);
					final int kIndex = notches.indexOf(k);

					//Add r'_ij to the data matrix
					addSegmentPrime(lPrime_ik);
					//segmentMatrix[iIndex][jIndex].add(rPrime_ij);
					//Add l'_ik to the data matrix
					addSegmentPrime(rPrime_ij);
					//segmentMatrix[iIndex][kIndex].add(lPrime_ik);

					//Get M_ij
					/*final PVector M_ij = lPrime_ik[0]; //= rPrime_ij[0] too.
					final boolean withinP = polygon.isInside(i, M_ij) && polygon.isInside(j, M_ij);
					System.out.println("i: " + i.toString() + ", j: " + j.toString() + ", M: " + M_ij.toString());
					System.out.println("within?: " + withinP);
					final var data = new PatternData(lPrime_ik, rPrime_ij, M_ij, withinP);

					if(!dataList.contains(data)) {
						dataList.add(data);
					}*/
				}
			}
		}
	}

	private void addSegmentPrime(final PVector[] segment) {
		boolean unique = true;

		for(final PVector[] vector : segmentPrimes.keySet()) {
			if(vector[0] == segment[0] && vector[1] == segment[1]) {
				unique = false;
				break;
			}
		}
		if(unique) {
			//Compute all intersections
			getIntersections(segment);
			segmentPrimes.put(segment, polygon.isInside(segment));
		}
	}

	private void getIntersections(final PVector[] segment) {
		for(final PVector[] other : segmentPrimes.keySet()) {
			SimplePolygon.getIntersection(segment, other)
					.ifPresent(intersection -> intersections.add(new IntersectionData(segment,
							other,
							intersection)
					));
		}
	}

	private void detectX3Pattern() {

	}

	public void displaySegments() {
		for(final var entrySet : segmentPrimes.entrySet()) {
			System.out.println(Arrays.toString(entrySet.getKey()) + ": " + entrySet.getValue());
		}
		System.out.println("Intersections:");
		for(final IntersectionData data : intersections) {
			System.out.println(data.toString());
		}
	}

	public static class SegmentData {

		private final List<PVector[]> data = new ArrayList<>();

		public void add(final PVector[] segmentPrime) {
			boolean unique = true;

			for(final PVector[] vector : data) {
				if(vector[0] == segmentPrime[0] && vector[1] == segmentPrime[1]) {
					unique = false;
					break;
				}
			}
			if(unique) {
				data.add(segmentPrime);
			}
		}

		public final String toString() {
			final var stringBuilder = new StringBuilder();

			for(final PVector[] vector : data) {
				stringBuilder.append(Arrays.toString(vector)).append(" ; ");
			}

			return stringBuilder.toString();
		}

	}

	public static class IntersectionData {

		private final PVector[] firstSegment;

		private final PVector[] secondSegment;

		private final PVector intersection;

		public IntersectionData(final PVector[] firstSegment, final PVector[] secondSegment, final PVector intersection) {
			this.firstSegment = firstSegment;
			this.secondSegment = secondSegment;
			this.intersection = intersection;
		}

		public final String toString() {
            return Arrays.toString(firstSegment) +
					" ; " +
					Arrays.toString(secondSegment) +
					" at " +
					intersection.toString();
		}

	}

	/*//TODO: Put it back to private if necessary
	public static class PatternData {

		private final PVector[] lPrime_ik;

		private final PVector[] rPrime_ij;

		private final PVector M_ij;

		private final boolean withinP;

		public PatternData(final PVector[] lPrime_ik, final PVector[] rPrime_ij, final PVector m_ij, final boolean withinP) {
			this.lPrime_ik = lPrime_ik;
			this.rPrime_ij = rPrime_ij;
			this.M_ij = m_ij;
			this.withinP = withinP;
		}

		public PVector[] getLPrime_ik() {
			return lPrime_ik;
		}

		public PVector[] getRPrime_ij() {
			return rPrime_ij;
		}

		public PVector getM_ij() {
			return M_ij;
		}

		public boolean isWithinP() {
			return withinP;
		}

		@Override
		public final int hashCode() {
			return Objects.hash(Arrays.hashCode(lPrime_ik), Arrays.hashCode(rPrime_ij), M_ij, withinP);
		}

		@Override
		public final boolean equals(final Object other) {
			if(other == null) {
				return false;
			}
			if(this == other) {
				return true;
			}
			if(!(other instanceof final PatternData otherPatternData)) {
				return false;
			}

			return Arrays.equals(otherPatternData.lPrime_ik, this.lPrime_ik)
					&& Arrays.equals(otherPatternData.rPrime_ij, this.rPrime_ij)
					&& Objects.equals(otherPatternData.M_ij, this.M_ij)
					&& otherPatternData.withinP == this.withinP;
		}

		public final String toString() {
			return "lPrime_ik: " + Arrays.toString(lPrime_ik) +
					", rPrime_ij: " + Arrays.toString(rPrime_ij) +
					", M_ij: " + M_ij.toString() +
					", withinP: " + withinP;
		}

	}*/

}
