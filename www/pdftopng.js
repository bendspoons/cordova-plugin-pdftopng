var exec = require('cordova/exec');

function PdfToPng() {}

PdfToPng.prototype.pluginVersion = function(success, error) {
  exec(success, error, 'PdfToPng', 'versioninfo', []);
};

PdfToPng.prototype.getPage = function(options, success, error) {
  exec(success, error, 'PdfToPng', 'getPage', [options.sourcePDF, options.page, options.width, options.height, options.dpi, options.output, options.targetDirectory, options.targetFilename]);
};

PdfToPng.prototype.countPages = function(options, success, error) {
  exec(success, error, 'PdfToPng', 'countPages', [options.sourcePDF]);
};

/*
PdfToPng.prototype.getPages = function(options, success, error) {
  exec(success, error, 'PdfToPng', 'getPages', [options.sourcePDF, options.pages, options.width, options.height, options.dpi, options.output, options.targetDirectory, options.targetFilename]);
};
*/

var pdftopng  = new PdfToPng();
module.exports = pdftopng;
