
var importModule = function (id) {
	return System.import(id);
}

var defaultNormalize = System.normalize;
System.normalize = function(name, parentName) {
    return defaultNormalize.call(System, name, parentName);
}
