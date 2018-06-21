import axios from 'axios'

/*
 * action types
 */
export const REQUEST_INFO = 'REQUEST_INFO'
export const RECEIVE_INFO = 'RECEIVE_INFO'
export const REQUEST_CONTROLLER_INFO = 'REQUEST_CONTROLLER_INFO'
export const RECEIVE_CONTROLLER_INFO = 'RECEIVE_CONTROLLER_INFO'

export const CLEAR_REQUESTED_VALUES = 'CLEAR_REQUESTED_VALUES'
export const CHANGE_REQUESTED_VALUE = 'CHANGE_REQUESTED_VALUE'

export const REQUEST_OPEN_CONNECTION_STATE = 'REQUEST_OPEN_CONNECTION_STATE'
export const REQUEST_CLOSED_CONNECTION_STATE = 'REQUEST_CLOSED_CONNECTION_STATE'
export const CONNECTION_STATE_CHANGED = 'CONNECTION_STATE_CHANGED'

export const SET_COMMAND_FILTER = 'SET_COMMAND_FILTER'

export const SET_POLLING_ENABLED = 'SET_POLLING_ENABLED'
export const POLL_TICK = 'POLL_TICK'
export const SEND_POLL_REQUEST = 'SEND_POLL_REQUEST'
export const RECEIVE_POLL_RESPONSE = 'RECEIVE_POLL_RESPONSE'

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

export const POLLING_STATES = {
  ENABLED: 'ENABLED',
  DISABLED: 'DISABLED',
  DISABLING: 'DISABLING'
}

/*
 * utilities
 */

const handleError = (dispatch, type, extra) => (error) => {
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
      message: "Unable to make request",
      detail: error.message
    }
  }

  error = { ...extra, ...error }
  
  dispatch({
    type: type,
    error
  })
}

const createExecutionRequest = (entry, action) => ({
  url : invokeActionAPI + commsRestID + "/" + entry.id + "/" + action.id,
  method: "POST",
  data: action.sendsOutput ? entry.output : {}
})

/*
 * action creators
 */

export const setFilter = (text) => ({ type: SET_COMMAND_FILTER, payload: text })
export const clearFilter = () => setFilter("")

export const changeOutputValue = (entry, item, index, value) => ({
  type: CHANGE_REQUESTED_VALUE,
  entry,
  item,
  index,
  payload: value
})

/*
 * Thunk action creators
 */

const commsInfoAPI = "/api/comms/commsInfo/"
const controllerInfoAPI = "/api/comms/controllerInfo/"
const entriesInfoAPI = "/api/comms/entriesInfo/"
const actionsInfoAPI = "/api/comms/actionsInfo/"

const openCommsAPI = "/api/comms/openComms/"
const closeCommsAPI = "/api/comms/resetComms/"

const invokeActionAPI = "/api/comms/actionInvocation/"

export const requestInfo = () => (dispatch) => {
  dispatch({ type: REQUEST_INFO })

  axios.get(commsInfoAPI + commsRestID)
    .then(data => {
      dispatch({
        type: RECEIVE_INFO,
        payload: {
          ...data.data
        }
      })
    })
    .catch(handleError(dispatch, RECEIVE_INFO))
}

export const requestControllerInfo = () => (dispatch) => {
  dispatch({ type: REQUEST_CONTROLLER_INFO })

  Promise.all([
    axios.get(controllerInfoAPI + commsRestID),
    axios.get(entriesInfoAPI + commsRestID),
    axios.get(actionsInfoAPI + commsRestID)
  ])
    .then(data => {
      dispatch({
        type: RECEIVE_CONTROLLER_INFO,
        payload: {
          ...data[0].data,
          entriesByID: data[1].data,
          actionsByID: data[2].data
        }
      })
    })
    .catch(handleError(dispatch, RECEIVE_CONTROLLER_INFO))
}

export const openConnection = () => (dispatch, getState) => {
  dispatch({ type: REQUEST_OPEN_CONNECTION_STATE })

  const request = {
    url : openCommsAPI,
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

      dispatch(requestControllerInfo())
    })
    .catch(handleError(dispatch, CONNECTION_STATE_CHANGED))
}

export const closeConnection = () => (dispatch, getState) => {
  dispatch({ type: REQUEST_CLOSED_CONNECTION_STATE })

  setPollingEnabled(false)(dispatch, getState)

  const request = {
    url : closeCommsAPI,
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

      dispatch({ type: CLEAR_REQUESTED_VALUES })
    })
    .catch(handleError(dispatch, CONNECTION_STATE_CHANGED))
}

const pollAction = (action) => (dispatch, getState) => {
  const { entries, entriesByID } = getState()

  const applicableEntries = entries
    .map(entry => entriesByID[entry])
    .filter(entry => entry.actions.includes(action.id))
  
  const actionRequests = applicableEntries
    .map(entry => createExecutionRequest(entry, action, dispatch))

  dispatch({
    type: SEND_POLL_REQUEST,
    entries: applicableEntries.map(entry => entry.id),
    action: action.id
  })

  Promise.all(actionRequests.map(axios))
    .then(data => {
      const result = {}

      for (let i in data) {
        const entry = applicableEntries[i]
        const entryData = data[i].data

        if (typeof entryData.error !== typeof undefined)
          throw entryData

        result[entry.id] = entryData
      }

      dispatch({
        type: RECEIVE_POLL_RESPONSE,
        action: action.id,
        payload: result
      })
    })
}

const poll = (dispatch, getState) => {
  const { pollingStatus, actions, actionsByID } = getState()

  dispatch({ type: POLL_TICK })

  if (pollingStatus !== POLLING_STATES.ENABLED)
    return

  const pollableActions = actions
    .map(action => actionsByID[action])
    .filter(action => action.pollable)

  let requests = Promise.resolve([])
  for (let action of pollableActions) {
    requests = requests.then(responses => [ ...responses, dispatch(pollAction(action)) ])
  }
  requests.then(responses => {
    setTimeout(() => poll(dispatch, getState), 350)
  })
  .catch(handleError(dispatch, RECEIVE_POLL_RESPONSE))
}

export const setPollingEnabled = (enablePolling) => (dispatch, getState) => {
  const { pollingStatus } = getState()

  dispatch({
    type: SET_POLLING_ENABLED,
    payload: enablePolling
  })

  if (enablePolling && pollingStatus === POLLING_STATES.DISABLED)
    poll(dispatch, getState)
}

export const sendExecutionRequest = (entry, action) => (dispatch, getState) => {
  const { entriesByID, actionsByID } = getState()

  const request = createExecutionRequest(entriesByID[entry], actionsByID[action], dispatch)

  dispatch({
    type: SEND_EXECUTION_REQUEST,
    entry: entry,
    action: action
  })

  return axios(request)
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
    .catch(handleError(dispatch, RECEIVE_EXECUTION_RESPONSE))
}
