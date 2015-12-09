package uk.co.saiman.msapex;

import org.eclipse.fx.ui.services.theme.Theme;
import org.eclipse.fx.ui.theme.AbstractTheme;
import org.osgi.service.component.annotations.Component;

@Component(service = Theme.class)
public class DefaultTheme extends AbstractTheme {
	public DefaultTheme() {
		super("theme.default", "Default theme", DefaultTheme.class.getClassLoader().getResource("css/default.css"));
	}
}
