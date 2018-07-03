package uk.co.saiman.acquisition.adq.impl;

import static uk.co.saiman.acquisition.adq.ProductFamily.K7;
import static uk.co.saiman.acquisition.adq.ProductFamily.V5;
import static uk.co.saiman.acquisition.adq.ProductFamily.V6;
import static uk.co.saiman.acquisition.adq.RevisionInformation.LOCAL_COPY;
import static uk.co.saiman.acquisition.adq.RevisionInformation.MIXED;
import static uk.co.saiman.acquisition.adq.RevisionInformation.SVN_MANAGED;
import static uk.co.saiman.acquisition.adq.RevisionInformation.SVN_UPDATED;

import java.nio.ByteBuffer;

import org.osgi.service.component.annotations.Component;

import com.sun.jna.Library;
import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.Pointer;

import uk.co.saiman.acquisition.adq.FirmwareRevision;
import uk.co.saiman.acquisition.adq.FpgaTarget;
import uk.co.saiman.acquisition.adq.ProductFamily;
import uk.co.saiman.acquisition.adq.RevisionInformation;

@Component(service = AdqDeviceManager.class, immediate = true)
public class AdqDeviceManager {
  interface AdqLib extends Library {
    Pointer CreateADQControlUnit();

    int ADQControlUnit_FindDevices();

    int ADQ_GetProductFamily(Pointer controlUnit, int deviceNumber, Pointer familyInt);

    Pointer ADQ_GetRevision(Pointer controlUnit, int deviceNumber);
  }

  private static final AdqLib LIB;

  static {
    LIB = Native.loadLibrary("adq", AdqLib.class);
  }

  private final Pointer controlUnit;
  private final int numberOfDevices;

  public AdqDeviceManager() {
    controlUnit = LIB.CreateADQControlUnit();
    numberOfDevices = 0;// LIB.ADQControlUnit_FindDevices();
  }

  public int getNumberOfDevices() {
    return numberOfDevices;
  }

  public ProductFamily getProductFamily(int deviceNumber) {
    Pointer family = new Memory(Integer.BYTES).getPointer(0);

    LIB.ADQ_GetProductFamily(controlUnit, deviceNumber, family);
    int familyId = family.getInt(0);
    switch (familyId) {
    case 5:
      return V5;
    case 6:
      return V6;
    case 7:
      return K7;
    default:
      throw new IllegalStateException("Unexpected product family id " + familyId);
    }
  }

  public FirmwareRevision getFirmwareRevisionFpga(int deviceNumber, FpgaTarget target) {
    ProductFamily productFamily = getProductFamily(deviceNumber);

    int offset;
    if (target == FpgaTarget.ALG_FPGA) {
      if (productFamily == V6) {
        offset = 0;
      } else {
        offset = 3;
      }
    } else {
      if (productFamily == V6) {
        throw new IllegalArgumentException(
            "Product family " + V6 + " does not support FPGA target " + target);
      }
      offset = 0;
    }

    Pointer revision = LIB.ADQ_GetRevision(controlUnit, deviceNumber);
    int revisionNumber = revision.getInt(0 + offset);
    int svnManaged = revision.getInt(1 + offset);
    int svnUpdated = revision.getInt(2 + offset);
    RevisionInformation revisionInformation = svnManaged == 0
        ? (svnUpdated == 0 ? SVN_UPDATED : SVN_MANAGED)
        : (svnUpdated == 0 ? LOCAL_COPY : MIXED);

    return new FirmwareRevision() {
      @Override
      public int revisionNumber() {
        return revisionNumber;
      }

      @Override
      public RevisionInformation information() {
        return revisionInformation;
      }
    };
  }
}
