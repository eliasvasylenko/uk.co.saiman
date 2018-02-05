/*
 * Copyright (C) 2018 Scientific Analysis Instruments Limited <contact@saiman.co.uk>
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
 * This file is part of uk.co.saiman.msapex.chart.
 *
 * uk.co.saiman.msapex.chart is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.msapex.chart is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.msapex.chart;

import javafx.scene.Node;
import uk.co.saiman.reflection.token.TypeToken;

/**
 * An annotation handler applies to all annotation of its type. It translates a
 * conceptual annotation into an UI representation.
 * 
 * @author Elias N Vasylenko
 *
 * @param <T>
 *          The type of the annotation to be handled by this handler
 */
public interface AnnotationHandler<T> {
	/**
	 * @return The data type of annotation to handle
	 */
	TypeToken<T> getDataType();

	/**
	 * @param annotationData
	 *          The data of an annotation
	 * @return The {@link Node} representation of the annotation to apply to the
	 *         scene
	 */
	Node handle(T annotationData);
}
