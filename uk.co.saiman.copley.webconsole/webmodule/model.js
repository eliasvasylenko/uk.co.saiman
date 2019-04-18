import {
  REQUEST_INFO,
  RECEIVE_INFO,
  REQUEST_CONTROLLER_INFO,
  RECEIVE_CONTROLLER_INFO,
  CLEAR_REQUESTED_VALUES,
  CHANGE_REQUESTED_VALUE,
  
  CONNECTION_STATES,
  REQUEST_CONNECTION_STATE,
  CONNECTION_STATE_CHANGED,
  
  SET_COMMAND_FILTER,

  POLLING_STATES,
  SET_POLLING_ENABLED,
  POLL_TICK,
  SEND_POLL_REQUEST,
  RECEIVE_POLL_RESPONSE,
  SEND_EXECUTION_REQUEST,
  RECEIVE_EXECUTION_RESPONSE
} from "./actions.js"

import { translate } from "@saiman/copley-i18n/../fr.js"

const UNKNOWN_TEXT = "..."

var model = {
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

function setConnection(connectionState = {}, requestedStatus) {
  if (typeof connectionState.waiting !== typeof undefined || requestedStatus == connectionState.status)
    return connectionState
  
  return {
    ...connectionState,
    waitingFor: requestedStatus
  }
}

function changeConnection(connectionState = {}, { channel, status, fault }) {
  connectionState = { ...connectionState }
  
  delete connectionState.waiting
  
  return {
    ...connectionState,
    channel,
    ...(status) && { status },
    ...(fault) && { fault }
  }
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

function present(action) {
  if (typeof action.error !== typeof undefined) {
    return handleError(model, action)
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
    model = { ...model, pollingStatus: enablePolling(model.pollingStatus, action.payload) }
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

  state.render(model)
}

function handleError(state, action) {
  console.log("error: " + action.error.message + "\n" + action.error.detail)

  switch (action.type) {
  case REQUEST_INFO:
  case RECEIVE_INFO:
  case REQUEST_CONTROLLER_INFO:
  case RECEIVE_CONTROLLER_INFO:
  case REQUEST_CONNECTION_STATE:
  case CONNECTION_STATE_CHANGED:
    state = { ...state, connection: { ...state.connection, fault: action.error } }
  case CLEAR_REQUESTED_VALUES:
  case CHANGE_REQUESTED_VALUE:
  case SET_COMMAND_FILTER:
  case SET_POLLING_ENABLED:
  case POLL_TICK:
  case SEND_POLL_REQUEST:
  case RECEIVE_POLL_RESPONSE:
  case SEND_EXECUTION_REQUEST:
  case RECEIVE_EXECUTION_RESPONSE:
    state = { ...state, fault: action.error }
  default:
  }
  
  return state
}

export const dispatch = (action) => {
	action(present)
}

export default dispatch
