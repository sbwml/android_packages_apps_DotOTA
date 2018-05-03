/*
 * Copyright (C) 2015 Matt Booth (Kryten2k35).
 * Copyright (C) 2017 The halogenOS Project.
 *
 * Licensed under the Attribution-NonCommercial-ShareAlike 4.0 International
 * (the "License") you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://creativecommons.org/licenses/by-nc-sa/4.0/legalcode
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.dot.systemupdates.utils;

import android.os.Environment;

public interface Constants {
    // Developer
    boolean DEBUGGING                       = false;

    // Settings
    String CURRENT_THEME                    = "current_theme";
    String LAST_CHECKED                     = "updater_last_update_check";
    String IS_DOWNLOAD_FINISHED             = "is_download_finished";
    String WIPE_CACHE                       = "wipe cache";
    String WIPE_DALVIK                      = "wipe dalvik";

    // Storage
    String SD_CARD                          = "sdcard";
    String OTA_DOWNLOAD_DIR                 = "";

    // Broadcast intents
    String MANIFEST_LOADED                  = "com.ota.update.MANIFEST_LOADED";
    String MANIFEST_CHECK_BACKGROUND        = "com.ota.update.MANIFEST_CHECK_BACKGROUND";
    String START_UPDATE_CHECK               = "com.ota.update.START_UPDATE_CHECK";
    String IGNORE_RELEASE                   = "com.ota.update.IGNORE_RELEASE";

    //Notification
    int NOTIFICATION_ID                         = 101;
}
