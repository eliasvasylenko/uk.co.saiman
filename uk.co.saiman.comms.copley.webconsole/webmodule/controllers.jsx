import * as React from 'react'
import { connect } from 'react-redux'

import { ConsoleComponent, MapTable, StatLine } from '@saiman/webconsole'

import { openConnection, closeConnection, CONNECTION_STATES } from './actions.js'

const CopleyInformation = ({ name, connection, bundle, setConnectionOpen }) => {
  const entries = {
    name,
    status: (
      <span>
        {connection.status}
        {connection.status != CONNECTION_STATES.OPEN &&
          <button onClick={e => setConnectionOpen(true)} disabled={connection.waiting}>
            {i18n["connection.open"]}
          </button>
        }
        {connection.status != CONNECTION_STATES.CLOSED &&
          <button onClick={e => setConnectionOpen(false)} disabled={connection.waiting}>
            {i18n["connection.close"]}
          </button>
        }
      </span>
    ),
    channel: connection.channel,
    bundle: (
      <div className="bName">
        <a href={appRoot + "/bundles/" + bundle.id}>
          {bundle.name}
          <span className="symName">{bundle.symbolicName}</span>
        </a>
      </div>
    )
  }
  return (
    <div id="commsInformationContainer">
      <StatLine status={connection.fault ? connection.fault.message : "Status OK"} />
      <MapTable header="infoHeader" entries={entries} />
    </div>
  )
}

const mapStateToProps = state => ({
  name: state.name,
  connection: state.connection,
  bundle: state.bundle,
  localize: (key, ...args) => translate(state.locale, key, args)
})

const mapDispatchToProps = dispatch => ({
  setConnectionOpen: (connectionState) => dispatch(connectionState ? openConnection() : closeConnection())
})

const CopleyInformationController = connect(
  mapStateToProps,
  mapDispatchToProps
)(CopleyInformation)

export default CopleyInformationController
