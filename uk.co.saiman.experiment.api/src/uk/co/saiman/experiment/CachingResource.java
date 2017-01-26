package uk.co.saiman.experiment;

import java.lang.ref.SoftReference;
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

	public void save() {
		if (requiresSave()) {
			save.accept(data);

			data = null;
		}
	}

	public boolean requiresSave() {
		return data != null;
	}

	public void invalidate() {
		this.data = dataReference.get();
	}

	public void setData(T data) {
		this.dataReference = new SoftReference<>(data);
		this.data = data;
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
}
