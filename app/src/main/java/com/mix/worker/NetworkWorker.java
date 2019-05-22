package com.mix.worker;

import com.mix.constant.ConstantValues;
import com.mix.domain.NetworkExecutorResult;
import com.mix.manager.NetworkManager;
import com.mix.constant.DataType;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.Callable;

public class NetworkWorker implements Callable<NetworkExecutorResult> {
    private String srcUrl;
    private DataType dataType;

    public NetworkWorker(String srcUrl, DataType dataType) {
        this.srcUrl = srcUrl;
        this.dataType = dataType;
    }

    @Override
    public NetworkExecutorResult call() throws Exception {
        return new NetworkExecutorResult(dataType, getInputStream(srcUrl));
    }

    private InputStream getInputStream(String srcUrl) {
        InputStream inputStream = null;
        URL url = null;
        try {
            url = new URL(srcUrl);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        try {
            inputStream = url.openConnection().getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return inputStream;

    }
}
