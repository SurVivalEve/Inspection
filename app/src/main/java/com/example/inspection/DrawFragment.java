package com.example.inspection;

import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.AppCompatImageView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;

import com.example.inspection.sync.SyncManager;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;


import java.util.concurrent.ExecutionException;


public class DrawFragment extends Fragment {

    private String empID="";
    DrawEventView dev;
    HorizontalScrollView hsv;
    ScrollView sv;
    FloatingActionButton fab, fab_q1, fab_q2, fabLayer, fabLayer_cancel, fabLayer_del, fabSend;
    SubButton sbtn1, sbtn2, sbtn3, sbtn4;
    public static final String EMP_ID = "empid";
    private  LinearLayout nag, lllayer;

    private QuotationsListener mCallback;

    public interface QuotationsListener {
        public void sendGraphMessage(Bitmap bitmap);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (QuotationsListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement QuotationsListener");
        }

    }

    @Override
    public void onDetach() {
        mCallback = null;
        super.onDetach();
    }

    public static DrawFragment newInstance(String id) {
        DrawFragment fragment = new DrawFragment();
        Bundle bundle = new Bundle();
        bundle.putString(EMP_ID, id);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_draw, container, false);
        final LayoutInflater layoutInflater = (LayoutInflater) view.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        nag =  (LinearLayout) view.findViewById(R.id.nag);;
        sbtn1 = (SubButton) view.findViewById(R.id.sbtn1);
        sbtn2 = (SubButton) view.findViewById(R.id.sbtn2);
        sbtn3 = (SubButton) view.findViewById(R.id.sbtn3);
        sbtn4 = (SubButton) view.findViewById(R.id.sbtn4);
        dev = (DrawEventView) view.findViewById(R.id.draw);
        hsv = (HorizontalScrollView) view.findViewById(R.id.hsv);
        sv = (ScrollView) view.findViewById(R.id.sv);
        fab = ((FloatingActionButton) view.findViewById(R.id.fab));
        fab_q1 = (FloatingActionButton) view.findViewById(R.id.fab_q1);
        fab_q2 = (FloatingActionButton) view.findViewById(R.id.fab_q2);
        fab.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.orange_button)));
        fab_q2.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.divider_grey)));
        fabLayer = ((FloatingActionButton) view.findViewById(R.id.fabLayer));
        fabLayer_cancel = ((FloatingActionButton) view.findViewById(R.id.fabLayer_cancel));
        fabLayer_del = ((FloatingActionButton) view.findViewById(R.id.fabLayer_del));
        fabSend = ((FloatingActionButton) view.findViewById(R.id.fabSend));
        lllayer = (LinearLayout) view.findViewById(R.id.lllayer);
        dev.initLayer(layoutInflater, inflater, container, lllayer);
        //setContentView(new DrawEventView(this,null));
        //Floating Menu
        fab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View viewFab) {
                // dev.setCurrentItem(0);
                fab_q1.setVisibility(View.INVISIBLE);
                fab_q2.setVisibility(View.INVISIBLE);
                if (nag.getVisibility() == View.VISIBLE && hsv.getVisibility() == View.VISIBLE){
                    nag.setVisibility(View.INVISIBLE);
                    hsv.setVisibility(View.INVISIBLE);
                }

                if (sbtn1.getVisibility() == View.VISIBLE && sbtn2.getVisibility() == View.VISIBLE && sbtn3.getVisibility() == View.VISIBLE && sbtn4.getVisibility() == View.VISIBLE ) {
                    sbtn1.setVisibility(View.INVISIBLE);
                    sbtn2.setVisibility(View.INVISIBLE);
                    sbtn3.setVisibility(View.INVISIBLE);
                    sbtn4.setVisibility(View.INVISIBLE);

                } else {
                    fab = ((FloatingActionButton) viewFab.findViewById(R.id.fab));
                    int t = fab.getTop();
                    int b = fab.getBottom();
                    int r = fab.getRight();
                    int l = fab.getLeft();
                    sbtn1.onShow(Float.parseFloat(String.valueOf((fab.getPivotX() + (r - l) / 2.0))), Float.parseFloat(String.valueOf((fab.getPivotY() + (b - t) / 2.0))));
                    sbtn2.onShow(Float.parseFloat(String.valueOf((fab.getPivotX() + (r-l)/2.0))), Float.parseFloat(String.valueOf(( fab.getPivotY() + (b-t)/2.0))));
                    sbtn3.onShow(Float.parseFloat(String.valueOf((fab.getPivotX() + (r - l) / 2.0))), Float.parseFloat(String.valueOf((fab.getPivotY() + (b - t) / 2.0))));
                    sbtn4.onShow(Float.parseFloat(String.valueOf((fab.getPivotX() + (r - l) / 2.0))), Float.parseFloat(String.valueOf((fab.getPivotY() + (b - t) / 2.0))));
                    nag.setVisibility(View.VISIBLE);
                    hsv.setVisibility(View.VISIBLE);
                    //((SubButton) findViewById(R.id.sbtn1)).onShow(((FloatingActionButton) findViewById(R.id.fab)).getPivotX(), ((FloatingActionButton) findViewById(R.id.fab)).getPivotY());
                }
                sbtn1.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View view) {
//                        Log.d("HIHI2", "Menu px:" + ((FloatingActionButton) findViewById(R.id.fab)).getPivotX() + "  py:" + ((FloatingActionButton) findViewById(R.id.fab)).getPivotY());
//                        Log.d("HIHI2", "Menu x:" + ((FloatingActionButton) findViewById(R.id.fab)).getX() + "  y:" + ((FloatingActionButton) findViewById(R.id.fab)).getY());
//                        Log.d("HIHI2", "Menu tx:" + ((FloatingActionButton) findViewById(R.id.fab)).getTranslationX() + "  ty:" + ((FloatingActionButton) findViewById(R.id.fab)).getTranslationY());
//                        Log.d("HIHI2", "Menu rx:" + ((FloatingActionButton) findViewById(R.id.fab)).getRotationX() + "  ry:" + ((FloatingActionButton) findViewById(R.id.fab)).getRotationY());
//                        Log.d("HIHI2", "Menu l:" + ((FloatingActionButton) findViewById(R.id.fab)).getLeft() + "  t:" + ((FloatingActionButton) findViewById(R.id.fab)).getTop());
//                        Log.d("HIHI2", "Menu r:" + ((FloatingActionButton) findViewById(R.id.fab)).getRight() + "  b:" + ((FloatingActionButton) findViewById(R.id.fab)).getBottom());

//                        Toast.makeText(MainActivity.this, "sbtn clicked", Toast.LENGTH_LONG).show();

                        dev.flag = 1;
                        dev.invalidate();
                    }
                });
                sbtn2.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
