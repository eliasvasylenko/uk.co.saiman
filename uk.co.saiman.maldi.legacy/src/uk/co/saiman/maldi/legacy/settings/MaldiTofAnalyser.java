package uk.co.saiman.maldi.legacy.settings;

import static org.osgi.service.component.annotations.ConfigurationPolicy.REQUIRE;
import static uk.co.saiman.measurement.Quantities.quantityFormat;

import java.util.Optional;

import javax.measure.quantity.Mass;
import javax.measure.quantity.Time;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@Designate(ocd = MaldiTofAnalyser.Settings.class, factory = true)
@Component(service = MaldiTofAnalyser.class, configurationPid = MaldiTofAnalyser.CONFIGURATION_PID, configurationPolicy = REQUIRE, immediate = true)
public class MaldiTofAnalyser {
  @ObjectClassDefinition(name = "Legacy MALDI ToF Analyser", description = "Experiment settings for MALDI ToF acquisition")
  public @interface Settings {
    @AttributeDefinition(name = "ID", description = "Unique identifier of the configuration")
    String id();

    String instrumentToFParametersName();

    String reflectronToFParametersName();

    @AttributeDefinition(name = "Delayed Extraction", description = "Enable delayed extraction settings.")
    boolean delayedExtractionEnabled();

    @AttributeDefinition(name = "Delayed Extraction Focus Mass")
    String delayedExtractrionFocusMass();

    @AttributeDefinition(name = "Beam Dumper", description = "Enable beam dumper settings.")
    boolean beamDumperEnabled();

    @AttributeDefinition(name = "Beam Dumper Mass")
    String beamDumperMass();

    String minimumOperatingMass();

    String maximumOperatingMass();

    boolean gateEnabled();

    String gateMass();

    String gateWidth();

    boolean cidEnabled();

    String cidTime();
  }

  static final String CONFIGURATION_PID = "uk.co.saiman.maldi.legacy.tofanalyser";

  private final String id;
  private final String instrumentToFParametersName;
  private final String reflectronToFParametersName;
  private final DelayedExtraction delayedExtraction;
  private final BeamDumper beamDumper;
  private final OperatingMassRange operatingMassRange;
  private final Gate gate;
  private final CID cid;

  @Activate
  public MaldiTofAnalyser(Settings settings) {
    this.id = settings.id();

    instrumentToFParametersName = settings.instrumentToFParametersName();
    reflectronToFParametersName = settings.reflectronToFParametersName();

    delayedExtraction = settings.delayedExtractionEnabled()
        ? new DelayedExtraction(quantityFormat().parse(settings.delayedExtractrionFocusMass()).asType(Mass.class))
        : null;
    beamDumper = settings.beamDumperEnabled()
        ? new BeamDumper(quantityFormat().parse(settings.beamDumperMass()).asType(Mass.class))
        : null;
    operatingMassRange = new OperatingMassRange(
        quantityFormat().parse(settings.minimumOperatingMass()).asType(Mass.class),
        quantityFormat().parse(settings.maximumOperatingMass()).asType(Mass.class));
    gate = settings.gateEnabled()
        ? new Gate(
            quantityFormat().parse(settings.gateMass()).asType(Mass.class),
            quantityFormat().parse(settings.gateWidth()).asType(Mass.class))
        : null;
    cid = settings.cidEnabled() ? new CID(quantityFormat().parse(settings.cidTime()).asType(Time.class)) : null;
  }

  public String id() {
    return id;
  }

  public String instrumentToFParametersName() {
    return instrumentToFParametersName;
  }

  public String reflectronToFParametersName() {
    return reflectronToFParametersName;
  }

  public Optional<DelayedExtraction> delayedExtractionEnabled() {
    return Optional.ofNullable(delayedExtraction);
  }

  public Optional<BeamDumper> beamDumper() {
    return Optional.ofNullable(beamDumper);
  }

  public OperatingMassRange operatingMassRange() {
    return operatingMassRange;
  }

  public Optional<Gate> gate() {
    return Optional.ofNullable(gate);
  }

  public Optional<CID> cidEnabled() {
    return Optional.ofNullable(cid);
  }
}
