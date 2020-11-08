package com.lucidsoftworksllc.sabotcommunity.others

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.google.android.material.snackbar.Snackbar
import com.lucidsoftworksllc.sabotcommunity.R
import com.lucidsoftworksllc.sabotcommunity.others.base.BaseFragment
import com.lucidsoftworksllc.sabotcommunity.util.DataState
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.abs
import kotlin.math.roundToInt


fun Context.toastShort(message: String) =
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()

fun Context.toastLong(message: String) =
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()

val Context.deviceUserID: String?
    get() { return SharedPrefManager.getInstance(this)!!.userID }

val Context.deviceUsername: String?
    get() { return SharedPrefManager.getInstance(this)!!.username }

val Context.fcmToken: String?
    get() { return SharedPrefManager.getInstance(this)!!.fCMToken }





fun <A : Activity> Activity.startNewActivity(activity: Class<A>){
    Intent(this, activity).also {
        it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(it)
    }
}

fun newFragmentMain(mCtx: Context, fragment: Fragment, replace: Boolean){
    val fragTransaction = (mCtx as FragmentActivity)
            .supportFragmentManager
            .beginTransaction()
            .setCustomAnimations(R.anim.slide_in, R.anim.fade_out, R.anim.fade_in, R.anim.slide_out)
            .setCustomAnimations(R.anim.slide_in, R.anim.fade_out)
    when(replace){
        true -> {
            fragTransaction.add(R.id.fragment_container, fragment)
                    .addToBackStack(null)
                    .commit()
        }
        false -> {
            fragTransaction.replace(R.id.fragment_container, fragment)
                    .addToBackStack(null)
                    .commit()
        }
    }
}

inline fun FragmentManager.doTransaction(func: FragmentTransaction.() ->
FragmentTransaction) {
    beginTransaction().setCustomAnimations(R.anim.slide_in, R.anim.fade_out, R.anim.fade_in, R.anim.slide_out).func().commit()
}

fun AppCompatActivity.addFragment(frameId: Int, fragment: Fragment){
    supportFragmentManager.doTransaction { add(frameId, fragment) }
}


fun AppCompatActivity.replaceFragment(frameId: Int, fragment: Fragment) {
    supportFragmentManager.doTransaction{ replace(frameId, fragment) }
}

fun AppCompatActivity.removeFragment(fragment: Fragment) {
    supportFragmentManager.doTransaction{ remove(fragment) }
}

fun View.visible(isVisible: Boolean){
    visibility = if(isVisible) View.VISIBLE else View.GONE
}

fun View.enable(enabled: Boolean){
    isEnabled = enabled
    alpha = if(enabled) 1f else 0.5f
}

fun View.snackbar(message: String, actionText: String, action: (() -> Unit)? = null){
    if (Build.VERSION.SDK_INT >= 23) {
        val snackbar = Snackbar.make(this, message, Snackbar.LENGTH_LONG)
        action?.let {
            snackbar.setAction(actionText) {
                it()
            }
        }
        snackbar.setActionTextColor(Color.parseColor("#45B431"))
        snackbar.setTextColor(Color.WHITE)
        val snackbarView = snackbar.view
        snackbarView.setBackgroundColor(Color.parseColor("#111111"))
        snackbar.show()
    }else{
        context?.toastLong(message)
    }
}

fun View.snackbarShort(message: String, actionText: String, action: (() -> Unit)? = null){
    if (Build.VERSION.SDK_INT >= 23) {
        val snackbar = Snackbar.make(this, message, Snackbar.LENGTH_SHORT)
        action?.let {
            snackbar.setAction(actionText) {
                it()
            }
        }
        snackbar.setActionTextColor(Color.parseColor("#45B431"))
        snackbar.setTextColor(Color.WHITE)
        val snackbarView = snackbar.view
        snackbarView.setBackgroundColor(Color.parseColor("#111111"))
        snackbar.show()
    }else{
        context?.toastShort(message)
    }
}

fun View.retrySnackbar(message: String, action: (() -> Unit)? = null){
    if (Build.VERSION.SDK_INT >= 23) {
        val snackbar = Snackbar.make(this, message, Snackbar.LENGTH_LONG)
        action?.let {
            snackbar.setAction("Retry"){
                it()
            }
        }
        snackbar.setActionTextColor(Color.parseColor("#45B431"))
        snackbar.setTextColor(Color.WHITE)
        val snackbarView = snackbar.view
        snackbarView.setBackgroundColor(Color.parseColor("#111111"))
        snackbar.show()
    }else{
        context?.toastShort(message)
    }
}

fun Fragment.handleApiError(
        failure: DataState.Failure,
        retry: (() -> Unit)? = null
){
    println("HANDLEAPIERROR: ${failure.errorBody?.string().toString()}")
    when{
        failure.isNetworkError -> requireView().retrySnackbar("Please check your connection!", retry)
        failure.errorCode == 401 -> {
            (this as BaseFragment<*, *, *>).logout()
        }
        else -> {
            val error = failure.errorBody?.string().toString()
            requireView().retrySnackbar(error)
        }
    }
}

fun stringToDate(string: String) : Date {
    val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    return try {
        val date: Date = format.parse(string)
        date
    } catch (e: ParseException) {
        // TODO Auto-generated catch block
        e.printStackTrace()
        currentDate()
    }
}

