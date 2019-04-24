import whatever from 'actions.js'

/*
 * The model represents our entire application state. It is the single source of truth.
 * 
 * Data is not intended to be read from or written to the model directly. Instead the
 * model exports only two API points, as documented in-place:
 * 
 * - `propose(action)`
 * 
 * - `onAccept(response)`
 * 
 * The model is consumed by the reactor, which wires the proposal/accept API into a
 * reactive loop.
 */

const UNKNOWN_TEXT = "..."

let model = {
	/*
	 * Info
	 */
	id: "copley.name",
	name: "copley.name",
	connection: {
		channel: UNKNOWN_TEXT,
		status: CONNECTION_STATES.CLOSED
	},
	bundle: {
		id: -1,
		name: UNKNOWN_TEXT,
		symbolicName: UNKNOWN_TEXT
	},

	/*
	 * Commands
	 */
	pollingStatus: POLLING_STATES.DISABLED,
	entriesFilter: "",
	entries: [],
	entriesByID: {},
	actions: [],
	actionsByID: {},

	/*
	 * Supplemental
	 */
	enums: {},
	locale: "en"
}

let accepted = (model) => { }

/*
 * Accepts a callback. Each time the model updates itself in response to a proposal,
 * it passes a copy of its new state to the callback.
 */
export const onAccept = (response) => {
	accepted = response
}

/*
 * Accepts a proposed action. An action is a declarative description of a proposed
 * action to be applied to the model. The model responds to a proposal how it sees
 * fit.
 *
 * The model currently accepts all the proposals defined in actions.js
 */
export const propose = (action) => {
	if (typeof action.error !== typeof undefined) {
		handleError(action)
	}

	if (typeof model.connection.fault !== typeof undefined) {
		model = { ...model, connection: { ...state.connection } }
		delete model.connection.fault
	}

	if (typeof model.fault !== typeof undefined) {
		model = { ...model }
		delete model.fault
	}

	console.log(action.type)

	switch (action.type) {
		case SET_LOCALE:
			model = { ...model, locale: action.payload }
			break
		case REQUEST_INFO:
			model = { ...model, waiting: true }
			break
		case RECEIVE_INFO:
			model = { ...model, ...action.payload }
			break

		case REQUEST_CONTROLLER_INFO:
			model = { ...model, waiting: true }
			break
		case RECEIVE_CONTROLLER_INFO:
			model = { ...model, ...action.payload }
			break

		case CLEAR_REQUESTED_VALUES:
			model = { ...model, entriesByID: clearData(model.entriesByID) }
			break
		case CHANGE_REQUESTED_VALUE:
			model = { ...model, entriesByID: requestValue(model.entriesByID, action) }
			break

		case REQUEST_CONNECTION_STATE:
			model = { ...model, connection: setConnection(model.connection, action.payload) }
			break
		case CONNECTION_STATE_CHANGED:
			model = { ...model, connection: changeConnection(model.connection, action.payload) }
			break

		case SET_COMMAND_FILTER:
			model = { ...model, entriesFilter: action.payload }
			break

		case SET_POLLING_ENABLED:
			break
		case POLL_TICK:
			model = { ...model, pollingStatus: pollTick(model.pollingStatus) }
			break
		case SEND_POLL_REQUEST:
			model = { ...model, entriesByID: pollingRequest(model.entriesByID, action) }
			break
		case RECEIVE_POLL_RESPONSE:
			model = { ...model, entriesByID: pollingResponse(model.entriesByID, action) }
			break
		case SEND_EXECUTION_REQUEST:
			model = { ...model, entriesByID: executionRequest(model.entriesByID, action) }
			break
		case RECEIVE_EXECUTION_RESPONSE:
			model = { ...model, entriesByID: executionResponse(model.entriesByID, action) }
			break

		default:
	}

	accepted({ ...model })
}

function handleError(action) {
	console.log("error: " + action.error.message + "\n" + action.error.detail)

	switch (action.type) {
		case REQUEST_INFO:
		case RECEIVE_INFO:
		case REQUEST_CONTROLLER_INFO:
		case RECEIVE_CONTROLLER_INFO:
		case REQUEST_CONNECTION_STATE:
		case CONNECTION_STATE_CHANGED:
			model = { ...model, connection: { ...model.connection, fault: action.error } }
		case CLEAR_REQUESTED_VALUES:
		case CHANGE_REQUESTED_VALUE:
		case SET_COMMAND_FILTER:
		case SET_POLLING_ENABLED:
		case POLL_TICK:
		case SEND_POLL_REQUEST:
		case RECEIVE_POLL_RESPONSE:
		case SEND_EXECUTION_REQUEST:
		case RECEIVE_EXECUTION_RESPONSE:
			model = { ...model, fault: action.error }
		default:
	}

	updated(model)
}

function clearData(entriesByID = {}) {
	const clearEntries = {}
	Object.keys(entriesByID).forEach(entry => {
		clearEntries[entry] = {
			...entriesByID[entry],
			input: {},
			output: {}
		}
	})
	return clearEntries
}

function requestValue(entriesByID = {}, { entry, item, index, payload }) {
	entriesByID = {
		...entriesByID,
		[entry]: {
			...entriesByID[entry],
			output: {
				...entriesByID[entry].output
			}
		}
	}

	if (index >= 0) {
		entriesByID[entry].output[item][index] = payload
	} else {
		entriesByID[entry].output[item] = payload
	}

	return entriesByID
}

function enablePolling(pollingStatus, pollingRequested) {
	return pollingRequested
		? POLLING_STATES.ENABLED
		: pollingStatus === POLLING_STATES.DISABLED
			? POLLING_STATES.DISABLED
			: POLLING_STATES.DISABLING
}

function pollTick(pollingStatus) {
	return pollingStatus === POLLING_STATES.ENABLED
		? POLLING_STATES.ENABLED
		: POLLING_STATES.DISABLED
}

function pollingRequest(entriesByID, { entries, action }) {
	entriesByID = { ...entriesByID }

	for (let entry of entries) {
		entriesByID[entry] = {
			...entriesByID[entry],
			waitingFor: action
		}
	}

	return entriesByID
}

function pollingResponse(entriesByID, { payload }) {
	entriesByID = { ...entriesByID }

	for (let entry in payload) {
		entriesByID[entry] = {
			...entriesByID[entry],
			...payload[entry]
		}

		delete entriesByID[entry].waitingFor
	}

	return entriesByID
}

function executionRequest(entriesByID, { entry, action }) {
	if (typeof entriesByID[entry].waitingFor !== typeof undefined)
		return entriesByID

	entriesByID = { ...entriesByID }

	entriesByID[entry] = {
		...entriesByID[entry],
		waitingFor: action
	}

	return entriesByID
}

function executionResponse(entriesByID, { entry, payload }) {
	entriesByID = { ...entriesByID }

	entriesByID[entry] = {
		...entriesByID[entry],
		...payload
	}

	delete entriesByID[entry].waitingFor

	return entriesByID
}

