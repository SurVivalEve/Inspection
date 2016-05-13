package com.example.inspection;


import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.res.TypedArrayUtils;
import android.support.v7.app.AlertDialog;
import android.util.AttributeSet;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.ExecutionException;

public class DrawEventView extends View{
    Context context;
    Canvas mCanvas;
    Paint tempPaint = new Paint();
    Path tempPath = new Path();
    Paint paint = new Paint();
    Path path = new Path();
    Float rx, ry, x, y, bottom = null, left = null, right = null, top = null;
    ArrayList<Path> paths;
    ArrayList<Paint> paints;
    Gson gson;
    String json;
    String pathsJson;
    String paintsColorJson;
    String paintsWidthJson;
    String paintsEffectJson;
    Collection pathsCollection;
    Collection paintsColorCollection;
    Collection paintsWidthCollection;
    Collection paintsEffectCollection;

    Collection tempPathsCollection;
    Collection tempPaintsColorCollection;
    Collection tempPaintsWidthCollection;
    Collection tempPaintsEffectCollection;
    String newPath;
    Bitmap bitmap;
    Bitmap preparedToSendBitmap;
    LayoutInflater layoutInflater;
    LayoutInflater inflater;
    ViewGroup container;
    LinearLayout lllayer;
    int selectedIndex = -1;
    int layerCount = 0;
    protected int flag = 1;
    private int pastFlag = 1;

    public DrawEventView(Context context, AttributeSet attrs) {
        super(context, attrs);

        this.context = context;
        this.setBackgroundColor(Color.WHITE);
        tempPaint = new Paint();
        tempPaint.setAntiAlias(true);
        tempPaint.setColor(Color.LTGRAY);
        tempPaint.setStrokeJoin(Paint.Join.ROUND);
        tempPaint.setStyle(Paint.Style.STROKE);
        tempPaint.setStrokeWidth(5f);

        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.BLACK);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(5f);

        paths = new ArrayList<Path>();
        paints = new ArrayList<Paint>();
        pathsCollection = new ArrayList<Float>();
        paintsColorCollection = new ArrayList();
        paintsWidthCollection = new ArrayList<Float>();
        paintsEffectCollection = new ArrayList<PathEffect>();

        tempPathsCollection = new ArrayList<Float>();
        tempPaintsColorCollection = new ArrayList();
        tempPaintsWidthCollection = new ArrayList<Float>();
        tempPaintsEffectCollection = new ArrayList<PathEffect>();

