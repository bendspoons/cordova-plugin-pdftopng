cordova-plugin-pdftopng
======================

Install via ``` cordova plugin add https://github.com/bendspoons/cordova-plugin-pdftopng ```

This Plugin allows to extract a page from a given PDF file.

### pdftopng.getPage(obj, success, error) ###

Extracts one selected page from the given PDF file.

Example

```<img src="" id="myImage" /> ```


```
pdftopng.getPage({
	sourcePDF: useFile, // mandatory
	page: 1,  // mandatory
	width: 1240,  // mandatory
	height: 1754,  // mandatory
	output: 'file',
	targetDirectory: cordova.file.dataDirectory,
	targetFilename: 'wurst.png'
}, function(success) {
	console.log("++++ getPdfPng SUCCESS");
	console.log(JSON.stringify(success));
	console.log(success);

	$('#myImage').attr('src', "data:image/png;base64," + success.base64);
}, function(error) {
	console.log("++++ getPdfPng ERROR");
	console.log(JSON.stringify(error));
	console.log(error);
);
```

### pdftopng.countPages(obj, success, error) ###

```
pdf2png.countPages({
    sourcePDF:"file:///path/to/file/test.pdf"  // mandatory
}, function(success){
  console.log("countPages success");    
  console.log(JSON.stringify(success);     
}, function(error){
  console.log("countPages error");     
  console.log(JSON.stringify(error);     
}
);
```

## Android Quirks

> Recommended use with Android Studio and gradle 4.4

> Set this in your config.xml, needed for android.graphics.pdf.*, only available from API 21 (Android 5.0)

```<preference name="android-minSdkVersion" value="21" />```

> Needed .jar, automatically copied when plugin is added. If not, copy manually in platforms/android/libs, check gradle files if libs folder is in dependencies section

```java-rt-jar-stubs-1.5.0```

#### TODO ####
* Save as file and return filename
* iOS Swift version
