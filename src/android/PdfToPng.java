package com.bendspoons.pdftopng;

import android.util.Log;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Base64;

import android.os.ParcelFileDescriptor;

import java.io.File;
import java.io.ByteArrayOutputStream;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.pdf.PdfRenderer;
import android.graphics.pdf.PdfRenderer.Page;

public class PdfToPng extends CordovaPlugin {
    File sourceFile;
    PdfRenderer renderer = null;

    final String pluginVersion = "0.3.0";
    final String pluginCode = "HansguckindieLuft";
    final String pluginAuthor = "Dominic Roesmann <dominic.roesmann@googlemail.com>";

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {

/* pluginVersion */
        if (action.equals("versioninfo")) {
            JSONObject successArr = new JSONObject();
            successArr.put("version", pluginVersion);
            successArr.put("code", pluginCode);
            successArr.put("author", pluginAuthor);

          JSONObject successObj = new JSONObject();
          successObj.put("success", successArr);

          callbackContext.success(successObj);
          return true;
/* getPage */
        } else if (action.equals("getPage")) {

            String inputFile;

            Integer usePage = 1;

            Integer useWidth = 0;
            Integer useHeight = 0;

            Integer useDpi = 0;
            Boolean calcWithDpi = false;
            Double dpiCalcWidthF = 8.2677165354330708661417322834646; // A4: 21/2.54 * dpi
            Double dpiCalcHeightF = 11.692913385826771653543307086614d; // A4: 21/2.54 * dpi

            String outputType = "base64";

            String targetFileDir = "";
            String targetFileName = "";

            String imageBase64Data;

            if(args.isNull(0)) {
              JSONObject errorObj = new JSONObject();
              errorObj.put("error", " Parameter is missing");
              errorObj.put("parameter",  "sourcePDF");

              callbackContext.error(errorObj);
              return false;
            }

            if(args.isNull(1)) {
              JSONObject errorObj = new JSONObject();
              errorObj.put("error", " Parameter is missing");
              errorObj.put("parameter",  "page");

              callbackContext.error(errorObj);
              return false;
            }

            if(!args.isNull(2)) {
                useWidth = args.getInt(2);
            }

            if(!args.isNull(3)) {
                useHeight = args.getInt(3);
            }

            // dpi given, overrides width and height
            if(!args.isNull(4)) {
                useDpi = args.getInt(4);
                if(useDpi > 0) {
                    useWidth = 0;
                    useHeight = 0;

                    calcWithDpi = true;
                }
            }

            if(!args.isNull(5)) {
                final String oT = args.getString(5);
                if(oT.equals("base64")) {
                    // no further processing whtsoever
                } else if(oT.equals("file")) {
                    outputType = oT;

                    targetFileDir = args.getString(6);
                    targetFileName = args.getString(7);

                  if(args.isNull(6) || targetFileDir.isEmpty()) {
                    JSONObject errorObj = new JSONObject();
                    errorObj.put("error", "output Parameter is \"file\", Parameter missing");
                    errorObj.put("parameter",  "targetFileDir");

                    callbackContext.error(errorObj);
                    return false;
                  }

                    if(args.isNull(7) || targetFileName.isEmpty()) {
                        JSONObject errorObj = new JSONObject();
                        errorObj.put("error", "output Parameter is \"file\", Parameter missing");
                        errorObj.put("parameter",  "targetFileName");

                        callbackContext.error(errorObj);
                        return false;
                    }

                    if(!targetFileName.endsWith(".png")) {
                        JSONObject errorObj = new JSONObject();
                        errorObj.put("error", "targetFileName Parameter invalid file extension");
                        errorObj.put("file",  targetFileName);
                        errorObj.put("allowed",  ".png");

                        callbackContext.error(errorObj);
                        return false;
                    }

                } else {
                  JSONObject errorObj = new JSONObject();
                  errorObj.put("error", "output Parameter is invalid");
                  errorObj.put("parameter",  outputType);
                  errorObj.put("allowed",  "base64|file");

                  callbackContext.error(errorObj);
                  return false;
                }
            }

            inputFile = args.getString(0).replace("file:///", "/");
            usePage = args.getInt(1)-1;

            Log.i("************** inputFile", inputFile);
            Log.i("************** usePage", usePage.toString());
            Log.i("************** useWidth", useWidth.toString());
            Log.i("************** useHeight", useHeight.toString());
            Log.i("************** useHeight", useHeight.toString());
            Log.i("************** outputType", outputType);
            Log.i("************** targetFileDir", targetFileDir);
            Log.i("************** targetFileName", targetFileName);

            try {
                sourceFile = new File(inputFile);

                renderer = new PdfRenderer(ParcelFileDescriptor.open(sourceFile, ParcelFileDescriptor.MODE_READ_ONLY));

                try {
                    Page page = renderer.openPage(usePage);

                    final int pageCount = renderer.getPageCount();

                    if(useWidth.equals(0) || useHeight.equals(0)) {
                        if(calcWithDpi) {
                            Log.i("************** Calc with dpi", useDpi.toString());

                            Double useWidthT = (dpiCalcWidthF*useDpi);
                            Double useHeightT = (dpiCalcHeightF*useDpi);

                            Log.i("************** useWidthT", useWidthT.toString());
                            Log.i("************** useHeightT", useHeightT.toString());

                            useWidth = useWidthT.intValue();
                            useHeight = useHeightT.intValue();

                            Log.i("************** useWidth FROM PDF width DPI", useWidth.toString());
                            Log.i("************** useHeight FROM PDF width DPI", useHeight.toString());
                        } else {
                            useWidth = page.getWidth();
                            useHeight = page.getHeight();
                            Log.i("************** useWidth FROM PDF", useWidth.toString());
                            Log.i("************** useHeight FROM PDF", useHeight.toString());
                        }
                    }

                    Bitmap preBitmap = Bitmap.createBitmap(useWidth, useHeight, Bitmap.Config.ARGB_4444);

                    Bitmap bitmap = Bitmap.createBitmap(preBitmap.getWidth(), preBitmap.getHeight(), preBitmap.getConfig());

                    Canvas canvas = new Canvas(bitmap);
                    canvas.drawColor(Color.WHITE);
                    canvas.drawBitmap(preBitmap, 0, 0, null);

                    Rect rect = new Rect(0, 0, useWidth, useHeight);

                    page.render(bitmap, rect, null, Page.RENDER_MODE_FOR_DISPLAY);

                    JSONObject jsonObj = new JSONObject();
                    jsonObj.put("pages", pageCount);
                    jsonObj.put("page", (usePage+1));
                    jsonObj.put("width", useWidth.toString());
                    jsonObj.put("height", useHeight.toString());

                    if(outputType.equals("file")) {

                        jsonObj.put("outputDirectory", "");
                        jsonObj.put("filename", "");
                    } else {
                        imageBase64Data = convert(bitmap);

                        final Integer b63str = imageBase64Data.length();
                        Log.i("************** b63str", b63str.toString());
                        final Double approxSize =  ((b63str * 0.75) - 2); // == -> 2, = -> 1

                        jsonObj.put("size", approxSize.toString());

                        jsonObj.put("base64", imageBase64Data);
                    }

                    page.close();

                    callbackContext.success(jsonObj);
                    return true;
                } catch (Exception e) {

                    JSONObject errorObj = new JSONObject();
                    errorObj.put("error", "Page not found in PDF");
                    errorObj.put("page",  usePage);
                    errorObj.put("file",  inputFile);
                    errorObj.put("exception",  e.getMessage());

                    callbackContext.error(errorObj);
                    return false;
                }
            } catch (Exception e) {
                JSONObject errorObj = new JSONObject();
                errorObj.put("error", "Source PDF unavailable, assure full qualified path, e.g. file:///emulated/...");
                errorObj.put("file",  inputFile);
                errorObj.put("exception",  e.getMessage());

                callbackContext.error(errorObj);
                return false;
            }
/* countPages */
        } else if (action.equals("countPages")) {
            if(args.isNull(0)) {
                JSONObject errorObj = new JSONObject();
                errorObj.put("error", " Parameter is missing");
                errorObj.put("parameter",  "sourcePDF");

                callbackContext.error(errorObj);
                return false;
            }

            final String inputFile = args.getString(0).replace("file:///", "/");
            Log.i("************** inputFile", inputFile);

            try {
                sourceFile = new File(inputFile);

                renderer = new PdfRenderer(ParcelFileDescriptor.open(sourceFile, ParcelFileDescriptor.MODE_READ_ONLY));

                try {
                    final int pageCount = renderer.getPageCount();

                    //JSONObject jsonObj = new JSONObject();
                    //jsonObj.put("pages", pageCount);

                    //callbackContext.success(jsonObj);
                    callbackContext.success(pageCount);
                    return true;
                } catch (Exception e) {
                    JSONObject errorObj = new JSONObject();
                    errorObj.put("error", "Could not read Pages from PDF");
                    errorObj.put("file",  inputFile);
                    errorObj.put("exception",  e.getMessage());

                    callbackContext.error(errorObj);
                    return false;
                }

            } catch (Exception e) {
                JSONObject errorObj = new JSONObject();
                errorObj.put("error", "Source PDF unavailable, assure full qualified path, e.g. file:///emulated/...");
                errorObj.put("file",  inputFile);
                errorObj.put("exception",  e.getMessage());

                callbackContext.error(errorObj);
                return false;
            }
        }
        return false;
    }

    public static Bitmap convert(String base64Str) throws IllegalArgumentException {
        byte[] decodedBytes = Base64.decode(
            base64Str.substring(base64Str.indexOf(",")  + 1),
            Base64.DEFAULT
        );

        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }

    public static String convert(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);

        return Base64.encodeToString(outputStream.toByteArray(), Base64.DEFAULT);
    }
}