        setDrawingCacheEnabled(true);

    }

    public Bitmap preparedToSendBitmap(){
        //preparedToSendBitmap
        try {
            int i = 0;
            preparedToSendBitmap = Bitmap.createBitmap(1080, 1464, Bitmap.Config.ARGB_8888);

            mCanvas = new Canvas(preparedToSendBitmap);
            for (Path p : paths) {
                mCanvas.drawPath(p, paints.get(i));
                i++;
            }
            int x, y;
            x = (int) Math.floor(left) - 50;
            y = (int) Math.floor(top) - 50;
            if (x < 0)
                x = (int) Math.floor(left);
            if (y < 0)
                y = (int) Math.floor(top);

            preparedToSendBitmap = Bitmap.createBitmap(preparedToSendBitmap, x, y, (int) (Math.ceil(right) - Math.floor(left)) + 50, (int) (Math.ceil(bottom) - Math.floor(top)) + 50);
            //mCanvas.clipRect(left+150f, top+150f, right-150f, bottom-150f);
            return preparedToSendBitmap;
        }catch (Exception e){
            return null;
        }
    }


    public static String encodeToBase64(Bitmap image, Bitmap.CompressFormat compressFormat, int quality)
    {
        ByteArrayOutputStream byteArrayOS = new ByteArrayOutputStream();
        image.compress(compressFormat, quality, byteArrayOS);
        return Base64.encodeToString(byteArrayOS.toByteArray(), Base64.DEFAULT);
    }

    public static Bitmap decodeBase64(String input)
    {
        byte[] decodedBytes = Base64.decode(input, 0);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }

    public void initLayer(LayoutInflater layoutInflater, LayoutInflater inflater, ViewGroup container, LinearLayout lllayer){
        this.layoutInflater = layoutInflater;
        this.inflater = inflater;
        this.lllayer = lllayer;
    }

    public void addLayer(ArrayList<Path> ps, ArrayList<Paint> pas){
        FragmentManager fm = ((MainMenu) getContext()).getSupportFragmentManager();
        LinearLayout lye = (LinearLayout) fm.findFragmentByTag("draw").getView().findViewById(R.id.lllayer);
        lye.removeAllViews();
        layerCount = 0;
        for(int i=0; i<ps.size(); i++) {

            Bitmap bm = Bitmap.createBitmap(1080, 1464, Bitmap.Config.ARGB_8888);
            Canvas c = new Canvas(bm);
            c.drawPath(ps.get(i), pas.get(i));

            final View view = inflater.inflate(R.layout.fragment_draw, container, false);
            View btn = layoutInflater.inflate(R.layout.draw_nag, null);
            btn.setLayoutParams(new ViewGroup.LayoutParams(450, 500));

            lye.addView(btn);
            Drawable d = new BitmapDrawable(view.getResources(), bm);

            btn.setBackground(d);
            final int index = layerCount++;

            btn.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    flag = 1;
                    selectedIndex = index;
                    seletedFlag = 1;
                    Log.d("btn", "P:" + selectedIndex);
                    invalidate();
                    seletedFlag = 0;
                }
            });
        }
    }

    public void delLayer(){
        try {
            paths.remove(selectedIndex);
            paints.remove(selectedIndex);
            Float[] tPaths = (Float[]) pathsCollection.toArray(new Float[pathsCollection.size()]);
            Integer[] tColors = (Integer[]) paintsColorCollection.toArray(new Integer[paintsColorCollection.size()]);
            Float[] tWidths = (Float[]) paintsWidthCollection.toArray(new Float[paintsWidthCollection.size()]);
            PathEffect[] tEffects = (PathEffect[]) paintsEffectCollection.toArray(new PathEffect[paintsEffectCollection.size()]);
            pathsCollection = new ArrayList<Float>();
            paintsColorCollection = new ArrayList();
            paintsWidthCollection = new ArrayList<Float>();
            paintsEffectCollection = new ArrayList<PathEffect>();
            //re-assign
            int delPathIndex = selectedIndex;
            for (int i = 0; i < layerCount; i++) {
                if (delPathIndex != i) {
                    pathsCollection.add(tPaths[(i * 4)]);
                    pathsCollection.add(tPaths[(i * 4) + 1]);
                    pathsCollection.add(tPaths[(i * 4) + 2]);
                    pathsCollection.add(tPaths[(i * 4) + 3]);
                }
                if (selectedIndex != i) {
                    paintsColorCollection.add(tColors[i]);
                    paintsWidthCollection.add(tWidths[i]);
                    paintsEffectCollection.add(tEffects[i]);
                }

            }
            selectedIndex = -1;
            invalidate();
        }catch (Exception e){}
    }

    public void saveBitmap(){
        try {
            int i = 0;
            bitmap = Bitmap.createBitmap(1080, 1464, Bitmap.Config.ARGB_8888);

            mCanvas = new Canvas(bitmap);
            for (Path p : paths) {
                mCanvas.drawPath(p, paints.get(i));
                i++;
            }
            int x, y;
            x = (int) Math.floor(left) - 50;
            y = (int) Math.floor(top) - 50;
            if (x < 0)
                x = (int) Math.floor(left);
            if (y < 0)
                y = (int) Math.floor(top);

            bitmap = Bitmap.createBitmap(bitmap, x, y, (int) (Math.ceil(right) - Math.floor(left)) + 50, (int) (Math.ceil(bottom) - Math.floor(top)) + 50);
            //mCanvas.clipRect(left+150f, top+150f, right-150f, bottom-150f);
        } catch(Exception e){}
    }

    int seletedFlag =0;
    @Override
    protected void onDraw(Canvas canvas) {
        try {
            if (flag == 1) {
                canvas.drawPath(tempPath, tempPaint);
                int i = 0;
                for (Path p : paths) {
                    if (selectedIndex == i) {
                        Paint highlight = new Paint(paints.get(i));
                        highlight.setColor(Color.YELLOW);
                        canvas.drawPath(p, highlight);
                    } else
                        canvas.drawPath(p, paints.get(i));
                    i++;
                }
                path = new Path();
                if (seletedFlag == 0)
                    addLayer(paths, paints);
                Log.d("FlFL", "Flag: 1");
                Log.d("FLFL", "RectF: " + canvas.getClipBounds());
//            canvas.setBitmap(bitmap);
                pastFlag = 1;
            } else if (flag == 2&& pastFlag !=2){
                //set data to db
                gson = new GsonBuilder().create();

                pathsJson = gson.toJson(pathsCollection);
                paintsColorJson = gson.toJson(paintsColorCollection);
                paintsWidthJson = gson.toJson(paintsWidthCollection);
                paintsEffectJson = gson.toJson(paintsEffectCollection);
                //canvas.clipRect(new RectF(left, top, right, bottom));
                //bitmap = Bitmap.createBitmap(getDrawingCache());
                saveBitmap();
                canvas.drawBitmap(bitmap, left, top, tempPaint);
                String bitmapString = encodeToBase64(bitmap, Bitmap.CompressFormat.PNG, 100);
                DrawTask dt = new DrawTask(context, pathsJson, paintsColorJson, paintsWidthJson, paintsEffectJson, bitmapString, "save");
                dt.execute();
                pathsCollection = new ArrayList<Float>();
                paintsColorCollection = new ArrayList();
                paintsWidthCollection = new ArrayList<Float>();
                paintsEffectCollection = new ArrayList<PathEffect>();

                Log.d("FlFL", "Flag: 2" + json);
                pastFlag = 2;
            } else if (flag == 3) {
                gson = new GsonBuilder().create();
                JsonParser parser = new JsonParser();
                JsonObject obj = parser.parse(json).getAsJsonObject();
                JsonArray pathArray = obj.getAsJsonArray("paths");
                JsonArray colorsArray = obj.getAsJsonArray("colors");
                JsonArray widthsArray = obj.getAsJsonArray("widths");
                JsonArray effectsArray = obj.getAsJsonArray("effects");

                Log.d("FLFL", "Flag: 3...size" + pathArray.size());
                for (int i = 0; i < pathArray.size(); i += 4) {
                    path = new Path();
                    path.moveTo(gson.fromJson(pathArray.get(i), Float.class), gson.fromJson(pathArray.get(i + 1), Float.class));
                    path.lineTo(gson.fromJson(pathArray.get(i + 2), Float.class), gson.fromJson(pathArray.get(i + 3), Float.class));
//                tempPathsCollection.add(gson.fromJson(pathArray.get(i), Float.class));
//                tempPathsCollection.add(gson.fromJson(pathArray.get(i + 1), Float.class));
//                tempPathsCollection.add(gson.fromJson(pathArray.get(i + 2), Float.class));
//                tempPathsCollection.add(gson.fromJson(pathArray.get(i + 3), Float.class));
//                tempPaintsColorCollection.add(gson.fromJson(colorsArray.get(i / 4), int.class));
//                tempPaintsWidthCollection.add(gson.fromJson(widthsArray.get(i / 4), Float.class));
//                tempPaintsEffectCollection.add(gson.fromJson(effectsArray.get(i / 4), PathEffect.class));
                    Paint pa = new Paint();
                    pa.setAntiAlias(true);
                    pa.setColor(gson.fromJson(colorsArray.get(i / 4), Integer.class));
                    pa.setStrokeJoin(Paint.Join.ROUND);
                    pa.setStyle(Paint.Style.STROKE);
                    pa.setStrokeWidth(gson.fromJson(widthsArray.get(i / 4), Float.class));
                    if (gson.fromJson(effectsArray.get(i / 4), PathEffect.class) != null)
                        pa.setPathEffect(gson.fromJson(effectsArray.get(i / 4), PathEffect.class));
                    canvas.drawPath(path, pa);

                }
                pastFlag = 3;
            } else if (flag == 4) {

            }
            Log.d("HIHI", "onDraw" + flag);
        }catch (Exception e){}
    }
    public void undo(){
        if(paths.size()>0) {
            paths.remove(paths.size() - 1);
            paints.remove(paints.size() - 1);
            flag = 1;
            invalidate();
        }
    }

    public void insertSample() {
        gson = new GsonBuilder().create();
        JsonParser parser = new JsonParser();
        JsonObject obj = parser.parse(json).getAsJsonObject();
        JsonArray pathArray = obj.getAsJsonArray("paths");
        JsonArray colorsArray = obj.getAsJsonArray("colors");
        JsonArray widthsArray = obj.getAsJsonArray("widths");
        JsonArray effectsArray = obj.getAsJsonArray("effects");

        Log.d("FLFL", "Flag: 3...size" + pathArray.size());

        for(int i=0; i< pathArray.size(); i+=4) {
            computeRectF(gson.fromJson(pathArray.get(i), Float.class), gson.fromJson(pathArray.get(i + 2), Float.class),
                    gson.fromJson(pathArray.get(i + 1), Float.class), gson.fromJson(pathArray.get(i + 3), Float.class));

            path = new Path();
            path.moveTo(gson.fromJson(pathArray.get(i), Float.class), gson.fromJson(pathArray.get(i + 1), Float.class));
            path.lineTo(gson.fromJson(pathArray.get(i + 2), Float.class), gson.fromJson(pathArray.get(i + 3), Float.class));
            pathsCollection.add(gson.fromJson(pathArray.get(i), Float.class));
            pathsCollection.add(gson.fromJson(pathArray.get(i + 1), Float.class));
            pathsCollection.add(gson.fromJson(pathArray.get(i + 2), Float.class));
            pathsCollection.add(gson.fromJson(pathArray.get(i + 3), Float.class));
            paintsColorCollection.add(gson.fromJson(colorsArray.get(i / 4), int.class));
            paintsWidthCollection.add(gson.fromJson(widthsArray.get(i / 4), Float.class));
            paintsEffectCollection.add(gson.fromJson(effectsArray.get(i / 4), PathEffect.class));
            Paint pa = new Paint();
            pa.setAntiAlias(true);
            pa.setColor(gson.fromJson(colorsArray.get(i / 4), int.class));
            pa.setStrokeJoin(Paint.Join.ROUND);
            pa.setStyle(Paint.Style.STROKE);
            pa.setStrokeWidth(gson.fromJson(widthsArray.get(i / 4), Float.class));
            if (gson.fromJson(effectsArray.get(i / 4), PathEffect.class) != null)
                pa.setPathEffect(gson.fromJson(effectsArray.get(i / 4), PathEffect.class));
            paths.add(path);
            paints.add(pa);
        }
        invalidate();
    }

    private void computeRectF(Float rx, Float x, Float ry, Float y){
        if(rx >= x){
            if(left == null && right == null) {
                left = x;
                right =rx;
            }else {
                if (left >= x)
                    left = x;
                if (right <= rx)
                    right = rx;
            }
        }else {
            if(left == null && right == null) {
                left = rx;
                right = x;
            }else {
                if (left >= rx || left == null)
                    left = rx;
                if (right <= x || right == null)
                    right = x;
            }
        }

        if(ry >= y){
            if(top == null && bottom == null){
                top = y;
                bottom = ry;
            }else {
                if (top >= y)
                    top = y;
                if (bottom <= ry)
                    bottom = ry;
            }
        }else {
            if(top ==null && bottom ==null){
                top = ry;
                bottom = y;
            }else {
                if (top >= ry)
                    top = ry;
                if (bottom <= y)
                    bottom = y;
            }
        }
        Log.d("bb", "left: " + left + ", top: " + top + ", right: " + right + ", bottom: " + bottom);
    }

    public void setNewPath(int id) {

        json = "";
        try {
            json = new DrawTask(context, id, "load").execute().get();
            Log.d("FLFL", "GotJson: " + json);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        flag = 3;
        invalidate();
    }



    @Override
    public boolean onTouchEvent(MotionEvent event) {
        x = event.getX();
        y = event.getY();
        if(flag==1)
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    tempPath.moveTo(x, y);
                    rx = x;
                    ry = y;
                    Log.d("HIHI", "x:" + x + ", y:" + y + " D");
                    return true;
                case MotionEvent.ACTION_MOVE:
                    tempPath.lineTo(x, y);
                    Log.d("HIHI", "x:" + x + ", y:" + y + " M");
                    break;
                case MotionEvent.ACTION_UP:
                    path.moveTo(rx, ry);
                    Log.d("HIHI", "rx:" + rx + ", ry:" + ry + " UP");
                    path.lineTo(x, y);
                    pathsCollection.add(rx);
                    pathsCollection.add(ry);
                    pathsCollection.add(x);
                    pathsCollection.add(y);
                    computeRectF(rx, x, ry, y);
                    Log.d("HIHI", "x:" + x + ", y:" + y + " UP");
                    Log.d("HIHI", paths.add(new Path(path)) + "");
                    Paint pa = new Paint(paint);
                    Log.d("HIHI", paints.add(pa) + " ");
                    Log.d("paint", pa.getPathEffect() + "");
                    paintsColorCollection.add(pa.getColor()); //int
                    paintsWidthCollection.add(pa.getStrokeWidth());//float
                    paintsEffectCollection.add(pa.getPathEffect());//PathEffect
                    //DrawTask drawTask = new DrawTask(context, path);
                    //paint.setColor(paint.getColor() + 12000);
                    tempPath = new Path();
                    break;
                default:
                    return false;
            }

        //invalidate();
        //rx + ", " + ry + ", " + x + ", " + y
        invalidate();
        return true;
    }
}
