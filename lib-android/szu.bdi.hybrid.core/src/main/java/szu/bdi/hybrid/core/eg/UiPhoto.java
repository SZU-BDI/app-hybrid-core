package szu.bdi.hybrid.core.eg;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.webkit.MimeTypeMap;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Locale;

import info.cmptech.JSO;
import szu.bdi.hybrid.core.ExifHelper;
import szu.bdi.hybrid.core.HybridTools;
import szu.bdi.hybrid.core.NativeUi;

public class UiPhoto extends NativeUi {
    final public static String REQUEST_CAMERA = "camera";
    final public static String REQUEST_ALBUM = "album";
    final public static int REQUEST_CODE_CAMERA = 1;
    final public static int REQUEST_CODE_ALBUM = 2;
    final private static String LOGTAG = new Throwable().getStackTrace()[0].getClassName();
    private static final int JPEG = 0;                  // Take a picture of type JPEG
    private static final int PNG = 1;                   // Take a picture of type PNG
    private ExifHelper exifData;            // Exif data from source
    //private int encodingType = 0;               // Type of encoding to use
    //private boolean orientationCorrected = true;   // Has the picture's orientation been corrected
    //    private int mQuality;                   // Compression quality hint (0-100: 0=low quality & high compression, 100=compress of max quality)
    private int targetWidth;                // desired width of the image
    private int targetHeight;               // desired height of the image
    private boolean correctOrientation = false;     // Should the pictures orientation be corrected

    private static void createWritableFile(File file) throws IOException {
        file.createNewFile();
        file.setWritable(true, false);
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     * @author paulburke
     */
    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {
        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {column};

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {

                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } catch (Exception e) {
            return null;
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     * @author paulburke
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     * @author paulburke
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     * @author paulburke
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static String getRealPathFromURI_API11_And_Above(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {

            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {

            // Return the remote address
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();

            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    public static String getRealPathFromURI_BelowAPI11(Context context, Uri contentUri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        String result = null;

        try {
            Cursor cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            result = cursor.getString(column_index);

        } catch (Exception e) {
            result = null;
        }
        return result;
    }

    public static String getRealPath(String uriString, Activity activity) {
        return getRealPath(Uri.parse(uriString), activity);
    }

    public static String getRealPath(Uri uri, Activity activity) {
        String realPath = null;

        if (Build.VERSION.SDK_INT < 11)
            realPath = getRealPathFromURI_BelowAPI11(activity, uri);

            // SDK >= 11
        else
            realPath = getRealPathFromURI_API11_And_Above(activity, uri);

        return realPath;
    }

    public static InputStream getInputStreamFromUriString(String uriString, Activity activity)
            throws IOException {
        InputStream returnValue = null;
        if (uriString.startsWith("content")) {
            Uri uri = Uri.parse(uriString);
            returnValue = activity.getContentResolver().openInputStream(uri);
        } else if (uriString.startsWith("file://")) {
            int question = uriString.indexOf("?");
            if (question > -1) {
                uriString = uriString.substring(0, question);
            }
            if (uriString.startsWith("file:///android_asset/")) {
                Uri uri = Uri.parse(uriString);
                String relativePath = uri.getPath().substring(15);
                returnValue = activity.getAssets().open(relativePath);
            } else {
                // might still be content so try that first
                try {
                    returnValue = activity.getContentResolver().openInputStream(Uri.parse(uriString));
                } catch (Exception e) {
                    returnValue = null;
                }
                if (returnValue == null) {
                    returnValue = new FileInputStream(getRealPath(uriString, activity));
                }
            }
        } else {
            returnValue = new FileInputStream(uriString);
        }
        return returnValue;
    }

    public static String stripFileProtocol(String uriString) {
        if (uriString.startsWith("file://")) {
            uriString = uriString.substring(7);
        }
        return uriString;
    }

    public static String getMimeTypeForExtension(String path) {
        String extension = path;
        int lastDot = extension.lastIndexOf('.');
        if (lastDot != -1) {
            extension = extension.substring(lastDot + 1);
        }
        // Convert the URI string to lower case to ensure compatibility with MimeTypeMap (see CB-2185).
        extension = extension.toLowerCase(Locale.getDefault());
        if (extension.equals("3ga")) {
            return "audio/3gpp";
        }
        return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
    }

    public static String getMimeType(String uriString, Activity activity) {
        String mimeType = null;

        Uri uri = Uri.parse(uriString);
        if (uriString.startsWith("content://")) {
            mimeType = activity.getContentResolver().getType(uri);
        } else {
            mimeType = getMimeTypeForExtension(uri.getPath());
        }

        return mimeType;
    }

    public static int calculateSampleSize(int srcWidth, int srcHeight, int dstWidth, int dstHeight) {
        final float srcAspect = (float) srcWidth / (float) srcHeight;
        final float dstAspect = (float) dstWidth / (float) dstHeight;

        if (srcAspect > dstAspect) {
            return srcWidth / dstWidth;
        } else {
            return srcHeight / dstHeight;
        }
    }

    public void getImage() {
        Intent intent = new Intent();
        String title = "Select Photo";
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        this.startActivityForResult(
                Intent.createChooser(intent, new String(title)),
                REQUEST_CODE_ALBUM);//@link onActivityResult()
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String srcType = getUiData("from").asString();
        if (REQUEST_CAMERA.equals(srcType)) {
            this.takePicture();
        } else if (REQUEST_ALBUM.equals(srcType)) {
            this.getImage();
        } else {
            JSO rt = new JSO();
            rt.setChild("STS", "KO");
            rt.setChild("errmsg", "Need param 'from'");
            closeUi(rt);
        }
    }

    private void takePicture() {

        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);

        // Specify file so that large image is captured and returned
        File photo = new File(HybridTools.getTempDirectoryPath(), "Capture.jpg");
        try {
            // the ACTION_IMAGE_CAPTURE is run under different credentials and has to be granted write permissions
            createWritableFile(photo);
        } catch (IOException ex) {
            ex.printStackTrace();
            return;
        }
        intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, Uri.fromFile(photo));

        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);//do top

        startActivityForResult(intent, REQUEST_CODE_CAMERA);
    }

    private void writeUncompressedImage(InputStream fis, Uri dest) throws FileNotFoundException,
            IOException {
        OutputStream os = null;
        try {
            os = this.getContentResolver().openOutputStream(dest);
            byte[] buffer = new byte[4096];
            int len;
            while ((len = fis.read(buffer)) != -1) {
                os.write(buffer, 0, len);
            }
            os.flush();
        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    Log.v(LOGTAG, "Exception while closing output stream.");
                }
            }
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    Log.v(LOGTAG, "Exception while closing file input stream.");
                }
            }
        }
    }

