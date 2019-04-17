/*
 * Copyright (C) 2019 Scientific Analysis Instruments Limited <contact@saiman.co.uk>
 *          ______         ___      ___________
 *       ,'========\     ,'===\    /========== \
 *      /== \___/== \  ,'==.== \   \__/== \___\/
 *     /==_/____\__\/,'==__|== |     /==  /
 *     \========`. ,'========= |    /==  /
 *   ___`-___)== ,'== \____|== |   /==  /
 *  /== \__.-==,'==  ,'    |== '__/==  /_
 *  \======== /==  ,'      |== ========= \
 *   \_____\.-\__\/        \__\\________\/
 *
 * This file is part of uk.co.saiman.chemistry.
 *
 * uk.co.saiman.chemistry is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.chemistry is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.chemistry;

import static java.util.Collections.emptyList;
import static uk.co.saiman.chemistry.ChemicalComposition.nothing;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

/*
 * TODO think of how to store composition and also *optionally* structural information and other meta-data
 */
public class Chemical {
	private static final Chemical UNKNOWN = new Chemical();

	private final List<String> names;
	private final ChemicalComposition composition;

	private Chemical() {
		names = emptyList();
		composition = nothing();
	}

	private Chemical(List<String> names, ChemicalComposition composition) {
		this.names = names;
		this.composition = composition;
	}

	/**
	 * @return a chemical with an empty composition
	 */
	public static Chemical unknownChemical() {
		return UNKNOWN;
	}

	/**
	 * @return the primary name of this chemical, of which there may be
	 *         {@link #names() alternatives}
	 */
	public Optional<String> name() {
		return names.isEmpty() ? Optional.empty() : Optional.of(names.get(0));
	}

	/**
	 * @return the name alternatives of this chemical, starting with its
	 *         {@link #name() primary} name
	 */
	public Stream<String> names() {
		return names.stream();
	}

	public Chemical withNames(String... names) {
		return withNames(Arrays.asList(names));
	}

	public Chemical withNames(List<String> names) {
		return new Chemical(names, composition);
	}

	public ChemicalComposition composition() {
		return composition;
	}

	public Chemical withComposition(ChemicalComposition composition) {
		return new Chemical(names, composition);
	}

	public Chemical withComposition(Function<ChemicalComposition, ChemicalComposition> composition) {
		return withComposition(composition.apply(this.composition));
	}
}
