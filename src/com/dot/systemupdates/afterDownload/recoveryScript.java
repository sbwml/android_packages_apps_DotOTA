package com.dot.systemupdates.afterDownload;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.dot.systemupdates.R;
import com.dot.systemupdates.utils.Constants;
import com.dot.systemupdates.utils.Prefs;
import com.dot.systemupdates.utils.SystemProperties;
import com.dot.systemupdates.utils.Tools;
import com.dot.systemupdates.utils.xmlParser;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class recoveryScript extends AsyncTask<Void, String, Boolean> implements Constants {

    private final String TAG = this.getClass().getSimpleName();

    @SuppressLint("StaticFieldLeak")
    private Context mContext;
    private StringBuilder mScript = new StringBuilder();
    private String mFilename;
    private String mScriptOutput;
    private String[] serverNodes;
    private String Url = "https://ota.sbwml.net/DotOS/";

    public recoveryScript(Context context) {
        mContext = context;
        String device = SystemProperties.get("ro.dotOS.device");
        try {
            xmlParser xmlParser = new xmlParser();
            serverNodes = xmlParser.execute(Url+device+".xml").get();
            mFilename = serverNodes[0] + ".zip";
        } catch ( InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    protected void onPreExecute() {
        String NEW_LINE = "\n";
        StringBuilder installRom = new StringBuilder()
                .append(WIPE_DALVIK)
                .append(NEW_LINE)
                .append(WIPE_CACHE)
                .append(NEW_LINE)
                .append("install ")
                .append(File.separator)
                .append(SD_CARD)
                .append(File.separator)
                .append("Download")
                .append(File.separator)
                .append(mFilename)
                .append(NEW_LINE)
                .append("reboot ")
                .append(NEW_LINE);
        mScript.append(installRom);
        if (DEBUGGING) Log.d(TAG,installRom.toString());
        mScriptOutput = mScript.toString();
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        boolean orsWrittenToCache = false;
        File orsDir = new File("/cache/recovery");
        File orsFile = new File("/cache/recovery/openrecoveryscript");
        if (orsDir.exists()){
            try {
                orsWrittenToCache = orsDir.createNewFile();
                if (!orsFile.exists()){
                    orsWrittenToCache = orsFile.createNewFile();
                    FileWriter fileWriter = new FileWriter(orsFile);
                    BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
                    bufferedWriter.write(String.format(mScriptOutput));
                    bufferedWriter.flush();
                    bufferedWriter.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return orsWrittenToCache;
    }
    @Override
    protected void onPostExecute(Boolean value) {
        Tools.recovery(mContext);
    }
}
