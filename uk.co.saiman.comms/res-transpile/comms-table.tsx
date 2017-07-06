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

const CommsData = (items = {}, changeValue = (item, index, value) => {}) =>
  <div className="commsData">
    {
      Object.keys(items).map(item => CommsDataItem(item, items[item], (value, index) => changeValue(item, index, value)))
    }
  </div>

const CommsDataItem = (item, value, changeValue) =>
  <div key={item} className="commsDataItem">
    <label>{item}</label>
    <span>
      {
        (typeof value === typeof [])
          ? value.map((value, index) => CommsDataValue(
              index,
              value,
              newValue => changeValue(newValue, index)))
          : CommsDataValue(-1, value, changeValue)
      }
    </span>
  </div>

const CommsDataValue = (index, value, change) => {
  const type = typeof value
  
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

  renderImpl({ fault, entries, filter, setFilter, clearFilter, pollingStatus, setPollingEnabled, execute, changeOutputValue }) {
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
                entryOutput: CommsData(entry.output, (item, index, value) => changeOutputValue(entry.id, item, index, value)),
                entryInput: CommsData(entry.input),
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
