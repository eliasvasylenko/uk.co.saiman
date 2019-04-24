import {
  FilterBox,
  TableControls,
  ArrayTable,
  StatLine
} from '@saiman/webconsole'

import {
  setFilter,
  clearFilter,
  setPollingEnabled,
  sendExecutionRequest,
  changeOutputValue,
  POLLING_STATES
} from './actions.js'

const CommsData = (enums, items = {}, changeValue) => html`
  <div className="commsData">
    ${
      Object.keys(items).map(item => CommsDataItem(
        enums,
        item,
        items[item],
        changeValue
          ? (value, index) => changeValue(item, index, value)
          : undefined
      ))
    }
  </div>
`

const CommsDataItem = (enums, item, value, changeValue) => html`
  <div key=${item} className="commsDataItem">
    <label>${item}</label>
    <span>
      ${
        (typeof value === typeof [])
          ? value.map((value, index) => CommsDataValue(
              enums,
              index,
              value,
              changeValue
                ? newValue => changeValue(newValue, index)
                : undefined
            ))
          : CommsDataValue(enums, -1, value, changeValue)
      }
    </span>
  </div>
`

const CommsDataValue = (enums, index, value, change) => {
  const type = typeof value

  /*
   * TODO this is a HORRIBLE hack to deal with the DTO to JSON serialization on
   * the Java end being a bit inflexible.
   * 
   * Luckily no string values are used in any comms interfaces so far so the
   * damage is minimized.
   * 
   * Once we have the OSGi object converter API we can get rid of this string
   * checking shit by having enums encoded as an object containing all the
   * necessary information.
   */
  if (type === typeof "") {
    for (let enumType in enums) {
      if (enums[enumType].includes(value)) {
        if (change) {
          return html`
            <select key=${index} value=${value} onChange=${event => change(event.target.value)}>
              ${ enums[enumType].map(e => html`<option key=${e} value=${e}>${e}</option>`) }
            </select>
          `
        }
      }
    }
  }

  if (type === typeof "")
    return html`<input key=${index} type="text" value=${value} onChange=${event => change(event.target.value)} />`

  if (type === typeof 0)
    return html`<input key=${index} type="number" value=${value} onChange=${event => change(Number(event.target.value))} />`

  if (type === typeof true)
    return html`<input key=${index} type="checkbox" checked=${value} onChange=${event => change(event.target.checked)} />`

  return html`<span>Unknown</span>`
}

const Actions = ({ id, actions, output }, execute) => html`
	<div>
	${
	  actions.map(action => html`
	    <button
	        key=${action.id}
	        onClick=${e => execute(id, action.id, action.sendsOutput ? output : {})}>
	      ${action.id}
	    </button>
	  `)
	}
	</div>
`

export const CopleyTable = ({ enums, fault, entries, filtering, polling, execute, changeOutputValue }) => html`
  <div id="commsTableContainer">
    ${StatLine({ status: (fault ? fault.message : "Status OK") })}
    ${TableControls({
      left: FilterBox(filtering),
      right: 
        polling.pollingStatus === POLLING_STATES.ENABLED
          ? html`<button onClick=${e => polling.setPollingEnabled(false)}> ${i18n.pollStop} </button>`
          : html`<button onClick=${e => polling.setPollingEnabled(true)}> ${i18n.pollStart} </button>`
    })}
    ${ArrayTable({
      columns: [
        "entryID",
        "entryOutput",
        "entryInput",
        "entryActions"
      ],
      rows: 
        entries
          .filter(entry => entry.id.toLowerCase().includes(filtering.filter.toLowerCase()))
          .map(entry => ({
            entryID: entry.id,
            entryOutput: CommsData(enums, entry.output, (item, index, value) => changeOutputValue(entry.id, item, index, value)),
            entryInput: CommsData(enums, entry.input),
            entryActions: Actions(entry, execute)
          })),
      keyColumn: "entryID"
    })}
  </div>
`

const mapStateToProps = state => ({
  entries: state.entries.map(entry => ({
    ...state.entriesByID[entry],
    actions: state.entriesByID[entry].actions.map(action => state.actionsByID[action])
  })),
  enums: state.enums,
  filter: state.entriesFilter,
  pollingStatus: state.pollingStatus
})

const mapDispatchToProps = dispatch => ({
  setFilter: (filterText) => dispatch(setFilter(filterText)),
  clearFilter: () => dispatch(clearFilter()),
  setPollingEnabled: (pollingState) => dispatch(setPollingEnabled(pollingState)),
  execute: (entry, action, payload) => dispatch(sendExecutionRequest(entry, action, payload)),
  changeOutputValue: (entry, item, index, value) => dispatch(changeOutputValue(entry, item, index, value))
})

const CommsTableController = connect(
  mapStateToProps,
  mapDispatchToProps
)(CommsTable)

