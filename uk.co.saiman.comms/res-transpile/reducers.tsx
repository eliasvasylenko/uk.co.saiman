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
  entriesByID: {}
}

function executeCommand(commandState = {}, { entry, action, payload }) {
  return {
    ...commandState,
    [entry]: {
      ...commandState[entry],
      [action]: {
        ...commandState[entry][action],
        waiting: true
      }
    }
  }
}

function setConnection(connectionState = {}, requestedStatus) {
  if (connectionState.waiting || requestedStatus == connectionState.status)
    return connectionState
  
  return {
    ...connectionState,
    waiting: true
  }
}

function changeConnection(connectionState = {}, { channel, status, fault} ) {
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
    return { ...state, connection : { ...state.connection, fault : action.error } }
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
    state = { ...state, entriesById: executeCommand(state.entriesById, action) }
    break
  case RECEIVE_EXECUTION_RESPONSE:
    state = { ...state } // TODO
    break

  default:
  }

  return state
}

export default commsApp
