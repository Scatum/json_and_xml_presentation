package com.mix.manager;

import android.content.Intent;
import android.util.Log;


import com.google.gson.Gson;
import com.mix.constant.ConstantValues;
import com.mix.constant.DataType;
import com.mix.domain.Feed;
import com.mix.domain.NetworkExecutorResult;
import com.mix.executor.AppExecutors;
import com.mix.notificationcenter.NotificationCenter;
import com.mix.parser.StreamParser;
import com.mix.worker.NetworkWorker;


import java.io.Serializable;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;


public class NetworkManager {

    NetworkWorker rssNetworkWorker = new NetworkWorker(ConstantValues.SRC_RSS, DataType.RSS);
    NetworkWorker jsonNetworkWorkerThread = new NetworkWorker(ConstantValues.SRC_JSON, DataType.JSON);
    final static String TAG = NetworkManager.class.getSimpleName();


    // Static and Volatile attribute.
    private static volatile NetworkManager instance = null;

    private NetworkManager() {
    }

    public static NetworkManager getInstance() {
        if (instance == null) {
            synchronized (NetworkManager.class) {
                if (instance == null) {
                    instance = new NetworkManager();
                }
            }
        }
        return instance;
    }


    public void fetchData() throws ExecutionException, InterruptedException {
        Future<NetworkExecutorResult> futureRssResult = AppExecutors.get().networkExecutorService().submit(rssNetworkWorker);

        Log.e(ConstantValues.BASE_TAG, TAG + " fetchData start time " + System.currentTimeMillis());
        Future<NetworkExecutorResult> futureJsonResult = AppExecutors.get().networkExecutorService().submit(jsonNetworkWorkerThread);

        if (futureRssResult != null) {
            handleNetworkResult(futureRssResult.get());
        }

        if (futureJsonResult != null) {
            handleNetworkResult(futureJsonResult.get());
        }
    }

    private synchronized void handleNetworkResult(final NetworkExecutorResult networkExecutorResult) {
        if (networkExecutorResult == null) {
            Log.e(ConstantValues.BASE_TAG, TAG + "something went Wrong");
            return;
        }
        List<Feed> feeds = null;
        Intent intent = new Intent();
        switch (networkExecutorResult.getDataType()) {
            case JSON:
                feeds = StreamParser.getInstance().parseJsonFeed(networkExecutorResult.getInputStream());
                Log.i(ConstantValues.BASE_TAG, TAG + " JSON from Server " + new Gson().toJson(feeds));
                break;
            case RSS:
                feeds = StreamParser.getInstance().parseRssFeed(networkExecutorResult.getInputStream());
                Log.i(ConstantValues.BASE_TAG, TAG + " JSON from RSS " + new Gson().toJson(feeds));
                break;
            default:
                throw new RuntimeException(TAG + " Unsupported Data Type");
        }
        intent.putExtra(ConstantValues.RECEIVED_DATA, (Serializable) feeds);
        NotificationCenter.INSTANCE.postNotificationName(
                NotificationCenter.NotificationType.DATA_RECEIVED, intent
        );

    }


}
