package com.example.inspection.sync;

import android.content.Context;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Base64;


import android.util.Log;

import com.example.inspection.DrawEventView;
import com.example.inspection.dao.LocalNextMonthScheduleDAO;
import com.example.inspection.dao.LocalPreMonthScheduleDAO;
import com.example.inspection.dao.LocalScheduleDAO;
import com.example.inspection.dao.RecentJobDAO;
import com.example.inspection.dao.WebAppointmentDAO;
import com.example.inspection.dbmodels.LocalRecentJob;
import com.example.inspection.dbmodels.LocalSchedule;
import com.example.inspection.dbmodels.WebAppointment;
import com.example.inspection.models.Appointment;
import com.example.inspection.models.Customer;
import com.example.inspection.models.History;
import com.example.inspection.models.Processing;
import com.example.inspection.models.RecentJob;
import com.example.inspection.models.Schedule;
import com.example.inspection.models.Task;
import com.example.inspection.util.FileWrapper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SyncManager {
    private static final String GET_URL = "http://58.177.9.234/fyp/json/";

    private static String appendUrl = "";
    private static Context context;

    public SyncManager(String url) {
        this.appendUrl = url;
    }

    public SyncManager(String url, Context context) {
        this.appendUrl = url;
        this.context = context;
    }

    private HttpURLConnection getHttpConn(String url, String method, JSONObject params) {
        URL u;
        HttpURLConnection conn = null;
        try {
            u = new URL(url);
            conn = (HttpURLConnection) u.openConnection();
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);

            conn.setRequestMethod(method);
            conn.setDoInput(true);

            if (method.equalsIgnoreCase("POST")) {
                conn.setDoOutput(true); //if this is set to true, method will always be POST
                conn.setRequestProperty("Content-Type", "application/json");
            }

            conn.setUseCaches(false);
            conn.connect();

            if (params != null) {
                DataOutputStream out = new DataOutputStream(conn.getOutputStream());
                out.write(params.toString().getBytes());
                out.flush();
                out.close();
            }

        } catch (Exception e) {
            Log.i("ex", Log.getStackTraceString(e));
        }
        return conn;
    }


    public static String encodeToBase64(Bitmap image, Bitmap.CompressFormat compressFormat, int quality) {
        ByteArrayOutputStream byteArrayOS = new ByteArrayOutputStream();
        image.compress(compressFormat, quality, byteArrayOS);
        return Base64.encodeToString(byteArrayOS.toByteArray(), Base64.DEFAULT);
    }

    public static Bitmap decodeBase64(String input) {
        byte[] decodedBytes = Base64.decode(input, 0);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }

    public String[] syncLogin(String username, String password) {
        String result[] = new String[2];
        try {
            JSONObject toSend = new JSONObject();
            toSend.put("username", username);
            toSend.put("password", password);
            HttpURLConnection conn = getHttpConn(GET_URL + appendUrl, "POST", toSend);
            InputStream is = conn.getInputStream();
            String recieve = stream2String(is);
            Log.d("result", recieve);
            result[0] = recieve.substring(0, 12);
            result[1] = recieve.substring(12);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    public List<Customer> syncCustomers() {
        List<Customer> result = new ArrayList<>();
        try {
            HttpURLConnection conn = getHttpConn(GET_URL + appendUrl, "GET", null);
            InputStream is = conn.getInputStream();
            JSONObject json = new JSONObject(stream2String(is));
            //parse json
            JSONArray customers = json.getJSONArray("Customer");
            for (int i = 0; i < customers.length(); i++) {
                JSONObject c = customers.getJSONObject(i);
//                Customer newC = new Customer(
//                        c.getString("custID"),
//                        c.getString("fullname"),
//                        c.getString("phone"),
//                        Customer.Sex.valueOf(c.getString("sex")),
//                        c.getString("address"),
//                        c.getString("district"),
//                        c.getString("email")
//                );
//                result.add(newC);
            }
        } catch (Exception e) {
            Log.e("ex", Log.getStackTraceString(e));
        }

        return result;
    }


    public String syncQuotation(Context context, String appID, String email, List<Uri> uris, List<Bitmap> graphList, JSONArray invoice, JSONArray orderForm) {
        String result = "false";

        try {
            JSONObject toSend = new JSONObject();
            JSONArray photoArray = new JSONArray();
            JSONArray graphArray = new JSONArray();

//            FileWrapper fw = new FileWrapper(context, FileWrapper.Storage.INTERNAL, "photo");
            for (Uri u : uris) {
                FileWrapper fw = new FileWrapper(context, u);
//                fw.copyForm(b, Bitmap.CompressFormat.JPEG, 100, FileWrapper.Behavior.CREATE_ALWAYS);

                JSONObject photos = new JSONObject();
                photos.put("photo", fw.getBase64String());
                photoArray.put(photos);
            }

            FileWrapper fw = new FileWrapper(context, FileWrapper.Storage.INTERNAL, "photo");

            for (Bitmap b : graphList) {
//                fw.copyForm(b, Bitmap.CompressFormat.PNG, 100, FileWrapper.Behavior.CREATE_ALWAYS);
//                String base = DrawEventView.encodeToBase64(b, Bitmap.CompressFormat.PNG, 100);
                JSONObject graphs = new JSONObject();
                graphs.put("graph", DrawEventView.encodeToBase64(b, Bitmap.CompressFormat.PNG, 100));
                graphArray.put(graphs);
            }


            toSend.put("appid", appID);
            toSend.put("email", email);
            if (uris.size() != 0) {
                toSend.put("photo", photoArray);
            }
            if (graphList.size() != 0) {
                toSend.put("graph", graphArray);
            }
            toSend.put("invoice", invoice);
            toSend.put("orderform", orderForm);


            HttpURLConnection conn = getHttpConn(GET_URL + appendUrl, "POST", toSend);
            InputStream is = conn.getInputStream();
            result = stream2String(is);
            return result;

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;

    }

    public String syncAppointment(String empid, String custName, String custPhone, String flatBlock, String building, String appTime, String appid, String email) {
        String result = "";
        try {
            JSONObject toSend = new JSONObject();
            toSend.put("empID", empid);
            toSend.put("custName", custName);
            toSend.put("custPhone", custPhone);
            toSend.put("flatBlock", flatBlock);
            toSend.put("building", building);
            toSend.put("appointmentTime", appTime);
            if (appid.equalsIgnoreCase("")) {
                toSend.put("appid", "");
            } else {
                toSend.put("appid", appid);
            }
            toSend.put("email", email);
            HttpURLConnection conn = getHttpConn(GET_URL + appendUrl, "POST", toSend);
            InputStream is = conn.getInputStream();
            result = stream2String(is);
            Log.e("Error is : ", result);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    public String syncExistsAppointment(Context context) {

        WebAppointmentDAO webDAO = new WebAppointmentDAO(context);
        try {
            HttpURLConnection conn = getHttpConn(GET_URL + appendUrl, "GET", null);
            InputStream is = conn.getInputStream();
            JSONObject json = new JSONObject(stream2String(is));

            webDAO.delete();

            JSONArray app = json.optJSONArray("NewAppointment");

            if (app != null) {
                for (int i = 0; i < app.length(); i++) {
                    JSONObject w = app.getJSONObject(i);
                    WebAppointment webApp = new WebAppointment(
                            w.getString("appointmentID"),
                            w.getString("custName"),
                            w.getString("custPhone"),
                            w.getString("building"),
                            w.getString("flatBlock"),
                            w.getString("date"),
                            w.getString("remark"),
                            w.getString("email")
                    );
                    webDAO.insert(webApp);
                }
                return "true";
            } else {
                return "empty";
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return  "false";
    }

    public String syncCancelAppoinmtent(Context context, String appid) {
        String result = "false";
        try {
            JSONObject toSend = new JSONObject();
            toSend.put("appid", appid);

            HttpURLConnection conn = getHttpConn(GET_URL + appendUrl, "POST", toSend);
            InputStream is = conn.getInputStream();
            result = stream2String(is);
            Log.e("Error is : ", result);
            return result;
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    public boolean syncRecentJob(Context context) {
//        RecentJob result = new RecentJob();
        RecentJobDAO dao = new RecentJobDAO(context);
        try {
            HttpURLConnection conn = getHttpConn(GET_URL + appendUrl, "GET", null);
            InputStream is = conn.getInputStream();
            JSONObject json = new JSONObject(stream2String(is));
            //parse json

            //clear database
            dao.delete();

            JSONArray processing = json.optJSONArray("Processing");

            if (processing != null) {
                for (int i = 0; i < processing.length(); i++) {
                    JSONObject p = processing.getJSONObject(i);
                    String title = "N/A", remark = "N/A", status = "N/A", email = "N/A";
                    if (!p.isNull("title"))
                        title = p.getString("title");
                    if (!p.isNull("remark"))
                        remark = p.getString("remark");
                    if (!p.isNull("tstatus"))
                        status = p.getString("tstatus");
                    if (!p.isNull("email"))
                        email = p.getString("email");

                    LocalRecentJob newC = new LocalRecentJob(
                            title,
                            remark,
                            status,
                            p.getString("phone"),
                            p.getString("fullname"),
                            p.getString("flatBlock"),
                            p.getString("building"),
                            p.getString("districtEN"),
                            p.getString("island"),
                            "processing",
                            p.getString("appointmentID"),
                            p.getString("appointmentTime"),
                            email
                    );
//                    result.getProcessings().add(newC);
                    dao.insert(newC);
                }
            }

            JSONArray history = json.optJSONArray("History");

            if (history != null) {

                for (int i = 0; i < history.length(); i++) {
                    JSONObject h = history.getJSONObject(i);
                    String title = "N/A", remark = "N/A", status = "N/A", email = "N/A";
                    if (!h.isNull("title"))
                        title = h.getString("title");
                    if (!h.isNull("remark"))
                        remark = h.getString("remark");
                    if (!h.isNull("tstatus"))
                        status = h.getString("tstatus");
                    if (!h.isNull("email"))
                        email = h.getString("email");
                    LocalRecentJob newH = new LocalRecentJob(
                            title,
                            remark,
                            status,
                            h.getString("phone"),
                            h.getString("fullname"),
                            h.getString("flatBlock"),
                            h.getString("building"),
                            h.getString("districtEN"),
                            h.getString("island"),
                            "processing",
                            h.getString("appointmentID"),
                            h.getString("appointmentTime"),
                            email
                    );
//                    result.getHistories().add(newH);
                    dao.insert(newH);

                }
            }

            if (processing == null && history == null) {
                return false;
            }


        } catch (JSONException e) {

        } catch (Exception e) {
            Log.e("ex", Log.getStackTraceString(e));
        }

        return true;
    }

    public LocalScheduleDAO syncCalendar() {
        LocalScheduleDAO schDAO = new LocalScheduleDAO(context);
        String receive = "";
        try {
            HttpURLConnection conn = getHttpConn(GET_URL + appendUrl, "GET", null);
            InputStream is = conn.getInputStream();
            receive = stream2String(is);
            if (receive.equalsIgnoreCase("false")) {
                schDAO.delete();
            } else {
                JSONObject json = new JSONObject(receive);
                //parse json
                JSONArray schedule = json.optJSONArray("Schedule");
                if (schedule != null) {
                    schDAO.delete();
                    for (int i = 0; i < schedule.length(); i++) {
                        JSONObject s = schedule.getJSONObject(i);
                        String empID = "", taskID = "", empName = "", aremark = "";
                        if (!s.isNull("empID")) {
                            empID = s.getString("empID");
                            empName = s.getString("empName");
                        }
                        if (!s.isNull("taskID"))
                            taskID = s.getString("taskID");
                        if (!s.isNull("aremark"))
                            aremark = s.getString("aremark");
                        LocalSchedule sch = new LocalSchedule(
                                s.getString("appointmentID"),
                                s.getString("astatus"),
                                aremark,
                                taskID,
                                s.getString("flatBlock"),
                                s.getString("building"),
                                s.getString("districtEN"),
                                s.getString("appointmentTime"),
                                empID,
                                empName,
                                s.getString("custID"),
                                s.getString("cust_fullname"),
                                s.getString("cust_phone")
                        );
                        schDAO.insert(sch);
                    }
                }
            }
        } catch (Exception e) {
            Log.e("ex", Log.getStackTraceString(e));
        }
        return schDAO;
    }

    public LocalPreMonthScheduleDAO syncPreMonthCalendar() {
        LocalPreMonthScheduleDAO schDAO = new LocalPreMonthScheduleDAO(context);
        String receive = "";
        try {
            HttpURLConnection conn = getHttpConn(GET_URL + appendUrl, "GET", null);
            InputStream is = conn.getInputStream();
            receive = stream2String(is);
            if (receive.equalsIgnoreCase("false")) {
                schDAO.delete();
            } else {
                JSONObject json = new JSONObject(receive);
                //parse json
                JSONArray schedule = json.optJSONArray("Schedule");
                if (schedule != null) {
                    schDAO.delete();
                    for (int i = 0; i < schedule.length(); i++) {
                        JSONObject s = schedule.getJSONObject(i);
                        String empID = "", taskID = "", empName = "", aremark = "";
                        if (!s.isNull("empID")) {
                            empID = s.getString("empID");
                            empName = s.getString("empName");
                        }
                        if (!s.isNull("taskID"))
                            taskID = s.getString("taskID");
                        if (!s.isNull("aremark"))
                            aremark = s.getString("aremark");
                        LocalSchedule sch = new LocalSchedule(
                                s.getString("appointmentID"),
                                s.getString("astatus"),
                                aremark,
                                taskID,
                                s.getString("flatBlock"),
                                s.getString("building"),
                                s.getString("districtEN"),
                                s.getString("appointmentTime"),
                                empID,
                                empName,
                                s.getString("custID"),
                                s.getString("cust_fullname"),
                                s.getString("cust_phone")
                        );
                        schDAO.insert(sch);
                    }
                }
            }
        } catch (Exception e) {
            Log.e("ex", Log.getStackTraceString(e));
        }
        return schDAO;
    }

    public LocalNextMonthScheduleDAO syncNextMonthCalendar() {
        LocalNextMonthScheduleDAO schDAO = new LocalNextMonthScheduleDAO(context);
        String receive = "";
        try {
            HttpURLConnection conn = getHttpConn(GET_URL + appendUrl, "GET", null);
            InputStream is = conn.getInputStream();
            receive = stream2String(is);
            if (receive.equalsIgnoreCase("false")) {
                schDAO.delete();
            } else {
                JSONObject json = new JSONObject(receive);
                //parse json
                JSONArray schedule = json.optJSONArray("Schedule");
                if (schedule != null) {
                    schDAO.delete();
                    for (int i = 0; i < schedule.length(); i++) {
                        JSONObject s = schedule.getJSONObject(i);
                        String empID = "", taskID = "", empName = "", aremark = "";
                        if (!s.isNull("empID")) {
                            empID = s.getString("empID");
                            empName = s.getString("empName");
                        }
                        if (!s.isNull("taskID"))
                            taskID = s.getString("taskID");
                        if (!s.isNull("aremark"))
                            aremark = s.getString("aremark");
                        LocalSchedule sch = new LocalSchedule(
                                s.getString("appointmentID"),
                                s.getString("astatus"),
                                aremark,
                                taskID,
                                s.getString("flatBlock"),
                                s.getString("building"),
                                s.getString("districtEN"),
                                s.getString("appointmentTime"),
                                empID,
                                empName,
                                s.getString("custID"),
                                s.getString("cust_fullname"),
                                s.getString("cust_phone")
                        );
                        schDAO.insert(sch);
                    }
                }
            }
        } catch (Exception e) {
            Log.e("ex", Log.getStackTraceString(e));
        }
        return schDAO;
    }

    public String loadDraw() {
        String receive = "";
        String url = "http://58.177.9.234/fyp/test/";
        try {
            HttpURLConnection conn = getHttpConn(url + appendUrl, "GET", null);
            InputStream is = conn.getInputStream();
            receive = stream2String(is);

        } catch (Exception e) {
            Log.e("ex", Log.getStackTraceString(e));
        }
        Log.d("FLFL", "Get: " + receive);
        return receive;
    }

    public String loadDraw(int id) {
        String receive = "";
        String url = "http://58.177.9.234/fyp/test/";
        try {
            JSONObject toSend = new JSONObject();
            toSend.put("id", id);
            HttpURLConnection conn = getHttpConn(url + appendUrl, "POST", toSend);
            InputStream is = conn.getInputStream();
            receive = stream2String(is);

        } catch (Exception e) {
            Log.e("ex", Log.getStackTraceString(e));
        }
        Log.d("FLFL", "Get: " + receive);
        return receive;
    }

    public void syncDraw(String pathsJson, String paintsColorJson, String paintsWidthJson, String paintsEffectJson, String bitmapString, String name) {
        String url = "http://58.177.9.234/fyp/test/";
        try {
            JSONObject toSend = new JSONObject();
            toSend.put("path", pathsJson);
            toSend.put("name", name);
            toSend.put("color", paintsColorJson);
            toSend.put("width", paintsWidthJson);
            toSend.put("effect", paintsEffectJson);
            toSend.put("bitmapString", bitmapString);
            HttpURLConnection conn = getHttpConn(url + appendUrl, "POST", toSend);
            InputStream is = conn.getInputStream();
            Log.d("FLFL", stream2String(is));
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String initDrawNag() {
        String receive = "";
        String url = "http://58.177.9.234/fyp/test/";
        try {
            HttpURLConnection conn = getHttpConn(url + appendUrl, "GET", null);
            InputStream is = conn.getInputStream();
            receive = stream2String(is);

        } catch (Exception e) {
            Log.e("ex", Log.getStackTraceString(e));
        }
        Log.d("FLFL", "Get: " + receive);
        return receive;
    }

    private static String stream2String(InputStream stream) {
        if (stream == null)
            return null;
        BufferedReader br = null;
        StringBuilder sb = new StringBuilder();

        String line;
        try {
            br = new BufferedReader(new InputStreamReader(stream));
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

//            ByteArrayOutputStream bao = new ByteArrayOutputStream();
//            String text;
//            int c;
//            byte[] buffer = new byte[4096];
//            while ((c = stream.read(buffer)) >= 0) {
//                bao.write(buffer, 0, c);
//            }
//            stream.close();
//            text = new String(bao.toByteArray());
//            Log.d("Hello",text);
//            bao.close();


        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return sb.toString();
    }
}
