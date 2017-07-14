import Thunk from "redux-thunk"
import {
  REQUEST_INFO,
  RECEIVE_INFO,
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
} from "./actions"

const UNKNOWN_TEXT = "..."
const initialState = {
  /*
   * Info
   */
  id: commsRestID,
  name: commsRestName,
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
  enums: {}
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

function commsApp(state = initialState, action) {
  if (typeof action.error !== typeof undefined) {
    return handleError(state, action)
  }

  if (typeof state.connection.fault !== typeof undefined) {
    state = { ...state, connection: { ...state.connection } }
    delete state.connection.fault
  }

  if (typeof state.fault !== typeof undefined) {
    state = { ...state }
    delete state.fault
  }

  switch (action.type) {
  case REQUEST_INFO:
    state = { ...state, waiting: true }
    break
  case RECEIVE_INFO:
    state = { ...state, ...action.payload }
    break

  case CLEAR_REQUESTED_VALUES:
    state = { ...state, entriesByID: clearData(state.entriesByID) }
    break
  case CHANGE_REQUESTED_VALUE:
    state = { ...state, entriesByID: requestValue(state.entriesByID, action) }
    break

  case REQUEST_CONNECTION_STATE:
    state = { ...state, connection: setConnection(state.connection, action.payload) }
    break
  case CONNECTION_STATE_CHANGED:
    state = { ...state, connection: changeConnection(state.connection, action.payload) }
    break

  case SET_COMMAND_FILTER:
    state = { ...state, entriesFilter: action.payload }
    break

  case SET_POLLING_ENABLED:
    state = { ...state, pollingStatus: enablePolling(state.pollingStatus, action.payload) }
    break
  case POLL_TICK:
    state = { ...state, pollingStatus: pollTick(state.pollingStatus) }
    break
  case SEND_POLL_REQUEST:
    state = { ...state, entriesByID: pollingRequest(state.entriesByID, action) }
    break
  case RECEIVE_POLL_RESPONSE:
    state = { ...state, entriesByID: pollingResponse(state.entriesByID, action) }
    break
  case SEND_EXECUTION_REQUEST:
    state = { ...state, entriesByID: executionRequest(state.entriesByID, action) }
    break
  case RECEIVE_EXECUTION_RESPONSE:
    state = { ...state, entriesByID: executionResponse(state.entriesByID, action) }
    break

  default:
  }

  return state
}

function handleError(state, action) {
  console.log("error: " + action.error.message + "\n" + action.error.detail)

  switch (action.type) {
  case REQUEST_INFO:
  case RECEIVE_INFO:
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

export default commsApp
