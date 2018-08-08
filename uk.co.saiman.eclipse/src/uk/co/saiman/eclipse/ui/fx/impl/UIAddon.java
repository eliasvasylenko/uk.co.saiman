package uk.co.saiman.eclipse.ui.fx.impl;

import javax.annotation.PostConstruct;

import org.eclipse.e4.core.contexts.IEclipseContext;

import uk.co.saiman.eclipse.ui.fx.ClipboardService;
import uk.co.saiman.eclipse.ui.fx.TreeService;

public class UIAddon {
  @PostConstruct
  public void initialize(
      IEclipseContext context,
      TreeServiceImpl treeService,
      ClipboardServiceImpl clipboardService) {
    context.set(ClipboardService.class, clipboardService);
    context.set(TreeService.class, treeService);
  }
}
