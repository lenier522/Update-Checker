<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
</head>
<body>

<h1>Update-Checker Android</h1>

<p><strong>Update-Checker</strong> es una librería ligera para Android que permite verificar y descargar actualizaciones de tu aplicación desde un servidor remoto. La librería utiliza un archivo JSON para obtener la información de la última versión disponible y gestiona la descarga e instalación del APK.</p>

<h2>Características</h2>
<ul>
    <li>Verifica si hay una nueva versión de la aplicación disponible.</li>
    <li>Descarga e instala automáticamente la actualización.</li>
    <li>Soporta notificaciones o diálogos de actualización.</li>
    <li>Fácil de integrar en cualquier proyecto Android.</li>
</ul>

<h2>Requisitos</h2>
<ul>
    <li>Android SDK 21 (Lollipop) o superior.</li>
    <li>Permisos de Internet y almacenamiento en el archivo <code>AndroidManifest.xml</code>.</li>
    <li>Un servidor que hospede un archivo JSON con la información de la actualización.</li>
</ul>

<h2>Configuración</h2>

<h3>1. Agrega JitPack a tu proyecto</h3>
<p>Para usar la librería, primero debes agregar JitPack como repositorio en tu archivo <code>build.gradle</code> a nivel de proyecto:</p>
<pre><code>allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
</code></pre>

<h3>2. Agrega la dependencia</h3>
<p>Luego, agrega la dependencia de la librería en el archivo <code>build.gradle</code> de tu módulo:</p>
<pre><code>dependencies {
    implementation 'com.github.lenier522:Update-Checker:1.1'
    implementation 'com.google.code.gson:gson:2.12.1'
}
</code></pre>

<h3>3. Configura los permisos en <code>AndroidManifest.xml</code></h3>
<pre><code>&lt;uses-permission android:name="android.permission.INTERNET" /&gt;
&lt;uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" /&gt;
&lt;uses-permission android:name="android.permission.REQUEST_INSTALL_PACKAGES" /&gt;
&lt;uses-permission android:name="android.permission.POST_NOTIFICATIONS" /&gt; &lt;!-- Solo para Android 13+ --&gt;
</code></pre>

<h3>4. Configura el <code>FileProvider</code> en <code>AndroidManifest.xml</code></h3>
<pre><code>&lt;provider
    android:name="androidx.core.content.FileProvider"
    android:authorities="${applicationId}.provider"
    android:exported="false"
    android:grantUriPermissions="true"&gt;
    &lt;meta-data
        android:name="android.support.FILE_PROVIDER_PATHS"
        android:resource="@xml/file_paths" /&gt;
&lt;/provider&gt;
</code></pre>

<h3>5. Crea el archivo <code>file_paths.xml</code> en <code>res/xml</code></h3>
<pre><code>&lt;?xml version="1.0" encoding="utf-8"?&gt;
&lt;paths xmlns:android="http://schemas.android.com/apk/res/android"&gt;
    &lt;external-path name="external_files" path="." /&gt;
&lt;/paths&gt;
</code></pre>

<h2>Uso</h2>

<h3>1. Sube el archivo JSON al servidor</h3>
<p>El archivo JSON debe tener el siguiente formato:</p>
<pre><code>{
    "latest_version_code": 2,
    "apk_url": "https://tudominio.com/app/miapp_v2.apk",
    "changelog": "Corrección de errores y mejoras en la estabilidad."
}
</code></pre>

<h3>2. Implementa la librería en tu <code>MainActivity</code></h3>
<pre><code>package com.example.myapp;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.lenier.update_checker.UpdateChecker;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

            PackageInfo pinfo = null;
        try {
            pinfo = getPackageManager().getPackageInfo(getPackageName(),0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        int currentVersion = pinfo.versionCode;
     //   String currentVersionCode = String.valueOf(currentVersion);

        String jsonUrl = "https://perf3ctsolutions.com/update.json"; // URL del JSON
        UpdateChecker.checkForUpdate(this,currentVersion,jsonUrl,true);

    /*    TextView txt = findViewById(R.id.textView);
        txt.setText(currentVersionCode);
        */
    }
}
</code></pre>

<h3>3. Personaliza el comportamiento</h3>
<ul>
    <li><strong>Usar notificaciones</strong>: Cambia <code>useNotification</code> a <code>true</code> para mostrar una notificación en lugar de un diálogo.</li>
    <li><strong>Manejar errores</strong>: La librería registra errores en el Logcat con la etiqueta <code>UpdateChecker</code>.</li>
</ul>

<h2>Ejemplo de JSON</h2>
<pre><code>{
    "latest_version_code": 3,
    "apk_url": "https://tudominio.com/app/miapp_v3.apk",
    "changelog": "Nuevas características y mejoras de rendimiento."
}
</code></pre>

<h2>Capturas de pantalla</h2>
<table>
    <tr>
        <th>Diálogo de actualización</th>
        <th>Notificación de actualización</th>
    </tr>
    <tr>
        <td><img src="https://perf3ctsolutions.com/cap1.jpg" alt="Diálogo de actualización"></td>
        <td><img src="https://perf3ctsolutions.com/cap2.jpg" alt="Notificación de actualización"></td>
    </tr>
</table>

<h2>Contribuciones</h2>
<p>Si deseas contribuir a este proyecto, ¡eres bienvenido! Abre un <em>issue</em> o envía un <em>pull request</em>.</p>

<h2>Licencia</h2>
<p>Este proyecto está bajo la licencia MIT. Consulta el archivo <a href="LICENSE">LICENSE</a> para más detalles.</p>

</body>
</html>
