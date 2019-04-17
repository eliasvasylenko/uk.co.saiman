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
 * This file is part of uk.co.saiman.webmodules.semver.
 *
 * uk.co.saiman.webmodules.semver is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.webmodules.semver is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
/**
 * An implementation of the Semantic Versioning 2.0.0 specification at
 * https://semver.org/ for interoperability with OSGi systems.
 * 
 * <p>
 * Semver versions can be consistently converted to OSGi versions while
 * retaining exact ordering semantics. Major, minor, and micro components are
 * mapped exactly, and an OSGi qualifier string is generated based on the
 * pre-release component of the semver.
 * 
 * <p>
 * Any semver which is supported can be round-tripped to OSGi and back again
 * while preserving ordering.
 * 
 * <p>
 * Translation quirks:
 * 
 * <ul>
 * 
 * <li>OSGi versions do not support negative qualifiers, so naive translation
 * from semver would order pre-releases higher than releases, therefore the
 * qualifier is prepended with REL or PRE for release or pre-release versions
 * respectively</li>
 * 
 * <li>OSGi qualifiers are always compared as strings, so in order for the
 * ordering of numeric components of semver pre-release versions to be respected
 * they must be padded with leading zeroes.</li>
 * 
 * </ul>
 * 
 * Limitations:
 * 
 * <ul>
 * 
 * <li>Build information is stripped on conversion to OSGi. Build information
 * has no bearing on ordering so this is not a significant issue.</li>
 * 
 * <li>Any numeric components are limited to 32 bit unsigned integers, including
 * major, minor, micro, and pre-release identifiers.</li>
 * 
 * </ul>
 */
@org.osgi.annotation.versioning.Version("1.0.0")
package uk.co.saiman.webmodule.semver;
