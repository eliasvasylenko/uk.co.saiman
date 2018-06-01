
var importModule = function (id) {
	return System.import(id);
}

var defaultNormalize = System.normalize;
console.log("wtf");
System.normalize = function(name, parentName) {
    console.log("Intercepting", name, parentName);
    return defaultNormalize.call(System, name, parentName);
}
