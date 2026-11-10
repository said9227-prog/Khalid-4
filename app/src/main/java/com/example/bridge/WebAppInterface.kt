package com.example.bridge

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.webkit.JavascriptInterface
import android.widget.Toast
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream

class WebAppInterface(
    private val context: Context,
    private val onProgress: (Int) -> Unit,
    private val onComplete: (String) -> Unit,
    private val onTimelineUpdate: (Float) -> Unit,
    private val onCameraState: (String, Int) -> Unit
) {

    @JavascriptInterface
    fun onExportProgress(jsonStr: String) {
        try {
            val json = JSONObject(jsonStr)
            val progress = json.optInt("progress", 0)
            onProgress(progress)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @JavascriptInterface
    fun onExportComplete(jsonStr: String) {
        try {
            val json = JSONObject(jsonStr)
            val base64Data = json.optString("data", "")
            val format = json.optString("format", "webm")

            val savedPath = saveVideoToMediaStore(base64Data, format)
            onComplete(savedPath)
        } catch (e: Exception) {
            e.printStackTrace()
            onComplete("")
        }
    }

    @JavascriptInterface
    fun onTimelineUpdate(jsonStr: String) {
        try {
            val json = JSONObject(jsonStr)
            val time = json.optDouble("currentTime", 0.0).toFloat()
            onTimelineUpdate(time)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    var onObjectSelectedListener: ((String, Float, Float, Float) -> Unit)? = null

    @JavascriptInterface
    fun onObjectSelected(jsonStr: String) {
        try {
            val json = JSONObject(jsonStr)
            val id = json.optString("id", "")
            val posX = json.optDouble("posX", 0.0).toFloat()
            val posY = json.optDouble("posY", 0.0).toFloat()
            val posZ = json.optDouble("posZ", 0.0).toFloat()
            onObjectSelectedListener?.invoke(id, posX, posY, posZ)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @JavascriptInterface
    fun onObjectDragged(jsonStr: String) {
        try {
            val json = JSONObject(jsonStr)
            val id = json.optString("id", "")
            val posX = json.optDouble("posX", 0.0).toFloat()
            val posY = json.optDouble("posY", 0.0).toFloat()
            val posZ = json.optDouble("posZ", 0.0).toFloat()
            onObjectSelectedListener?.invoke(id, posX, posY, posZ)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    var onSurfacePaintUpdateListener: ((Int) -> Unit)? = null

    @JavascriptInterface
    fun onSurfacePaintUpdate(jsonStr: String) {
        try {
            val json = JSONObject(jsonStr)
            val count = json.optInt("totalCount", 0)
            onSurfacePaintUpdateListener?.invoke(count)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    fun onCameraRecordingState(jsonStr: String) {
        try {
            val json = JSONObject(jsonStr)
            val status = json.optString("status", "")
            val count = json.optInt("count", 0)
            onCameraState(status, count)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun saveVideoToMediaStore(base64Data: String, format: String): String {
        return try {
            val cleanBase64 = if (base64Data.contains(",")) {
                base64Data.split(",")[1]
            } else {
                base64Data
            }
            val videoBytes = android.util.Base64.decode(cleanBase64, android.util.Base64.DEFAULT)

            val fileName = "Khalid3D_${System.currentTimeMillis()}.$format"

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val values = ContentValues().apply {
                    put(MediaStore.Video.Media.DISPLAY_NAME, fileName)
                    put(MediaStore.Video.Media.MIME_TYPE, if (format == "mp4") "video/mp4" else "video/webm")
                    put(MediaStore.Video.Media.RELATIVE_PATH, Environment.DIRECTORY_MOVIES + "/Khalid3DStudio")
                }

                val uri: Uri? = context.contentResolver.insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, values)
                uri?.let {
                    context.contentResolver.openOutputStream(it)?.use { os ->
                        os.write(videoBytes)
                    }
                    return uri.toString()
                }
            } else {
                val dir = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES), "Khalid3DStudio")
                if (!dir.exists()) dir.mkdirs()
                val file = File(dir, fileName)
                FileOutputStream(file).use { os ->
                    os.write(videoBytes)
                }
                return file.absolutePath
            }
            ""
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        }
    }
}
