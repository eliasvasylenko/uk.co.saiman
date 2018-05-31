var oldLoad = require.load;
require.load = function (context, id, url) {
	url = url.endsWith('.js') ? url : (url + '.js');
	return oldLoad.apply(require, [context, id, url]);
}
