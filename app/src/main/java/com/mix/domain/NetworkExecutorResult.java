package com.mix.domain;

import com.mix.constant.DataType;

import java.io.InputStream;

public class NetworkExecutorResult {
    private DataType dataType;
    private InputStream inputStream;

    public NetworkExecutorResult(DataType dataType, InputStream inputStream) {
        this.dataType = dataType;
        this.inputStream = inputStream;
    }

    public DataType getDataType() {
        return dataType;
    }

    public InputStream getInputStream() {
        return inputStream;
    }
}

