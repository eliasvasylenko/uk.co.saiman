/*
 * Copyright (C) 2017 Scientific Analysis Instruments Limited <contact@saiman.co.uk>
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
 * This file is part of uk.co.saiman.text.
 *
 * uk.co.saiman.text is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.text is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.text.parsing;

import java.text.ParsePosition;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.function.Function;

public class DateTimeParser<T> implements AbstractParser<T> {
	private final DateTimeFormatter format;
	private final Function<TemporalAccessor, T> accessorFunction;

	protected DateTimeParser(DateTimeFormatter format, Function<TemporalAccessor, T> accessorFunction) {
		this.format = format;
		this.accessorFunction = accessorFunction;
	}

	public static DateTimeParser<LocalDate> overIsoLocalDate() {
		return over(DateTimeFormatter.ISO_LOCAL_DATE, LocalDate::from);
	}

	public static DateTimeParser<LocalDate> over(DateTimeFormatter format) {
		return over(format, LocalDate::from);
	}

	public static <T> DateTimeParser<T> over(DateTimeFormatter format, Function<TemporalAccessor, T> accessorFunction) {
		return new DateTimeParser<>(format, accessorFunction);
	}

	@Override
	public ParseResult<T> parseSubstringImpl(ParseState currentState) {
		ParsePosition position = new ParsePosition(currentState.fromIndex());

		try {
			TemporalAccessor accessor = format.parse(currentState.literal(), position);

			return currentState.parseTo(position.getIndex(), s -> accessorFunction.apply(accessor));
		} catch (Exception e) {
			throw currentState.addException("Cannot parse temporal accessor",
					position.getErrorIndex() > 0 ? position.getErrorIndex() : position.getIndex(), e).getException();
		}
	}
}
