package com.example.inspection.service;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketException;
import com.neovisionaries.ws.client.WebSocketFactory;

import org.json.JSONObject;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by Sur.Vival on 7/5/2016.
 */
public class AppointmentService extends IntentService {


    public AppointmentService() {
        super("AppointmentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        WebSocket ws = null;
        try {
            ws = new WebSocketFactory().createSocket("ws://192.168.1.2:8080", 5000);
            ws.addListener(new WebSocketAdapter(){
                @Override
                public void onConnected(WebSocket websocket, Map<String, List<String>> headers) throws Exception {
                    super.onConnected(websocket, headers);
                }

                @Override
                public void onConnectError(WebSocket websocket, WebSocketException exception) throws Exception {
                    super.onConnectError(websocket, exception);
                }

                @Override
                public void onTextMessage(WebSocket websocket, String text) throws Exception {
                    JSONObject data = new JSONObject(text);
                    if(data.getString("action").equals("newAppointment")){

                    }
                }

            });
            ws.connectAsynchronously();
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

}
