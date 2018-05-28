package com.dot.systemupdates;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.app.job.JobWorkItem;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.RequiresPermission;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dot.systemupdates.afterDownload.prepareToFlash;
import com.dot.systemupdates.service.updaterService;
import com.dot.systemupdates.utils.ObjectToolsAnimator;
import com.dot.systemupdates.utils.SystemProperties;
import com.dot.systemupdates.utils.xmlParser;
import com.dot.systemupdates.views.ExpandableLayout;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

import static com.dot.systemupdates.utils.Constants.OTA_DOWNLOAD_DIR;
import static com.dot.systemupdates.utils.Constants.SD_CARD;

public class baseActivity extends AppCompatActivity {
    String Url = "https://ota.sbwml.net/DotOS/";
    String[] serverNodes;
    DownloadManager downloadManager;
    String changelog;
    boolean isOff;
    int intervals= 86400000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
        final ImageButton show_more_vector = findViewById(R.id.show_more);
        final ExpandableLayout updates_info_layout = findViewById(R.id.update_info);
        show_more_vector.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (updates_info_layout.isCollapsed()) {
                    updates_info_layout.expand(true);
                    ObjectToolsAnimator.rotate(show_more_vector, 0, 180);
                } else if (updates_info_layout.isExpanded()) {
                    updates_info_layout.collapse(true);
                    ObjectToolsAnimator.rotate(show_more_vector, 180, 360);
                }
            }
        });
        readChangelogFromSystem();
        RelativeLayout changelog_click = findViewById(R.id.changelog_click);
        changelog_click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder changes = new AlertDialog.Builder(baseActivity.this);
                changes.setTitle("Changelog")
                        .setMessage(changelog)
                        .setCancelable(true)
                        .setNeutralButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .show();
            }
        });
        RelativeLayout test = findViewById(R.id.beforeFlash_test);
        test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), prepareToFlash.class));
            }
        });
        updateDisplayVersion();
        getUpdateState();
        JobScheduler jobScheduler = new JobScheduler() {
			@Override
            @RequiresPermission(android.Manifest.permission.UPDATE_DEVICE_STATS)
            public int scheduleAsPackage(@NonNull JobInfo job, @NonNull String $,
                                                          int userId, String ta$) {
                return 0;
            }
            @SuppressLint("WrongConstant")
            @Override
            public int schedule(@NonNull JobInfo job) {
                return 0;
            }
            @SuppressLint("WrongConstant")
            @Override
            public int enqueue(@NonNull JobInfo job, @NonNull JobWorkItem work) {
                return 0;
            }

            @Override
            public void cancel(int jobId) {

            }

            @Override
            public void cancelAll() {

            }

            @NonNull
            @Override
            public List<JobInfo> getAllPendingJobs() {
                return null;
            }

            @Nullable
            @Override
            public JobInfo getPendingJob(int jobId) {
                return null;
            }
        };
        jobScheduler.schedule(new JobInfo.Builder(0,new ComponentName(this,updaterService.class)).setPeriodic(intervals).build());
        File installAfterFlashDir = new File(SD_CARD
                + File.separator
                + OTA_DOWNLOAD_DIR);
        if (!installAfterFlashDir.mkdirs()) Log.e("SystemUpdates","Download directory creation failed");
        ImageButton go_back = findViewById(R.id.go_back);
        go_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void getUpdateState() {
        FloatingActionButton updateCheck = findViewById(R.id.check_updates);
        Button changes = findViewById(R.id.v_changelog);
        downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        if (isUNOFFICIAL()) {
            updateCheck();
            updateCheck.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    updateCheck();
                }
            });
        }
        changes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder changes = new AlertDialog.Builder(baseActivity.this);
                changes.setTitle("Changelog")
                        .setMessage(changelog)
                        .setCancelable(true)
                        .setNeutralButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .show();
            }
        });
    }

    public void updateCheck() {
        FloatingActionButton updateCheck = findViewById(R.id.check_updates);
        TextView updateChangelog = findViewById(R.id.new_changes);
        TextView new_version = findViewById(R.id.update_version);
        TextView size = findViewById(R.id.size_mb);
        TextView dot_version = findViewById(R.id.dotOS_version);
        RelativeLayout no_updates = findViewById(R.id.no_updates);
        try {
            LinearLayout updateFound = findViewById(R.id.scroll_main);
            String localVersion = SystemProperties.get("ro.dot.version");
            final String device = SystemProperties.get("ro.dotOS.device");
            xmlParser xmlParser = new xmlParser();
            serverNodes = xmlParser.execute(Url+device+".xml").get();
            new_version.setText(serverNodes[0]);
		Log.d("jacob",(serverNodes[0]));
            String downloadCompleteIntentName = DownloadManager.ACTION_DOWNLOAD_COMPLETE;
            final IntentFilter downloadCompleteIntentFilter = new IntentFilter(downloadCompleteIntentName);
            if (!localVersion.equals(serverNodes[0]) && serverNodes[0] != null) {
                no_updates.setVisibility(View.GONE);
                dot_version.setText("Update Available");
                updateCheck.setContentDescription("yes");
                updateChangelog.setText(serverNodes[1]);
                updateCheck.setImageResource(R.drawable.ic_download);
                size.setText(getFileSize(Long.parseLong(serverNodes[3])));
                if (updateCheck.getContentDescription() == "yes") {
                    updateFound.setVisibility(View.VISIBLE);
                    updateCheck.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                                ActivityCompat.shouldShowRequestPermissionRationale(baseActivity.this,
                                        Manifest.permission.WRITE_EXTERNAL_STORAGE);
                                ActivityCompat.requestPermissions(baseActivity.this,
                                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                        0);
                            }
                            if (ContextCompat.checkSelfPermission(baseActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                                DownloadManager.Request request = new DownloadManager.Request(Uri.parse(serverNodes[2]));
                                request.setTitle("Downloading System Update");
                                request.setDescription(serverNodes[0] + " is available to download");
                                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, OTA_DOWNLOAD_DIR + "/" + serverNodes[0] + ".zip");
                                downloadManager.enqueue(request);
                                BroadcastReceiver downloadCompleteReceiver = new BroadcastReceiver() {
                                    @Override
                                    public void onReceive(final Context context, Intent intent) {
                                        AlertDialog.Builder down_end = new AlertDialog.Builder(baseActivity.this);
                                        down_end.setTitle("Download Finished")
                                                .setMessage("Update manually or do you want to AutoFlash?")
                                                .setCancelable(false)
                                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        startActivity(new Intent(getApplicationContext(), prepareToFlash.class));
                                                        dialog.dismiss();
                                                    }
                                                })
                                                .setNeutralButton("No, thanks", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        dialog.dismiss();
                                                    }
                                                })
                                                .show();
                                    }
                                };
                                v.getContext().registerReceiver(downloadCompleteReceiver, downloadCompleteIntentFilter);
                            }
                        }
                    });
                } else {
                    no_updates.setVisibility(View.VISIBLE);
                    if (updateCheck.getContentDescription() != "yes" && updateFound.getVisibility() == View.VISIBLE) {
                        updateFound.setVisibility(View.GONE);
                    }
                }
            }
        } catch ( InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

public void updateDisplayVersion() {
        final TextView dot_version = findViewById(R.id.dotOS_version);
        final TextView up_to_date = findViewById(R.id.up_to_date);
        final Button v_changelog = findViewById(R.id.v_changelog);
        final FloatingActionButton check_updates = findViewById(R.id.check_updates);
        new Thread(new Runnable() {
            public void run() {
                final List<String> txt = getTextFromWeb();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String device = SystemProperties.get("ro.dotOS.device");
                        String unofficial = SystemProperties.get("ro.dot.releasetype");
                        String txtR = device + " " + "userdebug";
                        if (isNetworkAvailable()) {
                            if (Objects.equals(unofficial, "UNOFFICIAL") && txt.toString().contains(txtR)) {
                                isOff = true;
                            }
                        }
                    }
                });
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (SystemProperties.get("ro.dotOS.device").isEmpty()) {
                            dot_version.setText("dotOS : " + SystemProperties.get("ro.modversion") + "- NOT DOTOS ROM");
                            up_to_date.setText("NOT DOTOS ROM");
                            v_changelog.setVisibility(View.GONE);
                            check_updates.setVisibility(View.GONE);
                        }
                        if (isNetworkAvailable()) {
	                        if (isUNOFFICIAL()) {
	                            v_changelog.setVisibility(View.VISIBLE);
	                            check_updates.setVisibility(View.VISIBLE);
	                            up_to_date.setText("Your System is up to date");
	                            dot_version.setText("dotOS : " + SystemProperties.get("ro.modversion") + " - " + SystemProperties.get("ro.dotOS.device"));
	                            getUpdateState();
	                        } else {
	                            dot_version.setText("dotOS : " + SystemProperties.get("ro.modversion"));
	                            up_to_date.setText("OFFICIAL Build/OTA Updates are not supported");
	                            v_changelog.setVisibility(View.GONE);
	                            check_updates.setVisibility(View.GONE);
	                        }
                        } else {
                            v_changelog.setVisibility(View.GONE);
                            check_updates.setVisibility(View.GONE);
                            up_to_date.setText("No internet connection!");
                            dot_version.setText("dotOS : " + SystemProperties.get("ro.modversion") + " - " + SystemProperties.get("ro.dotOS.device"));
                        }
                    }
                });
            }
        }).start();
    }

	private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
	
    public boolean isUNOFFICIAL() {
        return isOff;
    }

    public void readChangelogFromSystem() {
        InputStreamReader inputReader = null;
        StringBuilder data = new StringBuilder();
        char tmp[] = new char[2048];
        int numRead;

        try {
            inputReader = new FileReader("/system/etc/Changelog.txt");
            while ((numRead = inputReader.read(tmp)) >= 0) {
                data.append(tmp, 0, numRead);
            }
            changelog = data.toString();
        } catch (IOException e) {
            changelog = "Changelog not found";
        } finally {
            try {
                if (inputReader != null) {
                    inputReader.close();
                }
            } catch (IOException ignored) {
            }
        }
    }
    public static String getFileSize(long size) {
        if (size <= 0)
            return "0";
        final String[] units = new String[] { "B", "KB", "MB", "GB"};
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        return new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }
	
    public List<String> getTextFromWeb() {
        URLConnection feedUrl;
        List<String> placeAddress = new ArrayList<>();
        try {
            feedUrl = new URL("https://ota.sbwml.net/DotOS/dot.devices").openConnection();
            InputStream is = feedUrl.getInputStream();

            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            String line = null;
            while ((line = reader.readLine()) != null) {
                placeAddress.add(line);
            }
            is.close();
            return placeAddress;
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
