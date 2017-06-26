import {
  CONNECTION_STATES,
  REQUEST_CONNECTION_STATE,
  CONNECTION_STATE_CHANGED,
  
  SET_COMMAND_FILTER,
  
  SET_POLLING_ENABLED,
  SEND_EXECUTION_REQUEST,
  RECEIVE_EXECUTION_RESPONSE
} from './actions'


const pingEpic = action$ =>
  action$.ofType(REQUEST_CONNECTION_STATE)
    .delay(1000) // Asynchronously wait 1000ms then continue
    .mapTo({ type: CONNECTION_STATE_CHANGED });
