package com.example.vrcontrolleroffset;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
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
}
