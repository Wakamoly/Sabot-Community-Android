package com.lucidsoftworksllc.sabotcommunity.others

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.*
import com.google.android.material.snackbar.Snackbar
import com.lucidsoftworksllc.sabotcommunity.R
import com.lucidsoftworksllc.sabotcommunity.others.base.BaseFragment
import com.lucidsoftworksllc.sabotcommunity.util.DataState

fun Context.toastShort(message:String) =
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()

fun Context.toastLong(message:String) =
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()

val Context.deviceUserID: String?
    get() { return SharedPrefManager.getInstance(this)!!.userID }

val Context.deviceUsername: String?
    get() { return SharedPrefManager.getInstance(this)!!.username }

val Context.fcmToken: String?
    get() { return SharedPrefManager.getInstance(this)!!.fCMToken }





fun<A : Activity> Activity.startNewActivity(activity: Class<A>){
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

fun View.snackbar(message: String, action: (() -> Unit)? = null){
    val snackbar = Snackbar.make(this, message, Snackbar.LENGTH_LONG)
    action?.let {
        snackbar.setAction("Retry"){
            it()
        }
    }
    snackbar.show()
}

fun Fragment.handleApiError(
        failure: DataState.Failure,
        retry: (() -> Unit)? = null
){
    when{
        failure.isNetworkError -> requireView().snackbar("Please check your connection!", retry)
        failure.errorCode == 401 -> {
            (this as BaseFragment<*, *, *>).logout()
        }
        else -> {
            val error = failure.errorBody?.string().toString()
            requireView().snackbar(error)
        }
    }
}




/*
fun getTime(timeStart: String) : String{
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
}*/
