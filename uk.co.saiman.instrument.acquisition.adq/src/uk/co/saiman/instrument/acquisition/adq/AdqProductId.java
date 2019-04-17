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
 * This file is part of uk.co.saiman.instrument.acquisition.adq.
 *
 * uk.co.saiman.instrument.acquisition.adq is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.instrument.acquisition.adq is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.instrument.acquisition.adq;

public enum AdqProductId {
  ADQ214(0x0001),
  ADQ114(0x0003),
  ADQ112(0x0005),
  SphinxHS(0x000B),
  SphinxLS(0x000C),
  ADQ108(0x000E),
  ADQDSP(0x000F),
  SphinxAA14(0x0011),
  SphinxAA16(0x0012),
  ADQ412(0x0014),
  ADQ212(0x0015),
  SphinxAA_LS2(0x0016),
  SphinxHS_LS2(0x0017),
  SDR14(0x001B),
  ADQ1600(0x001C),
  SphinxXT(0x001D),
  ADQ208(0x001E),
  DSU(0x001F);

  private final int pid;

  AdqProductId(int id) {
    this.pid = id;
  }

  public int getPid() {
    return pid;
  }
}
