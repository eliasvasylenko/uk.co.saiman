import { html, render } from 'lighterhtml'

import { ConsoleComponent } from '@saiman/webconsole'

import { requestInfo } from './actions.js'
import dispatch from './model.js'
import state from './state.js'
import CopleyInformationController from './view.js'
import CopleyTableController from './variables.js'

requestInfo()

render(
  html`
    <div id="copleyApp">
      ${CopleyInformation({
		name: model.name,
        connection: model.connection,
        bundle: model.bundle,
        locale:model.locale
      })}
    </div>
  `,
  document.getElementById( 'content' )
)
