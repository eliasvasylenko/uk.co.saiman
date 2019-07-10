/**
 */
package uk.co.saiman.eclipse.model.ui.impl;

import java.lang.reflect.InvocationTargetException;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.eclipse.e4.core.contexts.IEclipseContext;

import org.eclipse.e4.ui.model.LocalizationHelper;

import org.eclipse.e4.ui.model.application.MApplicationElement;
import org.eclipse.e4.ui.model.application.MContribution;

import org.eclipse.e4.ui.model.application.commands.MHandler;
import org.eclipse.e4.ui.model.application.commands.MHandlerContainer;

import org.eclipse.e4.ui.model.application.commands.impl.CommandsPackageImpl;

import org.eclipse.e4.ui.model.application.impl.ApplicationPackageImpl;
import org.eclipse.e4.ui.model.application.impl.StringToObjectMapImpl;
import org.eclipse.e4.ui.model.application.impl.StringToStringMapImpl;

import org.eclipse.e4.ui.model.application.ui.MContext;
import org.eclipse.e4.ui.model.application.ui.MElementContainer;
import org.eclipse.e4.ui.model.application.ui.MExpression;
import org.eclipse.e4.ui.model.application.ui.MUIElement;

import org.eclipse.e4.ui.model.application.ui.advanced.MPlaceholder;

import org.eclipse.e4.ui.model.application.ui.impl.UiPackageImpl;

import org.eclipse.e4.ui.model.application.ui.menu.MPopupMenu;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.EMap;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;

import org.eclipse.emf.ecore.util.EDataTypeUniqueEList;
import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.EObjectContainmentWithInverseEList;
import org.eclipse.emf.ecore.util.EcoreEMap;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.util.InternalEList;

