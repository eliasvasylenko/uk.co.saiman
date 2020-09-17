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
 * This file is part of uk.co.saiman.eclipse.fx.
 *
 * uk.co.saiman.eclipse.fx is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.eclipse.fx is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.eclipse.ui.fx.impl;

import static java.util.stream.Collectors.toSet;
import static uk.co.saiman.eclipse.ui.fx.impl.UIAddon.findMethod;
import static uk.co.saiman.eclipse.ui.fx.impl.UIAddon.resolveRequestor;

import java.util.Set;

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.internal.di.MethodRequestor;

import javafx.scene.input.ClipboardContent;
import uk.co.saiman.eclipse.ui.BeginTransfer;
import uk.co.saiman.eclipse.ui.Remove;
import uk.co.saiman.eclipse.ui.TransferFormat;
import uk.co.saiman.eclipse.ui.TransferMode;
import uk.co.saiman.eclipse.ui.TransferSource;
import uk.co.saiman.eclipse.ui.fx.ClipboardService;

public class TransferCellOut {
  private final Runnable remove;

  private final Set<TransferMode> transferModes;
  private final ClipboardContent clipboardContent;

  public TransferCellOut(Object object, IEclipseContext context) {
    this.clipboardContent = new ClipboardContent();

    MethodRequestor beginRequestor = resolveBeginRequestor(object, context);
    if (beginRequestor == null) {
      this.transferModes = Set.of();
      this.remove = () -> {};
      return;
    }
    Object requestorResult = beginRequestor.execute();
    if (requestorResult == null || !(requestorResult instanceof TransferSource)) {
      this.transferModes = Set.of();
      this.remove = () -> {};
      return;
    }
    TransferSource transferSource = (TransferSource) requestorResult;

    ClipboardService clipboards = context.get(ClipboardService.class);
    transferSource
        .getTransferFormats()
        .forEach(format -> putValue(transferSource, clipboards, format));

    MethodRequestor removeRequestor = resolveRemoveRequestor(object, context);
    this.remove = removeRequestor != null ? removeRequestor::execute : null;

    // determine available transfer modes
    this.transferModes = transferSource
        .getTransferFormats()
        .map(TransferFormat::transferModes)
        .flatMap(Set::stream)
        .filter(mode -> removeRequestor != null || !mode.isDestructive())
        .collect(toSet());
  }

  private <T> void putValue(
      TransferSource transferSource,
      ClipboardService clipboards,
      TransferFormat<T> format) {
    clipboards.putValue(clipboardContent, format.dataFormat(), transferSource.getValue(format));
  }

  private static MethodRequestor resolveRemoveRequestor(Object object, IEclipseContext context) {
    return resolveRequestor(
        object,
        context,
        findMethod(object.getClass(), Remove.class, false),
        null,
        true,
        false);
  }

  private static MethodRequestor resolveBeginRequestor(Object object, IEclipseContext context) {
    return resolveRequestor(
        object,
        context,
        findMethod(object.getClass(), BeginTransfer.class, false),
        null,
        true,
        false);
  }

  public Set<TransferMode> supportedTransferModes() {
    return transferModes;
  }

  public void handle(TransferMode transferMode) {
    if (!transferModes.contains(transferMode)) {
      throw new IllegalArgumentException("Unsupported transfer mode " + transferMode);
    }
    if (transferMode.isDestructive()) {
      remove.run();
    }
  }

  public ClipboardContent getClipboardContent() {
    return clipboardContent;
  }
}