//    private void writeUncompressedImage(Uri src, Uri dest) throws FileNotFoundException,
//            IOException {
//
//        FileInputStream fis = new FileInputStream(stripFileProtocol(src.toString()));
//        writeUncompressedImage(fis, dest);
//
//    }
//
//    private String getMimetypeForFormat(int outputFormat) {
//        if (outputFormat == PNG) return "image/png";
//        if (outputFormat == JPEG) return "image/jpeg";
//        return "";
//    }

    private int exifToDegrees(int exifOrientation) {
        if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) {
            return 90;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {
            return 180;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {
            return 270;
        } else {
            return 0;
        }
    }

    public int[] calculateAspectRatio(int origWidth, int origHeight) {
        int newWidth = this.targetWidth;
        int newHeight = this.targetHeight;

        // If no new width or height were specified return the original bitmap
        if (newWidth <= 0 && newHeight <= 0) {
            newWidth = origWidth;
            newHeight = origHeight;
        }
        // Only the width was specified
        else if (newWidth > 0 && newHeight <= 0) {
            newHeight = (int) ((double) (newWidth / (double) origWidth) * origHeight);
        }
        // only the height was specified
        else if (newWidth <= 0 && newHeight > 0) {
            newWidth = (int) ((double) (newHeight / (double) origHeight) * origWidth);
        }
        // If the user specified both a positive width and height
        // (potentially different aspect ratio) then the width or height is
        // scaled so that the image fits while maintaining aspect ratio.
        // Alternatively, the specified width and height could have been
        // kept and Bitmap.SCALE_TO_FIT specified when scaling, but this
        // would result in whitespace in the new image.
        else {
            double newRatio = newWidth / (double) newHeight;
            double origRatio = origWidth / (double) origHeight;

            if (origRatio > newRatio) {
                newHeight = (newWidth * origHeight) / origWidth;
            } else if (origRatio < newRatio) {
                newWidth = (newHeight * origWidth) / origHeight;
            }
        }

        int[] retval = new int[2];
        retval[0] = newWidth;
        retval[1] = newHeight;
        return retval;
    }

    private Bitmap getScaledAndRotatedBitmap(String imageUrl) throws IOException {
        // If no new width or height were specified, and orientation is not needed return the original bitmap
        if (this.targetWidth <= 0 && this.targetHeight <= 0 && !(this.correctOrientation)) {
            InputStream fileStream = null;
            Bitmap image = null;
            try {
                fileStream = getInputStreamFromUriString(imageUrl, this);
                image = BitmapFactory.decodeStream(fileStream);
            } finally {
                if (fileStream != null) {
                    try {
                        fileStream.close();
                    } catch (IOException e) {
                        Log.v(LOGTAG, "Exception while closing file input stream.");
                    }
                }
            }
            return image;
        }

        /*  Copy the inputstream to a temporary file on the device.
            We then use this temporary file to determine the width/height/orientation.
            This is the only way to determine the orientation of the photo coming from 3rd party providers (Google Drive, Dropbox,etc)
            This also ensures we create a scaled bitmap with the correct orientation

             We delete the temporary file once we are done
         */
        File localFile = null;
        Uri galleryUri = null;
        int rotate = 0;
        try {
            InputStream fileStream = getInputStreamFromUriString(imageUrl, this);
            if (fileStream != null) {
                // Generate a temporary file
                //String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                //String fileName = "IMG_" + timeStamp + ".jpg";

                String fileName = "getScaledAndRotatedBitmap.tmp.jpg";
                localFile = new File(HybridTools.getTempDirectoryPath() + fileName);
                galleryUri = Uri.fromFile(localFile);
                writeUncompressedImage(fileStream, galleryUri);
                try {
                    String mimeType = getMimeType(imageUrl.toString(), this);
                    if ("image/jpeg".equalsIgnoreCase(mimeType)) {
                        //  ExifInterface doesn't like the file:// prefix
                        String filePath = galleryUri.toString().replace("file://", "");
                        // read exifData of source
                        exifData = new ExifHelper();
                        exifData.createInFile(filePath);
                        // Use ExifInterface to pull rotation information
                        if (this.correctOrientation) {
                            ExifInterface exif = new ExifInterface(filePath);
                            rotate = exifToDegrees(exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED));
                        }
                    }
                } catch (Exception oe) {
                    Log.v(LOGTAG, "Unable to read Exif data: " + oe.toString());
                    rotate = 0;
                }
            }
        } catch (Exception e) {
            Log.e(LOGTAG, "Exception while getting input stream: " + e.toString());
            return null;
        }


        try {
            // figure out the original width and height of the image
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            InputStream fileStream = null;
            try {
                fileStream = getInputStreamFromUriString(galleryUri.toString(), this);
                BitmapFactory.decodeStream(fileStream, null, options);
            } finally {
                if (fileStream != null) {
                    try {
                        fileStream.close();
                    } catch (IOException e) {
                        Log.v(LOGTAG, "Exception while closing file input stream.");
                    }
                }
            }

            //CB-2292: WTF? Why is the width null???
            if (options.outWidth == 0 || options.outHeight == 0) {
                return null;
            }

            // User didn't specify output dimensions, but they need orientation
            if (this.targetWidth <= 0 && this.targetHeight <= 0) {
                this.targetWidth = options.outWidth;
                this.targetHeight = options.outHeight;
            }

            // Setup target width/height based on orientation
            int rotatedWidth, rotatedHeight;
            boolean rotated = false;
            if (rotate == 90 || rotate == 270) {
                rotatedWidth = options.outHeight;
                rotatedHeight = options.outWidth;
                rotated = true;
            } else {
                rotatedWidth = options.outWidth;
                rotatedHeight = options.outHeight;
            }

            // determine the correct aspect ratio
            int[] widthHeight = calculateAspectRatio(rotatedWidth, rotatedHeight);

            // Load in the smallest bitmap possible that is closest to the size we want
            options.inJustDecodeBounds = false;
            options.inSampleSize = calculateSampleSize(rotatedWidth, rotatedHeight, widthHeight[0], widthHeight[1]);
            Bitmap unscaledBitmap = null;
            try {
                fileStream = getInputStreamFromUriString(galleryUri.toString(), this);
                unscaledBitmap = BitmapFactory.decodeStream(fileStream, null, options);
            } finally {
                if (fileStream != null) {
                    try {
                        fileStream.close();
                    } catch (IOException e) {
                        Log.v(LOGTAG, "Exception while closing file input stream.");
                    }
                }
            }
            if (unscaledBitmap == null) {
                return null;
            }

            int scaledWidth = (!rotated) ? widthHeight[0] : widthHeight[1];
            int scaledHeight = (!rotated) ? widthHeight[1] : widthHeight[0];

            Bitmap scaledBitmap = Bitmap.createScaledBitmap(unscaledBitmap, scaledWidth, scaledHeight, true);
            if (scaledBitmap != unscaledBitmap) {
                unscaledBitmap.recycle();
                unscaledBitmap = null;//release it like js
            }
            if (this.correctOrientation && (rotate != 0)) {
                Matrix matrix = new Matrix();
                matrix.setRotate(rotate);
                try {
                    scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true);
                    //this.orientationCorrected = true;
                } catch (OutOfMemoryError oom) {
                    //this.orientationCorrected = false;
                }
            }
            return scaledBitmap;
        } finally {
            // delete the temporary copy
            if (localFile != null) {
                localFile.delete();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        JSO jso = new JSO();

        if (this.RESULT_OK == resultCode) {
            String srcPhoto = null;
            if (this.REQUEST_CODE_ALBUM == requestCode) {
                if (null != intent) {
                    Uri uri = intent.getData();
                    srcPhoto = uri.toString();
                }
            } else if (this.REQUEST_CODE_CAMERA == requestCode) {
                srcPhoto = HybridTools.getTempDirectoryPath() + "/Capture.jpg";
            }

            String fileToUpload = null;
            InputStream fileStream = null;
            try {

                Bitmap image = null;
                if (srcPhoto.startsWith("content:")) {
                    Uri uuu = Uri.parse(srcPhoto);
                    fileStream = this.getContentResolver().openInputStream(uuu);
                } else if (srcPhoto.startsWith("file:///android_asset/")) {
                    Uri uuu = Uri.parse(srcPhoto);
                    String relativePath = srcPhoto.substring(15);
                    fileStream = this.getAssets().open(relativePath);
                } else {
                    fileStream = new FileInputStream(srcPhoto);
                }
                image = BitmapFactory.decodeStream(fileStream);
                int w0 = image.getWidth();
                int h0 = image.getHeight();
                jso.setChild("w0", "" + w0);
                jso.setChild("h0", "" + h0);
                if (w0 > 1920 || h0 > 1080) {
                    this.targetWidth = 1920;
                    this.targetHeight = 1080;
                }

                Bitmap bitmap = getScaledAndRotatedBitmap(srcPhoto);
                int w = bitmap.getWidth();
                int h = bitmap.getHeight();
                jso.setChild("w", "" + w);
                jso.setChild("h", "" + h);

                File tgtFile = new File(HybridTools.getTempDirectoryPath(), "ToUpload.jpg");
                Uri uriTgt = Uri.fromFile(tgtFile);

                // Add compressed version of captured image to returned media store Uri
                OutputStream os = this.getContentResolver().openOutputStream(uriTgt);
                Bitmap.CompressFormat compressFormat = Bitmap.CompressFormat.JPEG;

                //write bitmap to output stream with compress+quality
                bitmap.compress(compressFormat, 90, os);
                os.close();
                if (tgtFile.exists()) {
                    FileInputStream fis = new FileInputStream(tgtFile);
                    int tgtFileSize = fis.available();
                    jso.setChild("size", "" + tgtFileSize);
                }
                // Restore exif data to file
                String exifPath;
                fileToUpload = exifPath = uriTgt.getPath();

                ExifHelper exif = new ExifHelper();
                exif.createOutFile(exifPath);
                exif.writeExifData();

            } catch (Throwable ex) {
                ex.printStackTrace();
            } finally {
                if (fileStream != null) {
                    try {
                        fileStream.close();
                    } catch (IOException e) {
                        Log.v(LOGTAG, "Exception while closing file input stream.");
                    }
                }
            }
            if (HybridTools.isEmptyString(fileToUpload)) {
                jso.setChild("STS", "KO");
                jso.setChild("file", srcPhoto);
            } else {
                jso.setChild("STS", "OK");
                jso.setChild("orig", srcPhoto);
                jso.setChild("file", fileToUpload);

                //TODO
                //String uploadUrl = getUiData("upload").asString();
                //app_web post to uploadUrl with data
            }
        } else if (this.RESULT_CANCELED == resultCode) {
            jso.setChild("STS", "CANCEL");
        } else {
            jso.setChild("STS", "KO");
            jso.setChild("code", "" + resultCode);
        }

        closeUi(jso);
    }
}
