package com.bendspoons.cordova.plugin;

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

public class Pdf2png extends CordovaPlugin {
    File sourceFile;
    PdfRenderer renderer = null;

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {

        if (action.equals("getPage")) {
            String inputFile;
            Integer usePage = 1;
            Integer useHeight = 480;
            Integer useWidth = 320;
            Boolean autoRelease = true;

            String imageBase64Data;

            if(args.isNull(0)) {
                callbackContext.error("Input PDF missing");
                return false;
            }

            if(args.isNull(1)) {
                callbackContext.error("Page Parameter missing");
                return false;
            }

            if(args.isNull(2)) {
                callbackContext.error("Width Parameter missing");
                return false;
            }

            if(args.isNull(3)) {
                callbackContext.error("Height Parameter missing");
                return false;
            }

            if(!args.isNull(4)) {
                autoRelease = args.getBoolean(4);
            }

            inputFile = args.getString(0).replace("file:///", "/");
            usePage = args.getInt(1)-1;
            useHeight = args.getInt(3);
            useWidth = args.getInt(2);

            Log.i("************** inputFile", inputFile);
            Log.i("************** usePage", usePage.toString());
            Log.i("************** useWidth", useWidth.toString());
            Log.i("************** useHeight", useHeight.toString());
            Log.i("************** autoRelease auto", autoRelease.toString());

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

                    //JSONObject jsonObj = new JSONObject();
                    //jsonObj.put("page", usePage+1);

                    if(autoRelease) {
                        renderer.close();
                        renderer = null;
                        //jsonObj.put("pdf_released", true);
                    } else {
                        //jsonObj.put("pdf_released", false);
                    }

                    //jsonObj.put("base64", imageBase64Data);

                    //callbackContext.success(jsonObj);
                    callbackContext.success(imageBase64Data);
                    return true;
                } catch (Exception e) {
                    e.printStackTrace();
                    callbackContext.error("Page \"" + usePage + "\" not found");
                    return false;
                }
            } catch (Exception e) {
                callbackContext.error("Source PDF unavailable, assure full qualified path, e.g. file:///emulated/..., given source: " + inputFile);
                return false;
            }
        } else if (action.equals("closePDF")) {
            if (renderer == null) {
                callbackContext.error("PDF already closed");
                return false;
            } else {
                try {
                    renderer.close();
                    renderer = null;
                    callbackContext.success("PDF has been closed");
                } catch (Exception e) {
                    callbackContext.error("Could not close PDF");
                    return false;
                }
            }
        } else if (action.equals("countPages")) {
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
                    e.printStackTrace();
                    callbackContext.error("PDF could not be opened");
                    return false;
                }

            } catch (Exception e) {
                callbackContext.error("Source PDF unavailable, assure full qualified path, e.g. fil:///emulated/..., given source: " + inputFile);
                return false;
            }
        } else if (action.equals("getPageInForeground")) {
            callbackContext.error("Feature not implemented for Android");
            return true;
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
