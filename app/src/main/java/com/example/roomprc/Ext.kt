package com.example.roomprc

import android.Manifest
import android.app.*
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.text.format.DateFormat
import android.util.Base64
import android.util.Log
import android.view.View
import android.view.View.*
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.google.gson.Gson
import java.io.*
import java.text.DecimalFormat
import java.util.*
import java.util.concurrent.Executors
import kotlin.math.log10
import kotlin.math.pow


fun Bitmap.to64st(): String {
    val stream = ByteArrayOutputStream()
    compress(Bitmap.CompressFormat.PNG, 100, stream)
    return Base64.encodeToString(stream.toByteArray(), Base64.DEFAULT)
}

fun String.st64toBmp(): Bitmap? {
    if (isEmpty()) return null

    val bytes = Base64.decode(this, Base64.DEFAULT)
    return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
}

const val tempTabImg = "tempTabImg"

fun <T> Activity.toAc(java: Class<T>) = startActivity(Intent(this, java))

fun ctm() = System.currentTimeMillis()

fun longToDate(millis: Long) = DateFormat.format("dd/MM/yyyy", Date(millis)).toString()

fun <T : View> disableCLick(v: T) {
    v.isEnabled = false
    Handler(Looper.getMainLooper()).postDelayed({ v.isEnabled = true }, 1000)
}

fun deleteDirectoryContents(directory: File) {
    if (directory.exists() && directory.isDirectory) {
        directory.listFiles()?.forEach { file ->
            if (file.isFile) {
                "file deleted ${file.delete()}".log()
            } else if (file.isDirectory) {
                "directory found inside: ".log()
                deleteDirectoryContents(file)
            }
        }
    }
}

fun isServiceRunning(ctx: Context, serviceClass: Class<*>): Boolean {
    for (service in (ctx.getSystemService(androidx.appcompat.app.AppCompatActivity.ACTIVITY_SERVICE) as ActivityManager).getRunningServices(
        Int.MAX_VALUE
    )) {
        if (serviceClass.name == service.service.className) return true
    }
    return false
}

fun openApp(context: Context, appPackage: String, appUrl: String) {

    val i = context.packageManager.getLaunchIntentForPackage(appPackage)
    if (i != null) {
        //app is installed
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(i)
    } else {
        //app is not installed, open Facebook website in browser
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(appUrl))
        context.startActivity(browserIntent)
    }
}

fun View.toBitmap(): Bitmap {
    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    draw(Canvas(bitmap))
    return bitmap
}

fun View.toSmallBitmap(): Bitmap? {
    try {
        val bitmap = Bitmap.createBitmap(width / 3, height / 3, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val scale = (width / 3) / width.toFloat()
        canvas.scale(scale, scale)
        draw(canvas)
        return bitmap
    } catch (e: Exception) {
        e.print()
        return null
    }
}

fun Bitmap.faviconPng(ctx: Context): File {
    val myDir = File("${ctx.filesDir}", tempTabImg)
    if (!myDir.exists()) myDir.mkdir()

    val file = File(myDir, "${System.currentTimeMillis()}.png")
    if (file.exists()) file.delete()

    FileOutputStream(file).use { os -> compress(Bitmap.CompressFormat.PNG, 80, os) }

    return file
}

fun Bitmap.toJpg80(ctx: Context): File {
    val myDir = File("${ctx.filesDir}", tempTabImg)
    if (!myDir.exists()) myDir.mkdir()

    val file = File(myDir, "${System.currentTimeMillis()}.jpeg")
    if (file.exists()) file.delete()

    FileOutputStream(file).use { os -> compress(Bitmap.CompressFormat.JPEG, 80, os) }

    return file
}

//for img
fun enqueueDownload(context: Context, url: String) {
    val fileName = "${context.getString(st.app_name)}_${System.currentTimeMillis()}.png"
    val destination =
        "${Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).path}/$fileName"

    val file = File(destination)
    if (file.exists()) file.delete()

    val request = DownloadManager.Request(Uri.parse(url))
        .setMimeType("*/*")
        .setTitle(fileName)
        .setDescription("downloading")
        .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)
        .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        .setDestinationUri(Uri.parse("file://$destination"))

    (context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager).enqueue(request)
    "downloading".tosL(context)
}

fun getFileSize(size: Long): String {
    if (size <= 0) return "0"
    val units = arrayOf("B", "KB", "MB", "GB", "TB")
    val digitGroups = (log10(size.toDouble()) / log10(1024.0)).toInt()
    return DecimalFormat("#,##0.#").format(size / 1024.0.pow(digitGroups.toDouble())) + " " + units[digitGroups]
}

fun openKeyboard(v: View, activity: Activity?) {
    activity?.let {
        val imm = it.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(v, InputMethodManager.SHOW_IMPLICIT)
    }
}

fun closeKeyboard(activity: Activity?) {
    activity?.currentFocus?.let {
        val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(it.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
    }
}

fun onBackground(block: () -> Unit) {
    Executors.newSingleThreadExecutor().execute {
        block()
    }
}

inline fun exc(block: () -> Unit) {
    try {
        block()
    } catch (e: Exception) {
        e.print()
    }
}

fun delayInMillis(millis: Long, block: () -> Unit) =
    Handler(Looper.getMainLooper()).postDelayed({ block() }, millis)


//typealias andR = android.R
//typealias rid = R.id
typealias dw = R.drawable
typealias st = R.string
typealias clr = R.color

val gson = Gson()

fun <T> T.toGson(): String = gson.toJson(this)

fun View.visible() {
    visibility = VISIBLE
}

fun View.gon() {
    visibility = GONE
}

fun View.invisible() {
    visibility = INVISIBLE
}

fun Activity.ui(block: () -> Unit) {
    runOnUiThread { block() }
}

fun <T> T.tos(ctx: Context) = Toast.makeText(ctx, "$this", Toast.LENGTH_SHORT).show()
fun <T> T.tosL(ctx: Context) = Toast.makeText(ctx, "$this", Toast.LENGTH_SHORT).show()

fun <T> T.log() {
    if (com.example.roomprc.BuildConfig.DEBUG) exc { Log.wtf("FATZ", "$this") }
}

fun Exception.print() {
    if (com.example.roomprc.BuildConfig.DEBUG) {
        printStackTrace()
        (message + " | " + stackTrace[0].toString() + " | " + javaClass.name).log()
    }
//    FirebaseCrashlytics.getInstance().recordException(this)
}

fun ImageView.load(any: Any?) {
    Glide.with(this)
        .load(any)
        .transition(DrawableTransitionOptions.withCrossFade())
        .into(this)
}

fun ImageView.loadNew(any: Any?) {
    Glide.with(this)
        .load(any)
        .skipMemoryCache(true)
        .diskCacheStrategy(DiskCacheStrategy.NONE)
        .transition(DrawableTransitionOptions.withCrossFade())
        .into(this)
}

/*fun ImageView.load(any: Any?) {
    val cpd = CircularProgressDrawable(this.context)
    cpd.strokeWidth = 7f
    cpd.centerRadius = 35f
    cpd.setColorSchemeColors(Color.GREEN, Color.CYAN, Color.MAGENTA)
    cpd.start()

    Glide.with(this)
            .load(any)
//            .skipMemoryCache(true)
//            .diskCacheStrategy(DiskCacheStrategy.NONE)
//              .thumbnail(Glide.with(this).load("https://i.gifer.com/ZKZx.gif"))   //for gif
            .placeholder(cpd)                                    // comment when applying gif
//            .error(R.drawable.ic_error)
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(this)
}*/