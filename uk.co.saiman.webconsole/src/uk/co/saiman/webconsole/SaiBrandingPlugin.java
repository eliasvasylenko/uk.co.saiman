/*
 * Copyright (C) 2017 Scientific Analysis Instruments Limited <contact@saiman.co.uk>
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
 * This file is part of uk.co.saiman.webconsole.
 *
 * uk.co.saiman.webconsole is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.webconsole is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
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