//                        Log.d("HIHI2", "Menu px:" + ((FloatingActionButton) findViewById(R.id.fab)).getPivotX() + "  py:" + ((FloatingActionButton) findViewById(R.id.fab)).getPivotY());
//                        Log.d("HIHI2", "Menu x:" + ((FloatingActionButton) findViewById(R.id.fab)).getX() + "  y:" + ((FloatingActionButton) findViewById(R.id.fab)).getY());
//                        Log.d("HIHI2", "Menu tx:" + ((FloatingActionButton) findViewById(R.id.fab)).getTranslationX() + "  ty:" + ((FloatingActionButton) findViewById(R.id.fab)).getTranslationY());
//                        Log.d("HIHI2", "Menu rx:" + ((FloatingActionButton) findViewById(R.id.fab)).getRotationX() + "  ry:" + ((FloatingActionButton) findViewById(R.id.fab)).getRotationY());
//                        Log.d("HIHI2", "Menu l:" + ((FloatingActionButton) findViewById(R.id.fab)).getLeft() + "  t:" + ((FloatingActionButton) findViewById(R.id.fab)).getTop());
//                        Log.d("HIHI2", "Menu r:" + ((FloatingActionButton) findViewById(R.id.fab)).getRight() + "  b:" + ((FloatingActionButton) findViewById(R.id.fab)).getBottom());

