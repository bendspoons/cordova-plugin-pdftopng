var exec = require('cordova/exec');


function PdfToPng() {}

PdfToPng.prototype.pluginVersion = function(success, error) {
  exec(success, error, 'PdfToPng', 'versioninfo', []);
};

PdfToPng.prototype.getPage = function(options, success, error) {
  exec(success, error, 'PdfToPng', 'getPage', [options.sourcePDF, options.page, options.width, options.height, options.output, options.targetDirectory, options.targetFilename]);
};

PdfToPng.prototype.getPages = function(options, success, error) {
  exec(success, error, 'PdfToPng', 'getPages', [options.sourcePDF, options.pages, options.width, options.height, options.output]);
};

PdfToPng.prototype.countPages = function(options, success, error) {
  exec(success, error, 'PdfToPng', 'countPages', [options.sourcePDF]);
};

var pdftopng  = new PdfToPng();
module.exports = pdftopng;

/*
function pdftopng() {}

pdftopng.prototype.getPage = function(obj){
  exec(function(reply){
    obj.success(reply);
  }, function(err){
    obj.fail(err);
  }, "pdftopng", "getPage", [
    obj.pdf,
    obj.page,
    obj.width,
    obj.height,
    obj.autoRelease
  ]);
};

pdftopng.prototype.countPages = function(obj){
  exec(function(reply){
    obj.success(reply);
  }, function(err){
    obj.fail(err);
  }, "pdftopng", "countPages", [
    obj.pdf
  ]);
};


pdftopng.prototype.closePDF = function(obj){
  exec(function(reply){
    obj.success(reply);
  }, function(err){
    obj.fail(err);
  }, "pdftopng", "closePDF", [

  ]);
};



pdftopng.prototype.getPageInForeground = function(obj){
        exec(
            function(reply){ callback(reply); },
            function(err){ callback('Error: '+err); }
        , "Pdf2png", "getPageInForeground", [obj.pdf,obj.page,obj.width,obj.height,obj.autoRelease,obj.targetDirectory]);
};


var pdftopng = new pdftopng();
module.exports = pdftopng;
*/
