package com.example.permission_download;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Locale;

import jxl.Sheet;
import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;

public class ExcelExporter {

    private static final String TAG = "ExcelExporter";
    private static final String[] HEADERS = {"App Name", "Package Name", "Category", "Permissions"};

    public void exportToExcel(Context context, List<AppInfo> appList, String fileName) {
        try {
            File file = new File(context.getExternalFilesDir(null), fileName);
            if (!file.exists()) {
                file.createNewFile();
            }

            WritableWorkbook workbook = Workbook.createWorkbook(file);
            Sheet sheet = workbook.createSheet("Permissions", 0);

            // Add headers to sheet
            for (int i = 0; i < HEADERS.length; i++) {
                ((WritableSheet) sheet).addCell(new jxl.write.Label(i, 0, HEADERS[i]));
            }

            // Add data to sheet
            int row = 1;
            for (AppInfo appInfo : appList) {
                ((WritableSheet) sheet).addCell(new jxl.write.Label(0, row, appInfo.getName()));
                ((WritableSheet) sheet).addCell(new jxl.write.Label(1, row, appInfo.getPackageName()));
                ((WritableSheet) sheet).addCell(new jxl.write.Label(2, row, appInfo.getCategory()));

                List<String> permissions = appInfo.getPermissions();
                StringBuilder permissionString = new StringBuilder();
                for (String permission : permissions) {
                    permissionString.append(permission).append("\n");
                }
                ((WritableSheet) sheet).addCell(new jxl.write.Label(3, row, permissionString.toString()));

                row++;
            }

            workbook.write();
            workbook.close();

            Toast.makeText(context, "Permissions and category exported to Excel file", Toast.LENGTH_SHORT).show();

        } catch (IOException | WriteException e) {
            Toast.makeText(context, "Error exporting app permissions and category", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

    }

}
/*  Working code for Excel Export without category
public class ExcelExporter {

    private static final String TAG = "ExcelExporter";
    private static final String FILE_NAME = "app_permissions.xls";
    private static final String[] HEADERS = {"App Name", "Package Name", "Permissions"};



    public void exportToExcel(Context context, List<AppInfo> appList, String fileName) {
        try {
            File file = new File(context.getExternalFilesDir(null), fileName);
            if (!file.exists()) {
                file.createNewFile();
            }

            WritableWorkbook workbook = Workbook.createWorkbook(file);
            Sheet sheet = workbook.createSheet("Permissions", 0);

            // Add headers to sheet
            ((WritableSheet) sheet).addCell(new jxl.write.Label(0, 0, "App Name"));
            ((WritableSheet) sheet).addCell(new jxl.write.Label(1, 0, "Package Name"));
            ((WritableSheet) sheet).addCell(new jxl.write.Label(2, 0, "Permissions"));

            // Add data to sheet
            int row = 1;
            for (AppInfo appInfo : appList) {
                ((WritableSheet) sheet).addCell(new jxl.write.Label(0, row, appInfo.getName()));
                ((WritableSheet) sheet).addCell(new jxl.write.Label(1, row, appInfo.getPackageName()));

                List<String> permissions = appInfo.getPermissions();
                StringBuilder permissionString = new StringBuilder();
                for (String permission : permissions) {
                    permissionString.append(permission).append("\n");
                }
                ((WritableSheet) sheet).addCell(new jxl.write.Label(2, row, permissionString.toString()));

                row++;
            }

            workbook.write();
            workbook.close();

            Toast.makeText(context, "Permissions exported to Excel file", Toast.LENGTH_SHORT).show();

        } catch (IOException | WriteException e) {
            Toast.makeText(context, "Error exporting app permissions", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

    }

}

*/
