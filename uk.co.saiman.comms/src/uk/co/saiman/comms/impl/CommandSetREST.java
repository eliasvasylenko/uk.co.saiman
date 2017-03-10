package uk.co.saiman.comms.impl;

import static java.util.stream.Collectors.toList;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Modified;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

import osgi.enroute.rest.api.REST;
import osgi.enroute.rest.api.RequireRestImplementation;
import uk.co.saiman.comms.CommandDefinition;
import uk.co.saiman.comms.CommandId;
import uk.co.saiman.comms.CommandSet;
import uk.co.saiman.comms.CommsException;

@RequireRestImplementation
@Component(property = REST.ENDPOINT + "=/comms/*")
public class CommandSetREST implements REST {
	public static String NAME_KEY = "name";
	public static String ID_CLASS_KEY = "commandIdClass";
	public static String STATUS_KEY = "status";
	public static String CHANNEL_KEY = "channel";
	public static String BUNDLE_KEY = "registeringBundle";
	public static String BUNDLE_SYMBOLIC_NAME_KEY = "bundleSymbolicName";
	public static String BUNDLE_NAME_KEY = "bundleName";
	public static String BUNDLE_ID_KEY = "bundleId";
	public static String COMMANDS_KEY = "commands";

	@interface Configuration {}

	private Configuration config;

	private Map<CommandSet<?>, Bundle> commandSets;
	@SuppressWarnings("rawtypes")
	private ServiceTracker<CommandSet, CommandSet> commandSetTracker;

	@SuppressWarnings("rawtypes")
	@Activate
	void activate(Configuration config, BundleContext context) {
		modified(config);

		commandSets = new LinkedHashMap<>();
		commandSetTracker = new ServiceTracker<>(
				context,
				CommandSet.class,
				new ServiceTrackerCustomizer<CommandSet, CommandSet>() {
					@Override
					public CommandSet<?> addingService(ServiceReference<CommandSet> reference) {
						refreshCommandSets(context);
						return context.getService(reference);
					}

					@Override
					public void modifiedService(ServiceReference<CommandSet> reference, CommandSet service) {
						refreshCommandSets(context);
					}

					@Override
					public void removedService(ServiceReference<CommandSet> reference, CommandSet service) {
						refreshCommandSets(context);
					}
				});
		commandSetTracker.open();

		refreshCommandSets(context);
	}

	@Deactivate
	void deactivate() {
		commandSetTracker.close();
	}

	@Modified
	void modified(Configuration config) {
		this.config = config;
	}

	private synchronized void refreshCommandSets(BundleContext context) {
		commandSets.clear();
		try {
			for (@SuppressWarnings("rawtypes")
			ServiceReference<CommandSet> commandSet : context.getServiceReferences(CommandSet.class, null)) {
				commandSets.put(context.getService(commandSet), commandSet.getBundle());
			}
		} catch (InvalidSyntaxException e) {
			throw new RuntimeException(e);
		}
	}

	public List<String> getCommandSets() {
		return commandSets.keySet().stream().map(CommandSet::getName).collect(toList());
	}

	public List<Map<String, Object>> getCommandSetInfo() {
		return commandSets.keySet().stream().map(this::getCommandSetInfoImpl).collect(toList());
	}

	public Map<String, Object> getCommandSetInfo(String name) {
		return getCommandSetInfoImpl(getNamedCommandSet(name).orElseThrow(() -> commandSetNotFound(name)));
	}

	private <T extends CommandId> Map<String, Object> getCommandSetInfoImpl(CommandSet<T> commandSet) {
		Map<String, Object> info = new HashMap<>();

		info.put(NAME_KEY, commandSet.getName());
		info.put(ID_CLASS_KEY, commandSet.getCommandIdClass().toString());
		info.put(STATUS_KEY, commandSet.getChannel().getStatus());
		info.put(CHANNEL_KEY, commandSet.getChannel().getDescriptiveName());
		info.put(BUNDLE_KEY, getBundleInfoImpl(commandSet));
		info.put(COMMANDS_KEY, commandSet.getCommands().map(Objects::toString).collect(toList()));

		return info;
	}

	private Object getBundleInfoImpl(CommandSet<?> commandSet) {
		Bundle bundle = commandSets.get(commandSet);

		Map<String, Object> info = new HashMap<>();

		info.put(BUNDLE_NAME_KEY, bundle.getHeaders().get(Constants.BUNDLE_NAME));
		info.put(BUNDLE_SYMBOLIC_NAME_KEY, bundle.getSymbolicName());
		info.put(BUNDLE_ID_KEY, bundle.getBundleId());

		return info;
	}

	public List<String> getCommands(String name) {
		return getNamedCommandSet(name)
				.orElseThrow(() -> commandSetNotFound(name))
				.getCommands()
				.map(Objects::toString)
				.collect(toList());
	}

	public Map<String, Object> getCommandInfo(String commandSetName, String commandName) {
		CommandSet<?> commandSet = getNamedCommandSet(commandSetName).orElseThrow(() -> commandSetNotFound(commandSetName));

		return getCommandInfoImpl(commandSet, commandName);
	}

	private <T extends CommandId> Map<String, Object> getCommandInfoImpl(CommandSet<T> commandSet, String commandName) {
		return getCommandInfoImpl(
				commandSet.getCommand(
						commandSet.getCommands().filter(c -> c.toString().equals(commandName)).findAny().orElseThrow(
								() -> commandNotFound(commandSet.getName(), commandName))));
	}

	private RuntimeException commandNotFound(String commandSetName, String commandName) {
		return new CommsException("Command not found " + commandSetName + ": " + commandName);
	}

	private Map<String, Object> getCommandInfoImpl(CommandDefinition<?, ?, ?> command) {
		Map<String, Object> info = new HashMap<>();

		info.put("id", command.getId().toString());
		info.put("inputType", command.getInput().objectClass().toString());
		info.put("expectedInputBytes", command.getInput().expectedByteCount());
		info.put("outputType", command.getOutput().objectClass().toString());

		return info;
	}

	private Optional<CommandSet<?>> getNamedCommandSet(String name) {
		return commandSets.keySet().stream().filter(c -> c.getName().equals(name)).findAny();
	}

	private RuntimeException commandSetNotFound(String name) {
		return new CommsException("Command set not found " + name);
	}
}
