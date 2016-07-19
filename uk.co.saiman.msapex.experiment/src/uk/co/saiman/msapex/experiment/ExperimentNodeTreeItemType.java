/*
 * Copyright (C) 2016 Scientific Analysis Instruments Limited <contact@saiman.co.uk>
 *
 * This file is part of uk.co.saiman.msapex.experiment.
 *
 * uk.co.saiman.msapex.experiment is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.msapex.experiment is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.msapex.experiment;

import static java.util.stream.Collectors.toList;

import java.util.List;

import org.eclipse.e4.ui.services.EMenuService;

import uk.co.saiman.experiment.ExperimentNode;
import uk.co.saiman.experiment.ExperimentType;
import uk.co.strangeskies.fx.TreeItemData;
import uk.co.strangeskies.reflection.TypeToken;

public class ExperimentNodeTreeItemType<E extends ExperimentType<T>, T>
		extends ExperimentTreeItemType<ExperimentNode<E, T>> {
	private final E experimentType;

	public ExperimentNodeTreeItemType(E experimentType, EMenuService menuService) {
		super(menuService);
		this.experimentType = experimentType;
	}

	protected <F extends ExperimentType<U>, U> TreeItemData<ExperimentNode<F, U>> getItemData(
			ExperimentNode<F, U> child) {
		/*
		 * TODO we somehow need to fetch the best available published service here!
		 * 
		 * TODO also allow initialisation pop-up from ExperimentNodeTreeItemType
		 * implementation perhaps?
		 */
		return new TreeItemData<>(new ExperimentNodeTreeItemType<>(child.getType(), getMenuService()), child);
	}

	@Override
	public TypeToken<ExperimentNode<E, T>> getDataType() {
		return null;
		/*-
		 * TODO new TypeToken<ExperimentNode<E, T>>() {}.withTypeArgument(new TypeParameter<E>() {}, experimentType.getThisType());
		 */
	}

	@Override
	public boolean hasChildren(ExperimentNode<E, T> data) {
		return !data.getChildren().isEmpty();
	}

	@Override
	public List<TreeItemData<?>> getChildren(ExperimentNode<E, T> data) {
		return data.getChildren().stream().map(c -> getItemData(c)).collect(toList());
	}

	@Override
	public String getText(ExperimentNode<E, T> data) {
		return experimentType.getName();
	}

	@Override
	public String getSupplementalText(ExperimentNode<E, T> data) {
		return data.toString();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this)
			return true;
		if (obj.getClass() != ExperimentNodeTreeItemType.class)
			return false;

		ExperimentNodeTreeItemType<?, ?> that = (ExperimentNodeTreeItemType<?, ?>) obj;

		return experimentType.equals(that.experimentType) && getMenuService().equals(that.getMenuService());
	}

	@Override
	public int hashCode() {
		return experimentType.hashCode() ^ getMenuService().hashCode();
	}
}
