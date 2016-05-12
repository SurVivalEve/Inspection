package com.example.inspection.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Base64;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
/**
 * Created by Sur.Vival on 10/5/2016.
 */
public class FileWrapper {
    private Context context;
    private Uri uri;
    private File file;

    public enum Storage {
        INTERNAL, EXTERNAL_CACHE;
    }

    public enum Behavior {
        CREATE_ALWAYS, CREATE_IF_NOT_EXISTS, NEVER_CREATE;
    }

    public FileWrapper(Context context, File file) {
        this.file = file;
        this.uri = Uri.fromFile(file);
    }

    public FileWrapper(Context context, Uri uri) {
        this(context, new File(uri.getPath()));
    }

    public FileWrapper(Context context, Storage storage, String path) {
        this(context, new File(getDirOfStorage(context, storage), path));
    }

    public FileWrapper(Context context, String absolutePath) {
        this(context, new File(absolutePath));
    }

    private static File getDirOfStorage(Context context, Storage storage) {
        File dir = null;
        if (storage == Storage.INTERNAL)
            dir = context.getFilesDir();
        else if (storage == Storage.EXTERNAL_CACHE)
            dir = context.getExternalCacheDir();
        return dir;
    }

    public Uri getUri() {
        return uri;
    }

    public File getFile() {
        return file;
    }

    public FileOutputStream getFileOutputStream(Behavior behavior) throws IOException {
        createFileBaseOnBehaviour(behavior);
        return new FileOutputStream(file);
    }

    public FileInputStream getFileInputStream(Behavior behavior) throws IOException {
        createFileBaseOnBehaviour(behavior);
        return new FileInputStream(file);
    }

    private void createFileBaseOnBehaviour(Behavior behavior) throws IOException {
        if (behavior == Behavior.CREATE_ALWAYS) {
            if (file.exists())
                file.delete();
            file.createNewFile();
        } else if (behavior == Behavior.CREATE_IF_NOT_EXISTS && !file.exists())
            file.createNewFile();

    }

    public String getAbsolutePath() {
        return file.getAbsolutePath();
    }

    public Bitmap getBitmap() {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        return BitmapFactory.decodeFile(getAbsolutePath(), options);
    }

    public void copyForm(InputStream inputStream, Behavior behavior) throws IOException {
        OutputStream des = getFileOutputStream(behavior);
        // Transfer bytes from in to out
        byte[] buf = new byte[1024];
        int len;
        while ((len = inputStream.read(buf)) > 0) {
            des.write(buf, 0, len);
        }
        inputStream.close();
        des.close();
    }

    public void copyForm(FileWrapper fileWrapper, Behavior behavior) throws IOException {
        copyForm(fileWrapper.getFileInputStream(Behavior.NEVER_CREATE), behavior);
    }

    public void copyForm(File file, Behavior behavior) throws IOException {
        copyForm(new FileWrapper(context, file), behavior);
    }

    public void copyFrom(Uri uri, Behavior behavior) throws IOException {
        copyForm(new FileWrapper(context, uri), behavior);
    }

    public void copyForm(Storage storage, String path, Behavior behavior) throws IOException {
        copyForm(new FileWrapper(context, storage, path), behavior);
    }

    public void copyForm(byte[] bytes, Behavior behavior) throws IOException {
        copyForm(new ByteArrayInputStream(bytes), behavior);
    }

    public void copyForm(Bitmap bitmap, Bitmap.CompressFormat format, int quality,
                         Behavior behavior) throws IOException {
        FileOutputStream fout = getFileOutputStream(behavior);
        bitmap.compress(format, quality, fout);
    }

    //TODO copy to

    public byte[] getByteArray() throws IOException {
        InputStream is = getFileInputStream(Behavior.NEVER_CREATE);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] b = new byte[1024];
        int bytesRead ;
        while ((bytesRead = is.read(b)) != -1) {
            bos.write(b, 0, bytesRead);
        }
        is.close();
        return bos.toByteArray();
    }

    public String getBase64String() throws IOException {
        return Base64.encodeToString(getByteArray(), Base64.DEFAULT);
    }

    public boolean delete() {
        return file.delete();
    }
}
