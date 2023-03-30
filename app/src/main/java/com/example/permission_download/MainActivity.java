package com.example.permission_download;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PermissionInfo;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.io.FileOutputStream;
import java.io.IOException;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import com.example.permission_download.ExcelExporter;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;


public class MainActivity extends AppCompatActivity {
    private ListView appListView;
    private Context context;
    private ToggleButton systemAppsToggleButton;
    private AppListAdapter appListAdapter;

    private PackageManager packageManager;

    private boolean showSystemApps = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();
        context = this;

        appListView = findViewById(R.id.app_list_view);
        appListAdapter = new AppListAdapter(context, new ArrayList<>());
        appListView.setAdapter(appListAdapter);

        ToggleButton toggleButton = findViewById(R.id.system_apps_toggle_button);
        toggleButton.setOnCheckedChangeListener((buttonView, isChecked) -> {
            showSystemApps = isChecked;
            //      updateAppListAllApps();

        });


        updateAppListInstalledApps();

    }

    private void updateAppListInstalledApps() {
        List<AppInfo> appList = new ArrayList<>();

        PackageManager pm = context.getPackageManager();
        List<PackageInfo> packages = pm.getInstalledPackages(PackageManager.GET_PERMISSIONS);

        for (PackageInfo packageInfo : packages) {
            if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 1) {
                // Skip system apps
                continue;
            }
            String name = packageInfo.applicationInfo.loadLabel(pm).toString();
            String packageName = packageInfo.packageName;
            String category = getCategory(pm, packageName);
            System.out.println("Appname: " + name + " Category : " + category);
            List<String> permissions = new ArrayList<>();
            if (packageInfo.requestedPermissions != null) {
                for (String permission : packageInfo.requestedPermissions) {
                    if (pm.checkPermission(permission, packageName) == PackageManager.PERMISSION_GRANTED) {
                        permissions.add(permission);
                    }
                }
            }

            appList.add(new AppInfo(name, packageName, category, permissions));
        }

        appListAdapter.clear();
        appListAdapter.addAll(appList);
        appListAdapter.notifyDataSetChanged();

        // Export permissions and category to Excel file
        try {
            ExcelExporter exporter = new ExcelExporter();
            exporter.exportToExcel(context, appList, "installedAppsCategory.xls");
            Toast.makeText(context, "Permissions and category exported successfully", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "Error exporting app permissions and category", Toast.LENGTH_SHORT).show();
        }
    }

    private String getCategory(PackageManager pm, String packageName) {
        try {
            ApplicationInfo info = pm.getApplicationInfo(packageName, 0);
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                if (info.category != AppInfo.CATEGORY_UNDEFINED) {
//                    return String.valueOf(info.category);
//                }
//            }

            // Use Google Play Store API to fetch category
            String url = "https://play.google.com/store/apps/details?id=" + packageName;
            System.out.println("URL : "+url);
            String category = new GetCategoryTask().execute(url).get();

            return category;

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return "Unknown";
    }

    private class GetCategoryTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            try {
                Document doc = Jsoup.connect(urls[0]).get();
                String category = doc.select("[itemprop=genre]").first().text();
                System.out.println("Itemprop Category : "+category);
                return category;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
    }





/*
    private void updateAppListInstalledApps() {
        List<AppInfo> appList = new ArrayList<>();

        PackageManager pm = context.getPackageManager();
        List<PackageInfo> packages = pm.getInstalledPackages(PackageManager.GET_PERMISSIONS);

        for (PackageInfo packageInfo : packages) {
            if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 1) {
                // Skip system apps
                continue;
            }
            String name = packageInfo.applicationInfo.loadLabel(pm).toString();
            String packageName = packageInfo.packageName;

            List<String> permissions = new ArrayList<>();
            if (packageInfo.requestedPermissions != null) {
                for (String permission : packageInfo.requestedPermissions) {
                    if (pm.checkPermission(permission, packageName) == PackageManager.PERMISSION_GRANTED) {
                        permissions.add(permission);
                    }
                }
            }

            appList.add(new AppInfo(name, packageName, permissions));
        }

        appListAdapter.clear();
        appListAdapter.addAll(appList);
        appListAdapter.notifyDataSetChanged();

        // Export permissions to Excel file
        try {
            ExcelExporter exporter = new ExcelExporter();
            exporter.exportToExcel(context, appList, "installedApps.xls");
            Toast.makeText(context, "Permissions exported successfully", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "Error exporting app permissions", Toast.LENGTH_SHORT).show();
        }
    }

*/




    private void updateAppListAllApps() {
        List<AppInfo> appList = new ArrayList<>();

        PackageManager pm = context.getPackageManager();
        List<PackageInfo> packages = pm.getInstalledPackages(PackageManager.GET_PERMISSIONS);

        for (PackageInfo packageInfo : packages) {
            if (!showSystemApps && (packageInfo.applicationInfo.flags & android.content.pm.ApplicationInfo.FLAG_SYSTEM) == 1) {
                // Skip system apps
                continue;
            }

            String name = packageInfo.applicationInfo.loadLabel(pm).toString();
            String packageName = packageInfo.packageName;
            String category = getCategory(pm, packageName);

            List<String> permissions = new ArrayList<>();
            if (packageInfo.requestedPermissions != null) {
                for (String permission : packageInfo.requestedPermissions) {
                    if (pm.checkPermission(permission, packageName) == PackageManager.PERMISSION_GRANTED) {
                        permissions.add(permission);
                    }
                }
            }

            appList.add(new AppInfo(name, packageName, category, permissions));
        }

        appListAdapter.clear();
        appListAdapter.addAll(appList);
        appListAdapter.notifyDataSetChanged();

        // Export permissions to Excel file
        try {
            ExcelExporter exporter = new ExcelExporter();
            exporter.exportToExcel(context, appList, "allPermission.xls");
            Toast.makeText(context, "Permissions exported successfully", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "Error exporting app permissions", Toast.LENGTH_SHORT).show();
        }
    }

}



/*
    private void loadApps() {
        List<ApplicationInfo> apps = packageManager.getInstalledApplications(PackageManager.GET_META_DATA);
        ArrayList<AppInfo> appList = new ArrayList<>();

        for (ApplicationInfo app : apps) {
            if (showSystemApps || (app.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                try {
                    PackageInfo packageInfo = packageManager.getPackageInfo(app.packageName, PackageManager.GET_PERMISSIONS);
                    String installerPackageName = packageManager.getInstallerPackageName(app.packageName);
                    if (installerPackageName != null && installerPackageName.equals("com.android.vending")) {
                        List<String> permissions = new ArrayList<>();
                        if (packageInfo.requestedPermissions != null) {
                            for (String permission : packageInfo.requestedPermissions) {
                                permissions.add(permission);
                            }
                        }
                        appList.add(new AppInfo(app.loadLabel(packageManager).toString(), app.packageName, permissions));
                    }
                } catch (PackageManager.NameNotFoundException e) {
                    // Package not found, skip it
                }
            }
        }


        AppListAdapter adapter = new AppListAdapter(this, appList);
        appListView.setAdapter(adapter);
    }
*/
