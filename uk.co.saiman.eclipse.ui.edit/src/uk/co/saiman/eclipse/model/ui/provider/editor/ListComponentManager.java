package uk.co.saiman.eclipse.model.ui.provider.editor;

import static java.util.Arrays.asList;
import static uk.co.saiman.eclipse.model.ui.provider.UISaimanEditPlugin.INSTANCE;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import org.eclipse.e4.tools.emf.ui.common.component.AbstractComponentEditor;
import org.eclipse.e4.tools.emf.ui.internal.Messages;
import org.eclipse.e4.tools.emf.ui.internal.common.AbstractPickList;
import org.eclipse.e4.tools.emf.ui.internal.common.E4PickList;
import org.eclipse.e4.ui.model.application.descriptor.basic.MPartDescriptor;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.edit.command.AddCommand;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;

public class ListComponentManager {
  public static class Type {
    public final String name;
    public final ImageDescriptor image;
    public final EClass eClass;

    public Type(String name, ImageDescriptor image, EClass eClass) {
      this.name = name;
      this.image = image;
      this.eClass = eClass;
    }
  }

  private final AbstractComponentEditor editor;
  private final Messages messages;

  private final List<Type> types;
  private final EStructuralFeature feature;
  private final Function<Type, EObject> handleCreate;
  private final List<Action> actions = new ArrayList<>();

  private TableViewer viewer;

  protected ListComponentManager(
      AbstractComponentEditor editor,
      Messages messages,
      EStructuralFeature feature,
      Function<Type, EObject> handleCreate,
      Type... types) {
    this(editor, messages, feature, handleCreate, asList(types));
  }

  protected ListComponentManager(
      AbstractComponentEditor editor,
      Messages messages,
      EStructuralFeature feature,
      Function<Type, EObject> handleCreate,
      List<Type> types) {
    this.editor = editor;
    this.messages = messages;
    this.feature = feature;
    this.handleCreate = handleCreate;
    this.types = new ArrayList<>(types);

    for (Type type : types) {
      actions.add(new Action(type.name, type.image) {
        @Override
        public void run() {
          handleAdd(type);
        }
      });
    }
  }

  public List<Action> getActions() {
    return actions;
  }

  protected static String getString(String key) {
    return INSTANCE.getPluginResourceLocator().getString(key);
  }

  public void createForm(Composite parent) {
    final AbstractPickList pickList = new E4PickList(
        parent,
        SWT.NONE,
        null,
        messages,
        editor,
        feature) {
      @Override
      protected void addPressed() {
        final Type type = (Type) ((IStructuredSelection) getSelection()).getFirstElement();
        handleAdd(type);
      }

      @Override
      protected List<?> getContainerChildren(Object container) {
        if (container instanceof MPartDescriptor) {
          return ((MPartDescriptor) container).getMenus();
        } else if (container instanceof MPart) {
          return ((MPart) container).getMenus();
        } else {
          return null;
        }
      }
    };
    pickList.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 3, 1));
    viewer = pickList.getList();
    GridData gd = (GridData) viewer.getTable().getLayoutData();
    gd.heightHint = 150;

    pickList.setContentProvider(ArrayContentProvider.getInstance());
    pickList.setLabelProvider(new LabelProvider() {
      @Override
      public String getText(Object element) {
        return ((Type) element).name;
      }
    });
    pickList.setInput(types);
    pickList.setSelection(new StructuredSelection(types.get(0)));
  }

  public StructuredViewer getViewer() {
    return viewer;
  }

  protected void handleAdd(Type type) {
    final EObject handler = handleCreate.apply(type);

    final Command cmd = AddCommand
        .create(editor.getEditingDomain(), editor.getMaster().getValue(), feature, handler);

    if (cmd.canExecute()) {
      editor.getEditingDomain().getCommandStack().execute(cmd);
      editor.getEditor().setSelection(handler);
    }
  }
}
