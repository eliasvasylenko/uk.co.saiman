package uk.co.saiman.data.mzml;

import static uk.co.saiman.data.format.MediaType.APPLICATION_TYPE;
import static uk.co.saiman.data.format.RegistrationTree.VENDOR;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

import uk.ac.ebi.jmzml.model.mzml.BinaryDataArray;
import uk.ac.ebi.jmzml.model.mzml.CV;
import uk.ac.ebi.jmzml.model.mzml.CVParam;
import uk.ac.ebi.jmzml.model.mzml.params.BinaryDataArrayCVParam;
import uk.ac.ebi.jmzml.xml.io.MzMLMarshaller;
import uk.ac.ebi.jmzml.xml.io.MzMLUnmarshaller;
import uk.co.saiman.data.format.MediaType;
import uk.co.saiman.data.format.Payload;
import uk.co.saiman.data.format.TextFormat;
import uk.co.saiman.data.spectrum.Spectrum;

public class MzMlSpectrumFormat implements TextFormat<Spectrum> {
  private static final String VERSION = "1.1.0";

  public static final String FILE_EXTENSION = "mzML";
  public static final MediaType MEDIA_TYPE = new MediaType(APPLICATION_TYPE, "mzml.v" + VERSION, VENDOR)
      .withSuffix("xml");

  public MzMlSpectrumFormat() {}

  @Override
  public Stream<MediaType> getMediaTypes() {
    return Stream.of(MEDIA_TYPE);
  }

  @Override
  public Stream<String> getExtensions() {
    return Stream.of(FILE_EXTENSION);
  }

  @Override
  public Payload<? extends Spectrum> decodeString(String string) {
    /*
     * TODO This library only supports loading directly from a File object, which is
     * pretty stupid. There appears to be no simple way around this by subclassing
     * and cleverly overriding one or two methods; direct use of the File object is
     * spread around lots of places, some of them private. But we still don't want
     * to redo all the useful work in this lib, so we dump our text into a temporary
     * file in order to load it...
     */
    try {
      Path tempFile;
      tempFile = Files.createTempFile("mzML-unmarshalling-dump.", ".mzML");
      Files.write(tempFile, string.getBytes());

      var unmarshaller = new MzMLUnmarshaller(tempFile.toFile());
      var spectra = unmarshaller.getSpectrumIDs();

      throw new UnsupportedOperationException();
    } catch (IOException e) {
      throw new UnsupportedOperationException();
    }
  }

  @Override
  public String encodeString(Payload<? extends Spectrum> payload) {
    var cv = new CV();
    cv.setId("MS");
    cv.setFullName("Proteomics Standards Initiative Mass Spectrometry Ontology");
    cv.setVersion("2.1.0");
    cv.setURI("https://github.com/HUPO-PSI/psi-ms-CV/releases/download/4.1.31/psi-ms.obo");

    // define a CVParam for 64 bit precision
    var prec64bit = new BinaryDataArrayCVParam();
    prec64bit.setAccession(BinaryDataArray.MS_FLOAT64BIT_AC);
    prec64bit.setName(BinaryDataArray.MS_FLOAT64BIT_NAME);
    prec64bit.setCv(cv);

    // define a CVParam for 32 bit precision
    var prec32bit = new BinaryDataArrayCVParam();
    prec32bit.setAccession(BinaryDataArray.MS_FLOAT32BIT_AC);
    prec32bit.setName(BinaryDataArray.MS_FLOAT32BIT_NAME);
    prec32bit.setCv(cv);

    // define a CVParam for compression
    var compressed = new BinaryDataArrayCVParam();
    compressed.setAccession(BinaryDataArray.MS_COMPRESSED_AC);
    compressed.setName(BinaryDataArray.MS_COMPRESSED_NAME);
    compressed.setCv(cv);

    // define a CVParam for no compression
    var uncompressed = new BinaryDataArrayCVParam();
    uncompressed.setAccession(BinaryDataArray.MS_UNCOMPRESSED_AC);
    uncompressed.setName(BinaryDataArray.MS_UNCOMPRESSED_NAME);
    uncompressed.setCv(cv);

    var spectrum = payload.data;
    var domainUnits = spectrum.getTimeData().domain().getUnit();
    var rangeUnits = spectrum.getTimeData().range().getUnit();

    var masses = new BinaryDataArray();
    masses.set64BitFloatArrayAsBinaryData(spectrum.getMassData().range().toArray(), false, cv, new CVParam());

    var times = new BinaryDataArray();
    times.set64BitFloatArrayAsBinaryData(spectrum.getMassData().domain().toArray(), false, cv, new CVParam());

    var mzmlSpectrum = new uk.ac.ebi.jmzml.model.mzml.Spectrum();

    mzmlSpectrum.getBinaryDataArrayList().getBinaryDataArray().add(masses);
    mzmlSpectrum.getBinaryDataArrayList().getBinaryDataArray().add(times);

    var marshaller = new MzMLMarshaller();
    return marshaller.marshall(mzmlSpectrum);
  }
}
