import * as actions from './actions';

const initialState = {
  /*
   * Info
   */
  name: "...",
  connection: {
    channel: "...",
    status: "..."
  },
  bundle: {
    name: "...",
    symbolicName: "...",
    id: -1
  },
  
  /*
   * Commands
   */
  isPolling: false,
  commandFilter: "",
  commands: [],
  commandsById: {}
}

function executeCommand(state = {}, action) {
  console.log(state[action.entry]);
  switch (action.type) {
    case EXECUTE_COMMAND:
      return state
    default:
      return state
  }
}

function commsApp(state = initialState, action) {
  switch (action.type) {
    case actions.SET_COMMAND_FILTER:
      return { ...state, commandFilter: action.payload}
    case actions.EXECUTE_COMMAND:
      return { ...state, commandsById: executeCommand(state.commandsById, action)}
    default:
      return state
  }
}

export default commsApp