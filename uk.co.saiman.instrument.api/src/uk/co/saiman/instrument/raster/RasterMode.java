package uk.co.saiman.instrument.raster;

import java.util.Iterator;

import uk.co.strangeskies.mathematics.geometry.matrix.vector.Vector2;
import uk.co.strangeskies.mathematics.values.IntValue;

public interface RasterMode {
	public static final RasterMode SNAKE = null;
	public static final RasterMode SPIRAL = null;

	String getName();

	Iterator<Vector2<IntValue>> getPositionIterator(int width, int height);

	Iterator<Vector2<IntValue>> getReversePositionIterator(int width, int height);
}
