/*
 * action types
 */

export const SET_COMMAND_FILTER = 'SET_COMMAND_FILTER'
export const EXECUTE_COMMAND = 'EXECUTE_COMMAND'

/*
 * other constants
 */

export const VisibilityFilters = {
  SHOW_ALL: 'SHOW_ALL',
  SHOW_COMPLETED: 'SHOW_COMPLETED',
  SHOW_ACTIVE: 'SHOW_ACTIVE'
}

/*
 * action creators
 */

export function setFilter(text) {
  return { type: SET_COMMAND_FILTER, payload: text }
}
export function clearFilter() {
  return setFilter("")
}

export function executeCommand(entry, action, payload) {
  return { type: EXECUTE_COMMAND, entry, action, payload }
}
