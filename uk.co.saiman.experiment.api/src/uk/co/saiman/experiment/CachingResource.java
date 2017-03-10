package uk.co.saiman.experiment;

import static java.util.Objects.requireNonNull;

import java.lang.ref.SoftReference;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class CachingResource<T> {
	private SoftReference<T> dataReference;
	private T data;

	private final Supplier<T> load;
	private final Consumer<T> save;

	public CachingResource(Supplier<T> load, Consumer<T> save) {
		this.load = load;
		this.save = save;
	}

	public boolean save() {
		boolean requiresSave = isDirty();

		if (requiresSave) {
			save.accept(data);

			this.data = null;
		}

		return requiresSave;
	}

	/**
	 * @return true if the resource has been changed since it was last saved,
	 *         false otherwise
	 */
	public boolean isDirty() {
		return data != null;
	}

	/**
	 * Mark the data as inconsistent with the previously saved state.
	 */
	public boolean makeDirty() {
		boolean requiresDirty = !isDirty();

		T data = dataReference.get();
		requireNonNull(data);

		if (requiresDirty) {
			this.data = data;
		}

		return requiresDirty;
	}

	public boolean setData(T data) {
		requireNonNull(data);

		boolean requiresSet = !data.equals(this.data);

		if (requiresSet) {
			this.data = data;
			this.dataReference = new SoftReference<>(data);
		}

		return requiresSet;
	}

	/**
	 * Get the data, loading from the input channel if necessary.
	 * 
	 * @return the data
	 */
	public T getData() {
		T data = dataReference.get();

		if (data == null) {
			data = load.get();

			dataReference = new SoftReference<>(data);
		}

		return data;
	}

	public Optional<T> tryGetData() {
		return Optional.of(dataReference.get());
	}
}
