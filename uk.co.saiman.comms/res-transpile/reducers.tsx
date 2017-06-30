import Thunk from "redux-thunk"
import {
  REQUEST_INFO,
  RECEIVE_INFO,
  
  CONNECTION_STATES,
  REQUEST_CONNECTION_STATE,
  CONNECTION_STATE_CHANGED,
  
  SET_COMMAND_FILTER,
  
  SET_POLLING_ENABLED,
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
  pollingEnabled: false,
  entriesFilter: "",
  entries: [],
  entriesByID: {},
  actions: [],
  actionsByID: {}
}

function executionRequest(entriesByID = {}, { entry, action, payload }) {
  if (typeof entriesByID[entry].waiting !== typeof undefined)
    return entriesByID
  
  return {
    ...entriesByID,
    [entry]: {
      ...entriesByID[entry],
      waitingFor: action
    }
  }
}

function executionResponse(entriesByID = {}, actionsByID = {}, { entry, action, payload }) {
  entriesByID = { ...entriesByID }
  
  delete entriesByID[entry].waiting
  
  console.log(JSON.stringify(payload))
  
  return {
    ...entriesByID,
    [entry]: {
      ...entriesByID[entry],
      ...payload
    }
  }
}

function setConnection(connectionState = {}, requestedStatus) {
  if (typeof connectionState.waiting !== typeof undefined || requestedStatus == connectionState.status)
    return connectionState
  
  return {
    ...connectionState,
    waitingFor: requestedStatus
  }
}

function changeConnection(connectionState = {}, { channel, status, fault } ) {
  connectionState = { ...connectionState }
  
  delete connectionState.waiting
  
  return {
    ...connectionState,
    channel,
    ...(status) && { status },
    ...(fault) && { fault }
  }
}

function commsApp(state = initialState, action) {
  if (action.error) {
    return handleError(state, action)
  }

  if (state.connection.fault) {
    state = { ...state }
    delete state.connection.fault
  }

  switch (action.type) {
  case REQUEST_INFO:
    state = { ...state, waiting: true }
    break
  case RECEIVE_INFO:
    state = { ...state, ...action.payload }
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
    state = { ...state, pollingEnabled: action.payload }
    break
  case SEND_EXECUTION_REQUEST:
    state = { ...state, entriesByID: executionRequest(state.entriesByID, action) }
    break
  case RECEIVE_EXECUTION_RESPONSE:
    state = { ...state, entriesByID: executionResponse(state.entriesByID, state.actionsByID, action) }
    break

  default:
  }

  return state
}

function handleError(state, action) {
  switch (action.type) {
  case REQUEST_INFO:
  case RECEIVE_INFO:
  case REQUEST_CONNECTION_STATE:
  case CONNECTION_STATE_CHANGED:
  case SET_COMMAND_FILTER:
  case SET_POLLING_ENABLED:
  case SEND_EXECUTION_REQUEST:
  case RECEIVE_EXECUTION_RESPONSE:
  default:
  }
  
  return { ...state, connection : { ...state.connection, fault : action.error } }
}

export default commsApp
