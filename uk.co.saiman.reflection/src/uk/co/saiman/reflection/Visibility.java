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
 * This file is part of uk.co.saiman.reflection.
 *
 * uk.co.saiman.reflection is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.reflection is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.reflection;

import static java.util.Arrays.stream;
import static java.util.Optional.empty;
import static java.util.Optional.of;

import java.lang.reflect.Modifier;
import java.util.Optional;

/**
 * The visibility of a declaration.
 * 
 * @author Elias N Vasylenko
 */
public enum Visibility {
	/**
	 * Public visibility.
	 */
	PUBLIC,

	/**
	 * Protected visibility.
	 */
	PROTECTED,

	/**
	 * Package-private, or default, visibility.
	 */
	PACKAGE_PRIVATE,

	/**
	 * Private visibility.
	 */
	PRIVATE;

	/**
	 * @param visibility
	 *          the minimum visibility level
	 * @return true if the visibility is at least as much as the one given
	 */
	public boolean visibilityIsAtLeast(Visibility visibility) {
		return ordinal() <= visibility.ordinal();
	}

	/**
	 * @param visibility
	 *          the maximum visibility level
	 * @return true if the visibility is at most as much as the one given
	 */
	public boolean visibilityIsAtMost(Visibility visibility) {
		return ordinal() >= visibility.ordinal();
	}

	public Optional<String> getKeyword() {
		switch (this) {
		case PUBLIC:
			return of("public");
		case PROTECTED:
			return of("protected");
		case PACKAGE_PRIVATE:
			return empty();
		case PRIVATE:
			return of("private");
		default:
			throw new AssertionError();
		}
	}

	public static Visibility forModifiers(int modifiers) {
		if (Modifier.isPublic(modifiers)) {
			return Visibility.PUBLIC;
		} else if (Modifier.isProtected(modifiers)) {
			return Visibility.PROTECTED;
		} else if (Modifier.isPrivate(modifiers)) {
			return Visibility.PRIVATE;
		} else {
			return Visibility.PACKAGE_PRIVATE;
		}
	}

	public int getModifier() {
		switch (this) {
		case PUBLIC:
			return Modifier.PUBLIC;
		case PROTECTED:
			return Modifier.PROTECTED;
		case PACKAGE_PRIVATE:
			return 0;
		case PRIVATE:
			return Modifier.PRIVATE;
		default:
			throw new AssertionError();
		}
	}

	public int withModifiers(int modifiers) {
		int present = getModifier();
		int missing = stream(values()).map(Visibility::getModifier).reduce((a, b) -> a | b).get();

		return modifiers & ~missing | present;
	}
}
