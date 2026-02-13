package com.example.vroom.network;

import com.example.vroom.data.local.StorageManager;
import io.reactivex.disposables.Disposable;
import okhttp3.OkHttpClient;
import ua.naiksoftware.stomp.Stomp;
import ua.naiksoftware.stomp.StompClient;
import ua.naiksoftware.stomp.dto.StompHeader;
import java.util.ArrayList;
import java.util.List;

public class SocketProvider {
    private static SocketProvider instance;
    private StompClient stompClient;

    private SocketProvider(){}

    public static synchronized SocketProvider getInstance() {
        if (instance == null){
            instance = new SocketProvider();
        }
        return instance;
    }

    public void init(){
        if (stompClient != null && stompClient.isConnected()) return;
        stompClient = Stomp.over(Stomp.ConnectionProvider.OKHTTP, "ws://192.168.0.100:8080/socket/websocket");
        List<StompHeader> headers = new ArrayList<>();
        String token = StorageManager.getData("jwt", null);
        if (token != null) {
            headers.add(new StompHeader("Authorization", "Bearer " + token));
        }
        stompClient.connect(headers);
    }

    public void disconnect() {
        if (stompClient != null){
            stompClient.disconnect();
        }
    }

    public StompClient getClient() {
        if (stompClient == null){
            init();
        }
        return stompClient;
    }

}
