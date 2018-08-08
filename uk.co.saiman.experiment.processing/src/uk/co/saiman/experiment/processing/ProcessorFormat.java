package uk.co.saiman.experiment.processing;

import static uk.co.saiman.data.format.MediaType.APPLICATION_TYPE;
import static uk.co.saiman.data.format.RegistrationTree.VENDOR;

import java.io.IOException;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.util.stream.Stream;

import uk.co.saiman.data.format.DataFormat;
import uk.co.saiman.data.format.MediaType;
import uk.co.saiman.data.format.Payload;
import uk.co.saiman.experiment.state.StateMap;

public class ProcessorFormat implements DataFormat<Processor<?>> {
  public static final int VERSION = 1;

  public static final MediaType MEDIA_TYPE = new MediaType(
      APPLICATION_TYPE,
      "saiman.processor.v" + VERSION,
      VENDOR).withSuffix("json");

  private final DataFormat<StateMap> stateMapFormat;
  private final ProcessorService processorService;

  public ProcessorFormat(DataFormat<StateMap> stateMapFormat, ProcessorService processorService) {
    this.stateMapFormat = stateMapFormat;
    this.processorService = processorService;
  }

  @Override
  public String getExtension() {
    return "processor";
  }

  @Override
  public Stream<MediaType> getMediaTypes() {
    return Stream.of(MEDIA_TYPE);
  }

  @Override
  public Payload<? extends Processor<?>> load(ReadableByteChannel inputChannel) throws IOException {
    return new Payload<>(processorService.loadProcessor(stateMapFormat.load(inputChannel).data));
  }

  @Override
  public void save(WritableByteChannel outputChannel, Payload<? extends Processor<?>> payload)
      throws IOException {
    stateMapFormat.save(outputChannel, new Payload<>(payload.data.getState()));
  }
}