//                        Toast.makeText(MainActivity.this, "sbtn clicked", Toast.LENGTH_LONG).show();

                        dev.flag = 2;
                        dev.bitmap = Bitmap.createBitmap(dev.getDrawingCache());

                       // dev.invalidate();

                    }
                });
                sbtn3.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View view){
                        dev.flag = 3;
                    }
                });
                sbtn4.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v){
                        dev.undo();
                    }
                });
//                Toast.makeText(MainActivity.this, "Fab clicked", Toast.LENGTH_LONG).show();

                String json = "";
                Gson gson;
                try {
                    json = new DrawTask("loadNag").execute().get();
                    Log.d("FLFL", "GotJson: " + json);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
                gson = new GsonBuilder().create();
                JsonParser parser = new JsonParser();
                JsonObject obj = parser.parse(json).getAsJsonObject();
                JsonArray idArray = obj.getAsJsonArray("id");
                JsonArray bitmapArray = obj.getAsJsonArray("bitmapString");
                nag.removeAllViews();
                for(int i=0; i<idArray.size(); i++){
                    View btn = layoutInflater.inflate(R.layout.draw_nag, null);
                    //btn.setBackgroundResource(R.drawable.ic_media_play);
                    Bitmap bitmap = dev.decodeBase64(gson.fromJson(bitmapArray.get(i), String.class));
                    Drawable d = new BitmapDrawable(view.getResources(), bitmap);
                    //dev.drawBitmap(bitmap);
                    btn.setBackground(d);
                    //((Button) btn).setText(gson.fromJson(idArray.get(i), Object.class).toString());
                    Log.d("btn", gson.fromJson(idArray.get(i), Object.class).toString());
                    ((Button) btn).setHint(gson.fromJson(idArray.get(i), Object.class).toString());
                    final int index = i;

                    nag.addView(btn);
                    btn.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            dev.setNewPath((int) Double.parseDouble(((Button) v).getHint().toString()));
                            Log.d("btn", v.getBackground().toString());
                            fab_q1.setVisibility(View.VISIBLE);
                            fab_q2.setVisibility(View.VISIBLE);
                        }
                    });
                }
                View btn = layoutInflater.inflate(R.layout.draw_nag, null);
                btn.setLayoutParams(new ViewGroup.LayoutParams(450, 0));
                btn.setBackground(hsv.getBackground());
                btn.setEnabled(false);
                nag.addView(btn);
            }
        });

        fab_q1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dev.insertSample();
                dev.flag = 1;
                dev.invalidate();
                v.setVisibility(View.INVISIBLE);
                fab_q2.setVisibility(View.INVISIBLE);
            }
        });

        fab_q2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dev.flag = 1;
                dev.invalidate();
                v.setVisibility(View.INVISIBLE);
                fab_q1.setVisibility(View.INVISIBLE);
            }
        });

        fabLayer_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dev.selectedIndex = -1;
                v.setVisibility(View.INVISIBLE);
                fabLayer_del.setVisibility(View.INVISIBLE);
                fabLayer.setVisibility(View.VISIBLE);
                sv.setVisibility(View.INVISIBLE);
                dev.invalidate();
            }
        });

        fabLayer_del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dev.delLayer();
            }
        });

        fabLayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               v.setVisibility(View.INVISIBLE);
               fabLayer.setVisibility(View.VISIBLE);
               fabLayer_del.setVisibility(View.VISIBLE);
               fabLayer_cancel.setVisibility(View.VISIBLE);
               sv.setVisibility(View.VISIBLE);
            }
        });
        fabSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.sendGraphMessage(dev.preparedToSendBitmap());

                getActivity().getSupportFragmentManager().beginTransaction()
                        .remove(getActivity().getSupportFragmentManager().findFragmentByTag("draw"))
                        .commit();
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });


        return view;
    }


}
