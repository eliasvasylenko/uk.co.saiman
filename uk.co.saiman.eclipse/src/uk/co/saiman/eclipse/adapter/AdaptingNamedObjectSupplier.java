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
 * This file is part of uk.co.saiman.eclipse.
 *
 * uk.co.saiman.eclipse is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.eclipse is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.eclipse.adapter;

import static uk.co.saiman.reflection.Types.getErasedType;

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.suppliers.ExtendedObjectSupplier;
import org.eclipse.e4.core.di.suppliers.IObjectDescriptor;
import org.eclipse.e4.core.di.suppliers.IRequestor;
import org.eclipse.e4.core.services.adapter.Adapter;
import org.osgi.service.component.annotations.Component;

import uk.co.saiman.eclipse.utilities.TransformingNamedObjectSupplier;

/**
 * @since 1.2
 */
@Component(
    service = ExtendedObjectSupplier.class,
    property = "dependency.injection.annotation:String=uk.co.saiman.eclipse.adapter.AdaptNamed",
    immediate = true)
public class AdaptingNamedObjectSupplier extends TransformingNamedObjectSupplier<AdaptNamed> {
  class NamedVariableRequest extends AdaptingNamedObjectSupplier.Request {
    private final String name;
    private final Class<?> adapterType;

    public NamedVariableRequest(IObjectDescriptor descriptor, IRequestor requestor) {
      super(descriptor, requestor);

      this.name = getQualifier().value();
      this.adapterType = getErasedType(descriptor.getDesiredType());
    }

    @Override
    protected Object get(IEclipseContext context) {
      return context.get(Adapter.class).adapt(context.get(name), adapterType);
    }
  }

  public AdaptingNamedObjectSupplier() {
    super(AdaptNamed.class);
  }

  @Override
  protected NamedVariableRequest getRequest(IObjectDescriptor descriptor, IRequestor requestor) {
    return new NamedVariableRequest(descriptor, requestor);
  }
}
