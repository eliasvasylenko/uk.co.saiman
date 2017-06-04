/*
 * REST Endpoints
 */
var open_comms_rest_endpoint = '/api/comms/OpenCommsInterface';
var reset_comms_rest_endpoint = '/api/comms/ResetCommsInterface';
var comms_interface_rest_endpoint = '/api/comms/CommsInterfaceInfo';
var command_info_rest_endpoint = '/api/comms/commandInfo';
var command_invocation_rest_endpoint = '/api/comms/commandInvocation';
/*
 * comms_interface Table
 */
var comms_interface_table_body;
var comms_interface_template;

/*
 * Command Table
 */
var command_table;
var command_table_body;
var command_template;
var command_parameter_template;

/*
 * Data
 */
var selected_comms_interface;
var comms_interface_map;
var command_map;

/*
 * State
 */
var statline;
var polling = false;
var filter_text_box;
var filter;

/*
 * Initialization
 */
$(document).ready(() => {
	statline = $('.statline');

	comms_interface_table = $('#comms_interface_table');
	comms_interface_table_body = comms_interface_table.find('tbody');
	comms_interface_template = $('#comms_interface');

	comms_interface_table = initializeSorting(
			comms_interface_table,
			'comms_interface_table');

	commands_container = $('#commands_container');
	command_table = commands_container.find('#command_table');
	command_table_body = command_table.find('tbody');
	command_template = $('#command');
	command_parameter_template = $('#command_parameter');

	command_table = initializeSorting(
			command_table,
			'command_table',
			{
				1: { sorter: false },
				2: { sorter: false },
				3: { sorter: false }
			});

	$('.reloadButton').click(loadCommsInterfaces);
	$('.pollButton').click(switchPolling);
	setPolling(false);

	$('.filterButton').click(() => setFilter());
	$('.filterClear').click(() => setFilter(''));
	filter_text_box = $('.filter');
	filter_text_box.keyup(e => {
		if(e.keyCode == 13)
			setFilter();
	});

	loadCommsInterfaces();
});

function setFilter(filter_text) {
	if (typeof filter_text !== 'undefined') {
		filter_text_box[0].value = filter_text;
	} else {
		filter_text = filter_text_box[0].value;
	}

	filter = filter_text.toLowerCase();

	applyFilter();

	initStaticWidgets();
}

function applyFilter() {
	for (var command_id in command_map) {
		var visible = !filter || command_id.toLowerCase().indexOf(filter) !== -1;
		var node = $(command_map[command_id].node);

		if (visible) {
			node.show();
		} else {
			node.hide();
		}
	}
}

function initializeSorting(table, cookie, headers) {
	return table.tablesorter({
		headers: headers,
		textExtraction: mixedLinksExtraction
	}).bind('sortEnd', e => {
    var t = e.target.config;
    if (t && t.sortList) {
      setCookie(cookie, t.sortList);
    }
  });
}

function refreshSorting(table, cookie) {
	var cv = getCookie(cookie);
	if (cv && table.find('tr').size() > 1) {
		table.trigger('sorton', [cv]);
	}
}

/*
 * Polling
 */
function switchPolling() {
	polling = !polling;
	setPolling(polling);
}

function setPolling(polling) {
	var pollingButtonText = polling ? i18n.pollStop : i18n.pollStart
	$('.pollButton').text(pollingButtonText);

	poll();
}

function poll() {
	if (polling) {
		updateCommsInterfaces();
		updateCommands(() => {
			setTimeout(() => {
				poll();
			}, 1000);
		});
	}
}

/*
 * Loading And Rendering
 */
function loadCommsInterfaces() {
	selected_comms_interface = null;
	var comms_interface_id = unescape(location.pathname.substring(pluginRoot.length + 1));

	if (comms_interface_id) {
		loadSingleCommsInterface(comms_interface_id);
	} else {
		loadAllCommsInterfaces();
	}
}

function loadSingleCommsInterface(comms_interface_id) {
	$.get(comms_interface_rest_endpoint + '/' + comms_interface_id, comms_interface => {
		selected_comms_interface = comms_interface;

		$.get(command_info_rest_endpoint + '/' + comms_interface_id, commands => {
			comms_interface.link = pluginRoot;
			comms_interface.icon = 'ui-icon-triangle-1-w';
			comms_interface_map = {
					[comms_interface.id]: comms_interface
			}

			command_map = {};
			comms_interface.commands.forEach(command_id => {
				var command = commands[command_id];
				command_map[command_id] = command;
				
				command.template = command_template.get(0);

				command.input = loadArguments(command.input);
				command.output = loadArguments(command.output);
			});

			renderCommsInterfaces();
		}, 'json');
	}, 'json');
}

