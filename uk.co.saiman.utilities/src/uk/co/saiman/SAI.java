package uk.co.saiman;

import java.time.LocalDate;

import uk.co.strangeskies.modabi.Namespace;

public class SAI {
	public static final Namespace NAMESPACE = new Namespace(SAI.class.getPackage(), LocalDate.of(2015, 10, 8));
}
