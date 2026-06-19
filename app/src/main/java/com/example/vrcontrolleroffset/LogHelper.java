package com.example.vrcontrolleroffset;

import android.content.Context;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class LogHelper {
    private static File logFile;

    public static void init(Context ctx) {
        if (logFile == null) {
            File dir = ctx.getExternalFilesDir(null);
            if (dir != null) {
                logFile = new File(dir, "vr_offset_log.txt");
            }
        }
    }

    public static synchronized void append(String line) {
        if (logFile == null) return;
        String ts = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(new Date());
        try (BufferedWriter w = new BufferedWriter(new FileWriter(logFile, true))) {
            w.write(ts + " - " + line + "\n");
        } catch (IOException ignored) {
        }
    }

    public static File getLogFile() {
        return logFile;
    }
}
