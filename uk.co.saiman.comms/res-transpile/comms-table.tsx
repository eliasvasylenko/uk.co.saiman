import * as React from 'react'
import { connect } from 'react-redux'

import { ConsoleComponent, FilterBox, TableControls, ArrayTable, StatLine } from 'sai-webconsole'

import { setFilter, clearFilter, setPollingEnabled  } from './actions'

const CommsTable = ({ commands, filter, setFilter, pollingEnabled, setPollingEnabled }) =>
  <div id="commsTableContainer">
    <StatLine status="status" />
    <TableControls
      left = {
        <FilterBox filter setFilter />
      }
      right = {
        pollingEnabled
          ? <button onClick={e => setPollingEnabled(false)}>{i18n.pollStop}</button>
          : <button onClick={e => setPollingEnabled(true)}>{i18n.pollStart}</button>
      } />
    <ArrayTable
      columns={[
        "commandID",
        "commandOutput",
        "commandInput",
        "commandActions"
      ]}
      rows={commands} />
  </div>

const mapStateToProps = state => ({
  commands: state.commands.map(command => state.commandsById[command]),
  filter: state.commandFilter,
  pollingEnabled: state.pollingEnabled
})

const mapDispatchToProps = dispatch => ({
  setFilter: (filterText) => dispatch(setFilter(filterText)),
  setPollingEnabled: (pollingState) => dispatch(setPollingEnabled(pollingState))
})

const CommsTableController = connect(
  mapStateToProps,
  mapDispatchToProps
)(CommsTable)

export default CommsTableController
