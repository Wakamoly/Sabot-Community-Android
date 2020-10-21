package com.lucidsoftworksllc.sabotcommunity.others.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.lucidsoftworksllc.sabotcommunity.fragments.repositories.DashboardRepo
import com.lucidsoftworksllc.sabotcommunity.fragments.viewmodels.DashboardVM

@Suppress("UNCHECKED_CAST")
class ViewModelFactory(
        private val repository: BaseRepository
) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return when{
            //modelClass.isAssignableFrom(AuthViewModel::class.java) -> AuthViewModel(repository as AuthRepository) as T
            modelClass.isAssignableFrom(DashboardVM::class.java) -> DashboardVM(repository as DashboardRepo) as T
            else -> throw IllegalArgumentException("ViewModel Class Not Found")
        }
    }
}