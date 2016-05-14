package com.example.inspection.service;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.example.inspection.R;
import com.example.inspection.dao.WebAppointmentDAO;
import com.example.inspection.dbmodels.WebAppointment;
import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketException;
import com.neovisionaries.ws.client.WebSocketFactory;
import com.neovisionaries.ws.client.WebSocketFrame;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by Sur.Vival on 7/5/2016.
 */
public class AppointmentService extends Service {


    @Override
    public void onCreate() {
        super.onCreate();
        Log.i("Service", "onCreate");

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("Service","onStartCommand");

        WebSocket ws = null;
        try {
            ws = new WebSocketFactory().createSocket("ws://58.177.9.234:8080");
            ws.addListener(new WebSocketAdapter(){
                @Override
                public void onConnected(WebSocket websocket, Map<String, List<String>> headers) throws Exception {
                    super.onConnected(websocket, headers);
                }

                @Override
                public void onDisconnected(WebSocket websocket, WebSocketFrame serverCloseFrame, WebSocketFrame clientCloseFrame, boolean closedByServer) throws Exception {
                    super.onDisconnected(websocket, serverCloseFrame, clientCloseFrame, closedByServer);
                }

                @Override
                public void onTextMessage(WebSocket websocket, String text) throws Exception {
                    JSONObject data = new JSONObject(text);
                    JSONArray details = data.optJSONArray("NewAppointment");
                    if(details != null){
                        JSONObject o = details.getJSONObject(0);
                        WebAppointment webApp = new WebAppointment(
                                o.getString("appointmentID"),
                                o.getString("custName"),
                                o.getString("custPhone"),
                                o.getString("building"),
                                o.getString("flatBlock"),
                                o.getString("date"),
                                o.getString("remark")
                        );
                        WebAppointmentDAO webAppDAO = new WebAppointmentDAO(getApplicationContext());

                        if(webAppDAO.insert(webApp) != -1){
                            NotificationCompat.Builder builder =
                                    (NotificationCompat.Builder) new NotificationCompat.Builder(getBaseContext())
                                            .setSmallIcon(R.drawable.ic_launcher)
                                            .setContentTitle("Inspection")
                                            .setContentText("You have 1 new appointment")
                                            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                                            .setPriority(Notification.PRIORITY_HIGH);
                            NotificationManager notificationManager =
                                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                            notificationManager.notify(1,builder.build());
                        } else {

                        }
                    }
                }

            });
            ws.connectAsynchronously();
        } catch(IOException e) {
            e.printStackTrace();
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("Service", "onDestroy");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
