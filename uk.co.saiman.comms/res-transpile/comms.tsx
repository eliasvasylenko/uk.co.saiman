import React from 'react';
import { render } from 'react-dom'
import { Provider } from 'react-redux'
import { createStore } from 'redux'

import commsApp from './reducers'
import CommsInformationController from './comms-info'
import CommsTableController from './comms-table'

let store = createStore(commsApp)

class CommsApp extends ConsoleComponent {
  render() {
    <div id="commsApp">
      <CommsInformationController />
      <CommsTableController />
    </div>
  }
}

render(
  <Provider store={store}>
    <CommsApp />
  </Provider>,
  document.getElementById( 'content' )
);
