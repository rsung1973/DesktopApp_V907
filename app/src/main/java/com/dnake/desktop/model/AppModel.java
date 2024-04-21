package com.dnake.desktop.model;

import android.content.Intent;
import android.graphics.drawable.Drawable;

public class AppModel {
    private int id;
    private String name;
    private String action;
    private Drawable icon;
    private Intent intent;

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    private String packageName;

    public AppModel() {
    }

    public AppModel(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public AppModel(int id, String name, Drawable icon, Intent intent, String packageName, String action) {
        this.id = id;
        this.name = name;
        this.icon = icon;
        this.intent = intent;
        this.packageName = packageName;
        this.action = action;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public Intent getIntent() {
        return intent;
    }

    public void setIntent(Intent intent) {
        this.intent = intent;
    }
}
