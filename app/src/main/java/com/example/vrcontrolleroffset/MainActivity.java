package com.example.vrcontrolleroffset;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private SeekBar seekX;
    private SeekBar seekY;
    private SeekBar seekZ;
    private TextView textX;
    private TextView textY;
    private TextView textZ;
    private Switch fixedControllersSwitch;
    private Button startServiceButton;
    private Button stopServiceButton;
    private Button saveLogButton;
    private Button shareLogButton;
    private Button checkUpdateButton;
    private Button testNativeButton;
    private android.app.DownloadManager downloadManager;
    private long downloadId = -1L;
    private android.content.BroadcastReceiver downloadReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        seekX = findViewById(R.id.seekX);
        seekY = findViewById(R.id.seekY);
        seekZ = findViewById(R.id.seekZ);
        textX = findViewById(R.id.textX);
        textY = findViewById(R.id.textY);
        textZ = findViewById(R.id.textZ);
        fixedControllersSwitch = findViewById(R.id.fixedControllersSwitch);
        startServiceButton = findViewById(R.id.startServiceButton);
        stopServiceButton = findViewById(R.id.stopServiceButton);
        saveLogButton = findViewById(R.id.saveLogButton);
        shareLogButton = findViewById(R.id.shareLogButton);
        checkUpdateButton = findViewById(R.id.checkUpdateButton);
        testNativeButton = findViewById(R.id.testNativeButton);

        setupSeekBar(seekX, textX, "X");
        setupSeekBar(seekY, textY, "Y");
        setupSeekBar(seekZ, textZ, "Z");

        fixedControllersSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                OffsetService.setFixedControllers(isChecked);
            }
        });

        startServiceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, OffsetService.class);
                intent.setAction(OffsetService.ACTION_START_FOREGROUND);
                startService(intent);
            }
        });

        stopServiceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, OffsetService.class);
                intent.setAction(OffsetService.ACTION_STOP_FOREGROUND);
                startService(intent);
            }
        });

        LogHelper.init(this);

        saveLogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                float x = OffsetService.getOffsetX();
                float y = OffsetService.getOffsetY();
                float z = OffsetService.getOffsetZ();
                boolean fixed = OffsetService.isFixedControllers();
                LogHelper.append(String.format("Saved state: X=%.2f Y=%.2f Z=%.2f Fixed=%s", x, y, z, fixed ? "On" : "Off"));
                java.io.File f = LogHelper.getLogFile();
                if (f != null && f.exists()) {
                    try {
                        java.io.FileInputStream fis = new java.io.FileInputStream(f);
                        java.io.InputStreamReader isr = new java.io.InputStreamReader(fis);
                        java.io.BufferedReader br = new java.io.BufferedReader(isr);
                        StringBuilder sb = new StringBuilder();
                        String line;
                        while ((line = br.readLine()) != null) sb.append(line).append('\n');
                        br.close();
                        ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                        ClipData clip = ClipData.newPlainText("vr_offset_log", sb.toString());
                        if (cm != null) cm.setPrimaryClip(clip);
                        Toast.makeText(MainActivity.this, "Log copied to clipboard", Toast.LENGTH_SHORT).show();
                    } catch (Exception ignored) {
                    }
                }
            }
        });

        shareLogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                java.io.File file = LogHelper.getLogFile();
                if (file == null || !file.exists()) return;
                try {
                    java.io.FileInputStream fis = new java.io.FileInputStream(file);
                    java.io.InputStreamReader isr = new java.io.InputStreamReader(fis);
                    java.io.BufferedReader br = new java.io.BufferedReader(isr);
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) sb.append(line).append('\n');
                    br.close();
                    ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText("vr_offset_log", sb.toString());
                    if (cm != null) cm.setPrimaryClip(clip);
                    Toast.makeText(MainActivity.this, "Log copied to clipboard", Toast.LENGTH_SHORT).show();
                } catch (Exception ignored) {
                }
                android.net.Uri uri = androidx.core.content.FileProvider.getUriForFile(
                        MainActivity.this,
                        "com.example.vrcontrolleroffset.fileprovider",
                        file
                );
                Intent share = new Intent(Intent.ACTION_SEND);
                share.setType("text/plain");
                share.putExtra(Intent.EXTRA_STREAM, uri);
                share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                startActivity(Intent.createChooser(share, "Share log"));
            }
        });

        downloadManager = (android.app.DownloadManager) getSystemService(DOWNLOAD_SERVICE);
        checkUpdateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "https://github.com/ortizisaac030303-alt/All-vrrrr/raw/main/apk/app-debug.apk";
                android.app.DownloadManager.Request request = new android.app.DownloadManager.Request(android.net.Uri.parse(url));
                request.setTitle("Downloading update");
                request.setNotificationVisibility(android.app.DownloadManager.Request.VISIBILITY_VISIBLE);
                request.setDestinationInExternalFilesDir(MainActivity.this, null, "update.apk");
                if (downloadManager != null) {
                    downloadId = downloadManager.enqueue(request);
                }
            }
        });

        downloadReceiver = new android.content.BroadcastReceiver() {
            @Override
            public void onReceive(android.content.Context context, android.content.Intent intent) {
                long id = intent.getLongExtra(android.app.DownloadManager.EXTRA_DOWNLOAD_ID, -1L);
                if (id != downloadId) return;
                java.io.File file = new java.io.File(getExternalFilesDir(null), "update.apk");
                if (!file.exists()) return;
                android.net.Uri uri = androidx.core.content.FileProvider.getUriForFile(
                        MainActivity.this,
                        "com.example.vrcontrolleroffset.fileprovider",
                        file
                );
                Intent install = new Intent(Intent.ACTION_INSTALL_PACKAGE);
                install.setData(uri);
                install.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_ACTIVITY_NEW_TASK);
                try {
                    startActivity(install);
                } catch (Exception e) {
                    LogHelper.append("Failed to launch installer: " + e.getMessage());
                }
            }
        };
        registerReceiver(downloadReceiver, new android.content.IntentFilter(android.app.DownloadManager.ACTION_DOWNLOAD_COMPLETE));

        testNativeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                float x = OffsetService.getOffsetX();
                float y = OffsetService.getOffsetY();
                float z = OffsetService.getOffsetZ();
                boolean fixed = OffsetService.isFixedControllers();
                LogHelper.append("Testing native layer...");
                NativeVRLayer.setSynchronizedOffsets(x, y, z, fixed);
                float[] result = NativeVRLayer.getAndLogCurrentOffsets();
                if (result != null) {
                    Toast.makeText(MainActivity.this, "Native layer: L(" + String.format("%.1f,%.1f,%.1f", result[0], result[1], result[2]) + ")", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "Native layer not loaded", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void setupSeekBar(SeekBar seekBar, final TextView valueText, final String axis) {
        seekBar.setMax(200);
        seekBar.setProgress(100);
        updateAxisText(valueText, axis, 0.0f);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                float offset = (progress - 100) / 10.0f;
                updateAxisText(valueText, axis, offset);
                OffsetService.setOffset(axis, offset);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }

    private void updateAxisText(TextView textView, String axis, float offset) {
        textView.setText(axis + " Offset: " + String.format("%.1f", offset));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            if (downloadReceiver != null) unregisterReceiver(downloadReceiver);
        } catch (IllegalArgumentException ignored) {
        }
    }
}
