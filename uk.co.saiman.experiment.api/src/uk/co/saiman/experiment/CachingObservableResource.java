package uk.co.saiman.experiment;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import uk.co.strangeskies.utilities.Observable;

public class CachingObservableResource<T> extends CachingResource<T> {
	private final Function<? super T, ? extends Observable<?>> observable;

	public CachingObservableResource(
			Supplier<T> load,
			Consumer<T> save,
			Function<? super T, ? extends Observable<?>> observable) {
		super(load, save);

		this.observable = observable;
	}

	private void addObserver(T data) {
		observable.apply(data).addTerminatingObserver(this, (Object o) -> makeDirty());
	}

	private void removeObserver(T data) {
		observable.apply(data).removeTerminatingObserver(this);
	}

	@Override
	public boolean save() {
		boolean saved = super.save();

		if (saved) {
			addObserver(getData());
		}

		return saved;
	}

	/**
	 * Mark the data as inconsistent with the previously saved state.
	 */
	@Override
	public boolean makeDirty() {
		boolean madeDirty = super.makeDirty();

		if (madeDirty) {
			removeObserver(getData());
		}

		return madeDirty;
	}

	@Override
	public boolean setData(T data) {
		Optional<T> oldData = tryGetData();

		boolean dataSet = super.setData(data);

		if (dataSet) {
			oldData.ifPresent(this::removeObserver);
			addObserver(getData());
		}

		return dataSet;
	}

	/**
	 * Get the data, loading from the input channel if necessary.
	 * 
	 * @return the data
	 */
	@Override
	public T getData() {
		T data = super.getData();

		addObserver(data);

		return data;
	}
}
