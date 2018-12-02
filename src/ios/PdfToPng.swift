let pluginVersion = "0.3.0";
let pluginCode = "HansguckindieLuft";
let pluginAuthor = "Dominic Roesmann <dominic.roesmann@googlemail.com>";

@objc(PdfToPng) class PdfToPng : CDVPlugin {
  @objc(versioninfo:)
  func versioninfo(command: CDVInvokedUrlCommand) {
    print("PdfToPng :: versioninfo")

    let jsonObject: [AnyHashable: Any] = [
        "version": pluginVersion,
        "code": pluginCode,
        "author": pluginAuthor
    ]

    let pluginResult = CDVPluginResult(
        status: CDVCommandStatus_OK,
        messageAs: jsonObject
    )

    self.commandDelegate!.send(
        pluginResult,
        callbackId: command.callbackId
    )
    /* return jsondata as sting
    let jsonObject: [String: Any] = [
        "version": "0.3.0",
        "code": "HansguckindieLuft",
        "author": "Dominic Roesmann <dominic.roesmann@googlemail.com>"
    ]

    let jsonData: NSData

    do {
        jsonData = try JSONSerialization.data(withJSONObject: jsonObject, options: JSONSerialization.WritingOptions()) as NSData
        let versionResult = NSString(data: jsonData as Data, encoding: String.Encoding.utf8.rawValue) as! String

        print("json string = \(versionResult)")

        pluginResult = CDVPluginResult(
            status: CDVCommandStatus_OK,
            messageAs: jsonData
        )

    } catch _ {
        print("PdfToPng :: versioninfo -> json error")
    }
     */
  }

