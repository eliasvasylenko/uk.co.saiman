import axios from 'axios'

/*
 * action types
 */
export const REQUEST_INFO = 'REQUEST_INFO'
export const RECEIVE_INFO = 'RECEIVE_INFO'

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
 * utilities
 */

const handleError = (dispatch, type, error, extra) => {
  if (error.response) {
    error = {
      message: error.response.statusText + " (" + error.response.status + ")",
      detail: JSON.stringify(error.response.data)
    }
  } else if (error.request) {
    error = {
      messsage: "No response to request",
      detail: JSON.stringify(error.request)
    }
  } else if (error.trace) {
    error = {
      message: error.error,
      detail: error.trace
    }
  } else {
    error = {
      messsage: "Unable to make request",
      detail: error.message
    }
  }

  error = { ...extra, ...error }
  
  dispatch({
    type: type,
    error
  })
}

/*
 * action creators
 */

export const openConnection = () => changeConnection(CONNECTION_STATES.OPEN)
export const closeConnection = () => changeConnection(CONNECTION_STATES.CLOSED)

export const setFilter = (text) => ({ type: SET_COMMAND_FILTER, payload: text })
export const clearFilter = () => setFilter("")

/*
 * Thunk action creators
 */

export const requestInfo = () => (dispatch) => {
  dispatch({ type: REQUEST_INFO })

  Promise.all([
    axios.get("/api/comms/commsInterfaceInfo/" + commsRestID),
    axios.get("/api/comms/entries/" + commsRestID),
    axios.get("/api/comms/entriesInfo/" + commsRestID),
    axios.get("/api/comms/actions/" + commsRestID),
    axios.get("/api/comms/actionsInfo/" + commsRestID)
  ])
    .then(data => {
      dispatch({
        type: RECEIVE_INFO,
        payload: {
          ...data[0].data,
          entries: data[1].data,
          entriesByID: data[2].data,
          actions: data[3].data,
          actionsByID: data[4].data
        }
      })
    })
    .catch(error => handleError(dispatch, RECEIVE_INFO, error))
}

export const changeConnection = (requestedState) => (dispatch) => {
  dispatch({ type: REQUEST_CONNECTION_STATE, payload: requestedState })

  const request = {
    url : (requestedState == CONNECTION_STATES.OPEN)
      ? "/api/comms/openCommsInterface"
      : "/api/comms/resetCommsInterface",
    method: "POST",
    data: JSON.stringify(commsRestID)
  }

  axios(request)
    .then(data => {
      if (typeof data.data.error !== typeof undefined)
        throw data.data
      dispatch({
        type: CONNECTION_STATE_CHANGED,
        payload: data.data
      })
    })
    .catch(error => handleError(dispatch, CONNECTION_STATE_CHANGED, error))
}

export const setPollingEnabled = (pollingEnabled) => ({
  type: SET_POLLING_ENABLED,
  payload: pollingEnabled
})

export const sendExecutionRequest = (entry, action, payload) => (dispatch) => {
  dispatch({
    type: SEND_EXECUTION_REQUEST,
    entry,
    action,
    payload
  })

  const request = {
    url : "/api/comms/actionInvocation/" + commsRestID + "/" + entry + "/" + action,
    method: "POST",
    data: payload
  }

  axios(request)
    .then(data => {
      if (typeof data.data.error !== typeof undefined)
        throw data.data
      dispatch({
        type: RECEIVE_EXECUTION_RESPONSE,
        entry,
        action,
        payload: data.data
      })
    })
    .catch(error => handleError(dispatch, RECEIVE_EXECUTION_RESPONSE, error))
}
