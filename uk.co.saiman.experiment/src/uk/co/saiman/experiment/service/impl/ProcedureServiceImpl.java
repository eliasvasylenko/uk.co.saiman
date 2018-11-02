package uk.co.saiman.experiment.service.impl;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;

import uk.co.saiman.experiment.Procedure;
import uk.co.saiman.experiment.service.ProcedureService;
import uk.co.saiman.osgi.ServiceIndex;
import uk.co.saiman.osgi.ServiceRecord;

@Component
public class ProcedureServiceImpl implements ProcedureService {
  private final ServiceIndex<Procedure<?, ?>, String> procedures;

  @Activate
  public ProcedureServiceImpl(BundleContext context) {
    procedures = ServiceIndex.open(context, Procedure.class.getName());
  }

  @Override
  public Stream<Procedure<?, ?>> procedures() {
    return procedures.objects();
  }

  @Override
  public Procedure<?, ?> getProcedure(String id) {
    return procedures.get(id).get().serviceObject();
  }

  @Override
  public String getId(Procedure<?, ?> procedure) {
    System.out.println(procedure);
    System.out
        .println(
            procedures.records().map(ServiceRecord::serviceObject).collect(Collectors.toList()));
    return procedures.findRecord(procedure).get().id();
  }
}
