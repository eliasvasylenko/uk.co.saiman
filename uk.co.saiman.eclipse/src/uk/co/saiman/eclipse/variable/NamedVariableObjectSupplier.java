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
package uk.co.saiman.eclipse.variable;

import static uk.co.saiman.reflection.Types.getErasedType;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.function.Consumer;

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.suppliers.ExtendedObjectSupplier;
import org.eclipse.e4.core.di.suppliers.IObjectDescriptor;
import org.eclipse.e4.core.di.suppliers.IRequestor;
import org.osgi.service.component.annotations.Component;

import uk.co.saiman.eclipse.utilities.TransformingNamedObjectSupplier;
import uk.co.saiman.property.IdentityProperty;
import uk.co.saiman.property.Property;

/**
 * @since 1.2
 */
@Component(
    service = ExtendedObjectSupplier.class,
    property = "dependency.injection.annotation:String=uk.co.saiman.eclipse.variable.NamedVariable",
    immediate = true)
public class NamedVariableObjectSupplier extends TransformingNamedObjectSupplier<NamedVariable> {
  class NamedVariableRequest extends NamedVariableObjectSupplier.Request {
    private final String name;
    private final Consumer<Object> update;
    private final Object propertyObject;
    private final Class<?> valueType;

    public NamedVariableRequest(IObjectDescriptor descriptor, IRequestor requestor) {
      super(descriptor, requestor);

      this.name = getQualifier().value();
      this.valueType = getErasedArgumentType(descriptor.getDesiredType());

      Class<?> propertyType = getErasedType(descriptor.getDesiredType());

      if (propertyType.isAssignableFrom(Property.class)) {
        Property<Object> property = new IdentityProperty<>();
        this.update = property::set;
        this.propertyObject = property;

      } else {
        throw new IllegalArgumentException(
            "Cannot inject named variable for variable type " + propertyType);

      }
    }

    private Class<?> getErasedArgumentType(Type desiredType) {
      Type argument = ((ParameterizedType) desiredType).getActualTypeArguments()[0];

      if (argument instanceof ParameterizedType) {
        return (Class<?>) ((ParameterizedType) argument).getRawType();

      } else if (argument instanceof Class<?>) {
        return (Class<?>) argument;

      } else {
        return Object.class;
      }
    }

    @Override
    protected Object get(IEclipseContext context) {
      Object object = context.get(name);
      if (!valueType.isAssignableFrom(object.getClass()))
        object = null;
      update.accept(object);
      return propertyObject;
    }
  }

  public NamedVariableObjectSupplier() {
    super(NamedVariable.class);
  }

  @Override
  protected NamedVariableRequest getRequest(IObjectDescriptor descriptor, IRequestor requestor) {
    return new NamedVariableRequest(descriptor, requestor);
  }
}
