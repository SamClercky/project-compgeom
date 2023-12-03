package be.ulbvub.compgeom.chazelle;

import processing.core.PVector;

public interface Pattern {

	boolean isSimilar(Pattern pattern);

	class X2Pattern implements Pattern {

		private final PVector i;

		private final PVector j;

		public X2Pattern(final PVector i, final PVector j) {
			this.i = i;
			this.j = j;
		}

		@Override
		public boolean isSimilar(final Pattern pattern) {
			if(!(pattern instanceof X2Pattern x2Pattern)) {
				return false;
			}

			return (this.i.equals(x2Pattern.i) || this.i.equals(x2Pattern.j))
					&& (this.j.equals(x2Pattern.i) || this.j.equals(x2Pattern.j))
					&& !x2Pattern.i.equals(x2Pattern.j);
		}

	}

	class X3Pattern implements Pattern {

		private final PVector i;

		private final PVector j;

		private final PVector k;

		public X3Pattern(final PVector i, final PVector j, final PVector k) {
			this.i = i;
			this.j = j;
			this.k = k;
		}

		@Override
		public boolean isSimilar(final Pattern pattern) {
			if(!(pattern instanceof X3Pattern x3Pattern)) {
				return false;
			}

			return (this.i.equals(x3Pattern.i) || this.i.equals(x3Pattern.j) || this.i.equals(x3Pattern.k))
					&& (this.j.equals(x3Pattern.i) || this.j.equals(x3Pattern.j) || this.j.equals(x3Pattern.k))
					&& (this.k.equals(x3Pattern.i) || this.k.equals(x3Pattern.j) || this.k.equals(x3Pattern.k))
					&& !(x3Pattern.i.equals(x3Pattern.j) || x3Pattern.i.equals(x3Pattern.k) || x3Pattern.j.equals(x3Pattern.k));
		}

	}

	class X4Pattern implements Pattern {

		private final PVector i;

		private final PVector j;

		private final PVector k;

		private final PVector l;

		public X4Pattern(final PVector i, final PVector j, final PVector k, final PVector l) {
			this.i = i;
			this.j = j;
			this.k = k;
			this.l = l;
		}

		@Override
		public boolean isSimilar(final Pattern pattern) {
			return false;
		}

	}

}