  @objc(getPage:)
  func getPage(command: CDVInvokedUrlCommand) {
    var pluginResult = CDVPluginResult(
      status: CDVCommandStatus_ERROR
    )

    print("PdfToPng :: getPage")

    var inputFile: String;

    var usePage: Int = 1;

    var useWidth: Int = 0;
    var useHeight: Int = 0;

    var useDpi: Int = 0;
    var calcWithDpi: Bool = false;

    var dpiCalcWidthF: Double = 8.2677165354330708661417322834646;
    var dpiCalcHeightF: Double = 11.692913385826771653543307086614;

    var outputType: String = "base64";

    var targetFileDir: String = "";
    var targetFileName: String = "";

    var imageBase64Data: String;

    var createPng: Bool = false;

    // check file param
    inputFile = command.arguments[0] as? String ?? ""
    print("PdfToPng :: inputFile -> " + inputFile)
    if(!inputFile.isEmpty) {
        // check page param
        usePage = command.arguments[1] as? Int ?? -1
        print("PdfToPng :: usePage -> " + String(usePage))
        if(usePage >= 0) {
            createPng = true;

            // check width param
            useWidth = command.arguments[2] as? Int ?? 0
            print("PdfToPng :: useWidth -> " + String(useWidth))

            // check height param
            useHeight = command.arguments[3] as? Int ?? 0
            print("PdfToPng :: useHeight -> " + String(useHeight))

            // check dpi param
            useDpi = command.arguments[4] as? Int ?? 0
            print("PdfToPng :: useDpi -> " + String(useDpi))
            if(useDpi > 0) {
                useWidth = 0;
                useHeight = 0;

                calcWithDpi = true;
            }

            // check type param
            outputType = command.arguments[5] as? String ?? "base64"
            print("PdfToPng :: outputType -> " + outputType)

            if(outputType == "base64") {
                // no further processing whatsoever
            } else if(outputType == "file") {
                // check targetFileDir param
                targetFileDir = command.arguments[6] as? String ?? ""
                print("PdfToPng :: targetFileDir -> " + targetFileDir)

                if(targetFileDir.isEmpty) {
                    let jsonObject: [AnyHashable: Any] = [
                        "error": "output Parameter is \"file\", Parameter missing",
                        "parameter": "targetFileDir"
                    ]

                    pluginResult = CDVPluginResult(
                        status: CDVCommandStatus_ERROR,
                        messageAs: jsonObject
                    )

                    createPng = false;
                }

                // check targetFileName param
                targetFileName = command.arguments[7] as? String ?? ""
                print("PdfToPng :: targetFileName -> " + targetFileName)

                if(targetFileName.isEmpty) {
                    let jsonObject: [AnyHashable: Any] = [
                        "error": "output Parameter is \"file\", Parameter missing",
                        "parameter": "targetFileName"
                    ]

                    pluginResult = CDVPluginResult(
                        status: CDVCommandStatus_ERROR,
                        messageAs: jsonObject
                    )

                    createPng = false;
                } else if(!targetFileName.hasSuffix(".png")) {
                    let jsonObject: [AnyHashable: Any] = [
                        "error": "targetFileName Parameter invalid file extension",
                        "file": targetFileName,
                        "allowed": ".png"
                    ]

                    pluginResult = CDVPluginResult(
                        status: CDVCommandStatus_ERROR,
                        messageAs: jsonObject
                    )

                    createPng = false;
                }

            } else {
                let jsonObject: [AnyHashable: Any] = [
                    "error": "output Parameter is invalid",
                    "parameter": outputType,
                    "parameter": "base64|file"
                ]

                pluginResult = CDVPluginResult(
                    status: CDVCommandStatus_ERROR,
                    messageAs: jsonObject
                )

                createPng = false;
            }

            print("PdfToPng :: !!! createPng -> " + String(createPng))

            // create png
            if(createPng) {
                let pdfUrl = URL(fileURLWithPath: inputFile)
                do {
                    let document = CGPDFDocument(pdfUrl as CFURL);

                    let pages = document?.numberOfPages;
                    let page = document?.page(at: usePage)

                    let pageRect = page?.getBoxRect(.mediaBox)

                    if(calcWithDpi) {
                        print("PdfToPng :: calcWithDpi");
                        useWidth = Int(Double(dpiCalcWidthF) * Double(useDpi));
                        useHeight = Int(Double(dpiCalcHeightF) * Double(useDpi));
                    } else {
                        if(useWidth == 0) {
                            useWidth = Int((pageRect?.size.width)!);
                        }

                        if(useHeight == 0) {
                            useHeight = Int((pageRect?.size.height)!);
                        }
                    }

                    let scaleDpi = Float(useWidth)/Float(dpiCalcWidthF);
                    print(scaleDpi);
                    let scale = scaleDpi / 72.0
                    print(scale);

                    let format = UIGraphicsImageRendererFormat()
                    format.scale = CGFloat(scale)

                    let renderer = UIGraphicsImageRenderer(size: CGSize(width: useWidth, height: useHeight), format: format)
                    let img = renderer.image { ctx in
                        UIColor.white.set()
                        ctx.fill(CGRect(x: 0, y: 0, width: useWidth, height: useHeight))

                        ctx.cgContext.translateBy(x: 0.0, y: CGFloat(useHeight))

                        ctx.cgContext.scaleBy(x: CGFloat(scale), y: CGFloat(scale * -1))

                        ctx.cgContext.drawPDFPage(page!)
                    }

                    if(outputType == "file") {

                        let jsonObject: [AnyHashable: Any] = [
                            "pages": pages,
                            "page": usePage,
                            "width": useWidth,
                            "height": useHeight,
                            "size": 12344,
                            "outputDirectory": targetFileDir,
                            "filename": targetFileName,
                        ]

                        pluginResult = CDVPluginResult(
                            status: CDVCommandStatus_OK,
                            messageAs: jsonObject
                        )

                        self.commandDelegate!.send(
                            pluginResult,
                            callbackId: command.callbackId
                        )
                    } else { // base64
                        let imageData:NSData = UIImagePNGRepresentation(img)! as NSData
                        let imageStr = imageData.base64EncodedString(options: NSData.Base64EncodingOptions(rawValue: 0))

                        var approxSize: Double = Double(imageStr.lengthOfBytes(using: String.Encoding.utf8))
                        approxSize = approxSize * 0.75;
                        approxSize = approxSize-2;

                        let jsonObject: [AnyHashable: Any] = [
                            "pages": pages,
                            "page": usePage,
                            "width": useWidth,
                            "height": useHeight,
                            "size": approxSize,
                            "base64": imageStr
                        ]

                        pluginResult = CDVPluginResult(
                            status: CDVCommandStatus_OK,
                            messageAs: jsonObject
                        )

                        self.commandDelegate!.send(
                            pluginResult,
                            callbackId: command.callbackId
                        )
                    }
                } catch let e {
                    let jsonObject: [AnyHashable: Any] = [
                        "error": "Source PDF unavailable, assure full qualified path",
                        "file": inputFile,
                        "exception": e
                    ]

                    pluginResult = CDVPluginResult(
                        status: CDVCommandStatus_ERROR,
                        messageAs: jsonObject
                    )

                    self.commandDelegate!.send(
                        pluginResult,
                        callbackId: command.callbackId
                    )
                }
            } else {
                self.commandDelegate!.send(
                    pluginResult,
                    callbackId: command.callbackId
                )
            }

        } else {
            let jsonObject: [AnyHashable: Any] = [
                "error": "Parameter is missing",
                "parameter": "page"
            ]

            pluginResult = CDVPluginResult(
                status: CDVCommandStatus_ERROR,
                messageAs: jsonObject
            )

            self.commandDelegate!.send(
                pluginResult,
                callbackId: command.callbackId
            )
        }
    } else {
        let jsonObject: [AnyHashable: Any] = [
            "error": "Parameter is missing",
            "parameter": "sourcePDF"
        ]

        pluginResult = CDVPluginResult(
            status: CDVCommandStatus_ERROR,
            messageAs: jsonObject
        )

        self.commandDelegate!.send(
            pluginResult,
            callbackId: command.callbackId
        )
    }
  }

  @objc(countPages:)
  func countPages(command: CDVInvokedUrlCommand) {
    let pluginResult = CDVPluginResult(
      status: CDVCommandStatus_ERROR
    )

    print("PdfToPng :: countPages")
  }
}
