cordova-plugin-pdftopng
======================

Install via ``` cordova plugin add https://github.com/bendspoons/cordova-plugin-pdftopng ```

This Plugin allows to extract a page from a given PDF file.

### pdftopng.getPage(obj, success, error) ###

Extracts one selected page from the given PDF file.

Full featured example

```<img src="" id="myImage" /> ```


```
pdftopng.getPage({
	sourcePDF: 'file:///storage/emulated/0/360/security/file.txt',
	page: 1,
	width: 1240,
	height: 1754,
	output: 'file',
	targetDirectory: cordova.file.dataDirectory,
	targetFilename: 'output-image.png'
}, function(success) {
	console.log("pdftopng.getPage SUCCESS");
	console.log(JSON.stringify(success));

	document.getElementById('myImage').src = "data:image/png;base64," + success.base64;
}, function(error) {
	console.log("pdftopng.getPage ERROR");
	console.log(JSON.stringify(error));
);
```

**success**
> returns JSON object 
> ```{"success": {}}```

**error**
> returns JSON object
> ```{"error": {}}```

**Mandatory Parameters**
> sourcePDF, page

sourcePDF

> source of the PDF, full path: file:///...

page

> page of the PDF file

**Optional Parameters**
> width, height, output, targetDirectory, targetFilename

width

> sourcePDF width

height

> sourcePDF height

output

> 'base64'

>> 'base64' returns png as base64 encoded string

>> 'file' saves png to targetDirectory + targetFilename and returns JSON object 

>>> ```{"success": {"targetDirectory": targetDirectory, "targetFilename": targetFilename, "filesize": 123456}}```

>>> ```{"error": {}}```

targetDirectory

> no default value, if output = file, this field is mandatory

targetFilename

> no default value, if output = file, this field is mandatory



### pdftopng.countPages(obj, success, error) ###

```
pdf2png.countPages({
    sourcePDF:"file:///path/to/file/test.pdf"  // mandatory
}, function(success){
  console.log("pdf2png.countPages success");    
  console.log(JSON.stringify(success);     
}, function(error){
  console.log("pdf2png.countPages error");     
  console.log(JSON.stringify(error);     
}
);
```

**success**
> returns JSON object 
> ```{"success": {}}```

**error**
> returns JSON object
> ```{"error": {}}```

**Mandatory Parameters**
> sourcePDF 

## Android Quirks

> Recommended use with Android Studio and gradle 4.4

> Set this in your config.xml, needed for android.graphics.pdf.*, only available from API 21 (Android 5.0)

```<preference name="android-minSdkVersion" value="21" />```

> Needed .jar, automatically copied when plugin is added. If not, copy manually in platforms/android/libs, check gradle files if libs folder is in dependencies section

```java-rt-jar-stubs-1.5.0```

#### TODO ####
* Save as file and return filename
* iOS Swift version