function loadArguments(argument) {
	if (argument) {
		var argument_map = {};

		Object.keys(argument).map((key, index) => {
			argument_map[key] = {
				value: argument[key],
			};
		});

		return argument_map;
	} else {
		return null;
	}
}

function unloadArguments(argument) {
	if (argument) {
		var argument_map = {};

		Object.keys(argument).map((key, index) => {
			argument_map[key] = argument[key].value;
		});

		return argument_map;
	} else {
		return null;
	}
}

function loadAllCommsInterfaces() {
	$.get(comms_interface_rest_endpoint, comms_interfaces => {
		comms_interface_map = {};
		comms_interfaces.forEach(comms_interface => {
			comms_interface.link = pluginRoot + '/' + comms_interface.id;
			comms_interface.icon = 'ui-icon-triangle-1-e';

			comms_interface_map[comms_interface.id] = comms_interface;
		});

		command_map = null;

		renderCommsInterfaces();
	}, 'json');
}

/*
 * Rendering Static Data
 */
function renderCommsInterfaces() {
	comms_interface_table_body.empty();
	command_table_body.empty();

	for (var comms_interface_id in comms_interface_map) {
    renderCommsInterfaceInfo(comms_interface_map[comms_interface_id]);
	}

	if (command_map) {
		for (var command_id in command_map) {
	    renderCommandInfo(command_map[command_id]);
		}
	} else {
		commands_container.hide();
	}

	setFilter();
	refreshSorting(comms_interface_table, 'comms_interface_table');
	refreshSorting(command_table, 'command_table');

	initStaticWidgets();
}

function renderCommsInterfaceInfo(comms_interface) {
	var node = document.importNode(comms_interface_template.get(0).content, true);

	node.querySelector('#name').innerHTML += comms_interface.name;
	node.querySelector('#name').href = comms_interface.link;
	node.querySelector('#name span').className += ' ' + comms_interface.icon;
	node.querySelector('#bundle').innerText = comms_interface.registeringBundle.bundleName;
	node.querySelector('#bundle').href = appRoot  + '/bundles/' + comms_interface.registeringBundle.bundleId;

	node.querySelector('#actions #open').onclick = openResetClickFunction(comms_interface, true);
	node.querySelector('#actions #reset').onclick = openResetClickFunction(comms_interface, false);

	comms_interface.node = node;
	fillCommsInterfaceInfo(comms_interface);

	comms_interface_table_body.append(node);
	comms_interface.node = comms_interface_table_body.children().last()[0];
}

function openResetClickFunction(comms_interface, open) {
	var rest_endpoint;

	if (open) {
		rest_endpoint = open_comms_rest_endpoint;
	} else {
		rest_endpoint = reset_comms_rest_endpoint;
	}

	return () => {
		$.ajax({
			url: rest_endpoint,
			contentType: 'application/json',
			type: 'POST',
			data: JSON.stringify(comms_interface.id),
			success: () => {
				updateCommsInterfaces();
			},
			error: () => {
				updateCommsInterfaces();
			}
		});
	};
}

function renderCommandInfo(command) {
	var node = document.importNode(command.template.content, true);

	node.querySelector('#id').innerText = command.id;

	node.querySelector('#actions #execute').onclick = executeClickFunction(command);

	command.node = node;
	fillCommandInfo(command);

	command_table_body.append(node);
	command.node = command_table_body.children().last()[0];
}

function executeClickFunction(command) {
	return () => {
		executeCommand(command);
	};
}

function executeCommand(command, success, error) {
	executeCommands([command], success, error);
}

function executeCommands(commands, success, error) {
	if (commands.length == 0) {
		if (success)
			success();
		updateCommsInterfaces();
		return;
	}

	command = commands.splice(commands.length - 1, 1)[0];

	derenderData(command.output);

	$.ajax({
		url: command_invocation_rest_endpoint + '/' + selected_comms_interface.id + '/' + command.id,
		contentType: 'application/json',
		type: 'POST',
		data: JSON.stringify(unloadArguments(command.output)),
		success: command_result => {
			command.input = loadArguments(command_result);
			fillCommandInfo(command);
			executeCommands(commands, success, error);
		},
		error: (request, msg, error) => {
			command.input = {
				error: msg,
				trace: error
			};
			fillCommandInfo(command)
			if (error)
				error();
			updateCommsInterfaces();
		}
	});
}

