import {
  CONNECTION_STATES,
  REQUEST_CONNECTION_STATE,
  CONNECTION_STATE_CHANGED,
  
  SET_COMMAND_FILTER,
  
  SET_POLLING_ENABLED,
  SEND_EXECUTION_REQUEST,
  RECEIVE_EXECUTION_RESPONSE
} from './actions'

const initialState = {
  /*
   * Info
   */
  id: commsRestID,
  name: commsRestName,
  connection: {
    channel: "...",
    status: CONNECTION_STATES.CLOSED
  },
  bundle: {
    id: -1,
    name: "...",
    symbolicName: "..."
  },

  /*
   * Commands
   */
  pollingEnabled: false,
  commandFilter: "",
  commands: [],
  commandsById: {}
}

function executeCommand(commandState = {}, { entry, action, payload }) {
  console.log(commandState[entry] + ", " + action)
  return {
    ...commandState,
    [entry]: {
      ...state[entry],
      [action]: {
        ...state[entry][action],
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

function commsApp(state = initialState, action) {
  switch (action.type) {
  case REQUEST_CONNECTION_STATE:
    return { ...state, connection: setConnection(state.connection, action.payload) }
  case CONNECTION_STATE_CHANGED:
    return { ...state }
  
  case SET_COMMAND_FILTER:
    return { ...state, commandFilter: action.payload }

  case SET_POLLING_ENABLED:
    return { ...state, pollingEnabled: action.payload }
  case SEND_EXECUTION_REQUEST:
    return { ...state, commandsById: executeCommand(state.commandsById, action) }
  case RECEIVE_EXECUTION_RESPONSE:
    return { ...state }

  default:
    return state
  }
}

export default commsApp
