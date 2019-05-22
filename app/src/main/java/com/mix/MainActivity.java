package com.mix;

import android.Manifest;
import android.arch.lifecycle.ViewModelProviders;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.google.gson.Gson;
import com.mix.adapter.FeedsAdapter;
import com.mix.constant.ConstantValues;
import com.mix.constant.DataType;
import com.mix.domain.Feed;
import com.mix.manager.FileManager;
import com.mix.manager.NetworkManager;
import com.mix.notificationcenter.NotificationCenter;
import com.mix.permission.PermissionUtils;
import com.mix.service.NetworkIntentService;

import java.util.ArrayList;
import java.util.List;

import kotlin.Unit;

public class MainActivity extends AppCompatActivity implements FeedsAdapter.OnItemClickListener {
    final static String TAG = MainActivity.class.getSimpleName();

    RecyclerView recyclerView;
    FeedsAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initList();
        startNetworkService();

        NotificationCenter.INSTANCE.addObserver(this,
                NotificationCenter.NotificationType.DATA_RECEIVED, intent -> {
                    List<Feed> feeds = (List<Feed>) ((Intent) intent).getSerializableExtra(ConstantValues.RECEIVED_DATA);
                    updateItems(feeds);
                    return Unit.INSTANCE;
                });

    }

    private void updateItems(List<Feed> feeds) {
        Log.i(ConstantValues.BASE_TAG, TAG + " update list " + new Gson().toJson(feeds));
        adapter.setData(feeds);
    }

    private void initList() {
        adapter = new FeedsAdapter(this, this);
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
    }

    private void startNetworkService() {
        Intent intent = new Intent(this, NetworkIntentService.class);
        startService(intent);
    }


    @Override
    protected void onDestroy() {
        NotificationCenter.INSTANCE.removeObserver(this);
        super.onDestroy();
    }

    @Override
    public void openWebView(Feed feed) {
        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra(ConstantValues.FEED_URL, feed.getLink());
        startActivity(intent);
    }

    @Override
    public void saveFeedInfo(Feed feed, View view) {
        PopupMenu popup = new PopupMenu(MainActivity.this, view);
        popup.getMenuInflater().inflate(R.menu.save_option_menu, popup.getMenu());
        popup.setOnMenuItemClickListener(item -> {
            if (!PermissionUtils.hasPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                String[] perm = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
                PermissionUtils.requestPermissions(MainActivity.this, perm, 1);
            } else if (item.getTitle().equals("XML")) {
                FileManager.getInstance().saveToFile(getApplicationContext(), feed, DataType.RSS);
            } else if (item.getTitle().equals("JSON")) {
                FileManager.getInstance().saveToFile(getApplicationContext(), feed, DataType.JSON);
            }

            return true;
        });

        popup.show();//showing popup menu

    }
}
