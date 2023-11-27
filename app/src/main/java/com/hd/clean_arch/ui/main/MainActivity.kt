package com.hd.clean_arch.ui.main

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.hd.clean_arch.base.ViewBindingActivity
import com.hd.clean_arch.databinding.ActivityMainBinding
import com.hd.clean_arch.ui.permissions.PermissionsActivity
import com.hd.clean_arch.utils.viewModelOf
import com.hd.presentation.main.MainNavigation
import com.hd.presentation.main.MainViewModel
import kotlinx.coroutines.launch

class MainActivity : ViewBindingActivity<ActivityMainBinding>() {

    private val viewModelPermissions: MainViewModel by lazy { viewModelOf(mViewModelProvider) }

    override val bindingInflater: (layoutInflater: LayoutInflater) -> ActivityMainBinding
        get() = ActivityMainBinding::inflate


    private val startForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_CANCELED) {
                viewModelPermissions.dismissPermissions()
            }
        }

    override fun inject() {
        super.inject()
        appComponent.inject(this)
    }

    override fun onResume() {
        super.onResume()
        viewModelPermissions.checkAllPermissions()
    }

    override fun observeViewModel() {
        super.observeViewModel()
        viewModelPermissions.navigationEvent.observe(this) {
            if (it is MainNavigation.ShowPermissionsActivity) {
                startPermissionActivity(it.doNotAskVisibility)
            }
        }
    }

    override fun collectFlows() {
        super.collectFlows()
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModelPermissions.permissionsState.collect { state ->
                    binding.btnPermissions.setText(state.text)
                    binding.btnPermissions.setIconResource(state.icon)
                }
            }
        }
    }

    override fun setupClickListeners() {
        super.setupClickListeners()
        binding.btnPermissions.setOnClickListener {
            viewModelPermissions.openPermissionActivity()
        }
    }

    private fun startPermissionActivity(doNotAskVisibility: Int) {
        val intent = Intent(this, PermissionsActivity::class.java)
            .apply { putExtra(PermissionsActivity.DO_NOT_ASK_VISIBILITY, doNotAskVisibility) }
        startForResult.launch(intent)
    }
}