
package com.mix.service;

import android.app.IntentService;
import android.content.Intent;

import com.mix.manager.NetworkManager;

import java.util.concurrent.ExecutionException;


/*
*
* We can use the work manager, but in this case I'll use the IntentService.
* */
public class NetworkIntentService extends IntentService {

    public static final String TAG = "NetworkIntentService";

    public NetworkIntentService() {
        super(TAG);
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        try {
            NetworkManager.getInstance().fetchData();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
