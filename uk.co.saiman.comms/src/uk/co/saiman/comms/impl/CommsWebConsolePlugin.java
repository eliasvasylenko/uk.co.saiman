package uk.co.saiman.comms.impl;

import static uk.co.saiman.instrument.Instrument.INSTRUMENT_CATEGORY;

import java.io.IOException;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.felix.webconsole.SimpleWebConsolePlugin;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;

@Component(
		name = "osgi.enroute.examples.webconsole",
		service = Servlet.class,
		property = "felix.webconsole.label=" + CommsWebConsolePlugin.PLUGIN)
@MultipartConfig(location = "/tmp/comms")
public class CommsWebConsolePlugin extends SimpleWebConsolePlugin {
	private static final long serialVersionUID = 1L;

	final static String PLUGIN = "comms";
	final static String TITLE = "Comms";

	private final String template;

	public CommsWebConsolePlugin() {
		super(PLUGIN, TITLE, INSTRUMENT_CATEGORY, new String[] { "/comms/static/sai/comms.css" });
		template = this.readTemplateFile("/static/sai/comms.html");
	}

	@Activate
	@Override
	public void activate(BundleContext bundleContext) {
		super.activate(bundleContext);
	}

	@Override
	@Deactivate
	public void deactivate() {
		super.deactivate();
	}

	@Override
	protected void renderContent(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.getWriter().append(template);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		System.out.println(req.getParts());
	}
}
