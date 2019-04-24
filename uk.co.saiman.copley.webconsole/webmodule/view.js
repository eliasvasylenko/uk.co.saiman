import { html, render } from 'lighterhtml'

import { MapTable, StatLine } from '@saiman/webconsole'

import CopleyTable from './variables.js'

const CopleyInformation = ({ name, connection, bundle, setConnectionOpen }) => {
  const entries = {
    name,
    status: html`
      <span>
        ${connection.status}
        ${connection.status != CONNECTION_STATES.OPEN && html`
          <button onClick=${e => openConnection()} disabled=${connection.waiting}>
            ${i18n["connection.open"]}
          </button>
        `}
        ${connection.status != CONNECTION_STATES.CLOSED && html`
          <button onClick=${e => closeConnection()} disabled=${connection.waiting}>
            ${i18n["connection.close"]}
          </button>
        `}
      </span>
    `,
    channel: connection.channel,
    bundle: html`
      <div className="bName">
        <a href=${appRoot + "/bundles/" + bundle.id}>
          ${bundle.name}
          <span className="symName">${bundle.symbolicName}</span>
        </a>
      </div>
    `
  }
  return html`
    <div id="commsInformationContainer">
      ${ StatLine({ status: (connection.fault ? connection.fault.message : "Status OK") }) }
      ${ MapTable({ header: "infoHeader", entries: entries }) }
    </div>
  `
}

const view = {
	
}

render(
  html`
    <div id="copleyApp">
      ${CopleyInformation({
		name: model.name,
        connection: model.connection,
        bundle: model.bundle,
        locale:model.locale
      })}
      ${CopleyTable({})}
    </div>
  `,
  document.getElementById( 'content' )
)

export default view
