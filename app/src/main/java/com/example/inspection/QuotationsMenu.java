package com.example.inspection;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.inspection.sync.SyncManager;
import com.example.inspection.util.FileWrapper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class QuotationsMenu extends Fragment {


    private static final String TEMP_IMG = "tempImg";
    private Button invoice, orderForm, update;

    private ImageButton btnGallery, btnCamera, delPhoto, addGraph, delGraph, findAppointment;
    private LinearLayout photoContainer, graphContainer;
    private CoordinatorLayout coordinatorLayout;
    private EditText edtAppNo;

    public Snackbar snackbar;

    private static final String EMP_ID = "empid";
    private static final int CAMERA_REQUEST = 1;
    private static final int GALLERY_REQUEST = 2;

    private String empID = "";

    private String picturePath = null;

    private List<Bitmap> photoList = new ArrayList<>();
    private static List<Uri> photoUriList = new ArrayList<>();
    private static List<Bitmap> graphList = new ArrayList<>();
    private static JSONArray orderFormJson, invoiceJson;

    public void setOrderForm(JSONArray orderForm) {
        this.orderFormJson = orderForm;
    }

    public void setInvoice(JSONArray invoice) {
        this.invoiceJson = invoice;
    }

    public void setText(String text) {
        edtAppNo.setText(text);
    }

    public void setGraphList(Bitmap bitmap) {
        if(bitmap != null) {
            graphList.add(bitmap);

            ImageView imageView = new ImageView(getContext());
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(400, ViewGroup.LayoutParams.MATCH_PARENT);
            lp.setMargins(0, 0, 50, 0);
            imageView.setLayoutParams(lp);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);

            imageView.setImageBitmap(bitmap);
            graphContainer.addView(imageView);
        } else {
            Log.d("bitmap", "empty bitmap");
        }
    }

    public static QuotationsMenu newInstance(String id) {
        QuotationsMenu fragment = new QuotationsMenu();
        Bundle bundle = new Bundle();
        bundle.putString(EMP_ID, id);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_quotations_menu, container, false);
        setRetainInstance(true);
        init(view);

        ((MainMenu)getActivity()).setDrawerLock(true);

        return view;
    }

    private void init(View view) {
        empID = getArguments().getString(EMP_ID);
        update = (Button) view.findViewById(R.id.btnUpdateQuotation);
        edtAppNo = (EditText) view.findViewById(R.id.edtAppNo);
        invoice = (Button) view.findViewById(R.id.invoice);
        orderForm = (Button) view.findViewById(R.id.orderForm);

        btnCamera = (ImageButton) view.findViewById(R.id.addFromCamera);
        btnGallery = (ImageButton) view.findViewById(R.id.addFromGallery);
        delPhoto = (ImageButton) view.findViewById(R.id.delPhoto);

        addGraph = (ImageButton) view.findViewById(R.id.addGraph);
        delGraph = (ImageButton) view.findViewById(R.id.delGraph);

        graphContainer = (LinearLayout) view.findViewById(R.id.graphContainer);
        photoContainer = (LinearLayout) view.findViewById(R.id.photoContainer);
        coordinatorLayout = (CoordinatorLayout) view.findViewById(R.id.cdlayoutForQuotaitonMenu);

        findAppointment = (ImageButton) view.findViewById(R.id.findAppointment);


        try {
            String appid = (String) this.getArguments().get("appid");
            if (appid != null) {
                edtAppNo.setText(appid);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(invoiceJson==null){
                    snackbar = Snackbar.make(coordinatorLayout, "Invoice must include", Snackbar.LENGTH_LONG);

                    // Changing action button text color
                    View sbView = snackbar.getView();
                    TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
                    textView.setTextColor(Color.YELLOW);

                    snackbar.setAction("RETRY", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String appid = edtAppNo.getText().toString();
                            new syncQuotation().execute(appid,photoUriList,graphList,invoiceJson,orderFormJson);
                        }
                    });

                    snackbar.setActionTextColor(Color.RED);


                    snackbar.show();
                } else {
                    String appid = edtAppNo.getText().toString();
                    new syncQuotation().execute(appid,photoUriList,graphList,invoiceJson,orderFormJson);
                }



            }
        });

        invoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                QuotationInvoice quotationInvoice = new QuotationInvoice();
                FragmentManager fm = getActivity().getSupportFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                Fragment fragment = fm.findFragmentByTag("quotationInvoice");

                if(fragment == null){
                    ft.add(R.id.main_fragment,quotationInvoice,"quotationInvoice")
                        .addToBackStack(null)
                        .commit();
                }else {
                    ft.show(fragment).commit();
                }


            }
        });
        orderForm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                QuotationOrderForm quotationOrderForm = new QuotationOrderForm();
                FragmentManager fm = getActivity().getSupportFragmentManager();
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                Fragment fragment = fm.findFragmentByTag("quotationOrderForm");

                if(fragment == null){
                    ft.add(R.id.main_fragment,quotationOrderForm,"quotationOrderForm")
                            .addToBackStack(null)
                            .commit();
                }else {
                    ft.show(fragment).commit();
                }
            }
        });

        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                FileWrapper tempPhoto = new FileWrapper(getContext(), FileWrapper.Storage.EXTERNAL_CACHE, TEMP_IMG);
                tempPhoto.delete();
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, tempPhoto.getUri());
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            }
        });

        btnGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                galleryIntent.setType("image/*");
                startActivityForResult(Intent.createChooser(galleryIntent, "Sekect File"), GALLERY_REQUEST);
            }
        });

        addGraph.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.add(R.id.main_fragment, DrawFragment.newInstance(empID), "draw").addToBackStack(null).commit();
            }
        });

        delPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    if (photoContainer.getChildCount() > 0) {
                        photoUriList.remove(photoUriList.size()-1);
                        photoContainer.removeViewAt(photoContainer.getChildCount()-1);

                    }
                }catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

        delGraph.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    if(graphContainer.getChildCount() > 0) {
                        graphList.remove(graphList.size()-1);
                        graphContainer.removeViewAt(graphContainer.getChildCount()-1);

                    }
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        findAppointment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = ((MainMenu) getActivity()).getEmpID();
                RecentJobFragment recentJobFragment = RecentJobFragment.newInstance(1, id);
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.main_fragment, recentJobFragment, "recentjob")
                        .addToBackStack(null)
                        .commit();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        ContentResolver resolver = getActivity().getContentResolver();

        try {
            ImageView imageView = new ImageView(getContext());
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(400, ViewGroup.LayoutParams.MATCH_PARENT);
            lp.setMargins(0, 0, 50, 0);
            imageView.setLayoutParams(lp);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);

            if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {

//                Bitmap photo = (Bitmap) data.getExtras().get("data");
//                imageView.setImageBitmap(photo);
//                photoContainer.addView(imageView);
//
//                photoList.add(photo);

                FileWrapper tempPhoto;
                tempPhoto = new FileWrapper(getContext(), FileWrapper.Storage.EXTERNAL_CACHE, TEMP_IMG);

                FileWrapper clone = new FileWrapper(getContext(), FileWrapper.Storage.INTERNAL, Calendar.getInstance().getTimeInMillis() + "");
                clone.copyForm(tempPhoto, FileWrapper.Behavior.CREATE_ALWAYS);

//                Uri selectedImage = data.getData();
//                Bitmap bitmap = MediaStore.Images.Media.getBitmap(resolver, selectedImage);

                imageView.setImageBitmap(tempPhoto.getBitmap());


                photoContainer.addView(imageView);
                photoUriList.add(clone.getUri());


            } else if (requestCode == GALLERY_REQUEST && resultCode == Activity.RESULT_OK && data != null) {

                Uri selectedImage = data.getData();
                Bitmap bm = MediaStore.Images.Media.getBitmap(resolver, selectedImage);
                imageView.setImageBitmap(bm);

                if (!bm.isRecycled()){
                    bm.isRecycled();
                    System.gc();
                }

                photoContainer.addView(imageView);
                photoUriList.add(Uri.parse(getRealPathFromURI(selectedImage)));

//                String[] filePathColumn = { MediaStore.Images.Media.DATA };
//                Cursor cursor = resolver.query(selectedImage, filePathColumn, null, null, null);
//                cursor.moveToFirst();
//                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
//                picturePath = cursor.getString(columnIndex);
//                cursor.close();
//                imageView.setImageBitmap(BitmapFactory.decodeFile(picturePath));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private String getRealPathFromURI(Uri contentURI) {
        String result;
        Cursor cursor = getActivity().getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) { // Source is Dropbox or other similar local file path
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }

    private class syncQuotation extends AsyncTask<Object, Integer, String> {

        @Override
        protected String doInBackground(Object... params) {
            SyncManager syncManager = new SyncManager("uploadQuotation.php");

            return syncManager.syncQuotation(getContext(), ((String)params[0]), ((List<Uri>)params[1]), ((List<Bitmap>)params[2]), ((JSONArray)params[3]), ((JSONArray)params[4]));
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if(s.equalsIgnoreCase("false")) {
                snackbar = Snackbar.make(coordinatorLayout, "Update Fail", Snackbar.LENGTH_LONG);

                // Changing action button text color
                View sbView = snackbar.getView();
                TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
                textView.setTextColor(Color.YELLOW);

                snackbar.setAction("RETRY", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String appid = edtAppNo.getText().toString();
                        new syncQuotation().execute(appid,photoUriList,graphList,invoiceJson,orderFormJson);
                    }
                });

                snackbar.setActionTextColor(Color.RED);


                snackbar.show();
            } else if(s.equalsIgnoreCase("true")) {
                snackbar = Snackbar.make(coordinatorLayout, "Updated", Snackbar.LENGTH_LONG);

                // Changing action button text color
                View sbView = snackbar.getView();
                TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
                textView.setTextColor(Color.GREEN);


                snackbar.show();
            }


        }
    }
}
