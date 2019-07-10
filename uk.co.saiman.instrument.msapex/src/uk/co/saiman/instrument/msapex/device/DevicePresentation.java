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
 * This file is part of uk.co.saiman.instrument.msapex.
 *
 * uk.co.saiman.instrument.msapex is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.instrument.msapex is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.instrument.msapex.device;

public interface DevicePresentation {
  String getLabel();

  String getIconURI();

  String getDeviceId();

  /*
   * TODO A wrapper around a device, gives a localized name, an icon, and possibly
   * links to a part to display the device (or possibly a component within e.g. a
   * composite part).
   * 
   * Does this concept carry it's own weight? Do we need a "central" listing of
   * devices and their labels/icons?
   * 
   * Alternatively we can just have the instrument view and device parts
   * customized separately. Each device in the instrument view table customizes
   * its own cell in the view by setting an icon and label. Each part is unique,
   * some devices might not have a part, some parts might combine multiple
   * devices, etc.
   * 
   * Rather than having e.g. a single "Acquisition" view and a single "Sample"
   * view, maybe it's better for each (collection of) device(s) to provide custom
   * views? Or at least just to re-use views only when appropriate.
   * 
   * Pros:
   * 
   * + No need for complex "selection" mechanisms to decide which devices are to
   * be displayed in a part at any given time.
   * 
   * + Easy to add custom features specific to a particular device implementation
   * without the need to predict in advance which parts of the UI are extensible
   * and design mechanisms to contribute to them.
   * 
   * + No peculiar/arbitrary restriction of "one device to one view".
   * 
   * Cons:
   * 
   * - If we have some new functionality which should probably apply to all basic
   * "acquisition" views, there's no way to add it to them all as they're all
   * separate.
   * 
   * - Perhaps more difficult to share code/work/resources.
   * 
   * Maybe we can have the best of both worlds by allowing to "compose" common UI
   * elements from other places, then when we have new generally applicable
   * behavior, hopefully we can add it to one of those composeable elements so
   * existing part implementations will benefit for free. For example, readback
   * controls, stage diagram, acquisition graph...
   * 
   * Still committed to an open workspace only being for (at most) one instrument.
   * From a UI perspective that's just simpler (and it is ONLY a limitation of the
   * UI, the underlying framework can host many). An "instrument" determines what
   * experiment types are available, and what views are available over both
   * devices and experiments. Do we want this to be implicit in the organisation
   * of the application and which parts are deployed, or explicit in some
   * identified unit of configuration or deployment?
   * 
   * If it's implicit we can't do things like switch to a different instrument
   * easily...
   * 
   * Is there value in richer configuration/specification of relationships between
   * devices? (e.g. defining physical relationships, optics configuration, blah
   * blah.)
   * 
   * Since a hardware component may be reused in different contexts (e.g. a single
   * stage supporting different types of analysis hardware) and since the
   * relationships between devices are only useful WRT how they facilitate the
   * actual physical process of an experiment, perhaps these relationships should
   * be defined by the experiment procedures/instructions, e.g. the
   * Conductor/Executors materialize a description of the participating hardware.
   * 
   * So let's say a workspace is define by the configurations deployed in it,
   * which result in a different set of devices and views being instantiated.
   * Simple as that.
   * 
   * 
   * 
   * 
   * But then if there's no central concept of an "instrument", how do we collect
   * info about the hardware involved in an experiment without duplicating it for
   * every step? Where would this info fit into the experiment model when the
   * results are collected and saved?
   * 
   * Answer: each participating experiment EXECUTOR would have the opportunity to
   * output some kind of result into the global scope, and would only do so once.
   * So e.g. they can dump any info about *constant* instrument configuration.
   * 
   * TODO executor has a StateMap just like an instruction. If the executor
   * StateMap is changed, the results are invalidated (i.e. can't run part of an
   * experiment with one executor config and part with another, has to be all the
   * same).
   * 
   * TODO think about how to manage metadata attached to a conducted/completed
   * experiment, both generated (e.g. output info about the hardware used in an
   * experiment) and manual (e.g. notes about the preparation of a sample)
   * 
   * Possibly some mechanism for steps to collaborate over their configuration
   * that is independent of actually conducting them?
   */
}
