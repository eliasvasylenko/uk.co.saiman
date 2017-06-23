/*
 * action types
 */

export const REQUEST_CONNECTION_STATE = 'REQUEST_CONNECTION_STATE'
export const CONNECTION_STATE_CHANGED = 'CONNECTION_STATE_CHANGED'

export const SET_COMMAND_FILTER = 'SET_COMMAND_FILTER'

export const SET_POLLING_ENABLED = 'SET_POLLING_ENABLED'
export const SEND_EXECUTION_REQUEST = 'SEND_EXECUTION_REQUEST'
export const RECEIVE_EXECUTION_RESPONSE = 'RECEIVE_EXECUTION_RESPONSE'

/*
 * other constants
 */

export const CONNECTION_STATES = {
  OPEN: 'OPEN',
  CLOSED: 'CLOSED',
  FAULT: 'FAULT'
}

/*
 * action creators
 */

export var openConnection = () => ({ type: REQUEST_CONNECTION_STATE, payload: CONNECTION_STATES.OPEN })

export var closeConnection = () => ({ type: REQUEST_CONNECTION_STATE, payload: CONNECTION_STATES.CLOSED })

export var setFilter = (text) => ({ type: SET_COMMAND_FILTER, payload: text })

export var clearFilter = () => setFilter("")

export var setPollingEnabled = (pollingEnabled) => ({
  type: SET_POLLING_ENABLED,
  payload: pollingEnabled
})

export var sendExecutionRequest = (entry, action, payload) => ({
  type: SEND_EXECUTION_REQUEST,
  entry,
  action,
  payload
})

export var receiveExecutionResponse = (entry, action, data) => ({
  type: RECEIVE_EXECUTION_RESPONSE,
  entry,
  action,
  data
})
