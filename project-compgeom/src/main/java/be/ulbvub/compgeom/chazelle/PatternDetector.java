package be.ulbvub.compgeom.chazelle;

import be.ulbvub.compgeom.SimplePolygon;
import processing.core.PVector;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

public class PatternDetector {

	private final SimplePolygon polygon;

	private final List<PVector> notches;

	private final int nP;

	//Link each segment to a boolean value set to true if the entirety of this segment is inside P
	//private final Map<PVector[], Boolean> segmentPrimes = new HashMap<>();
	private final ArrayList<SegmentData> leftSegments = new ArrayList<>();

	private final ArrayList<SegmentData> rightSegments = new ArrayList<>();

	private final List<IntersectionData> intersections = new ArrayList<>();

	private final Set<Pattern> xkPatterns = new HashSet<>();

	public PatternDetector(final SimplePolygon polygon) {
		this.polygon = polygon;
		this.notches = new NotchFinder(polygon).findNotches().getNotches();
		this.nP = notches.size();
	}

	/**
	 * Compute the right segment from a point to another. from is typically i and to, j.
	 *
	 * @param from the vertex from which the segment is computed
	 * @param to the vertex to which the segment is computed
	 *
	 * @return the right segment (r_ij) or an empty optional if no notch exist between i and j
	 */
	private Optional<PVector[]> makeRight(final PVector from, final PVector to) {
		System.out.println("Building r': " + from.toString() + "; " + to.toString());
		boolean inRange = false;
		PVector bestT = null;
		double bestAngle = Double.NEGATIVE_INFINITY;

		for(final PVector t : notches) {
			//Check whether the notches are between a and b
			boolean equal = t.equals(from) || t.equals(to);

			if(equal) {
				inRange = !inRange;
			}
			//Only consider the notches between a and b and ignore a and b
			if(inRange && !equal) {
				double angle = SimplePolygon.orientedAngle(t, from, to);
				final int orientation = SimplePolygon.getTurnDirection(t, from, to);
				System.out.println("Testing angle " + t.toString() + "; " + from.toString() + "; " + to.toString());

				if(orientation != 0) {
					angle *= orientation;
				}
				System.out.println("Angle: " + angle);
				if(angle > bestAngle) {
					System.out.println("Better");
					bestAngle = angle;
					bestT = t;
				}
			}
		}
		if(bestT == null) {
			System.out.println("No notches between this pair of points");
			return Optional.of(new PVector[]{from, to});
		}

		System.out.println("Returning (" + from.toString() + ", " + bestT.toString() + ")");
		return Optional.of(new PVector[] {from, bestT});
	}

	/**
	 * Compute the left segment from a point to another. from is typically i and to, k.
	 *
	 * @param from the vertex from which the segment is computed
	 * @param to the vertex to which the segment is computed
	 *
	 * @return the left segment (l_ik)
	 */
	private Optional<PVector[]> makeLeft(final PVector from, final PVector to) {
		System.out.println("Building l': " + from.toString() + "; " + to.toString());
		boolean inRange = false;
		PVector bestT = null;
		double bestAngle = Double.NEGATIVE_INFINITY;

		for(final PVector t : notches) {
			//Check whether the notches are between a and b
			boolean equal = t.equals(to) || t.equals(from);

			if(equal) {
				inRange = !inRange;
			}
			//Only consider the notches between a and b and ignore a and b
			if(inRange && !equal) {
				double angle = SimplePolygon.orientedAngle(to, from, t);
				final int orientation = SimplePolygon.getTurnDirection(to, from, t);
				System.out.println("Testing angle " + to.toString() + "; " + from.toString() + "; " + t.toString());

				if(orientation != 0) {
					angle *= orientation;
				}
				System.out.println("Angle: " + angle);
				if(angle > bestAngle) {
					System.out.println("Better");
					bestAngle = angle;
					bestT = t;
				}
			}
		}
		if(bestT == null) {
			System.out.println("No notches between this pair of points");
			return Optional.of(new PVector[]{from, to});
		}

		System.out.println("Returning (" + from.toString() + ", " + bestT.toString() + ")");
		return Optional.of(new PVector[] {from, bestT});
	}