fun currentDate(): Date {
    TimeZone.setDefault(TimeZone.getTimeZone("America/Chicago"))
    val calendar: Calendar = Calendar.getInstance(TimeZone.getTimeZone("America/Chicago"))
    return calendar.time
}

fun getTimeAgo(dateString: String, ctx: Context): String? {
    val date = stringToDate(dateString)
    val time: Long = date.time
    val curDate: Date = currentDate()
    val now: Long = curDate.time
    if (time > now || time <= 0) {
        return null
    }
    val timeAgo = when (val dim = getTimeDistanceInMinutes(time)) {
        0 -> ctx.resources.getString(R.string.date_util_term_less) + " " + ctx.resources.getString(R.string.date_util_term_a) + " " + ctx.resources.getString(R.string.date_util_unit_minute)
        1 -> "1 " + ctx.resources.getString(R.string.date_util_unit_minute)
        in 2..50 -> dim.toString() + " " + ctx.resources.getString(R.string.date_util_unit_minutes)
        in 51..89 -> ctx.resources.getString(R.string.date_util_prefix_about) + " " + ctx.resources.getString(R.string.date_util_term_an) + " " + ctx.resources.getString(R.string.date_util_unit_hour)
        in 90..1439 -> ctx.resources.getString(R.string.date_util_prefix_about) + " " + (dim / 60.toFloat()).roundToInt() + " " + ctx.resources.getString(R.string.date_util_unit_hours)
        in 1440..2519 -> "1 " + ctx.resources.getString(R.string.date_util_unit_day)
        in 2520..43199 -> (dim / 1440.toFloat()).roundToInt().toString() + " " + ctx.resources.getString(R.string.date_util_unit_days)
        in 43200..86399 -> ctx.resources.getString(R.string.date_util_prefix_about) + " " + ctx.resources.getString(R.string.date_util_term_a) + " " + ctx.resources.getString(R.string.date_util_unit_month)
        in 86400..525599 -> (dim / 43200.toFloat()).roundToInt().toString() + " " + ctx.resources.getString(R.string.date_util_unit_months)
        in 525600..655199 -> ctx.resources.getString(R.string.date_util_prefix_about) + " " + ctx.resources.getString(R.string.date_util_term_a) + " " + ctx.resources.getString(R.string.date_util_unit_year)
        in 655200..914399 -> ctx.resources.getString(R.string.date_util_prefix_over) + " " + ctx.resources.getString(R.string.date_util_term_a) + " " + ctx.resources.getString(R.string.date_util_unit_year)
        in 914400..1051199 -> ctx.resources.getString(R.string.date_util_prefix_almost) + " 2 " + ctx.resources.getString(R.string.date_util_unit_years)
        else -> ctx.resources.getString(R.string.date_util_prefix_about) + " " + (dim / 525600.toFloat()).roundToInt() + " " + ctx.resources.getString(R.string.date_util_unit_years)
    }
    return timeAgo + " " + ctx.resources.getString(R.string.date_util_suffix)
}

fun isUserOnline(dateString: String): Boolean {
    val date = stringToDate(dateString)
    val time: Long = date.time
    val curDate: Date = currentDate()
    val now: Long = curDate.time
    if (time > now || time <= 0) {
        return false
    }
    return when (getTimeDistanceInMinutes(time)) {
        in 0..5 -> true
        else -> false
    }
}

private fun getTimeDistanceInMinutes(time: Long): Int {
    val timeDistance: Long = currentDate().time - time
    return (abs(timeDistance) / 1000 / 60.toFloat()).roundToInt()
}





/*fun getTime(timeStart: String) : String{
    val dbDateTime = ZonedDateTime.now(ZoneId.of("America/New_York"))


    val date_time_now = date("Y-m-d H:i:s");
    $start_date = new DateTime($datetime); //Time of post
    $end_date = new DateTime($date_time_now); //Current time
    $interval = $start_date->diff($end_date); //Difference between dates
    if($interval->y >= 1) {
        if($interval == 1)
        $time_message = $interval->y . " year ago"; //1 year ago
        else
        $time_message = $interval->y . " years ago"; //1+ year ago
    }else if ($interval->m >= 1) {
        if($interval->d == 0) {
        $days = " ago";
    }else if($interval->d == 1) {
        $days = $interval->d . " day ago";
    }else {
        $days = $interval->d . " days ago";
    }
        if($interval->m == 1) {
        $time_message = $interval->m . " month ". $days;
    }else {
        $time_message = $interval->m . " months ". $days;
    }
    }else if($interval->d >= 1) {
        if($interval->d == 1) {
        $time_message = "Yesterday";
    }else {
        $time_message = $interval->d . " days ago";
    }
    }else if($interval->h >= 1) {
        if($interval->h == 1) {
        $time_message = $interval->h . " hour ago";
    }else {
        $time_message = $interval->h . " hours ago";
    }
    }else if($interval->i >= 1) {
        if($interval->i == 1) {
        $time_message = $interval->i . " minute ago";
    }else {
        $time_message = $interval->i . " minutes ago";
    }
    }else{
        if($interval->s < 30) {
        $time_message = "Just now";
    }else {
        $time_message = $interval->s . " seconds ago";
    }
    }
    return time_message
}*/
