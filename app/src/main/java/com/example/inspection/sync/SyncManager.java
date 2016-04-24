package com.example.inspection.sync;

import android.util.Log;

import com.example.inspection.models.Appointment;
import com.example.inspection.models.Customer;
import com.example.inspection.models.History;
import com.example.inspection.models.Processing;
import com.example.inspection.models.RecentJob;
import com.example.inspection.models.Schedule;
import com.example.inspection.models.Task;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
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

    public SyncManager(String url) {
        this.appendUrl = url;
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

    public String syncLogin(String username, String password) {
        String result = "";
        try {
            JSONObject toSend = new JSONObject();
            toSend.put("username", username);
            toSend.put("password", password);
            HttpURLConnection conn = getHttpConn(GET_URL+appendUrl, "POST", toSend);
            InputStream is = conn.getInputStream();
            result += stream2String(is);
            Log.d("Result is", result);
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
            HttpURLConnection conn = getHttpConn(GET_URL+appendUrl, "GET", null);
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

    public String syncAppointment(String empid, String custName, String custPhone, String flatBlock, String building, String appTime) {
        String result = "";
            try {
                JSONObject toSend = new JSONObject();
                toSend.put("empID", empid);
                toSend.put("custName", custName);
                toSend.put("custPhone", custPhone);
                toSend.put("flatBlock", flatBlock);
                toSend.put("building", building);
                toSend.put("appointmentTime", appTime);
                HttpURLConnection conn = getHttpConn("http://58.177.9.234/fyp/json/addAppointment.php", "POST", toSend);
                InputStream is = conn.getInputStream();
                result += stream2String(is);
                Log.e("Error is : ",result);
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        return result;
    }

    public RecentJob syncRecentJob() {
        RecentJob result = new RecentJob();
        try {
            HttpURLConnection conn = getHttpConn(GET_URL + appendUrl, "GET", null);
            InputStream is = conn.getInputStream();
            JSONObject json = new JSONObject(stream2String(is));
            //parse json
            JSONArray processing = json.optJSONArray("Processing");

            if(processing != null) {

                for (int i = 0; i < processing.length(); i++) {
                    JSONObject p = processing.getJSONObject(i);
                    Processing newC = new Processing(
                            p.getString("title"),
                            p.getString("remark"),
                            p.getString("tstatus"),
                            p.getString("phone"),
                            p.getString("fullname"),
                            p.getString("flatBlock"),
                            p.getString("building"),
                            p.getString("districtEN"),
                            p.getString("island")
                    );
                    result.getProcessings().add(newC);
                }
            }

            JSONArray history = json.optJSONArray("History");

            if(history != null) {

                for (int i = 0; i < history.length(); i++) {
                    JSONObject h = history.getJSONObject(i);
                    History newH = new History(
                            h.getString("title"),
                            h.getString("remark"),
                            h.getString("tstatus"),
                            h.getString("phone"),
                            h.getString("fullname"),
                            h.getString("flatBlock"),
                            h.getString("building"),
                            h.getString("districtEN"),
                            h.getString("island")
                    );
                    result.getHistories().add(newH);
                }
            }

            if(processing == null && history == null) {
                return null;
            }


        } catch (JSONException e) {

        } catch (Exception e) {
            Log.e("ex", Log.getStackTraceString(e));
        }

        return result;
    }

    public Schedule syncCalendar(){
        List<Appointment> result = new ArrayList<Appointment>();
        int[] assign = new int[31];
        int[] notAssign = new int[31];
        for(int i=0; i<31; i++){
            assign[i] = 0;
            notAssign[i] = 0;
        }
        try {
            HttpURLConnection conn = getHttpConn(GET_URL + appendUrl, "GET", null);
            InputStream is = conn.getInputStream();
            String receive = stream2String(is);
            if(!receive.equalsIgnoreCase("false")){
                JSONObject json = new JSONObject(receive);
                //parse json
                JSONArray schedule = json.optJSONArray("Schedule");
                DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                for (int i = 0; i < schedule.length(); i++) {
                    JSONObject s = schedule.getJSONObject(i);
                    Date appointmentTime = formatter.parse(s.getString("appointmentTime"));
                    String empID = "", taskID = "", empName = "";
                    if (!s.isNull("empID")) {
                        empID = s.getString("empID");
                        empName = s.getString("empName");
                        assign[appointmentTime.getDate() - 1]++;
                    } else {
                        notAssign[appointmentTime.getDate() - 1]++;
                    }
                    if(s.isNull("taskID"))
                        taskID = s.getString("taskID");
                    Customer cust = new Customer(
                            s.getString("custID"),
                            s.getString("cust_fullname"),
                            s.getString("cust_phone"),
                            Customer.Sex.valueOf("M"),
                            ""//customer email
                    );
                    Appointment apt = new Appointment(
                            s.getString("appointmentID"),
                            s.getString("astatus"),
                            s.getString("aremark"),
                            taskID,
                            s.getString("flatBlock"),
                            s.getString("building"),
                            s.getString("districtEN"),
                            appointmentTime,
                            empID,
                            empName,
                            cust
                    );
                    result.add(apt);
                }
            }
        } catch (Exception e) {
            Log.e("ex", Log.getStackTraceString(e));
        }

        return new Schedule(result, assign, notAssign);
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
