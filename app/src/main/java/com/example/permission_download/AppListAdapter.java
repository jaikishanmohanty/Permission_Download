package com.example.permission_download;

import android.content.Context;
import android.content.pm.PackageManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


public class AppListAdapter extends ArrayAdapter<AppInfo> {
    private Context context;
    private List<AppInfo> appList;

    public AppListAdapter(Context context, List<AppInfo> appList) {
        super(context, R.layout.list_item, appList);
        this.context = context;
        this.appList = appList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.list_item, null);
        }

        AppInfo appInfo = appList.get(position);

        TextView nameTextView = convertView.findViewById(R.id.app_name);
        nameTextView.setText(appInfo.getName());

        TextView packageNameTextView = convertView.findViewById(R.id.package_name);
        packageNameTextView.setText(appInfo.getPackageName());

        TextView permissionTextView = convertView.findViewById(R.id.permissions);
        permissionTextView.setText(TextUtils.join(", ", appInfo.getPermissions()));
        return convertView;
    }
}