/*
 * REST Endpoints
 */
var comms_rest_endpoint = '/comms';
var command_set_rest_endpoint = '/comms/commandSetInfo';
var command_info_rest_endpoint = '/comms/commandInfo';
var command_invocation_rest_endpoint = '/comms/commandInvocation';
/*
 * Command Set Table
 */
var command_set_table_body;
var command_set_template;
var command_set_detail_template;

/*
 * Command Table
 */
var command_table;
var command_table_body;
var command_template;

/*
 * Data
 */
var selected_command_set;
var command_set_map = {};
var command_map = {};

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

	command_set_table = $('#command_set_table');
	command_set_table_body = command_set_table.find('tbody');
	command_set_template = $('#command_set');
	command_set_detail_template = $('#command_set_detail');

	command_set_table = initializeSorting(
			command_set_table,
			'command_set_table');

	commands_container = $('#commands_container');
	command_table = commands_container.find('#command_table');
	command_table_body = command_table.find('tbody');
	command_template = $('#command');

	command_table = initializeSorting(
			command_table,
			'command_table',
			{
				1: { sorter: false },
				2: { sorter: false },
				3: { sorter: false }
			});

	$('.reloadButton').click(loadCommandSets);
	$('.pollButton').click(switchPolling);

	setPolling(false);
	loadCommandSets();

	$('.filterButton').click(() => setFilter());
	$('.filterClear').click(() => setFilter(''));
	filter_text_box = $('.filter');
	filter_text_box.keyup(e => {
		if(e.keyCode == 13)
			setFilter();
	});
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
	for (command_id in command_map) {
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
		updateCommandSets();
		updateCommands();
		
		setTimeout(() => {
			poll();
		}, 1000);
	}
}

/*
 * Loading And Rendering
 */
function loadCommandSets() {
	selected_command_set = null;
	var command_set_id = unescape(location.pathname.substring(pluginRoot.length + 1));

	if (command_set_id) {
		loadSingleCommandSet(command_set_id);
	} else {
		loadAllCommandSets();
	}
}

function loadSingleCommandSet(command_set_id) {
	$.get(command_set_rest_endpoint + '/' + command_set_id, command_set => {
		selected_command_set = command_set;
		
		$.get(command_info_rest_endpoint + '/' + command_set_id, commands => {
			command_set.link = pluginRoot;
			command_set.template = command_set_detail_template.get(0);
			command_set_map = {
					[command_set.id]: command_set
			}

			command_map = commands;
			command_set.commands.forEach(command_id => {
				commands[command_id].template = command_template.get(0);
			});
	
			renderCommandSets();
		}, 'json');
	}, 'json');
}

function loadAllCommandSets() {
	$.get(command_set_rest_endpoint, command_sets => {
		command_set_map = {};
		command_sets.forEach(command_set => {
			command_set.link = pluginRoot + '/' + command_set.id;
			command_set.template = command_set_template.get(0);

			command_set_map[command_set.id] = command_set;
		});

		command_map = null;

		renderCommandSets();
	}, 'json');
}

/*
 * Rendering Static Data
 */
function renderCommandSets() {
	command_set_table_body.empty();
	command_table_body.empty();

	for (command_set_id in command_set_map) {
		renderCommandSetInfo(command_set_map[command_set_id]);
	}

	if (command_map) {
		for (command_id in command_map) {
			renderCommandInfo(command_map[command_id]);
		}
	} else {
		commands_container.hide();
	}

	initStaticWidgets();

	refreshSorting(command_set_table, 'command_set_table');
	refreshSorting(command_table, 'command_table');
}

function renderCommandSetInfo(command_set) {
	var node = document.importNode(command_set.template.content, true);

	node.querySelector('#name').innerHTML += command_set.name;
	node.querySelector('#name').href = command_set.link;
	node.querySelector('#bundle').innerText = command_set.registeringBundle.bundleName;
	node.querySelector('#bundle').href = appRoot  + '/bundles/' + command_set.registeringBundle.bundleId;

	command_set.node = node;
	fillCommandSetInfo(command_set);

	command_set_table_body.append(node);
	command_set.node = command_set_table_body.children().last()[0];
}

function renderCommandInfo(command) {
	var node = document.importNode(command.template.content, true);

	node.querySelector('#id').innerText = command.id;
	node.querySelector('#actions #execute').onclick = clickFunction(command);

	command.node = node;
	fillCommandInfo(command);
	
	command_table_body.append(node);
	command.node = command_table_body.children().last()[0];
}

function clickFunction(command) {
	return () => {
		executeCommand(command);
		updateCommandSets();
	}
}

function executeCommand(command) {
	$.ajax({
		url: command_invocation_rest_endpoint + '/' + selected_command_set.id + '/' + command.id,
		contentType: 'application/json',
		type: 'PUT',
		data: JSON.stringify(command.output),
		success: command_result => {
			command.input = command_result;
			fillCommandInfo(command);
			initStaticWidgets();
		},
		error: (request, msg, error) => {
			command.input = {
				error: msg,
				trace: error
			};
		}
	});
}

/*
 * Updating Dynamic Data
 */
function updateCommandSets() {
	$.get(command_set_rest_endpoint, command_sets => {
		command_set_updates = {};
		command_sets.forEach(command_set => {
			command_set_updates[command_set.id] = command_set;
		});

		for (command_set_id in command_set_map) {
			var command_set = command_set_map[command_set_id];
			var command_set_update = command_set_updates[command_set_id];
			
			if (command_set_updates) {
				command_set.status.code = command_set_update.status.code;
				command_set.status.fault = command_set_update.status.fault;

				fillCommandSetInfo(command_set);
			} else {
				command_table_body.remove(command_set.node);
				command_set_map[command_set_id] = null;
			}
		}
	});
}

function updateCommands() {
	for (command_id in command_map) {
		var command = command_map[command_id];
		if (Object.keys(command.output).length === 0) {
			executeCommand(command);
		}
	}
}

function fillCommandSetInfo(command_set) {
	var node = command_set.node;

	node.querySelector('#status').innerText = command_set.status.code;
	if (command_set.status.fault) {
		node.querySelector('#status').title = command_set.status.fault;
	}
	node.querySelector('#channel').innerText = command_set.channel;
}

function fillCommandInfo(command) {
	var node = command.node;

	renderData(command.input, node.querySelector('#input'));
	renderData(command.output, node.querySelector('#output'));
}

function renderData(data, node) {
	$(node).empty();

	var container = document.createElement('div');
	container.className = 'dataTable';
	var body = container.appendChild(document.createElement('table')).appendChild(document.createElement('tbody'));

	for (data_item in data) {
		data_value = data[data_item];

		var row = body.appendChild(document.createElement('tr'));
		row.appendChild(document.createElement('td')).innerText = data_item;

		var value = row.appendChild(document.createElement('td'));
		renderDataValue(data_value, value);
	}

	node.appendChild(container);
}

function renderDataValue(value, node) {
	if (value != null) {

		if (value instanceof Boolean || typeof value == "boolean") {
			var input = node.appendChild(document.createElement('input'));
			input.setAttribute("type", "checkbox");
			input.checked = value;

		} else if (value instanceof String || typeof value == "string") {
			var input = node.appendChild(document.createElement('input'));
			input.setAttribute("type", "text");
			input.value = value;

		} else if (value instanceof Number || typeof value == "number") {
			var input = node.appendChild(document.createElement('input'));
			input.setAttribute("type", "number ");
			input.value = value;

		} else {
			node.innerText = 'unknown';
		}
	}
}
