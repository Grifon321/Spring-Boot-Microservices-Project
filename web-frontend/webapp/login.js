sap.ui.define([
    "sap/ui/core/mvc/XMLView"
    
], function (XMLView) {
	"use strict";

	XMLView.create({
		viewName: "com.example.ui5app.view.App"
	}).then(function (oView) {
		oView.placeAt("content");
	});

});