	public List<SimplePolygon> partition() {
		final List<SimplePolygon> subPolygons = new ArrayList<>();
		//Add the original polygon in the list
		subPolygons.add(polygon.clone());

		final List<Pattern.X3Pattern> x3Patterns = xkPatterns.stream()
				.filter(pattern -> pattern instanceof Pattern.X3Pattern)
				.map(pattern -> (Pattern.X3Pattern) pattern)
				.toList();
		final List<Pattern.X2Pattern> x2Patterns = xkPatterns.stream()
				.filter(pattern -> pattern instanceof Pattern.X2Pattern)
				.map(pattern -> (Pattern.X2Pattern) pattern)
				.toList();

		//Start with x3 patterns
		for(final Pattern.X3Pattern x3Pattern : x3Patterns) {
			SimplePolygon target = null;

			//Only attempt to split sub polygons that can actually be split
			//Every time a (sub) polygon is split, this split will influence whether the other sub polygons can be split
			for(final var subPolygon : subPolygons) {
				if(x3Pattern.canSplit(subPolygon)) {
					target = subPolygon;
					break;
				}
			}
			if(target != null) {
				System.out.println("Splitting polygon " + target.points() + "using an x3");
				final List<SimplePolygon> splitPolygons = x3Pattern.split(target);
				System.out.println("Split: ");
				splitPolygons.forEach(p -> System.out.println("p: " + p.points()));

				int remainingNotchesInSplit = 0;
				final int targetNotchesAmount = new NotchFinder(target).findNotches().getNotches().size();

				for(final SimplePolygon split : splitPolygons) {
					//Sum the notches of the new split
					remainingNotchesInSplit += new NotchFinder(split).findNotches().getNotches().size();
				}
				System.out.println("Notches in splits: " + remainingNotchesInSplit);
				System.out.println("Notches in original sub polygon: " + targetNotchesAmount);

				//Only accept new splits if they effectively improve the solution
				if(remainingNotchesInSplit < targetNotchesAmount) {
					System.out.println("Improving solution");
					System.out.println("Size: " + subPolygons.size());
					//Remove the polygon that has been split and replace it with its split components
					subPolygons.remove(target);
					System.out.println("Removing target: " + subPolygons.size());
					//Update the solution
					subPolygons.addAll(splitPolygons);
					System.out.println("Adding split: " + subPolygons.size());
				}
			}
		}
		//End with x2 patterns
		for(final Pattern.X2Pattern x2Pattern : x2Patterns) {
			SimplePolygon target = null;

			for(final var subPolygon : subPolygons) {
				if(x2Pattern.canSplit(subPolygon)) {
					target = subPolygon;
					break;
				}
			}
			if(target != null) {
				System.out.println("Splitting polygon " + target.points() + "using an x3");
				final List<SimplePolygon> splitPolygons = x2Pattern.split(target);
				System.out.println("Split: ");
				splitPolygons.forEach(p -> System.out.println("p: " + p.points()));
				int remainingNotchesInSplit = 0;
				final int targetNotchesAmount = new NotchFinder(target).findNotches().getNotches().size();

				for(final SimplePolygon split : splitPolygons) {
					//Sum the notches of the new split
					remainingNotchesInSplit += new NotchFinder(split).findNotches().getNotches().size();
				}
				System.out.println("Notches in splits: " + remainingNotchesInSplit);
				System.out.println("Notches in original sub polygon: " + targetNotchesAmount);

				//Only accept new splits if they effectively improve the solution
				if(remainingNotchesInSplit < targetNotchesAmount) {
					System.out.println("Improving solution");
					System.out.println("Size: " + subPolygons.size());
					//Remove the polygon that has been split and replace it with its split components
					subPolygons.remove(target);
					System.out.println("Removing target: " + subPolygons.size());
					//Update the solution
					subPolygons.addAll(splitPolygons);
					System.out.println("Adding split: " + subPolygons.size());
				}
			}
		}

		return subPolygons;
	}

