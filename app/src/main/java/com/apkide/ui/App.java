package com.apkide.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.preference.PreferenceManager;

import com.apkide.common.Application;
import com.apkide.ui.browsers.file.FileBrowserService;
import com.apkide.ui.browsers.project.ProjectBrowserService;
import com.apkide.ui.services.ApkService;
import com.apkide.ui.services.CodeService;
import com.apkide.ui.services.build.BuildService;
import com.apkide.ui.services.decode.DecodeService;
import com.apkide.ui.services.error.ErrorService;
import com.apkide.ui.services.file.FileService;
import com.apkide.ui.services.project.ProjectService;

import java.util.ArrayList;
import java.util.List;

public final class App {
    
    private static final List<StyledUI> sActivities = new ArrayList<>();
    private static App sApp;
    private static MainUI sMainUI;
    
    private final CodeService myCodeService = new CodeService();
    private final ApkService myApkService=new ApkService();
    private final FileBrowserService myFileBrowserService = new FileBrowserService();
    private final ProjectBrowserService myProjectBrowserService=new ProjectBrowserService();
    private final FileService myFileService = new FileService();
    private final ErrorService myErrorService =new ErrorService();
    private final ProjectService myProjectService = new ProjectService();
    private final BuildService myBuildService=new BuildService();
    private final DecodeService myDecodeService=new DecodeService();
    
    private App() {
    }
    
    
    public static CodeService getCodeService() {
        return sApp.myCodeService;
    }
    
    public static ApkService getAPkService(){
        return sApp.myApkService;
    }
    
    public static FileBrowserService getFileBrowserService() {
        return sApp.myFileBrowserService;
    }
    
    public static ProjectBrowserService getProjectBrowserService(){
        return sApp.myProjectBrowserService;
    }
    
    public static FileService getFileService() {
        return sApp.myFileService;
    }
    
    public static ErrorService getErrorService(){
        return sApp.myErrorService;
    }
    
    public static ProjectService getProjectService() {
        return sApp.myProjectService;
    }
    
    public static BuildService getBuildService(){
        return sApp.myBuildService;
    }
    
    public static DecodeService getDecodeService(){
        return sApp.myDecodeService;
    }
    
    public static void init(@NonNull MainUI mainUI) {
        sApp = new App();
        sMainUI = mainUI;
        sApp.myCodeService.initialize();
        sApp.myApkService.initialize();
        sApp.myFileService.initialize();
        sApp.myProjectService.initialize();
        sApp.myFileBrowserService.initialize();
        sApp.myErrorService.initialize();
        sApp.myProjectBrowserService.initialize();;
        sApp.myBuildService.initialize();
        sApp.myDecodeService.initialize();
    }
    
    
    public static void shutdown() {
        if (sApp != null) {
            
            sApp.myDecodeService.shutdown();
            sApp.myBuildService.shutdown();
            sApp.myProjectBrowserService.shutdown();
            sApp.myFileBrowserService.shutdown();
            sApp.myProjectService.shutdown();
            sApp.myErrorService.shutdown();
            sApp.myFileService.shutdown();
            sApp.myApkService.shutdown();
            sApp.myCodeService.shutdown();
            sActivities.clear();
            sMainUI = null;
            sApp = null;
        }
    }
    
    public static boolean isShutdown() {
        return sApp == null;
    }
    
    public static boolean postRun(@NonNull Runnable runnable) {
        return Application.get().postExec(runnable);
    }
    
    public static boolean postRun(@NonNull Runnable runnable, long delayMillis) {
        return Application.get().postExec(runnable, delayMillis);
    }
    
    public static MainUI getMainUI() {
        return sMainUI;
    }
    
    @NonNull
    public static StyledUI getUI() {
        return sActivities.isEmpty() ? sMainUI : sActivities.get(sActivities.size() - 1);
    }
    
    public static void gotoUI(@NonNull String className) {
        gotoUI(App.getContext().getPackageName(), className);
    }
    
    public static void gotoUI(@NonNull String packageName, @NonNull String className) {
        Intent intent = new Intent();
        intent.setClassName(packageName, className);
        App.getUI().startActivity(intent);
    }
    
    public static void startUI(@NonNull StyledUI ui) {
        int index = sActivities.indexOf(ui);
        if (index != -1)
            sActivities.remove(index);
        
        sActivities.add(ui);
    }
    
    public static void stopUI(@NonNull StyledUI ui) {
        sActivities.remove(ui);
    }
    
    public static void runOnBackground(@NonNull Runnable backgroundRun) {
        Application.get().syncExec(backgroundRun);
    }
    
    public static void runOnBackground(@NonNull Runnable backgroundRun, @Nullable Runnable uiRun) {
        Application.get().syncExec(backgroundRun, uiRun);
    }
    
    @NonNull
    public static Context getContext() {
        if (sMainUI != null) {
            return sMainUI;
        }
        if (sActivities.isEmpty()) {
            return Application.get().getContext();
        }
        return sActivities.get(sActivities.size() - 1);
    }
    
    
    @NonNull
    public static String getString(@StringRes int id) {
        return getContext().getString(id);
    }
    
    @NonNull
    public static SharedPreferences getPreferences(@NonNull String name) {
        return getContext().getSharedPreferences(name, Context.MODE_PRIVATE);
    }
    
    @NonNull
    public static SharedPreferences getPreferences() {
        return PreferenceManager.getDefaultSharedPreferences(getContext());
    }
}
