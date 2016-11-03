package uk.co.saiman.data;

import javax.measure.Unit;

import uk.co.strangeskies.text.properties.Localized;
import uk.co.strangeskies.text.properties.Properties;

public interface DataProperties extends Properties<DataProperties> {
	Localized<String> incompatibleDomainUnits(Unit<?> first, Unit<?> second);
	
	Localized<String> noChartData();
}