import uk.co.saiman.eclipse.model.ui.MCell;
import uk.co.saiman.eclipse.model.ui.MPackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Cell</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link uk.co.saiman.eclipse.model.ui.impl.CellImpl#getLabel <em>Label</em>}</li>
 *   <li>{@link uk.co.saiman.eclipse.model.ui.impl.CellImpl#getIconURI <em>Icon URI</em>}</li>
 *   <li>{@link uk.co.saiman.eclipse.model.ui.impl.CellImpl#getTooltip <em>Tooltip</em>}</li>
 *   <li>{@link uk.co.saiman.eclipse.model.ui.impl.CellImpl#getLocalizedLabel <em>Localized Label</em>}</li>
 *   <li>{@link uk.co.saiman.eclipse.model.ui.impl.CellImpl#getLocalizedTooltip <em>Localized Tooltip</em>}</li>
 *   <li>{@link uk.co.saiman.eclipse.model.ui.impl.CellImpl#getContext <em>Context</em>}</li>
 *   <li>{@link uk.co.saiman.eclipse.model.ui.impl.CellImpl#getVariables <em>Variables</em>}</li>
 *   <li>{@link uk.co.saiman.eclipse.model.ui.impl.CellImpl#getProperties <em>Properties</em>}</li>
 *   <li>{@link uk.co.saiman.eclipse.model.ui.impl.CellImpl#getElementId <em>Element Id</em>}</li>
 *   <li>{@link uk.co.saiman.eclipse.model.ui.impl.CellImpl#getPersistedState <em>Persisted State</em>}</li>
 *   <li>{@link uk.co.saiman.eclipse.model.ui.impl.CellImpl#getTags <em>Tags</em>}</li>
 *   <li>{@link uk.co.saiman.eclipse.model.ui.impl.CellImpl#getContributorURI <em>Contributor URI</em>}</li>
 *   <li>{@link uk.co.saiman.eclipse.model.ui.impl.CellImpl#getTransientData <em>Transient Data</em>}</li>
 *   <li>{@link uk.co.saiman.eclipse.model.ui.impl.CellImpl#getContributionURI <em>Contribution URI</em>}</li>
 *   <li>{@link uk.co.saiman.eclipse.model.ui.impl.CellImpl#getObject <em>Object</em>}</li>
 *   <li>{@link uk.co.saiman.eclipse.model.ui.impl.CellImpl#getWidget <em>Widget</em>}</li>
 *   <li>{@link uk.co.saiman.eclipse.model.ui.impl.CellImpl#getRenderer <em>Renderer</em>}</li>
 *   <li>{@link uk.co.saiman.eclipse.model.ui.impl.CellImpl#isToBeRendered <em>To Be Rendered</em>}</li>
 *   <li>{@link uk.co.saiman.eclipse.model.ui.impl.CellImpl#isOnTop <em>On Top</em>}</li>
 *   <li>{@link uk.co.saiman.eclipse.model.ui.impl.CellImpl#isVisible <em>Visible</em>}</li>
 *   <li>{@link uk.co.saiman.eclipse.model.ui.impl.CellImpl#getParent <em>Parent</em>}</li>
 *   <li>{@link uk.co.saiman.eclipse.model.ui.impl.CellImpl#getContainerData <em>Container Data</em>}</li>
 *   <li>{@link uk.co.saiman.eclipse.model.ui.impl.CellImpl#getCurSharedRef <em>Cur Shared Ref</em>}</li>
 *   <li>{@link uk.co.saiman.eclipse.model.ui.impl.CellImpl#getVisibleWhen <em>Visible When</em>}</li>
 *   <li>{@link uk.co.saiman.eclipse.model.ui.impl.CellImpl#getAccessibilityPhrase <em>Accessibility Phrase</em>}</li>
 *   <li>{@link uk.co.saiman.eclipse.model.ui.impl.CellImpl#getLocalizedAccessibilityPhrase <em>Localized Accessibility Phrase</em>}</li>
 *   <li>{@link uk.co.saiman.eclipse.model.ui.impl.CellImpl#getChildren <em>Children</em>}</li>
 *   <li>{@link uk.co.saiman.eclipse.model.ui.impl.CellImpl#getSelectedElement <em>Selected Element</em>}</li>
 *   <li>{@link uk.co.saiman.eclipse.model.ui.impl.CellImpl#getHandlers <em>Handlers</em>}</li>
 *   <li>{@link uk.co.saiman.eclipse.model.ui.impl.CellImpl#getPopupMenu <em>Popup Menu</em>}</li>
 *   <li>{@link uk.co.saiman.eclipse.model.ui.impl.CellImpl#isExpanded <em>Expanded</em>}</li>
 * </ul>
 *
 * @generated
 */
public class CellImpl extends org.eclipse.emf.ecore.impl.MinimalEObjectImpl.Container implements MCell {
  /**
   * The default value of the '{@link #getLabel() <em>Label</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getLabel()
   * @since 1.0
   * @generated
   * @ordered
   */
  protected static final String LABEL_EDEFAULT = null;

  /**
   * The cached value of the '{@link #getLabel() <em>Label</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getLabel()
   * @since 1.0
   * @generated
   * @ordered
   */
  protected String label = LABEL_EDEFAULT;

  /**
   * The default value of the '{@link #getIconURI() <em>Icon URI</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getIconURI()
   * @since 1.0
   * @generated
   * @ordered
   */
  protected static final String ICON_URI_EDEFAULT = null;

  /**
   * The cached value of the '{@link #getIconURI() <em>Icon URI</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getIconURI()
   * @since 1.0
   * @generated
   * @ordered
   */
  protected String iconURI = ICON_URI_EDEFAULT;

  /**
   * The default value of the '{@link #getTooltip() <em>Tooltip</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getTooltip()
   * @since 1.0
   * @generated
   * @ordered
   */
  protected static final String TOOLTIP_EDEFAULT = null;

  /**
   * The cached value of the '{@link #getTooltip() <em>Tooltip</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getTooltip()
   * @since 1.0
   * @generated
   * @ordered
   */
  protected String tooltip = TOOLTIP_EDEFAULT;

  /**
   * The default value of the '{@link #getLocalizedLabel() <em>Localized Label</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getLocalizedLabel()
   * @generated
   * @ordered
   */
  protected static final String LOCALIZED_LABEL_EDEFAULT = "";

  /**
   * The default value of the '{@link #getLocalizedTooltip() <em>Localized Tooltip</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getLocalizedTooltip()
   * @generated
   * @ordered
   */
  protected static final String LOCALIZED_TOOLTIP_EDEFAULT = "";

  /**
   * The default value of the '{@link #getContext() <em>Context</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getContext()
   * @generated
   * @ordered
   */
  protected static final IEclipseContext CONTEXT_EDEFAULT = null;

  /**
   * The cached value of the '{@link #getContext() <em>Context</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getContext()
   * @generated
   * @ordered
   */
  protected IEclipseContext context = CONTEXT_EDEFAULT;

  /**
   * The cached value of the '{@link #getVariables() <em>Variables</em>}' attribute list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getVariables()
   * @generated
   * @ordered
   */
  protected EList<String> variables;

  /**
   * The cached value of the '{@link #getProperties() <em>Properties</em>}' map.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getProperties()
   * @generated
   * @ordered
   */
  protected EMap<String, String> properties;

  /**
   * The default value of the '{@link #getElementId() <em>Element Id</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getElementId()
   * @generated
   * @ordered
   */
  protected static final String ELEMENT_ID_EDEFAULT = null;

  /**
   * The cached value of the '{@link #getElementId() <em>Element Id</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getElementId()
   * @generated
   * @ordered
   */
  protected String elementId = ELEMENT_ID_EDEFAULT;

  /**
   * The cached value of the '{@link #getPersistedState() <em>Persisted State</em>}' map.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getPersistedState()
   * @generated
   * @ordered
   */
  protected EMap<String, String> persistedState;

  /**
   * The cached value of the '{@link #getTags() <em>Tags</em>}' attribute list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getTags()
   * @generated
   * @ordered
   */
  protected EList<String> tags;

  /**
   * The default value of the '{@link #getContributorURI() <em>Contributor URI</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getContributorURI()
   * @generated
   * @ordered
   */
  protected static final String CONTRIBUTOR_URI_EDEFAULT = null;

  /**
   * The cached value of the '{@link #getContributorURI() <em>Contributor URI</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getContributorURI()
   * @generated
   * @ordered
   */
  protected String contributorURI = CONTRIBUTOR_URI_EDEFAULT;

  /**
   * The cached value of the '{@link #getTransientData() <em>Transient Data</em>}' map.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getTransientData()
   * @generated
   * @ordered
   */
  protected EMap<String, Object> transientData;

  /**
   * The default value of the '{@link #getContributionURI() <em>Contribution URI</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getContributionURI()
   * @since 1.0
   * @generated
   * @ordered
   */
  protected static final String CONTRIBUTION_URI_EDEFAULT = null;

  /**
   * The cached value of the '{@link #getContributionURI() <em>Contribution URI</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getContributionURI()
   * @since 1.0
   * @generated
   * @ordered
   */
  protected String contributionURI = CONTRIBUTION_URI_EDEFAULT;

  /**
   * The default value of the '{@link #getObject() <em>Object</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getObject()
   * @generated
   * @ordered
   */
  protected static final Object OBJECT_EDEFAULT = null;

  /**
   * The cached value of the '{@link #getObject() <em>Object</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getObject()
   * @generated
   * @ordered
   */
  protected Object object = OBJECT_EDEFAULT;

  /**
   * The default value of the '{@link #getWidget() <em>Widget</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getWidget()
   * @generated
   * @ordered
   */
  protected static final Object WIDGET_EDEFAULT = null;

  /**
   * The cached value of the '{@link #getWidget() <em>Widget</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getWidget()
   * @generated
   * @ordered
   */
  protected Object widget = WIDGET_EDEFAULT;

  /**
   * The default value of the '{@link #getRenderer() <em>Renderer</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getRenderer()
   * @generated
   * @ordered
   */
  protected static final Object RENDERER_EDEFAULT = null;

  /**
   * The cached value of the '{@link #getRenderer() <em>Renderer</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getRenderer()
   * @generated
   * @ordered
   */
  protected Object renderer = RENDERER_EDEFAULT;

  /**
   * The default value of the '{@link #isToBeRendered() <em>To Be Rendered</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #isToBeRendered()
   * @generated
   * @ordered
   */
  protected static final boolean TO_BE_RENDERED_EDEFAULT = true;

  /**
   * The cached value of the '{@link #isToBeRendered() <em>To Be Rendered</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #isToBeRendered()
   * @generated
   * @ordered
   */
  protected boolean toBeRendered = TO_BE_RENDERED_EDEFAULT;

  /**
   * The default value of the '{@link #isOnTop() <em>On Top</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #isOnTop()
   * @generated
   * @ordered
   */
  protected static final boolean ON_TOP_EDEFAULT = false;

  /**
   * The cached value of the '{@link #isOnTop() <em>On Top</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #isOnTop()
   * @generated
   * @ordered
   */
  protected boolean onTop = ON_TOP_EDEFAULT;

  /**
   * The default value of the '{@link #isVisible() <em>Visible</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #isVisible()
   * @generated
   * @ordered
   */
  protected static final boolean VISIBLE_EDEFAULT = true;

  /**
   * The cached value of the '{@link #isVisible() <em>Visible</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #isVisible()
   * @generated
   * @ordered
   */
  protected boolean visible = VISIBLE_EDEFAULT;

  /**
   * The default value of the '{@link #getContainerData() <em>Container Data</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getContainerData()
   * @generated
   * @ordered
   */
  protected static final String CONTAINER_DATA_EDEFAULT = null;

  /**
   * The cached value of the '{@link #getContainerData() <em>Container Data</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getContainerData()
   * @generated
   * @ordered
   */
  protected String containerData = CONTAINER_DATA_EDEFAULT;

  /**
   * The cached value of the '{@link #getCurSharedRef() <em>Cur Shared Ref</em>}' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getCurSharedRef()
   * @generated
   * @ordered
   */
  protected MPlaceholder curSharedRef;

  /**
   * The cached value of the '{@link #getVisibleWhen() <em>Visible When</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getVisibleWhen()
   * @generated
   * @ordered
   */
  protected MExpression visibleWhen;

  /**
   * The default value of the '{@link #getAccessibilityPhrase() <em>Accessibility Phrase</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getAccessibilityPhrase()
   * @generated
   * @ordered
   */
  protected static final String ACCESSIBILITY_PHRASE_EDEFAULT = null;

  /**
   * The cached value of the '{@link #getAccessibilityPhrase() <em>Accessibility Phrase</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getAccessibilityPhrase()
   * @generated
   * @ordered
   */
  protected String accessibilityPhrase = ACCESSIBILITY_PHRASE_EDEFAULT;

  /**
   * The default value of the '{@link #getLocalizedAccessibilityPhrase() <em>Localized Accessibility Phrase</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getLocalizedAccessibilityPhrase()
   * @generated
   * @ordered
   */
  protected static final String LOCALIZED_ACCESSIBILITY_PHRASE_EDEFAULT = null;

  /**
   * The cached value of the '{@link #getChildren() <em>Children</em>}' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getChildren()
   * @generated
   * @ordered
   */
  protected EList<MCell> children;

  /**
   * The cached value of the '{@link #getSelectedElement() <em>Selected Element</em>}' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getSelectedElement()
   * @generated
   * @ordered
   */
  protected MCell selectedElement;

  /**
   * The cached value of the '{@link #getHandlers() <em>Handlers</em>}' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getHandlers()
   * @generated
   * @ordered
   */
  protected EList<MHandler> handlers;

  /**
   * The cached value of the '{@link #getPopupMenu() <em>Popup Menu</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getPopupMenu()
   * @generated
   * @ordered
   */
  protected MPopupMenu popupMenu;

  /**
   * The default value of the '{@link #isExpanded() <em>Expanded</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #isExpanded()
   * @generated
   * @ordered
   */
  protected static final boolean EXPANDED_EDEFAULT = false;

  /**
   * The cached value of the '{@link #isExpanded() <em>Expanded</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #isExpanded()
   * @generated
   * @ordered
   */
  protected boolean expanded = EXPANDED_EDEFAULT;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected CellImpl() {
    super();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  protected EClass eStaticClass() {
    return MPackage.Literals.CELL;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @since 1.0
   * @generated
   */
  @Override
  public String getLabel() {
    return label;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @since 1.0
   * @generated
   */
  @Override
  public void setLabel(String newLabel) {
    String oldLabel = label;
    label = newLabel;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, MPackage.CELL__LABEL, oldLabel, label));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @since 1.0
   * @generated
   */
  @Override
  public String getIconURI() {
    return iconURI;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @since 1.0
   * @generated
   */
  @Override
  public void setIconURI(String newIconURI) {
    String oldIconURI = iconURI;
    iconURI = newIconURI;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, MPackage.CELL__ICON_URI, oldIconURI, iconURI));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @since 1.0
   * @generated
   */
  @Override
  public String getTooltip() {
    return tooltip;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @since 1.0
   * @generated
   */
  @Override
  public void setTooltip(String newTooltip) {
    String oldTooltip = tooltip;
    tooltip = newTooltip;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, MPackage.CELL__TOOLTIP, oldTooltip, tooltip));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public String getLocalizedLabel() {
    return LocalizationHelper.getLocalizedFeature(UiPackageImpl.Literals.UI_LABEL__LABEL, this);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public String getLocalizedTooltip() {
    return LocalizationHelper.getLocalizedFeature(UiPackageImpl.Literals.UI_LABEL__TOOLTIP, this);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public IEclipseContext getContext() {
    return context;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setContext(IEclipseContext newContext) {
    IEclipseContext oldContext = context;
    context = newContext;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, MPackage.CELL__CONTEXT, oldContext, context));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public List<String> getVariables() {
    if (variables == null) {
      variables = new EDataTypeUniqueEList<String>(String.class, this, MPackage.CELL__VARIABLES);
    }
    return variables;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Map<String, String> getProperties() {
    if (properties == null) {
      properties = new EcoreEMap<String,String>(ApplicationPackageImpl.Literals.STRING_TO_STRING_MAP, StringToStringMapImpl.class, this, MPackage.CELL__PROPERTIES);
    }
    return properties.map();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public String getElementId() {
    return elementId;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setElementId(String newElementId) {
    String oldElementId = elementId;
    elementId = newElementId;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, MPackage.CELL__ELEMENT_ID, oldElementId, elementId));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Map<String, String> getPersistedState() {
    if (persistedState == null) {
      persistedState = new EcoreEMap<String,String>(ApplicationPackageImpl.Literals.STRING_TO_STRING_MAP, StringToStringMapImpl.class, this, MPackage.CELL__PERSISTED_STATE);
    }
    return persistedState.map();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public List<String> getTags() {
    if (tags == null) {
      tags = new EDataTypeUniqueEList<String>(String.class, this, MPackage.CELL__TAGS);
    }
    return tags;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public String getContributorURI() {
    return contributorURI;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setContributorURI(String newContributorURI) {
    String oldContributorURI = contributorURI;
    contributorURI = newContributorURI;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, MPackage.CELL__CONTRIBUTOR_URI, oldContributorURI, contributorURI));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Map<String, Object> getTransientData() {
    if (transientData == null) {
      transientData = new EcoreEMap<String,Object>(ApplicationPackageImpl.Literals.STRING_TO_OBJECT_MAP, StringToObjectMapImpl.class, this, MPackage.CELL__TRANSIENT_DATA);
    }
    return transientData.map();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @since 1.0
   * @generated
   */
  @Override
  public String getContributionURI() {
    return contributionURI;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @since 1.0
   * @generated
   */
  @Override
  public void setContributionURI(String newContributionURI) {
    String oldContributionURI = contributionURI;
    contributionURI = newContributionURI;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, MPackage.CELL__CONTRIBUTION_URI, oldContributionURI, contributionURI));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Object getObject() {
    return object;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setObject(Object newObject) {
    Object oldObject = object;
    object = newObject;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, MPackage.CELL__OBJECT, oldObject, object));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Object getWidget() {
    return widget;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setWidget(Object newWidget) {
    Object oldWidget = widget;
    widget = newWidget;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, MPackage.CELL__WIDGET, oldWidget, widget));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Object getRenderer() {
    return renderer;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setRenderer(Object newRenderer) {
    Object oldRenderer = renderer;
    renderer = newRenderer;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, MPackage.CELL__RENDERER, oldRenderer, renderer));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public boolean isToBeRendered() {
    return toBeRendered;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setToBeRendered(boolean newToBeRendered) {
    boolean oldToBeRendered = toBeRendered;
    toBeRendered = newToBeRendered;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, MPackage.CELL__TO_BE_RENDERED, oldToBeRendered, toBeRendered));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public boolean isOnTop() {
    return onTop;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setOnTop(boolean newOnTop) {
    boolean oldOnTop = onTop;
    onTop = newOnTop;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, MPackage.CELL__ON_TOP, oldOnTop, onTop));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public boolean isVisible() {
    return visible;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setVisible(boolean newVisible) {
    boolean oldVisible = visible;
    visible = newVisible;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, MPackage.CELL__VISIBLE, oldVisible, visible));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @SuppressWarnings("unchecked")
  @Override
  public MElementContainer<MUIElement> getParent() {
    if (eContainerFeatureID() != MPackage.CELL__PARENT) return null;
    return (MElementContainer<MUIElement>)eInternalContainer();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public NotificationChain basicSetParent(MElementContainer<MUIElement> newParent, NotificationChain msgs) {
    msgs = eBasicSetContainer((InternalEObject)newParent, MPackage.CELL__PARENT, msgs);
    return msgs;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setParent(MElementContainer<MUIElement> newParent) {
    if (newParent != eInternalContainer() || (eContainerFeatureID() != MPackage.CELL__PARENT && newParent != null)) {
      if (EcoreUtil.isAncestor(this, (EObject)newParent))
        throw new IllegalArgumentException("Recursive containment not allowed for " + toString());
      NotificationChain msgs = null;
      if (eInternalContainer() != null)
        msgs = eBasicRemoveFromContainer(msgs);
      if (newParent != null)
        msgs = ((InternalEObject)newParent).eInverseAdd(this, UiPackageImpl.ELEMENT_CONTAINER__CHILDREN, MElementContainer.class, msgs);
      msgs = basicSetParent(newParent, msgs);
      if (msgs != null) msgs.dispatch();
    }
    else if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, MPackage.CELL__PARENT, newParent, newParent));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public String getContainerData() {
    return containerData;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setContainerData(String newContainerData) {
    String oldContainerData = containerData;
    containerData = newContainerData;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, MPackage.CELL__CONTAINER_DATA, oldContainerData, containerData));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public MPlaceholder getCurSharedRef() {
    if (curSharedRef != null && ((EObject)curSharedRef).eIsProxy()) {
      InternalEObject oldCurSharedRef = (InternalEObject)curSharedRef;
      curSharedRef = (MPlaceholder)eResolveProxy(oldCurSharedRef);
      if (curSharedRef != oldCurSharedRef) {
        if (eNotificationRequired())
          eNotify(new ENotificationImpl(this, Notification.RESOLVE, MPackage.CELL__CUR_SHARED_REF, oldCurSharedRef, curSharedRef));
      }
    }
    return curSharedRef;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public MPlaceholder basicGetCurSharedRef() {
    return curSharedRef;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setCurSharedRef(MPlaceholder newCurSharedRef) {
    MPlaceholder oldCurSharedRef = curSharedRef;
    curSharedRef = newCurSharedRef;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, MPackage.CELL__CUR_SHARED_REF, oldCurSharedRef, curSharedRef));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public MExpression getVisibleWhen() {
    return visibleWhen;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public NotificationChain basicSetVisibleWhen(MExpression newVisibleWhen, NotificationChain msgs) {
    MExpression oldVisibleWhen = visibleWhen;
    visibleWhen = newVisibleWhen;
    if (eNotificationRequired()) {
      ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, MPackage.CELL__VISIBLE_WHEN, oldVisibleWhen, newVisibleWhen);
      if (msgs == null) msgs = notification; else msgs.add(notification);
    }
    return msgs;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setVisibleWhen(MExpression newVisibleWhen) {
    if (newVisibleWhen != visibleWhen) {
      NotificationChain msgs = null;
      if (visibleWhen != null)
        msgs = ((InternalEObject)visibleWhen).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - MPackage.CELL__VISIBLE_WHEN, null, msgs);
      if (newVisibleWhen != null)
        msgs = ((InternalEObject)newVisibleWhen).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - MPackage.CELL__VISIBLE_WHEN, null, msgs);
      msgs = basicSetVisibleWhen(newVisibleWhen, msgs);
      if (msgs != null) msgs.dispatch();
    }
    else if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, MPackage.CELL__VISIBLE_WHEN, newVisibleWhen, newVisibleWhen));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public String getAccessibilityPhrase() {
    return accessibilityPhrase;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setAccessibilityPhrase(String newAccessibilityPhrase) {
    String oldAccessibilityPhrase = accessibilityPhrase;
    accessibilityPhrase = newAccessibilityPhrase;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, MPackage.CELL__ACCESSIBILITY_PHRASE, oldAccessibilityPhrase, accessibilityPhrase));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public String getLocalizedAccessibilityPhrase() {
    return LocalizationHelper.getLocalizedAccessibilityPhrase(this);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public List<MCell> getChildren() {
    if (children == null) {
      children = new EObjectContainmentWithInverseEList<MCell>(MCell.class, this, MPackage.CELL__CHILDREN, UiPackageImpl.UI_ELEMENT__PARENT) { private static final long serialVersionUID = 1L; @Override public Class<?> getInverseFeatureClass() { return MUIElement.class; } };
    }
    return children;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public MCell getSelectedElement() {
    if (selectedElement != null && ((EObject)selectedElement).eIsProxy()) {
      InternalEObject oldSelectedElement = (InternalEObject)selectedElement;
      selectedElement = (MCell)eResolveProxy(oldSelectedElement);
      if (selectedElement != oldSelectedElement) {
        if (eNotificationRequired())
          eNotify(new ENotificationImpl(this, Notification.RESOLVE, MPackage.CELL__SELECTED_ELEMENT, oldSelectedElement, selectedElement));
      }
    }
    return selectedElement;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public MCell basicGetSelectedElement() {
    return selectedElement;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setSelectedElement(MCell newSelectedElement) {
    MCell oldSelectedElement = selectedElement;
    selectedElement = newSelectedElement;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, MPackage.CELL__SELECTED_ELEMENT, oldSelectedElement, selectedElement));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public List<MHandler> getHandlers() {
    if (handlers == null) {
      handlers = new EObjectContainmentEList<MHandler>(MHandler.class, this, MPackage.CELL__HANDLERS);
    }
    return handlers;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public MPopupMenu getPopupMenu() {
    return popupMenu;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public NotificationChain basicSetPopupMenu(MPopupMenu newPopupMenu, NotificationChain msgs) {
    MPopupMenu oldPopupMenu = popupMenu;
    popupMenu = newPopupMenu;
    if (eNotificationRequired()) {
      ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, MPackage.CELL__POPUP_MENU, oldPopupMenu, newPopupMenu);
      if (msgs == null) msgs = notification; else msgs.add(notification);
    }
    return msgs;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setPopupMenu(MPopupMenu newPopupMenu) {
    if (newPopupMenu != popupMenu) {
      NotificationChain msgs = null;
      if (popupMenu != null)
        msgs = ((InternalEObject)popupMenu).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - MPackage.CELL__POPUP_MENU, null, msgs);
      if (newPopupMenu != null)
        msgs = ((InternalEObject)newPopupMenu).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - MPackage.CELL__POPUP_MENU, null, msgs);
      msgs = basicSetPopupMenu(newPopupMenu, msgs);
      if (msgs != null) msgs.dispatch();
    }
    else if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, MPackage.CELL__POPUP_MENU, newPopupMenu, newPopupMenu));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public boolean isExpanded() {
    return expanded;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setExpanded(boolean newExpanded) {
    boolean oldExpanded = expanded;
    expanded = newExpanded;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, MPackage.CELL__EXPANDED, oldExpanded, expanded));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @since 1.1
   * @generated
   */
  @Override
  public void updateLocalization() {
    if (eNotificationRequired()) {
      eNotify(new ENotificationImpl(
          this, Notification.SET, UiPackageImpl.UI_ELEMENT__LOCALIZED_ACCESSIBILITY_PHRASE, null, getLocalizedAccessibilityPhrase()));
    }
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @SuppressWarnings("unchecked")
  @Override
  public NotificationChain eInverseAdd(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
    switch (featureID) {
      case MPackage.CELL__PARENT:
        if (eInternalContainer() != null)
          msgs = eBasicRemoveFromContainer(msgs);
        return basicSetParent((MElementContainer<MUIElement>)otherEnd, msgs);
      case MPackage.CELL__CHILDREN:
        return ((InternalEList<InternalEObject>)(InternalEList<?>)getChildren()).basicAdd(otherEnd, msgs);
    }
    return super.eInverseAdd(otherEnd, featureID, msgs);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
    switch (featureID) {
      case MPackage.CELL__PROPERTIES:
        return ((InternalEList<?>)((EMap.InternalMapView<String, String>)getProperties()).eMap()).basicRemove(otherEnd, msgs);
      case MPackage.CELL__PERSISTED_STATE:
        return ((InternalEList<?>)((EMap.InternalMapView<String, String>)getPersistedState()).eMap()).basicRemove(otherEnd, msgs);
      case MPackage.CELL__TRANSIENT_DATA:
        return ((InternalEList<?>)((EMap.InternalMapView<String, Object>)getTransientData()).eMap()).basicRemove(otherEnd, msgs);
      case MPackage.CELL__PARENT:
        return basicSetParent(null, msgs);
      case MPackage.CELL__VISIBLE_WHEN:
        return basicSetVisibleWhen(null, msgs);
      case MPackage.CELL__CHILDREN:
        return ((InternalEList<?>)getChildren()).basicRemove(otherEnd, msgs);
      case MPackage.CELL__HANDLERS:
        return ((InternalEList<?>)getHandlers()).basicRemove(otherEnd, msgs);
      case MPackage.CELL__POPUP_MENU:
        return basicSetPopupMenu(null, msgs);
    }
    return super.eInverseRemove(otherEnd, featureID, msgs);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public NotificationChain eBasicRemoveFromContainerFeature(NotificationChain msgs) {
    switch (eContainerFeatureID()) {
      case MPackage.CELL__PARENT:
        return eInternalContainer().eInverseRemove(this, UiPackageImpl.ELEMENT_CONTAINER__CHILDREN, MElementContainer.class, msgs);
    }
    return super.eBasicRemoveFromContainerFeature(msgs);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Object eGet(int featureID, boolean resolve, boolean coreType) {
    switch (featureID) {
      case MPackage.CELL__LABEL:
        return getLabel();
      case MPackage.CELL__ICON_URI:
        return getIconURI();
      case MPackage.CELL__TOOLTIP:
        return getTooltip();
      case MPackage.CELL__LOCALIZED_LABEL:
        return getLocalizedLabel();
      case MPackage.CELL__LOCALIZED_TOOLTIP:
        return getLocalizedTooltip();
      case MPackage.CELL__CONTEXT:
        return getContext();
      case MPackage.CELL__VARIABLES:
        return getVariables();
      case MPackage.CELL__PROPERTIES:
        if (coreType) return ((EMap.InternalMapView<String, String>)getProperties()).eMap();
        else return getProperties();
      case MPackage.CELL__ELEMENT_ID:
        return getElementId();
      case MPackage.CELL__PERSISTED_STATE:
        if (coreType) return ((EMap.InternalMapView<String, String>)getPersistedState()).eMap();
        else return getPersistedState();
      case MPackage.CELL__TAGS:
        return getTags();
      case MPackage.CELL__CONTRIBUTOR_URI:
        return getContributorURI();
      case MPackage.CELL__TRANSIENT_DATA:
        if (coreType) return ((EMap.InternalMapView<String, Object>)getTransientData()).eMap();
        else return getTransientData();
      case MPackage.CELL__CONTRIBUTION_URI:
        return getContributionURI();
      case MPackage.CELL__OBJECT:
        return getObject();
      case MPackage.CELL__WIDGET:
        return getWidget();
      case MPackage.CELL__RENDERER:
        return getRenderer();
      case MPackage.CELL__TO_BE_RENDERED:
        return isToBeRendered();
      case MPackage.CELL__ON_TOP:
        return isOnTop();
      case MPackage.CELL__VISIBLE:
        return isVisible();
      case MPackage.CELL__PARENT:
        return getParent();
      case MPackage.CELL__CONTAINER_DATA:
        return getContainerData();
      case MPackage.CELL__CUR_SHARED_REF:
        if (resolve) return getCurSharedRef();
        return basicGetCurSharedRef();
      case MPackage.CELL__VISIBLE_WHEN:
        return getVisibleWhen();
      case MPackage.CELL__ACCESSIBILITY_PHRASE:
        return getAccessibilityPhrase();
      case MPackage.CELL__LOCALIZED_ACCESSIBILITY_PHRASE:
        return getLocalizedAccessibilityPhrase();
      case MPackage.CELL__CHILDREN:
        return getChildren();
      case MPackage.CELL__SELECTED_ELEMENT:
        if (resolve) return getSelectedElement();
        return basicGetSelectedElement();
      case MPackage.CELL__HANDLERS:
        return getHandlers();
      case MPackage.CELL__POPUP_MENU:
        return getPopupMenu();
      case MPackage.CELL__EXPANDED:
        return isExpanded();
    }
    return super.eGet(featureID, resolve, coreType);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @SuppressWarnings("unchecked")
  @Override
  public void eSet(int featureID, Object newValue) {
    switch (featureID) {
      case MPackage.CELL__LABEL:
        setLabel((String)newValue);
        return;
      case MPackage.CELL__ICON_URI:
        setIconURI((String)newValue);
        return;
      case MPackage.CELL__TOOLTIP:
        setTooltip((String)newValue);
        return;
      case MPackage.CELL__CONTEXT:
        setContext((IEclipseContext)newValue);
        return;
      case MPackage.CELL__VARIABLES:
        getVariables().clear();
        getVariables().addAll((Collection<? extends String>)newValue);
        return;
      case MPackage.CELL__PROPERTIES:
        ((EStructuralFeature.Setting)((EMap.InternalMapView<String, String>)getProperties()).eMap()).set(newValue);
        return;
      case MPackage.CELL__ELEMENT_ID:
        setElementId((String)newValue);
        return;
      case MPackage.CELL__PERSISTED_STATE:
        ((EStructuralFeature.Setting)((EMap.InternalMapView<String, String>)getPersistedState()).eMap()).set(newValue);
        return;
      case MPackage.CELL__TAGS:
        getTags().clear();
        getTags().addAll((Collection<? extends String>)newValue);
        return;
      case MPackage.CELL__CONTRIBUTOR_URI:
        setContributorURI((String)newValue);
        return;
      case MPackage.CELL__TRANSIENT_DATA:
        ((EStructuralFeature.Setting)((EMap.InternalMapView<String, Object>)getTransientData()).eMap()).set(newValue);
        return;
      case MPackage.CELL__CONTRIBUTION_URI:
        setContributionURI((String)newValue);
        return;
      case MPackage.CELL__OBJECT:
        setObject(newValue);
        return;
      case MPackage.CELL__WIDGET:
        setWidget(newValue);
        return;
      case MPackage.CELL__RENDERER:
        setRenderer(newValue);
        return;
      case MPackage.CELL__TO_BE_RENDERED:
        setToBeRendered((Boolean)newValue);
        return;
      case MPackage.CELL__ON_TOP:
        setOnTop((Boolean)newValue);
        return;
      case MPackage.CELL__VISIBLE:
        setVisible((Boolean)newValue);
        return;
      case MPackage.CELL__PARENT:
        setParent((MElementContainer<MUIElement>)newValue);
        return;
      case MPackage.CELL__CONTAINER_DATA:
        setContainerData((String)newValue);
        return;
      case MPackage.CELL__CUR_SHARED_REF:
        setCurSharedRef((MPlaceholder)newValue);
        return;
      case MPackage.CELL__VISIBLE_WHEN:
        setVisibleWhen((MExpression)newValue);
        return;
      case MPackage.CELL__ACCESSIBILITY_PHRASE:
        setAccessibilityPhrase((String)newValue);
        return;
      case MPackage.CELL__CHILDREN:
        getChildren().clear();
        getChildren().addAll((Collection<? extends MCell>)newValue);
        return;
      case MPackage.CELL__SELECTED_ELEMENT:
        setSelectedElement((MCell)newValue);
        return;
      case MPackage.CELL__HANDLERS:
        getHandlers().clear();
        getHandlers().addAll((Collection<? extends MHandler>)newValue);
        return;
      case MPackage.CELL__POPUP_MENU:
        setPopupMenu((MPopupMenu)newValue);
        return;
      case MPackage.CELL__EXPANDED:
        setExpanded((Boolean)newValue);
        return;
    }
    super.eSet(featureID, newValue);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void eUnset(int featureID) {
    switch (featureID) {
      case MPackage.CELL__LABEL:
        setLabel(LABEL_EDEFAULT);
        return;
      case MPackage.CELL__ICON_URI:
        setIconURI(ICON_URI_EDEFAULT);
        return;
      case MPackage.CELL__TOOLTIP:
        setTooltip(TOOLTIP_EDEFAULT);
        return;
      case MPackage.CELL__CONTEXT:
        setContext(CONTEXT_EDEFAULT);
        return;
      case MPackage.CELL__VARIABLES:
        getVariables().clear();
        return;
      case MPackage.CELL__PROPERTIES:
        getProperties().clear();
        return;
      case MPackage.CELL__ELEMENT_ID:
        setElementId(ELEMENT_ID_EDEFAULT);
        return;
      case MPackage.CELL__PERSISTED_STATE:
        getPersistedState().clear();
        return;
      case MPackage.CELL__TAGS:
        getTags().clear();
        return;
      case MPackage.CELL__CONTRIBUTOR_URI:
        setContributorURI(CONTRIBUTOR_URI_EDEFAULT);
        return;
      case MPackage.CELL__TRANSIENT_DATA:
        getTransientData().clear();
        return;
      case MPackage.CELL__CONTRIBUTION_URI:
        setContributionURI(CONTRIBUTION_URI_EDEFAULT);
        return;
      case MPackage.CELL__OBJECT:
        setObject(OBJECT_EDEFAULT);
        return;
      case MPackage.CELL__WIDGET:
        setWidget(WIDGET_EDEFAULT);
        return;
      case MPackage.CELL__RENDERER:
        setRenderer(RENDERER_EDEFAULT);
        return;
      case MPackage.CELL__TO_BE_RENDERED:
        setToBeRendered(TO_BE_RENDERED_EDEFAULT);
        return;
      case MPackage.CELL__ON_TOP:
        setOnTop(ON_TOP_EDEFAULT);
        return;
      case MPackage.CELL__VISIBLE:
        setVisible(VISIBLE_EDEFAULT);
        return;
      case MPackage.CELL__PARENT:
        setParent((MElementContainer<MUIElement>)null);
        return;
      case MPackage.CELL__CONTAINER_DATA:
        setContainerData(CONTAINER_DATA_EDEFAULT);
        return;
      case MPackage.CELL__CUR_SHARED_REF:
        setCurSharedRef((MPlaceholder)null);
        return;
      case MPackage.CELL__VISIBLE_WHEN:
        setVisibleWhen((MExpression)null);
        return;
      case MPackage.CELL__ACCESSIBILITY_PHRASE:
        setAccessibilityPhrase(ACCESSIBILITY_PHRASE_EDEFAULT);
        return;
      case MPackage.CELL__CHILDREN:
        getChildren().clear();
        return;
      case MPackage.CELL__SELECTED_ELEMENT:
        setSelectedElement((MCell)null);
        return;
      case MPackage.CELL__HANDLERS:
        getHandlers().clear();
        return;
      case MPackage.CELL__POPUP_MENU:
        setPopupMenu((MPopupMenu)null);
        return;
      case MPackage.CELL__EXPANDED:
        setExpanded(EXPANDED_EDEFAULT);
        return;
    }
    super.eUnset(featureID);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public boolean eIsSet(int featureID) {
    switch (featureID) {
      case MPackage.CELL__LABEL:
        return LABEL_EDEFAULT == null ? label != null : !LABEL_EDEFAULT.equals(label);
      case MPackage.CELL__ICON_URI:
        return ICON_URI_EDEFAULT == null ? iconURI != null : !ICON_URI_EDEFAULT.equals(iconURI);
      case MPackage.CELL__TOOLTIP:
        return TOOLTIP_EDEFAULT == null ? tooltip != null : !TOOLTIP_EDEFAULT.equals(tooltip);
      case MPackage.CELL__LOCALIZED_LABEL:
        return LOCALIZED_LABEL_EDEFAULT == null ? getLocalizedLabel() != null : !LOCALIZED_LABEL_EDEFAULT.equals(getLocalizedLabel());
      case MPackage.CELL__LOCALIZED_TOOLTIP:
        return LOCALIZED_TOOLTIP_EDEFAULT == null ? getLocalizedTooltip() != null : !LOCALIZED_TOOLTIP_EDEFAULT.equals(getLocalizedTooltip());
      case MPackage.CELL__CONTEXT:
        return CONTEXT_EDEFAULT == null ? context != null : !CONTEXT_EDEFAULT.equals(context);
      case MPackage.CELL__VARIABLES:
        return variables != null && !variables.isEmpty();
      case MPackage.CELL__PROPERTIES:
        return properties != null && !properties.isEmpty();
      case MPackage.CELL__ELEMENT_ID:
        return ELEMENT_ID_EDEFAULT == null ? elementId != null : !ELEMENT_ID_EDEFAULT.equals(elementId);
      case MPackage.CELL__PERSISTED_STATE:
        return persistedState != null && !persistedState.isEmpty();
      case MPackage.CELL__TAGS:
        return tags != null && !tags.isEmpty();
      case MPackage.CELL__CONTRIBUTOR_URI:
        return CONTRIBUTOR_URI_EDEFAULT == null ? contributorURI != null : !CONTRIBUTOR_URI_EDEFAULT.equals(contributorURI);
      case MPackage.CELL__TRANSIENT_DATA:
        return transientData != null && !transientData.isEmpty();
      case MPackage.CELL__CONTRIBUTION_URI:
        return CONTRIBUTION_URI_EDEFAULT == null ? contributionURI != null : !CONTRIBUTION_URI_EDEFAULT.equals(contributionURI);
      case MPackage.CELL__OBJECT:
        return OBJECT_EDEFAULT == null ? object != null : !OBJECT_EDEFAULT.equals(object);
      case MPackage.CELL__WIDGET:
        return WIDGET_EDEFAULT == null ? widget != null : !WIDGET_EDEFAULT.equals(widget);
      case MPackage.CELL__RENDERER:
        return RENDERER_EDEFAULT == null ? renderer != null : !RENDERER_EDEFAULT.equals(renderer);
      case MPackage.CELL__TO_BE_RENDERED:
        return toBeRendered != TO_BE_RENDERED_EDEFAULT;
      case MPackage.CELL__ON_TOP:
        return onTop != ON_TOP_EDEFAULT;
      case MPackage.CELL__VISIBLE:
        return visible != VISIBLE_EDEFAULT;
      case MPackage.CELL__PARENT:
        return getParent() != null;
      case MPackage.CELL__CONTAINER_DATA:
        return CONTAINER_DATA_EDEFAULT == null ? containerData != null : !CONTAINER_DATA_EDEFAULT.equals(containerData);
      case MPackage.CELL__CUR_SHARED_REF:
        return curSharedRef != null;
      case MPackage.CELL__VISIBLE_WHEN:
        return visibleWhen != null;
      case MPackage.CELL__ACCESSIBILITY_PHRASE:
        return ACCESSIBILITY_PHRASE_EDEFAULT == null ? accessibilityPhrase != null : !ACCESSIBILITY_PHRASE_EDEFAULT.equals(accessibilityPhrase);
      case MPackage.CELL__LOCALIZED_ACCESSIBILITY_PHRASE:
        return LOCALIZED_ACCESSIBILITY_PHRASE_EDEFAULT == null ? getLocalizedAccessibilityPhrase() != null : !LOCALIZED_ACCESSIBILITY_PHRASE_EDEFAULT.equals(getLocalizedAccessibilityPhrase());
      case MPackage.CELL__CHILDREN:
        return children != null && !children.isEmpty();
      case MPackage.CELL__SELECTED_ELEMENT:
        return selectedElement != null;
      case MPackage.CELL__HANDLERS:
        return handlers != null && !handlers.isEmpty();
      case MPackage.CELL__POPUP_MENU:
        return popupMenu != null;
      case MPackage.CELL__EXPANDED:
        return expanded != EXPANDED_EDEFAULT;
    }
    return super.eIsSet(featureID);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public int eBaseStructuralFeatureID(int derivedFeatureID, Class<?> baseClass) {
    if (baseClass == MContext.class) {
      switch (derivedFeatureID) {
        case MPackage.CELL__CONTEXT: return UiPackageImpl.CONTEXT__CONTEXT;
        case MPackage.CELL__VARIABLES: return UiPackageImpl.CONTEXT__VARIABLES;
        case MPackage.CELL__PROPERTIES: return UiPackageImpl.CONTEXT__PROPERTIES;
        default: return -1;
      }
    }
    if (baseClass == MApplicationElement.class) {
      switch (derivedFeatureID) {
        case MPackage.CELL__ELEMENT_ID: return ApplicationPackageImpl.APPLICATION_ELEMENT__ELEMENT_ID;
        case MPackage.CELL__PERSISTED_STATE: return ApplicationPackageImpl.APPLICATION_ELEMENT__PERSISTED_STATE;
        case MPackage.CELL__TAGS: return ApplicationPackageImpl.APPLICATION_ELEMENT__TAGS;
        case MPackage.CELL__CONTRIBUTOR_URI: return ApplicationPackageImpl.APPLICATION_ELEMENT__CONTRIBUTOR_URI;
        case MPackage.CELL__TRANSIENT_DATA: return ApplicationPackageImpl.APPLICATION_ELEMENT__TRANSIENT_DATA;
        default: return -1;
      }
    }
    if (baseClass == MContribution.class) {
      switch (derivedFeatureID) {
        case MPackage.CELL__CONTRIBUTION_URI: return ApplicationPackageImpl.CONTRIBUTION__CONTRIBUTION_URI;
        case MPackage.CELL__OBJECT: return ApplicationPackageImpl.CONTRIBUTION__OBJECT;
        default: return -1;
      }
    }
    if (baseClass == MUIElement.class) {
      switch (derivedFeatureID) {
        case MPackage.CELL__WIDGET: return UiPackageImpl.UI_ELEMENT__WIDGET;
        case MPackage.CELL__RENDERER: return UiPackageImpl.UI_ELEMENT__RENDERER;
        case MPackage.CELL__TO_BE_RENDERED: return UiPackageImpl.UI_ELEMENT__TO_BE_RENDERED;
        case MPackage.CELL__ON_TOP: return UiPackageImpl.UI_ELEMENT__ON_TOP;
        case MPackage.CELL__VISIBLE: return UiPackageImpl.UI_ELEMENT__VISIBLE;
        case MPackage.CELL__PARENT: return UiPackageImpl.UI_ELEMENT__PARENT;
        case MPackage.CELL__CONTAINER_DATA: return UiPackageImpl.UI_ELEMENT__CONTAINER_DATA;
        case MPackage.CELL__CUR_SHARED_REF: return UiPackageImpl.UI_ELEMENT__CUR_SHARED_REF;
        case MPackage.CELL__VISIBLE_WHEN: return UiPackageImpl.UI_ELEMENT__VISIBLE_WHEN;
        case MPackage.CELL__ACCESSIBILITY_PHRASE: return UiPackageImpl.UI_ELEMENT__ACCESSIBILITY_PHRASE;
        case MPackage.CELL__LOCALIZED_ACCESSIBILITY_PHRASE: return UiPackageImpl.UI_ELEMENT__LOCALIZED_ACCESSIBILITY_PHRASE;
        default: return -1;
      }
    }
    if (baseClass == MElementContainer.class) {
      switch (derivedFeatureID) {
        case MPackage.CELL__CHILDREN: return UiPackageImpl.ELEMENT_CONTAINER__CHILDREN;
        case MPackage.CELL__SELECTED_ELEMENT: return UiPackageImpl.ELEMENT_CONTAINER__SELECTED_ELEMENT;
        default: return -1;
      }
    }
    if (baseClass == MHandlerContainer.class) {
      switch (derivedFeatureID) {
        case MPackage.CELL__HANDLERS: return CommandsPackageImpl.HANDLER_CONTAINER__HANDLERS;
        default: return -1;
      }
    }
    return super.eBaseStructuralFeatureID(derivedFeatureID, baseClass);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public int eDerivedStructuralFeatureID(int baseFeatureID, Class<?> baseClass) {
    if (baseClass == MContext.class) {
      switch (baseFeatureID) {
        case UiPackageImpl.CONTEXT__CONTEXT: return MPackage.CELL__CONTEXT;
        case UiPackageImpl.CONTEXT__VARIABLES: return MPackage.CELL__VARIABLES;
        case UiPackageImpl.CONTEXT__PROPERTIES: return MPackage.CELL__PROPERTIES;
        default: return -1;
      }
    }
    if (baseClass == MApplicationElement.class) {
      switch (baseFeatureID) {
        case ApplicationPackageImpl.APPLICATION_ELEMENT__ELEMENT_ID: return MPackage.CELL__ELEMENT_ID;
        case ApplicationPackageImpl.APPLICATION_ELEMENT__PERSISTED_STATE: return MPackage.CELL__PERSISTED_STATE;
        case ApplicationPackageImpl.APPLICATION_ELEMENT__TAGS: return MPackage.CELL__TAGS;
        case ApplicationPackageImpl.APPLICATION_ELEMENT__CONTRIBUTOR_URI: return MPackage.CELL__CONTRIBUTOR_URI;
        case ApplicationPackageImpl.APPLICATION_ELEMENT__TRANSIENT_DATA: return MPackage.CELL__TRANSIENT_DATA;
        default: return -1;
      }
    }
    if (baseClass == MContribution.class) {
      switch (baseFeatureID) {
        case ApplicationPackageImpl.CONTRIBUTION__CONTRIBUTION_URI: return MPackage.CELL__CONTRIBUTION_URI;
        case ApplicationPackageImpl.CONTRIBUTION__OBJECT: return MPackage.CELL__OBJECT;
        default: return -1;
      }
    }
    if (baseClass == MUIElement.class) {
      switch (baseFeatureID) {
        case UiPackageImpl.UI_ELEMENT__WIDGET: return MPackage.CELL__WIDGET;
        case UiPackageImpl.UI_ELEMENT__RENDERER: return MPackage.CELL__RENDERER;
        case UiPackageImpl.UI_ELEMENT__TO_BE_RENDERED: return MPackage.CELL__TO_BE_RENDERED;
        case UiPackageImpl.UI_ELEMENT__ON_TOP: return MPackage.CELL__ON_TOP;
        case UiPackageImpl.UI_ELEMENT__VISIBLE: return MPackage.CELL__VISIBLE;
        case UiPackageImpl.UI_ELEMENT__PARENT: return MPackage.CELL__PARENT;
        case UiPackageImpl.UI_ELEMENT__CONTAINER_DATA: return MPackage.CELL__CONTAINER_DATA;
        case UiPackageImpl.UI_ELEMENT__CUR_SHARED_REF: return MPackage.CELL__CUR_SHARED_REF;
        case UiPackageImpl.UI_ELEMENT__VISIBLE_WHEN: return MPackage.CELL__VISIBLE_WHEN;
        case UiPackageImpl.UI_ELEMENT__ACCESSIBILITY_PHRASE: return MPackage.CELL__ACCESSIBILITY_PHRASE;
        case UiPackageImpl.UI_ELEMENT__LOCALIZED_ACCESSIBILITY_PHRASE: return MPackage.CELL__LOCALIZED_ACCESSIBILITY_PHRASE;
        default: return -1;
      }
    }
    if (baseClass == MElementContainer.class) {
      switch (baseFeatureID) {
        case UiPackageImpl.ELEMENT_CONTAINER__CHILDREN: return MPackage.CELL__CHILDREN;
        case UiPackageImpl.ELEMENT_CONTAINER__SELECTED_ELEMENT: return MPackage.CELL__SELECTED_ELEMENT;
        default: return -1;
      }
    }
    if (baseClass == MHandlerContainer.class) {
      switch (baseFeatureID) {
        case CommandsPackageImpl.HANDLER_CONTAINER__HANDLERS: return MPackage.CELL__HANDLERS;
        default: return -1;
      }
    }
    return super.eDerivedStructuralFeatureID(baseFeatureID, baseClass);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public int eDerivedOperationID(int baseOperationID, Class<?> baseClass) {
    if (baseClass == MContext.class) {
      switch (baseOperationID) {
        default: return -1;
      }
    }
    if (baseClass == MApplicationElement.class) {
      switch (baseOperationID) {
        default: return -1;
      }
    }
    if (baseClass == MContribution.class) {
      switch (baseOperationID) {
        default: return -1;
      }
    }
    if (baseClass == MUIElement.class) {
      switch (baseOperationID) {
        case UiPackageImpl.UI_ELEMENT___UPDATE_LOCALIZATION: return MPackage.CELL___UPDATE_LOCALIZATION;
        default: return -1;
      }
    }
    if (baseClass == MElementContainer.class) {
      switch (baseOperationID) {
        default: return -1;
      }
    }
    if (baseClass == MHandlerContainer.class) {
      switch (baseOperationID) {
        default: return -1;
      }
    }
    return super.eDerivedOperationID(baseOperationID, baseClass);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Object eInvoke(int operationID, EList<?> arguments) throws InvocationTargetException {
    switch (operationID) {
      case MPackage.CELL___UPDATE_LOCALIZATION:
        updateLocalization();
        return null;
    }
    return super.eInvoke(operationID, arguments);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public String toString() {
    if (eIsProxy()) return super.toString();

    StringBuilder result = new StringBuilder(super.toString());
    result.append(" (label: ");
    result.append(label);
    result.append(", iconURI: ");
    result.append(iconURI);
    result.append(", tooltip: ");
    result.append(tooltip);
    result.append(", context: ");
    result.append(context);
    result.append(", variables: ");
    result.append(variables);
    result.append(", elementId: ");
    result.append(elementId);
    result.append(", tags: ");
    result.append(tags);
    result.append(", contributorURI: ");
    result.append(contributorURI);
    result.append(", contributionURI: ");
    result.append(contributionURI);
    result.append(", object: ");
    result.append(object);
    result.append(", widget: ");
    result.append(widget);
    result.append(", renderer: ");
    result.append(renderer);
    result.append(", toBeRendered: ");
    result.append(toBeRendered);
    result.append(", onTop: ");
    result.append(onTop);
    result.append(", visible: ");
    result.append(visible);
    result.append(", containerData: ");
    result.append(containerData);
    result.append(", accessibilityPhrase: ");
    result.append(accessibilityPhrase);
    result.append(", expanded: ");
    result.append(expanded);
    result.append(')');
    return result.toString();
  }

} //CellImpl
