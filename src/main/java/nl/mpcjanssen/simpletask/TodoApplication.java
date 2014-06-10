/**
 * This file is part of Todo.txt Touch, an Android app for managing your todo.txt file (http://todotxt.com).
 *
 * Copyright (c) 2009-2012 Todo.txt contributors (http://todotxt.com)
 *
 * LICENSE:
 *
 * Todo.txt Touch is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 2 of the License, or (at your option) any
 * later version.
 *
 * Todo.txt Touch is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with Todo.txt Touch.  If not, see
 * <http://www.gnu.org/licenses/>.
 *
 * @author Todo.txt contributors <todotxt@yahoogroups.com>
 * @license http://www.gnu.org/licenses/gpl.html
 * @copyright 2009-2012 Todo.txt contributors (http://todotxt.com)
 */
package nl.mpcjanssen.simpletask;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.app.Dialog;
import android.appwidget.AppWidgetManager;
import android.content.*;
import android.content.SharedPreferences.Editor;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.FileObserver;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Window;
import android.widget.EditText;
import android.support.v4.content.LocalBroadcastManager;

import java.io.File;

import nl.mpcjanssen.simpletask.remote.FileStoreInterface;
import nl.mpcjanssen.simpletask.task.TaskBag;
import nl.mpcjanssen.simpletask.util.Util;


public class TodoApplication extends Application implements SharedPreferences.OnSharedPreferenceChangeListener {
    private final static String TAG = TodoApplication.class.getSimpleName();
    private static Context m_appContext;
    private static SharedPreferences m_prefs;
    private TaskBag taskBag;
    private LocalBroadcastManager localBroadcastManager;
    private String mTodoFile;
    private FileStore mFileStore;

    public static Context getAppContext() {
        return m_appContext;
    }

    public static SharedPreferences getPrefs() {
        return m_prefs;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        TodoApplication.m_appContext = getApplicationContext();
        TodoApplication.m_prefs = PreferenceManager.getDefaultSharedPreferences(getAppContext());
        localBroadcastManager = LocalBroadcastManager.getInstance(this);
        mTodoFile = getTodoFileName();
        initStorage(mTodoFile);

    }

    public void initStorage(String todoFile) {
        TaskBag.Preferences taskBagPreferences = new TaskBag.Preferences(
                m_prefs);
        if (mFileStore!=null) {
            mFileStore.stopWatching();
        }
        mFileStore = new FileStore(todoFile);
        this.taskBag = new TaskBag(taskBagPreferences, mFileStore);
        mFileStore.startWatching(localBroadcastManager, new Intent(Constants.BROADCAST_UPDATE_UI));
        taskBag.reload();
        updateUI();
    }

    public void startWatching() {
        if (mFileStore!=null) {
            mFileStore.startWatching(localBroadcastManager,new Intent(Constants.BROADCAST_UPDATE_UI));
        }
    }

    public void stopWatching() {
        if (mFileStore!=null) {
            mFileStore.stopWatching();
        }
    }

    @Override
    public void onTerminate() {
        Log.v(TAG, "Deregistered receiver");
        m_prefs.unregisterOnSharedPreferenceChangeListener(this);
        super.onTerminate();
    }


    public String[] getDefaultSorts () {
        return getResources().getStringArray(R.array.sortKeys);
    }


    public TaskBag getTaskBag() {
        return taskBag;
    }

    public boolean isManualMode() {
        return m_prefs.getBoolean(getString(R.string.manual_sync_pref_key), false);
    }

    public boolean hasSyncOnResume() {
        return m_prefs.getBoolean(getString(R.string.resume_sync_pref_key), false);
    }

    public boolean showCompleteCheckbox() {
        return m_prefs.getBoolean(getString(R.string.ui_complete_checkbox), true);
    }

    public boolean showHidden() {
        return m_prefs.getBoolean(getString(R.string.show_hidden), false);
    }

    public boolean showEmptyLists() {
        return m_prefs.getBoolean(getString(R.string.show_empty_lists), true);
    }

    public String getTodoFileName() {
        return m_prefs.getString(getString(R.string.todo_file_key), "");
    }

    public void setTodoFile(String todo) {
            m_prefs.edit().putString(getString(R.string.todo_file_key), todo).commit();
    }

    public boolean isAutoArchive() {
        return m_prefs.getBoolean(getString(R.string.auto_archive_pref_key), false);
    }

    public boolean isBackSaving() {
        return m_prefs.getBoolean(getString(R.string.back_key_saves_key), false);
    }

    public boolean hasShareTaskShowsEdit() {
        return m_prefs.getBoolean(getString(R.string.share_task_show_edit), false);
    }

    public boolean hasCapitalizeTasks() {
        return m_prefs.getBoolean(getString(R.string.capitalize_tasks), false);
    }

    public boolean hasColorDueDates() {
        return m_prefs.getBoolean(getString(R.string.color_due_date_key), true);
    }

    public boolean hasRecurOriginalDates() {
        return m_prefs.getBoolean(getString(R.string.recur_from_original_date), true);
    }

    public boolean hasLandscapeDrawers() {
        return (m_prefs.getBoolean(getString(R.string.ui_drawer_fixed_landscape), false) &&
                getResources().getBoolean(R.bool.is_landscape));
    }

