package com.lucidsoftworksllc.sabotcommunity.others.base

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.viewbinding.ViewBinding
import com.lucidsoftworksllc.sabotcommunity.activities.LoginActivity
import com.lucidsoftworksllc.sabotcommunity.others.SharedPrefManager
import com.lucidsoftworksllc.sabotcommunity.others.startNewActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext
import kotlin.properties.Delegates

abstract class BaseFragment<VM: BaseViewModel, B: ViewBinding, R: BaseRepository> : CoFragment() {

    protected lateinit var userPreferences: SharedPrefManager
    protected lateinit var binding : B
    protected lateinit var viewModel: VM
    protected val remoteDataSource = RemoteDataSource()
    protected var deviceUserID by Delegates.notNull<Int>()
    protected lateinit var deviceUsername: String
    protected lateinit var mCtx: Context

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        userPreferences = SharedPrefManager.getInstance(requireContext())!!
        binding = getFragmentBinding(inflater, container)
        val factory = ViewModelFactory(getFragmentRepository())
        viewModel = ViewModelProvider(this, factory).get(getViewModel())

        lifecycleScope.launch {
            if (userPreferences.isLoggedIn){
                deviceUserID = userPreferences.userID?.toInt()!!
                deviceUsername = userPreferences.username.toString()
            }else{
                logout()
            }
        }

        return binding.root
    }

    fun logout() = lifecycleScope.launch {
        userPreferences.logout()
        requireActivity().startNewActivity(LoginActivity::class.java)
    }

    abstract fun getViewModel(): Class<VM>

    abstract fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?): B

    abstract fun getFragmentRepository(): R

}