	public void detectPatterns() {
		buildData();
		detectX2Pattern();
		detectX3Pattern();
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
					System.out.println("--------------------------------");
					System.out.println("Working on triplet (i:" + i.toString() + ", j:" + j.toString() + ", k:" + k.toString() + ")");
					final List<PVector> rangeV_i = polygon.getNotchRange(i, nP);
					System.out.println("Notch range of i: " + rangeV_i.toString());
					final List<PVector> rangeV_j = polygon.getNotchRange(j, nP);
					final List<PVector> rangeV_k = polygon.getNotchRange(k, nP);
					final AtomicReference<PVector[]> finalL_ik = new AtomicReference<>();
					final AtomicReference<PVector[]> finalR_ij = new AtomicReference<>();
					final AtomicReference<PVector[]> finalL_ji = new AtomicReference<>();
					final AtomicReference<PVector[]> finalR_jk = new AtomicReference<>();
					final AtomicReference<PVector[]> finalL_kj = new AtomicReference<>();
					final AtomicReference<PVector[]> finalR_ki = new AtomicReference<>();

					//TODO: Refactor this repetitive code
					/*
					 * Build v_i data
					 */
					if(!rangeV_i.isEmpty()) {
						System.out.println("Making l_ik");
						makeLeft(i, k).ifPresent(
								l_ik -> {
									final PVector[] l = {i, rangeV_i.get(0)};
									System.out.println("l: " + Arrays.toString(l));
									final PVector[] lPrime_ik = SimplePolygon.getTurnDirection(l_ik[1], l_ik[0], l[1]) > 0 ? l : l_ik;
									System.out.println("final l': " + Arrays.toString(lPrime_ik));
									finalL_ik.set(lPrime_ik);
									final var data = new SegmentData(i, k, lPrime_ik);

									if(!leftSegments.contains(data)) {
										leftSegments.add(new SegmentData(i, k, lPrime_ik));
										System.out.println("l_ik from " + i.toString() + " to " + k.toString() + ": " + Arrays.toString(lPrime_ik));
									}
								}
						);
						System.out.println("Making r_ij");
						makeRight(i, j).ifPresent(
								r_ij -> {
									final PVector[] r = {i, rangeV_i.get(rangeV_i.size()-1)};
									final PVector[] rPrime_ij = SimplePolygon.getTurnDirection(r[1], r[0], r_ij[1]) > 0 ? r : r_ij;
									finalR_ij.set(rPrime_ij);
									final var data = new SegmentData(i, j, rPrime_ij);

									if(!rightSegments.contains(data)) {
										rightSegments.add(new SegmentData(i, j, rPrime_ij));
										System.out.println("r_ij from " + i.toString() + " to " + j.toString() + ": " + Arrays.toString(rPrime_ij));
									}
								}
						);
					}
					/*
					 * Build v_j data
					 */
					if(!rangeV_j.isEmpty()) {
						System.out.println("Making l_ji");
						makeLeft(j, i).ifPresent(
								l_ji -> {
									final PVector[] l = {j, rangeV_j.get(0)};
									final PVector[] lPrime_ji = SimplePolygon.getTurnDirection(l_ji[1], l_ji[0], l[1]) > 0 ? l : l_ji;
									finalL_ji.set(lPrime_ji);
									final var data = new SegmentData(j, i, lPrime_ji);

									if(!leftSegments.contains(data)) {
										leftSegments.add(data);
										System.out.println("l_ji from " + j.toString() + " to " + i.toString() + ": " + Arrays.toString(lPrime_ji));
									}
								}
						);
						System.out.println("Making r_jk");
						makeRight(j, k).ifPresent(
								r_jk -> {
									final PVector[] r = {j, rangeV_j.get(rangeV_j.size()-1)};
									final PVector[] rPrime_jk = SimplePolygon.getTurnDirection(r[1], r[0], r_jk[1]) > 0 ? r : r_jk;
									finalR_jk.set(rPrime_jk);
									final var data = new SegmentData(j, k, rPrime_jk);

									if(!rightSegments.contains(data)) {
										rightSegments.add(new SegmentData(j, k, rPrime_jk));
										System.out.println("r_jk from " + j.toString() + " to " + k.toString() + ": " + Arrays.toString(rPrime_jk));
									}
								}
						);
					}
					/*
					 * Build v_k data
					 */
					if(!rangeV_k.isEmpty()) {
						System.out.println("Making l_kj");
						makeLeft(k, j).ifPresent(
								l_kj -> {
									final PVector[] l = {k, rangeV_k.get(0)};
									final PVector[] lPrime_kj = SimplePolygon.getTurnDirection(l_kj[1], l_kj[0], l[1]) > 0 ? l : l_kj;
									finalL_kj.set(lPrime_kj);
									final var data = new SegmentData(k, j, lPrime_kj);

									if(!leftSegments.contains(data)) {
										leftSegments.add(new SegmentData(k, j, lPrime_kj));
										System.out.println("l_kj from " + k.toString() + " to " + j.toString() + ": " + Arrays.toString(lPrime_kj));
									}
								}
						);
						System.out.println("Making r_ki");
						makeRight(k, i).ifPresent(
								r_ki -> {
									final PVector[] r = {k, rangeV_k.get(rangeV_k.size()-1)};
									final PVector[] rPrime_ki = SimplePolygon.getTurnDirection(r[1], r[0], r_ki[1]) > 0 ? r : r_ki;
									finalR_ki.set(rPrime_ki);
									final var data = new SegmentData(k, i, rPrime_ki);

									if(!rightSegments.contains(data)) {
										rightSegments.add(new SegmentData(k, i, rPrime_ki));
										System.out.println("r_ki from " + k.toString() + " to " + i.toString() + ": " + Arrays.toString(rPrime_ki));
									}
								}
						);
					}
					//Check for intersections
					//Check for M_ij
					if(finalR_ij.get() != null && finalL_ji.get() != null) {
						SimplePolygon.getIntersection(finalR_ij.get(), finalL_ji.get())
								.ifPresent(intersection -> {
									final boolean flag = polygon.isInside(i, intersection) && polygon.isInside(intersection, j);

									intersections.add(new IntersectionData(i, j, intersection, flag));
									System.out.println("M_ij: " + intersections.get(intersections.size() - 1).toString());
								});
					}
					//Check for M_jk
					if(finalR_jk.get() != null && finalL_kj.get() != null) {
						SimplePolygon.getIntersection(finalR_jk.get(), finalL_kj.get())
								.ifPresent(intersection -> {
									final boolean flag = polygon.isInside(j, intersection) && polygon.isInside(intersection, k);

									intersections.add(new IntersectionData(j, k, intersection, flag));
									System.out.println("M_jk: " + intersections.get(intersections.size() - 1).toString());
								});
					}
					//Check for M_ki
					if(finalR_ki.get() != null && finalL_ik.get() != null) {
						SimplePolygon.getIntersection(finalR_ki.get(), finalL_ik.get())
								.ifPresent(intersection -> {
									final boolean flag = polygon.isInside(k, intersection) && polygon.isInside(intersection, i);

									intersections.add(new IntersectionData(k, i, intersection, flag));
									System.out.println("M_ki: " + intersections.get(intersections.size() - 1).toString());
								});
					}
				}
			}
		}
	}

	private void detectX2Pattern() {
		for(final PVector i : notches) {
			for(final PVector j : notches) {
				final Optional<SegmentData> l_ij = leftSegments.stream()
						.filter(data -> data.from.equals(i) && data.to.equals(j))
						.findFirst();
				final Optional<SegmentData> l_ji = leftSegments.stream()
						.filter(data -> data.from.equals(j) && data.to.equals(i))
						.findFirst();
				final Optional<SegmentData> r_ij = rightSegments.stream()
						.filter(data -> data.from.equals(i) && data.to.equals(j))
						.findFirst();
				final Optional<SegmentData> r_ji = rightSegments.stream()
						.filter(data -> data.from.equals(j) && data.to.equals(i))
						.findFirst();

				if(l_ij.isEmpty() || l_ji.isEmpty() || r_ij.isEmpty() || r_ji.isEmpty()) {
					continue;
				}
				if(SimplePolygon.getTurnDirection(l_ij.get().segment[1], i, r_ij.get().segment[1]) > 0
					|| SimplePolygon.getTurnDirection(l_ji.get().segment[1], j, r_ji.get().segment[1]) > 0) {
					continue;
				}
				final var x2Pattern = new Pattern.X2Pattern(i, j);
				boolean unique = true;

				for(final Pattern pattern : xkPatterns) {
					if(pattern.isSimilar(x2Pattern)) {
						unique = false;
					}
				}
				if(unique) {
					System.out.println("Adding " + i + j);
					xkPatterns.add(x2Pattern);
				}
			}
		}
	}

	private void detectX3Pattern() {
		for(final PVector i : notches) {
			for(final PVector j : notches) {
				for(final PVector k : notches) {
					final Optional<SegmentData> lPrime_ik = leftSegments.stream()
							.filter(data -> data.from.equals(i) && data.to.equals(k))
							.findFirst();
					final Optional<SegmentData> rPrime_ij = rightSegments.stream()
							.filter(data -> data.from.equals(i) && data.to.equals(j))
							.findFirst();
					final Optional<SegmentData> lPrime_ji = leftSegments.stream()
							.filter(data -> data.from.equals(j) && data.to.equals(i))
							.findFirst();
					final Optional<SegmentData> rPrime_jk = rightSegments.stream()
							.filter(data -> data.from.equals(j) && data.to.equals(k))
							.findFirst();
					final Optional<SegmentData> lPrime_kj = leftSegments.stream()
							.filter(data -> data.from.equals(k) && data.to.equals(j))
							.findFirst();
					final Optional<SegmentData> rPrime_ki = rightSegments.stream()
							.filter(data -> data.from.equals(k) && data.to.equals(i))
							.findFirst();

					if(lPrime_ik.isEmpty() || rPrime_ij.isEmpty() || lPrime_ji.isEmpty() || rPrime_jk.isEmpty()
							|| lPrime_kj.isEmpty() || rPrime_ki.isEmpty()) {
						continue;
					}
					if(SimplePolygon.getTurnDirection(lPrime_ik.get().segment, rPrime_ij.get().getSegment()) > 0
						|| SimplePolygon.getTurnDirection(lPrime_ji.get().segment, rPrime_jk.get().getSegment()) > 0
						|| SimplePolygon.getTurnDirection(lPrime_kj.get().segment, rPrime_ki.get().getSegment()) > 0) {
						continue;
					}
					final Optional<IntersectionData> M_ij = intersections.stream()
							.filter(data -> data.from.equals(i) && data.to.equals(j))
							.findFirst();
					final Optional<IntersectionData> M_jk = intersections.stream()
							.filter(data -> data.from.equals(j) && data.to.equals(k))
							.findFirst();
					final Optional<IntersectionData> M_ki = intersections.stream()
							.filter(data -> data.from.equals(k) && data.to.equals(i))
							.findFirst();

					if(M_ij.isEmpty() || M_jk.isEmpty() || M_ki.isEmpty()) {
						continue;
					}
					final var x3Pattern = new Pattern.X3Pattern(i, j, k);
					boolean unique = true;

					for(final Pattern pattern : xkPatterns) {
						if(pattern.isSimilar(x3Pattern)) {
							unique = false;
						}
					}
					if(unique) {
						System.out.println("Adding " + i + j + k);
						xkPatterns.add(x3Pattern);
					}
				}
			}
		}
	}

	public void displaySegments() {
		System.out.println("\n-===[ l' ]===-\n");
		for(final var segmentData : leftSegments) {
			System.out.println("l'_(" + segmentData.from + ")(" + segmentData.to + "): " + Arrays.toString(segmentData.segment));
		}
		System.out.println("\n-===[r']===-\n");
		for(final var segmentData : rightSegments) {
			System.out.println("r'_(" + segmentData.from + ")(" + segmentData.to + "): " + Arrays.toString(segmentData.segment));
		}
		System.out.println("\nINTERSECTIONS\n");
		for(final IntersectionData intersectionData : intersections) {
			System.out.println(intersectionData.toString());
		}
	}

	private static class IntersectionData {

		private final PVector from;

		private final PVector to;

		private final PVector intersection;

		private final boolean flag;

		public IntersectionData(final PVector from, final PVector to, final PVector intersection, final boolean flag) {
			this.from = from;
			this.to = to;
			this.intersection = intersection;
			this.flag = flag;
		}

		public final String toString() {
            return from.toString() +
					" ; " +
					to.toString() +
					" at " +
					intersection.toString() +
					" flag: " + flag;
		}

	}

	private static class SegmentData {

		private final PVector from;

		private final PVector to;

		private final PVector[] segment;

		public SegmentData(PVector from, PVector to, PVector[] segment) {
			this.from = from;
			this.to = to;
			this.segment = segment;
		}

		@Override
		public final int hashCode() {
			return Objects.hash(from, to, Arrays.hashCode(segment));
		}

		@Override
		public final boolean equals(final Object other) {
			if(other == null) {
				return false;
			}
			if(!(other instanceof SegmentData otherData)) {
				return false;
			}

			return Objects.equals(otherData.from, this.from)
					&& Objects.equals(otherData.to, this.to)
					&& Arrays.equals(otherData.segment, this.segment);
		}

		public PVector[] getSegment() {
			return segment;
		}
	}

}
