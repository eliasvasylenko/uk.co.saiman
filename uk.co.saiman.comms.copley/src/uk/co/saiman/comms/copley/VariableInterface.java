package uk.co.saiman.comms.copley;

public interface VariableInterface<T, U> {
	U getForAxis(T axis);

	VariableInterface<T, U> setForAxis(T axis, U value);

	VariableInterface<T, U> getActive();

	VariableInterface<T, U> setActive();

	VariableInterface<T, U> getDefault();

	VariableInterface<T, U> setDefault();

	VariableInterface<T, U> loadDefault();

	VariableInterface<T, U> saveDefault();
}