/*
 * Updating Dynamic Data
 */
function updateCommsInterfaces() {
	$.get(comms_interface_rest_endpoint, comms_interfaces => {
		comms_interface_updates = {};
		comms_interfaces.forEach(comms_interface => {
			comms_interface_updates[comms_interface.id] = comms_interface;
		});

		for (var comms_interface_id in comms_interface_map) {
			var comms_interface = comms_interface_map[comms_interface_id];
			var comms_interface_update = comms_interface_updates[comms_interface_id];

			if (comms_interface_updates) {
				comms_interface.status.code = comms_interface_update.status.code;
				comms_interface.status.fault = comms_interface_update.status.fault;

				fillCommsInterfaceInfo(comms_interface);
			} else {
				command_table_body.remove(comms_interface.node);
				comms_interface_map[comms_interface_id] = null;
			}
		}

		initStaticWidgets();
	});
}

function updateCommands(success, error) {
	var commands = Object.values(command_map).filter(command => {
		return (command.output == null || Object.keys(command.output).length == 0)
						&& command.node.style.display != "none";
	});

	executeCommands(commands, success, error);
}

function fillCommsInterfaceInfo(comms_interface) {
	var node = comms_interface.node;

	node.querySelector('#status').innerText = comms_interface.status.code;
	if (comms_interface.status.fault) {
		node.querySelector('#status').title = comms_interface.status.fault;
	}
	node.querySelector('#channel').innerText = comms_interface.channel;

	initStaticWidgets();
}

function fillCommandInfo(command) {
	var node = command.node;

	renderData(command.input, node.querySelector('#input'), false);
	renderData(command.output, node.querySelector('#output'), true);

	initStaticWidgets();
}

function renderData(data, node, enabled) {
	if (!data)
		return;
	
	$(node).empty();

	var parameters = node.appendChild(document.createElement('div'));
	parameters.id = 'command_parameters';

	for (var data_item_key in data) {
		var data_item = data[data_item_key];

		var parameter = document.importNode(command_parameter_template.get(0).content, true);
		data_item.node = parameter;
		
		renderDataValue(data_item_key, data_item, enabled);

		parameters.appendChild(parameter);
		data_item.node = $(parameters).children().last()[0];
	}
}

function renderDataValue(key, item, enabled) {
	var label_node = item.node.querySelector('#label');
	var value_node = item.node.querySelector('#value');

	label_node.innerText = key;

	if (Array.isArray(item.value)) {
		item.value.forEach(value => renderDataValueElement(value_node, value));
	} else {
		renderDataValueElement(value_node, item.value);
	}
}

function renderDataValueElement(value_node, value) {
	var type = 'text';
	var input_node = value_node.appendChild(document.createElement('input'));

	if (value instanceof Boolean || typeof value == 'boolean') {
		type = 'checkbox';
		input_node.checked = value;

	} else if (value instanceof String || typeof value == 'string') {
		input_node.value = value;

	} else if (value instanceof Number || typeof value == 'number') {
		type = 'number';
		input_node.value = value;

	} else {
		input_node.value = 'unknown';
	}

	input_node.setAttribute('type', type);
}

function derenderData(data) {
	if (!data)
		return;

	for (var data_item_key in data) {
		data_item = data[data_item_key];

		derenderDataValue(data_item);
	}
}

function derenderDataValue(item) {
	var value_node_elements = $(item.node).find('#value').children();

	if (Array.isArray(item.value)) {
		for (var i = 0; i < value_node_elements.length; i++) {
			item.value[i] = derenderDataValueElement(value_node_elements.get(i));
		}
	} else {
		item.value = derenderDataValueElement(value_node_elements.get(0));
	}
}

function derenderDataValueElement(node) {
	var value;

	if (node.getAttribute('type') == 'checkbox') {
		value = node.checked;

	} else if (node.getAttribute('type') == 'text') {
		value = node.value;

	} else if (node.getAttribute('type') == 'number') {
		value = parseInt(node.value);

	}

	return value;
}
