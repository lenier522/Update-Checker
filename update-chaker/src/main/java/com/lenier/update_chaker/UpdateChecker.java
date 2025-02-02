package com.lenier.update_chaker;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.FileProvider;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Clase para gestionar la verificación y actualización de la aplicación.
 */
public class UpdateChecker {

    /**
     * Método que inicia la verificación de actualizaciones.
     *
     * @param context           Contexto de la aplicación.
     * @param currentVersionCode Código de versión actual de la app.
     * @param jsonUrl           URL del JSON con la información de la actualización.
     * @param useNotification   true para usar notificación, false para un AlertDialog.
     */
    public static void checkForUpdate(Context context, int currentVersionCode, String jsonUrl, boolean useNotification) {
        new CheckUpdateTask(context, currentVersionCode, jsonUrl, useNotification).execute();
    }

    /**
     * Clase AsyncTask para verificar la actualización en segundo plano.
     */
    private static class CheckUpdateTask extends AsyncTask<Void, Void, JsonObject> {
        private final Context context;
        private final int currentVersionCode;
        private final String jsonUrl;
        private final boolean useNotification;

        public CheckUpdateTask(Context context, int currentVersionCode, String jsonUrl, boolean useNotification) {
            this.context = context;
            this.currentVersionCode = currentVersionCode;
            this.jsonUrl = jsonUrl;
            this.useNotification = useNotification;
        }

        @Override
        protected JsonObject doInBackground(Void... voids) {
            try {
                URL url = new URL(jsonUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();
                return JsonParser.parseString(response.toString()).getAsJsonObject();
            } catch (Exception e) {
                Log.e("UpdateChecker", "Error verificando actualización", e);
                return null;
            }
        }

        @Override
        protected void onPostExecute(JsonObject jsonObject) {
            if (jsonObject != null) {
                try {
                    int latestVersionCode = jsonObject.get("latest_version_code").getAsInt();
                    final String apkUrl = jsonObject.get("apk_url").getAsString();
                    String changelog = jsonObject.get("changelog").getAsString();

                    if (latestVersionCode > currentVersionCode) {
                        if (useNotification) {
                            showUpdateNotification(context, apkUrl, changelog);
                        } else {
                            showUpdateDialog(context, apkUrl, changelog);
                        }
                    }
                } catch (Exception e) {
                    Log.e("UpdateChecker", "Error analizando JSON", e);
                }
            }
        }
    }

    /**
     * Muestra un AlertDialog con información sobre la nueva versión.
     */
    private static void showUpdateDialog(Context context, String apkUrl, String changelog) {
        new AlertDialog.Builder(context)
                .setTitle("Nueva actualización disponible")
                .setMessage("Novedades:\n" + changelog)
                .setPositiveButton("Descargar", (dialog, which) -> downloadAndInstallApk(context, apkUrl))
                .setNegativeButton("Cancelar", null)
                .show();
    }

    /**
     * Muestra una notificación sobre la nueva actualización.
     */
    private static void showUpdateNotification(Context context, String apkUrl, String changelog) {
        String channelId = "update_channel";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, "Actualizaciones", NotificationManager.IMPORTANCE_HIGH);
            NotificationManager manager = context.getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }

        // ✅ Comprobación de permisos en Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            return; // ⛔ No se puede mostrar la notificación sin permiso
        }

        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(apkUrl));
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(android.R.drawable.stat_sys_download_done)
                .setContentTitle("Nueva actualización disponible")
                .setContentText("Toca para descargar la última versión.")
                .setStyle(new NotificationCompat.BigTextStyle().bigText(changelog))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(1001, builder.build());
    }

    /**
     * Descarga el APK y lo instala automáticamente.
     */
    private static void downloadAndInstallApk(Context context, String apkUrl) {
        try {
            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(apkUrl));
            request.setTitle("Descargando actualización");
            request.setDescription("Espere mientras se descarga la actualización...");
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "update.apk");
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

            DownloadManager manager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
            long downloadId = manager.enqueue(request);

            new Thread(() -> {
                boolean downloading = true;
                while (downloading) {
                    DownloadManager.Query query = new DownloadManager.Query();
                    query.setFilterById(downloadId);
                    Cursor cursor = manager.query(query);
                    if (cursor.moveToFirst()) {
                        int status = cursor.getInt(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_STATUS));
                        if (status == DownloadManager.STATUS_SUCCESSFUL) {
                            downloading = false;
                            String filePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/update.apk";
                            installApk(context, filePath);
                        }
                    }
                    cursor.close();
                }
            }).start();
        } catch (Exception e) {
            Log.e("UpdateChecker", "Error descargando el APK", e);
        }
    }

    /**
     * Instala el APK descargado.
     */
    private static void installApk(Context context, String filePath) {
        File apkFile = new File(filePath);
        Uri apkUri;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            apkUri = FileProvider.getUriForFile(context, context.getPackageName() + ".provider", apkFile);
        } else {
            apkUri = Uri.fromFile(apkFile);
        }

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_GRANT_READ_URI_PERMISSION);

        try {
            context.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Log.e("UpdateChecker", "No se pudo abrir el instalador", e);
        }
    }
}


