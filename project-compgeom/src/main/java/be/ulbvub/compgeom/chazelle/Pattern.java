package be.ulbvub.compgeom.chazelle;

import processing.core.PVector;

public interface Pattern {

	class X2Pattern implements Pattern {

		private final PVector i;

		private final PVector j;

		public X2Pattern(final PVector i, final PVector j) {
			this.i = i;
			this.j = j;
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

	}

}
