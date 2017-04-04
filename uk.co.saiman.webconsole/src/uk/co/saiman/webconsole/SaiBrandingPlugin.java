package uk.co.saiman.webconsole;

import org.apache.felix.webconsole.BrandingPlugin;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.http.HttpService;
import org.osgi.service.http.NamespaceException;

@Component(property = Constants.SERVICE_RANKING + ":Integer=" + 2000)
public class SaiBrandingPlugin implements BrandingPlugin {
	private static final String APP_ROOT_PATH = "/system/console";
	private static final String RESOURCE_ALIAS_PATH = "/sai-res";
	private static final String RESOURCE_NAME_PATH = "/res";

	private static final String VENDOR_NAME = "Scientific Analysis Instruments";
	private static final String VENDOR_URL = "http://saiman.co.uk/";

	@Reference
	HttpService service;

	@Activate
	void activate() throws NamespaceException {
		service.registerResources(APP_ROOT_PATH + RESOURCE_ALIAS_PATH, RESOURCE_NAME_PATH, null);
	}

	@Override
	public String getBrandName() {
		return VENDOR_NAME;
	}

	@Override
	public String getFavIcon() {
		return RESOURCE_ALIAS_PATH + "/favicon.png";
	}

	@Override
	public String getMainStyleSheet() {
		return RESOURCE_ALIAS_PATH + "/sai-web-console.css";
	}

	@Override
	public String getProductImage() {
		return RESOURCE_ALIAS_PATH + "/sai-logo.svg";
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
		return RESOURCE_ALIAS_PATH + "/sai-logo.svg";
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
