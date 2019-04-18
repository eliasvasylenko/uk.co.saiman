import view from './view.js'

var state = {
	view: view,

	ready: (model) => true,

	nextAction: (model) => { },

	render: (model) => {
		state.representation(model)
		state.nextAction(model);
	},
	
	representation: (model) => {
		var representation = 'oops... something went wrong, the system is in an invalid state';
	
		if (state.ready(model)) {
			representation = state.view.ready(model, actions.intents);
		}
	
		// complete the reactive loop
		state.view.display(representation);
	}
};

export default state