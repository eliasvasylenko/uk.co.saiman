import * as actions from 'actions';

const initialState = {
  name: "...",
  status: "...",
  channel: "...",
  bundle: {
    name: "...",
    symbolicName: "...",
    id: -1
  },
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
    case SET_COMMAND_FILTER:
      return { ...state, commandFilter: action.payload}
    case EXECUTE_COMMAND:
      return { ...state, commandsById: executeCommand(state.commandsById, action)}
    default:
      return state
  }
}

export default commsApp