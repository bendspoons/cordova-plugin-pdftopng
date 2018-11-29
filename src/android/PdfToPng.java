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

            Integer useHeight = 480;
            Integer useWidth = 320;

            String outputType = "";
            Boolean outputBase64 = true;
            Boolean outputSave = false;

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

            if(args.isNull(2)) {
              JSONObject errorObj = new JSONObject();
              errorObj.put("error", " Parameter is missing");
              errorObj.put("parameter",  "Width");

              callbackContext.error(errorObj);
              return false;
            }

            if(args.isNull(3)) {
              JSONObject errorObj = new JSONObject();
              errorObj.put("error", " Parameter is missing");
              errorObj.put("parameter",  "height");

              callbackContext.error(errorObj);
              return false;
            }

            if(!args.isNull(4)) {
                outputType = args.getString(4);
                if(outputType.equals("base64")) {
                  outputBase64 = true;
                  outputSave = false;
                } else if(outputType.equals("file")) {

                    targetFileDir = args.getString(5);
                    targetFileName = args.getString(6);

                  if(args.isNull(5) || targetFileDir.isEmpty()) {
                    JSONObject errorObj = new JSONObject();
                    errorObj.put("error", "output Parameter is \"file\", Parameter missing");
                    errorObj.put("parameter",  "targetFileDir");

                    callbackContext.error(errorObj);
                    return false;
                  }

                    if(args.isNull(6) || targetFileName.isEmpty()) {
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
            useHeight = args.getInt(3);
            useWidth = args.getInt(2);

            Log.i("************** inputFile", inputFile);
            Log.i("************** usePage", usePage.toString());
            Log.i("************** useWidth", useWidth.toString());
            Log.i("************** useHeight", useHeight.toString());
            Log.i("************** outputType", outputType);
            Log.i("************** targetFileDir", targetFileDir);
            Log.i("************** targetFileName", targetFileName);

            try {
                sourceFile = new File(inputFile);

                renderer = new PdfRenderer(ParcelFileDescriptor.open(sourceFile, ParcelFileDescriptor.MODE_READ_ONLY));

                try {
                    Bitmap preBitmap = Bitmap.createBitmap(useWidth, useHeight, Bitmap.Config.ARGB_4444);

                    Bitmap bitmap = Bitmap.createBitmap(preBitmap.getWidth(), preBitmap.getHeight(), preBitmap.getConfig());

                    Canvas canvas = new Canvas(bitmap);
                    canvas.drawColor(Color.WHITE);
                    canvas.drawBitmap(preBitmap, 0, 0, null);

                    Page page = renderer.openPage(usePage);

                    Rect rect = new Rect(0, 0, useWidth, useHeight);

                    page.render(bitmap, rect, null, Page.RENDER_MODE_FOR_DISPLAY);

                    page.close();

                    imageBase64Data = convert(bitmap);

                    JSONObject jsonObj = new JSONObject();
                    jsonObj.put("page", usePage+1);

                    jsonObj.put("base64", imageBase64Data);

                    callbackContext.success(jsonObj);
                    return true;
                } catch (Exception e) {
                    JSONObject errorObj = new JSONObject();
                    errorObj.put("error", "Page not found in PDF");
                    errorObj.put("page",  usePage);
                    errorObj.put("file",  inputFile);

                    callbackContext.error(errorObj);
                    return false;
                }
            } catch (Exception e) {
                JSONObject errorObj = new JSONObject();
                errorObj.put("error", "Source PDF unavailable, assure full qualified path, e.g. file:///emulated/...");
                errorObj.put("file",  inputFile);

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

                    callbackContext.error(errorObj);
                    return false;
                }

            } catch (Exception e) {
                JSONObject errorObj = new JSONObject();
                errorObj.put("error", "Source PDF unavailable, assure full qualified path, e.g. file:///emulated/...");
                errorObj.put("file",  inputFile);

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
