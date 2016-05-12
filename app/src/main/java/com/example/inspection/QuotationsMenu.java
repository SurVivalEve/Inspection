package com.example.inspection;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

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
    private ImageButton btnGallery, btnCamera, delPhoto;
    private LinearLayout photoContainer;
    private EditText edtAppNo;

    public static final String EMP_ID = "empid";
    public static final int CAMERA_REQUEST = 1;
    public static final int GALLERY_REQUEST = 2;

    private String picturePath = null;

    private List<Bitmap> photoList = new ArrayList<>();
    private List<Uri> photoUriList = new ArrayList<>();

    private JSONArray orderFormJson, invoiceJson;

    public void setOrderForm(JSONArray orderForm){
        this.orderFormJson = orderForm;
    }

    public void setInvoice(JSONArray invoice){
        this.invoiceJson = invoice;
    }

    public void setText(String text) {
        edtAppNo.setText(text);
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

        return view;
    }

    private void init(View view) {
        update = (Button) view.findViewById(R.id.btnUpdateQuotation);
        edtAppNo = (EditText) view.findViewById(R.id.edtAppNo);
        invoice = (Button) view.findViewById(R.id.invoice);
        orderForm = (Button) view.findViewById(R.id.orderForm);
        photoContainer = (LinearLayout) view.findViewById(R.id.photoContainer);
        btnCamera = (ImageButton) view.findViewById(R.id.addFromCamera);
        btnGallery = (ImageButton) view.findViewById(R.id.addFromGallery);
        delPhoto = (ImageButton) view.findViewById(R.id.delPhoto);

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new syncQuotation().execute(photoUriList);
            }
        });

        invoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                QuotationInvoice quotationInvoice = new QuotationInvoice();
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.main_fragment, quotationInvoice, "quotationInvoice").addToBackStack(null).commit();
            }
        });
        orderForm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                QuotationOrderForm quotationOrderForm = new QuotationOrderForm();
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.add(R.id.main_fragment, quotationOrderForm, "quotationOrderForm").addToBackStack(null).commit();
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

        delPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (photoContainer.getChildCount() > 0)
                    photoContainer.removeViewAt(photoContainer.getChildCount() - 1);
                photoUriList.remove(photoUriList.size());
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
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(resolver, selectedImage);
                imageView.setImageBitmap(bitmap);
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

    private class syncQuotation extends AsyncTask<List<Uri>, Integer, String> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(List<Uri>... params) {
            SyncManager syncManager = new SyncManager("uploadPhoto.php");

            return syncManager.syncQuotation(getContext(), "A00000000024", params[0], invoiceJson, orderFormJson);
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }
    }
}
