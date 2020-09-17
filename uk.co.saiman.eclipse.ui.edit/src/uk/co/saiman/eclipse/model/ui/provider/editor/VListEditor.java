/*
 * Copyright (C) 2020 Scientific Analysis Instruments Limited <contact@saiman.co.uk>
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
 * This file is part of uk.co.saiman.eclipse.ui.edit.
 *
 * uk.co.saiman.eclipse.ui.edit is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.eclipse.ui.edit is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.eclipse.model.ui.provider.editor;

import static java.util.Arrays.asList;
import static uk.co.saiman.eclipse.model.ui.provider.UISaimanEditPlugin.INSTANCE;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.core.databinding.observable.list.IObservableList;
import org.eclipse.core.databinding.observable.value.WritableValue;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.tools.emf.ui.common.component.AbstractComponentEditor;
import org.eclipse.e4.tools.emf.ui.internal.common.VirtualEntry;
import org.eclipse.emf.databinding.EMFDataBindingContext;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.jface.action.Action;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.widgets.Composite;

import uk.co.saiman.eclipse.model.ui.provider.editor.ListComponentManager.Type;

public abstract class VListEditor extends AbstractComponentEditor {
  private final EStructuralFeature feature;
  private final List<Type> types;

  private ListComponentManager listEditor;

  private Composite composite;
  private EMFDataBindingContext context;

  @Inject
  IEclipseContext eclipseContext;

  protected VListEditor(EStructuralFeature feature, Type... types) {
    this(feature, asList(types));
  }

  protected VListEditor(EStructuralFeature feature, List<Type> types) {
    super();
    this.feature = feature;
    this.types = new ArrayList<>(types);
  }

  @PostConstruct
  void init() {
    listEditor = new ListComponentManager(this, Messages, feature, this::handleCreate, types);
  }

  protected static String getString(String key) {
    return INSTANCE.getPluginResourceLocator().getString(key);
  }

  @Override
  public String getLabel(Object element) {
    return Messages.VMenuEditor_TreeLabel;
  }

  @Override
  public String getDetailLabel(Object element) {
    return null;
  }

  @Override
  public String getDescription(Object element) {
    return Messages.VMenuEditor_TreeLabelDescription;
  }

  @SuppressWarnings("unchecked")
  @Override
  public Composite doGetEditor(Composite parent, Object object) {
    if (composite == null) {
      context = new EMFDataBindingContext();
      composite = createForm(parent, context, getMaster());
    }
    final VirtualEntry<?,?> o = (VirtualEntry<?,?>) object;
    listEditor.getViewer().setInput(o.getList());
    getMaster().setValue(o.getOriginalParent());
    return composite;
  }

  private Composite createForm(
      Composite parent,
      EMFDataBindingContext context,
      WritableValue<?> master) {
    final CTabFolder folder = new CTabFolder(parent, SWT.BOTTOM);

    final CTabItem item = new CTabItem(folder, SWT.NONE);
    item.setText(Messages.ModelTooling_Common_TabDefault);

    parent = createScrollableContainer(folder);
    item.setControl(parent.getParent());

    listEditor.createForm(parent);

    folder.setSelection(0);

    return folder;
  }

  @Override
  public IObservableList<?> getChildList(Object element) {
    return null;
  }

  private EObject handleCreate(Type type) {
    final EObject handler = EcoreUtil.create(type.eClass);
    setElementId(handler);
    return handler;
  }

  @Override
  public List<Action> getActions(Object element) {
    final ArrayList<Action> l = new ArrayList<>(super.getActions(element));
    l.addAll(listEditor.getActions());
    return l;
  }
}
