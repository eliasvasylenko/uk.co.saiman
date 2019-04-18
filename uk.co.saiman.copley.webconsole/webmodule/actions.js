import axios from 'axios'

/*
 * action types
 */
export const SET_LOCALE = 'SET_LOCALE'

export const REQUEST_SYSTEM_INFO = 'REQUEST_SYSTEM_INFO'
export const RECEIVE_SYSTEM_INFO = 'RECEIVE_SYSTEM_INFO'
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

const handleError = (present, type, extra) => (error) => {
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
  
  present({
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

const api = {
	system : () => "/api/copley",
	controllers : () => "/api/copley/controllers",
 	controller : controller => api.controllers() + "/" + controller,
	nodes : controller => api.controller(controller) + "/nodes",
	node : (controller, node) => api.nodes(controller) + "/" + node,
	axes : (controller, node) => api.node(controller, node) + "/axes",
	axis : (controller, node, axis) => api.axes(controller, node) + "/" + axis,
	variables : (controller, node, axis) => api.axis(controller, node, axis) + "/variables",
	variable : (controller, node, axis, variable) => api.variables(controller, node, axis) + "/" + variable
}

export const translate = (locale, key, ...args) => (present) => {
  locale
}

export const setLocale = (locale) => (present) => {
  present({ type: SET_LOCALE, payload: locale })
}

export const requestInfo = () => (present) => {
  present({ type: REQUEST_SYSTEM_INFO })

  axios.get(api.system())
    .then(data => {
      present({
        type: RECEIVE_SYSTEM_INFO,
        payload: {
          ...data.data
        }
      })
    })
    .catch(handleError(present, RECEIVE_SYSTEM_INFO))
}

export const requestControllerInfo = () => (present) => {
  present({ type: REQUEST_CONTROLLER_INFO })

  Promise.all([
    axios.get(controllerInfoAPI + commsRestID),
    axios.get(entriesInfoAPI + commsRestID),
    axios.get(actionsInfoAPI + commsRestID)
  ])
    .then(data => {
      present({
        type: RECEIVE_CONTROLLER_INFO,
        payload: {
          ...data[0].data,
          entriesByID: data[1].data,
          actionsByID: data[2].data
        }
      })
    })
    .catch(handleError(present, RECEIVE_CONTROLLER_INFO))
}

export const openConnection = () => (present) => {
  present({ type: REQUEST_OPEN_CONNECTION_STATE })

  const request = {
    url : openCommsAPI,
    method: "POST",
    data: JSON.stringify(commsRestID)
  }

  axios(request)
    .then(data => {
      if (typeof data.data.error !== typeof undefined)
        throw data.data
      present({
        type: CONNECTION_STATE_CHANGED,
        payload: data.data
      })

      present(requestControllerInfo())
    })
    .catch(handleError(present, CONNECTION_STATE_CHANGED))
}

export const closeConnection = () => (present) => {
  present({ type: REQUEST_CLOSED_CONNECTION_STATE })

  setPollingEnabled(false)(present)

  const request = {
    url : closeCommsAPI,
    method: "POST",
    data: JSON.stringify(commsRestID)
  }

  axios(request)
    .then(data => {
      if (typeof data.data.error !== typeof undefined)
        throw data.data
      present({
        type: CONNECTION_STATE_CHANGED,
        payload: data.data
      })

      present({ type: CLEAR_REQUESTED_VALUES })
    })
    .catch(handleError(present, CONNECTION_STATE_CHANGED))
}

const pollAction = (entries, action) => (present) => {
  const entries = entries
    .filter(entry => entry.actions.includes(action.id))
  
  const actionRequests = entries
    .map(entry => createExecutionRequest(entry, action, present))

  present({
    type: SEND_POLL_REQUEST,
    entries: entries.map(entry => entry.id),
    action: action.id
  })

  Promise.all(actionRequests.map(axios))
    .then(data => {
      const result = {}

      for (let i in data) {
        const entry = entries[i]
        const entryData = data[i].data

        if (typeof entryData.error !== typeof undefined)
          throw entryData

        result[entry.id] = entryData
      }

      present({
        type: RECEIVE_POLL_RESPONSE,
        action: action.id,
        payload: result
      })
    })
}

const poll = (entries, actions) => (present) => {
  present({ type: POLL_TICK })

  let requests = Promise.resolve([])
  for (let action of actions) {
    requests = requests.then(responses => [ ...responses, present(pollAction(entries, action)) ])
  }
  requests.catch(handleError(present, RECEIVE_POLL_RESPONSE))
}

export const setPollingEnabled = (enablePolling) => (present) => {
  present({
    type: SET_POLLING_ENABLED,
    payload: enablePolling
  })
}

export const sendExecutionRequest = (entry, action) => (present) => {
  const request = createExecutionRequest(entry, action, present)

  present({
    type: SEND_EXECUTION_REQUEST,
    entry: entry.id,
    action: action.id
  })

  return axios(request)
    .then(data => {
      if (typeof data.data.error !== typeof undefined)
        throw data.data
      present({
        type: RECEIVE_EXECUTION_RESPONSE,
        entry: entry.id,
        action: action.id,
        payload: data.data
      })
    })
    .catch(handleError(present, RECEIVE_EXECUTION_RESPONSE))
}
