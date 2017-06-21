import * as React from 'react'
import { connect } from 'react-redux'

import { ConsoleComponent, FilterBox, TableControls, ArrayTable, StatLine } from 'app/sai-web-console'

import { setFilter, clearFilter, setPolling } from './actions'

const CommsTable = ({commands, isPolling, setFilter, clearFilter, setPolling}) =>
  <div id="comms_table_container">
    <StatLine status="status" />
    <TableControls
      left = {
        <FilterBox />
      }
      right = {
        <button>Start polling</button>
      } />
    <ArrayTable
      columns={[
        "command_id",
        "command_output",
        "command_input",
        "command_actions"
      ]}
      rows={commands} />
  </div>

const mapStateToProps = state => {
  return {
    commands: state.commands.map(command => state.commandsById[command]),
    isPolling: state.isPolling
  }
}

const mapDispatchToProps = dispatch => {
  return {
    clear_filter: () => dispatch(clearFilter()),
    setFilter: (filterText) => dispatch(setFilter(filterText)),
    setPolling: (pollingState) => dispatch(setPolling(pollingState))
  }
}

const CommsTableController = connect(
  mapStateToProps,
  mapDispatchToProps
)(CommsTable)

export default CommsTableController
