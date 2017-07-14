import * as React from 'react'
import { connect } from 'react-redux'

import {
  ConsoleComponent,
  FilterBox,
  TableControls,
  ArrayTable,
  StatLine
} from 'sai-webconsole'

import {
  setFilter,
  clearFilter,
  setPollingEnabled,
  sendExecutionRequest,
  changeOutputValue,
  POLLING_STATES
} from './actions'

const CommsData = (enums, items = {}, changeValue) =>
  <div className="commsData">
    {
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

const CommsDataItem = (enums, item, value, changeValue) =>
  <div key={item} className="commsDataItem">
    <label>{item}</label>
    <span>
      {
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

const CommsDataValue = (enums, index, value, change) => {
  const type = typeof value

  /*
   * TODO this is a nasty hack to deal with the DTO to JSON serialization on
   * the Java end being a bit inflexible.
   * 
   * Once we have the OSGi object converter API we can get rid of this string
   * checking shit by having enums encoded as an object containing all the
   * necessary information.
   */
  if (type === typeof "") {
    for (let enumType in enums) {
      if (enums[enumType].includes(value)) {
        if (change) {
          return (
            <select key={index} value={value} onChange={event => change(event.target.value)}>
              {
                enums[enumType].map(e => (
                  <option key={e} value={enumType + "." + e}>
                    {e}
                  </option>
                ))
              }
            </select>
          )
        } else {
          value = enums[enumType].filter(e => value.endsWith("." + e))[0]
        }
      }
    }
  }

  if (type === typeof "")
    return <input key={index} type="text" value={value} onChange={event => change(event.target.value)} />

  if (type === typeof 0)
    return <input key={index} type="number" value={value} onChange={event => change(Number(event.target.value))} />

  if (type === typeof true)
    return <input key={index} type="checkbox" checked={value} onChange={event => change(event.target.checked)} />

  return <span>Unknown</span>
}

class CommsTable extends ConsoleComponent {
  constructor(props) {
    super(props)
  }

  render() { return this.renderImpl(this.props) }

  renderImpl({ enums, fault, entries, filter, setFilter, clearFilter, pollingStatus, setPollingEnabled, execute, changeOutputValue }) {
    return (
      <div id="commsTableContainer">
        <StatLine status={fault ? fault.message : "Status OK"} />
        <TableControls
          left={
            <FilterBox filter={filter} setFilter={setFilter} clearFilter={clearFilter} />
          }
          right={
            pollingStatus === POLLING_STATES.ENABLED
              ? <button onClick={e => setPollingEnabled(false)}>{i18n.pollStop}</button>
              : <button onClick={e => setPollingEnabled(true)}>{i18n.pollStart}</button>
          } />
        <ArrayTable
          columns={[
            "entryID",
            "entryOutput",
            "entryInput",
            "entryActions"
          ]}
          rows={
            entries
              .filter(entry => entry.id.toLowerCase().includes(filter.toLowerCase()))
              .map(entry => ({
                entryID: entry.id,
                entryOutput: CommsData(enums, entry.output, (item, index, value) => changeOutputValue(entry.id, item, index, value)),
                entryInput: CommsData(enums, entry.input),
                entryActions: this.renderActions(entry, execute)
              }))
          }
          keyColumn="entryID" />
      </div>
    )
  }

  renderActions({ id, actions, output }, execute) {
    return (
      <div>
        {
          actions.map(action => (
            <button
                key={action.id}
                onClick={e => execute(id, action.id, action.sendsOutput ? output : {})}>
              {action.id}
            </button>
          ))
        }
      </div>
    )
  }
}

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

export default CommsTableController
