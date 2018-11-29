exec = require('cordova/exec');

function Pdf2png() {}


Pdf2png.prototype.getPage = function(obj){
  exec(function(reply){
    obj.success(reply);
  }, function(err){
    obj.fail(err);
  }, "Pdf2png", "getPage", [
    obj.pdf,
    obj.page,
    obj.width,
    obj.height,
    obj.autoRelease
  ]);
};

Pdf2png.prototype.countPages = function(obj){
  exec(function(reply){
    obj.success(reply);
  }, function(err){
    obj.fail(err);
  }, "Pdf2png", "countPages", [
    obj.pdf
  ]);
};


Pdf2png.prototype.closePDF = function(obj){
  exec(function(reply){
    obj.success(reply);
  }, function(err){
    obj.fail(err);
  }, "Pdf2png", "closePDF", [

  ]);
};



Pdf2png.prototype.getPageInForeground = function(obj){
        exec(
            function(reply){ callback(reply); },
            function(err){ callback('Error: '+err); }
        , "Pdf2png", "getPageInForeground", [obj.pdf,obj.page,obj.width,obj.height,obj.autoRelease,obj.targetDirectory]);
};


var pdf2png = new Pdf2png();
module.exports = pdf2png;
