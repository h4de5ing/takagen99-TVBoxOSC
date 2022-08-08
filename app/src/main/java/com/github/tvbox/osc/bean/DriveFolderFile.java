package com.github.tvbox.osc.bean;

import android.util.Base64;

import com.github.tvbox.osc.cache.StorageDrive;
import com.github.tvbox.osc.ui.adapter.DriveAdapter;
import com.github.tvbox.osc.util.StorageDriveType;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.thegrizzlylabs.sardineandroid.Sardine;
import com.thegrizzlylabs.sardineandroid.impl.OkHttpSardine;

import org.jsoup.Connection;

import java.io.UnsupportedEncodingException;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.smb.SmbFile;

public class DriveFolderFile {
    public DriveFolderFile parentFolder;
    public String name;
    public boolean isFile;
    public String fileType;
    private StorageDrive driveData;
    public Long lastModifiedDate;
    public boolean isSelected;
    public boolean isDelMode;
    private String[] accessingPath;
    private List<DriveFolderFile> children;
    private Sardine webDAV;
    private JsonObject config;

    public DriveFolderFile(StorageDrive driveData) {
        this.driveData = driveData;
        this.name = driveData.name;
        if(driveData.configJson != null && driveData.configJson.length() > 0)
            this.config = JsonParser.parseString(driveData.configJson).getAsJsonObject();
    }

    public DriveFolderFile(DriveFolderFile parent, String name, boolean isFile, String fileType, Long lastModifiedDate) {
        accessingPath = new String[0];
        if(parent != null) {
            LinkedList<String> path = new LinkedList<>();
            DriveFolderFile currentParent = parent;
            while(currentParent != null) {
                path.add(0, currentParent.name);
                currentParent = currentParent.parentFolder;
            }
            accessingPath = path.toArray(accessingPath);
        }
        this.parentFolder = parent;
        this.name = name;
        this.isFile = isFile;
        if(fileType != null)
            this.fileType = fileType.toUpperCase(Locale.ROOT);
        this.lastModifiedDate = lastModifiedDate;
    }

    public String[] getAccessingPath() {
        return Arrays.copyOf(accessingPath, accessingPath.length);
    }

    public String getAccessingPathStr() {
        String path = "";
        for (String pathItem : accessingPath) {
            path += (pathItem + "/");
        }
        return path;
    }

    public boolean isDrive() {
        return driveData != null;
    }

    public StorageDriveType.TYPE getDriveType() {
        return StorageDriveType.TYPE.values()[driveData.type];
    }

    public StorageDrive getDriveData() {
        return driveData;
    }

    public List<DriveFolderFile> getChildren() {
        return children;
    }

    public void setChildren(List<DriveFolderFile> children) {
        this.children = children;
    }

    public String getFormattedLastModified() {
        if(this.lastModifiedDate != null) {
            Date date = new Date(this.lastModifiedDate);
            Format fmt = new SimpleDateFormat("MM/dd/yyyy hh:mm aa");
            return fmt.format(date);
        }
        return "";
    }

    private boolean initWebDav() {
        if(webDAV != null)
            return true;
        try {
            if (getDriveType() == StorageDriveType.TYPE.WEBDAV) {
                JsonObject config = JsonParser.parseString(driveData.configJson).getAsJsonObject();
                webDAV = new OkHttpSardine();
                if(config.has("username") && config.has("password")) {
                    webDAV.setCredentials(config.get("username").getAsString(), config.get("password").getAsString());
                }
                return true;
            }
        } catch (Exception ex) {}
        return false;
    }

    public Sardine getWebDAV() {
        if(initWebDav()) {
            return webDAV;
        }
        return null;
    }

//    public SmbFile getSMB(String path) {
//        NtlmPasswordAuthentication auth;
//        try {
//            if (getDriveType() == StorageDriveType.TYPE.WEBDAV) {
//                JsonObject config = JsonParser.parseString(driveData.configJson).getAsJsonObject();
//                webDAV = new OkHttpSardine();
//                if(config.has("username") && config.has("password")) {
//                    webDAV.setCredentials(config.get("username").getAsString(), config.get("password").getAsString());
//                }
//                return true;
//            }
//        } catch (Exception ex) {}
//
//        if(config.has("username") && config.has("password")) {
//            auth = new NtlmPasswordAuthentication(config.get("username").getAsString(), config.get("password").getAsString());
//        }
//    }

    public String getWebDAVBase64Credential() {
        try {
            if (config.has("username") && config.has("password")) {
                byte[] data = (config.get("username").getAsString() + ":" + config.get("password").getAsString()).getBytes("UTF-8");
                return Base64.encodeToString(data, Base64.NO_WRAP);
            }
        }catch (Exception ex) {}
        return null;
    }

    public JsonObject getConfig() {
        return config;
    }
}
