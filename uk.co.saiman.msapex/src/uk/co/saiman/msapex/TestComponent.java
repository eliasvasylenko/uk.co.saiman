package uk.co.saiman.msapex;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;

@Component
public class TestComponent {
	@Activate
	public void activate() {
		System.out.println("Test component activating...");
	}
}