    public void setEditTextHint(EditText editText, int resid ) {
        if (m_prefs.getBoolean(getString(R.string.ui_show_edittext_hints), true)) {
            editText.setHint(resid);
        }
    }

    public boolean isAddTagsCloneTags() {
        return m_prefs.getBoolean(getString(R.string.clone_tags_key),false);
    }

    public void setAddTagsCloneTags(boolean bool) {
        m_prefs.edit()
                .putBoolean(getString(R.string.clone_tags_key),bool)
                .commit();
    }

    public boolean isWordWrap() {
        return m_prefs.getBoolean(getString(R.string.word_wrap_key),true);
    }

    public boolean hasDonated() {
        try {
            getPackageManager().getInstallerPackageName("nl.mpcjanssen.simpletask.donate");
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public void setWordWrap(boolean bool) {
        m_prefs.edit()
                .putBoolean(getString(R.string.word_wrap_key),bool)
                .commit();
    }

    public void showToast(int resid) {
        Util.showToastLong(this, resid);
    }

    public void showToast(String string) {
        Util.showToastLong(this, string);
    }



    /**
     * Update user interface
     *
     * Update the elements of the user interface. The listview with tasks will be updated
     * if it is visible (by broadcasting an intent). All widgets will be updated as well.
     * This method should be called whenever the TaskBag changes.
     */
    private void updateUI() {
        localBroadcastManager.sendBroadcast(new Intent(Constants.BROADCAST_UPDATE_UI));
        updateWidgets();
    }

    public void updateWidgets() {
        AppWidgetManager mgr = AppWidgetManager.getInstance(getApplicationContext());
        for (int appWidgetId : mgr.getAppWidgetIds(new ComponentName(getApplicationContext(), MyAppWidgetProvider.class))) {
            mgr.notifyAppWidgetViewDataChanged(appWidgetId, R.id.widgetlv);
            Log.v(TAG, "Updating widget: " + appWidgetId);
        }
    }

    private void redrawWidgets(){
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(getApplicationContext());
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this, MyAppWidgetProvider.class));
        if (appWidgetIds.length > 0) {
            new MyAppWidgetProvider().onUpdate(this, appWidgetManager, appWidgetIds);
        }
    }

    public int getActiveTheme() {
        String theme =  getPrefs().getString(getString(R.string.theme_pref_key), "");
        if (theme.equals("android.R.style.Theme_Holo")) {
            return android.R.style.Theme_Holo;
        } else if (theme.equals("android.R.style.Theme_Holo_Light_DarkActionBar")) {
            return android.R.style.Theme_Holo_Light_DarkActionBar;
        } else if (theme.equals("android.R.style.Theme_Holo_Light")) {
            return android.R.style.Theme_Holo_Light;
        } else  {
            return android.R.style.Theme_Holo_Light_DarkActionBar;
        }
    }

    public void setActionBarStyle(Window window) {
        if (getPrefs().getBoolean(getString(R.string.split_actionbar_key), true)) {
            window.setUiOptions(ActivityInfo.UIOPTION_SPLIT_ACTION_BAR_WHEN_NARROW);
        }
    }

    public boolean isDarkTheme() {
        switch (getActiveTheme()) {
            case android.R.style.Theme_Holo:
                return true;
            case android.R.style.Theme_Holo_Light_DarkActionBar:
            case android.R.style.Theme_Holo_Light:
                return false;
            default:
                return false;
        }
    }

    public boolean isDarkActionbar() {
        switch (getActiveTheme()) {
            case android.R.style.Theme_Holo:
            case android.R.style.Theme_Holo_Light_DarkActionBar:
                return true;
            case android.R.style.Theme_Holo_Light:
                return false;
            default:
                return false;
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        if (s.equals(getString(R.string.widget_theme_pref_key)) ||
                s.equals(getString(R.string.widget_extended_pref_key)) ||
                s.equals(getString(R.string.widget_background_transparency)) ||
                s.equals(getString(R.string.widget_header_transparency))) {
            redrawWidgets();
        }
    }

    public int getActiveFont() {
        String fontsize =  getPrefs().getString("fontsize", "medium");
        if (fontsize.equals("small")) {
            return R.style.FontSizeSmall;
        } else if (fontsize.equals("large")) {
            return R.style.FontSizeLarge;
        } else {
            return R.style.FontSizeMedium;
        }
    }

    public FileStoreInterface getFileStore() {
        return mFileStore;
    }

    public void setNeedToPush(boolean b) {

    }

    private final class BroadcastReceiverExtension extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

        }
    }

    public void showConfirmationDialog(Context cxt, int msgid,
                                              DialogInterface.OnClickListener oklistener, int titleid) {
        boolean show = getPrefs().getBoolean(getString(R.string.ui_show_confirmation_dialogs), true);

        AlertDialog.Builder builder = new AlertDialog.Builder(cxt);
        builder.setTitle(titleid);
        builder.setMessage(msgid);
        builder.setPositiveButton(android.R.string.ok, oklistener);
        builder.setNegativeButton(android.R.string.cancel, null);
        builder.setCancelable(true);
        Dialog dialog = builder.create();
        if (show) {
           dialog.show();
        } else {
            oklistener.onClick(dialog , DialogInterface.BUTTON_POSITIVE);
        }
    }
}
