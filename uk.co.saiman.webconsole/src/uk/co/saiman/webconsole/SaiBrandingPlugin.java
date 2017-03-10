package uk.co.saiman.webconsole;

import org.apache.felix.webconsole.BrandingPlugin;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.http.HttpService;
import org.osgi.service.http.NamespaceException;

@Component
public class SaiBrandingPlugin implements BrandingPlugin {
	private static final String RESOURCE_ALIAS_PATH = "/system/console/sai/";
	private static final String RESOURCE_NAME_PATH = "/static/sai/";

	private static final String VENDOR_NAME = "Scientific Analysis Instruments";
	private static final String VENDOR_URL = "http://saiman.co.uk/";

	@Reference
	HttpService service;

	@Activate
	void activate() throws NamespaceException {
		registerResource("sai-logo.svg");
		registerResource("sai-web-console.css");
		registerResource("favicon.png");
	}

	private void registerResource(String resource) throws NamespaceException {
		service.registerResources(RESOURCE_ALIAS_PATH + resource, RESOURCE_NAME_PATH + resource, null);
	}

	@Override
	public String getBrandName() {
		return VENDOR_NAME;
	}

	@Override
	public String getFavIcon() {
		return "/sai/favicon.png";
	}

	@Override
	public String getMainStyleSheet() {
		return "/sai/sai-web-console.css";
	}

	@Override
	public String getProductImage() {
		return "/sai/sai-logo.svg";
	}

	@Override
	public String getProductName() {
		return VENDOR_NAME;
	}

	@Override
	public String getProductURL() {
		return VENDOR_URL;
	}

	@Override
	public String getVendorImage() {
		return "/sai/sai-logo.svg";
	}

	@Override
	public String getVendorName() {
		return VENDOR_NAME;
	}

	@Override
	public String getVendorURL() {
		return VENDOR_URL;
	}
}
