package uk.co.saiman.comms.copley;

import static java.util.Arrays.stream;
import static java.util.stream.Stream.concat;

/**
 * The field axes describe the axes internal to the motor relative to the stator
 * and rotor. Each motor has its own set of field axis.
 * 
 * @author Elias N Vasylenko
 */
public interface FieldAxis {
	public enum Stator implements FieldAxis {
		X(0), Y(1);

		private final int index;

		private Stator(int index) {
			this.index = index;
		}

		@Override
		public int getIndex() {
			return index;
		}
	}

	public enum Rotor implements FieldAxis {
		D(2), Q(3);

		private final int index;

		private Rotor(int index) {
			this.index = index;
		}

		@Override
		public int getIndex() {
			return index;
		}
	}

	public static FieldAxis[] values() {
		return concat(stream(Stator.values()), stream(Rotor.values())).toArray(FieldAxis[]::new);
	}

	public static FieldAxis valueOf(String name) {
		try {
			return Stator.valueOf(name);
		} catch (IllegalArgumentException e) {
			return Rotor.valueOf(name);
		}
	}

	int getIndex();
}
