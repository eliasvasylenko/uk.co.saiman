package uk.co.saiman.acquisition.adq;

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
