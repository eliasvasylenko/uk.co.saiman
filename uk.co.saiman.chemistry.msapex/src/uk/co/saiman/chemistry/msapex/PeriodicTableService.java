package uk.co.saiman.chemistry.msapex;

import java.util.stream.Stream;

import uk.co.saiman.chemistry.PeriodicTable;
import uk.co.strangeskies.utilities.ObservableProperty;

public interface PeriodicTableService {
	ObservableProperty<PeriodicTable, PeriodicTable> periodicTable();

	Stream<PeriodicTable> periodicTables();
}
