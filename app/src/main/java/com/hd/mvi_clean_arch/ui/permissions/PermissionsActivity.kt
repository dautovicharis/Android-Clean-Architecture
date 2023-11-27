package com.hd.mvi_clean_arch.ui.permissions

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import android.view.LayoutInflater
import androidx.activity.addCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.hd.mvi_clean_arch.base.ViewBindingActivity
import com.hd.mvi_clean_arch.databinding.ActivityPermissionsBinding
import com.hd.mvi_clean_arch.utils.viewModelOf
import com.hd.presentation.permissions.PermissionsViewModel
import com.hd.presentation.permissions.model.PermissionUI
import com.hd.presentation.permissions.model.event.NavigationEvent
import com.hd.presentation.permissions.model.event.UiVisibilityEvent
import com.hd.presentation.permissions.model.uistate.PermissionsUiState
import kotlinx.coroutines.launch

class PermissionsActivity : ViewBindingActivity<ActivityPermissionsBinding>(),
    PermissionsAdapter.PermissionToggleListener {

    override val bindingInflater: (LayoutInflater) -> ActivityPermissionsBinding
        get() = ActivityPermissionsBinding::inflate

    private val viewModelPermissions: PermissionsViewModel by lazy { viewModelOf(mViewModelProvider) }
    private val permissionsAdapter = PermissionsAdapter(this)

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            viewModelPermissions.checkAllPermissions()
            if (!isGranted) {
                openAppSettings()
            }
        }

    override fun inject() {
        super.inject()
        appComponent.inject(this)
    }

    override fun setupUIElements() {
        super.setupUIElements()
        binding.rvPermissions.layoutManager = LinearLayoutManager(this)
        binding.rvPermissions.adapter = permissionsAdapter

        val showFlag = intent.getBooleanExtra(SHOW_DO_NOT_REMIND, false)
        if (showFlag) {
            viewModelPermissions.showDoNotRemindMeAgain()
        }
    }

    override fun setupClickListeners() {
        super.setupClickListeners()
        binding.dontAskMeAgain.setOnCheckedChangeListener { _, isChecked ->
            viewModelPermissions.doNotAskMeAgain(isChecked)
        }

        onBackPressedDispatcher.addCallback {
            setResult(Activity.RESULT_CANCELED)
            finish()
        }
    }

    override fun collectFlows() {
        super.collectFlows()
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModelPermissions.permissionsFlow.collect { permissionsState ->
                    if (permissionsState.permissionsState is PermissionsUiState.Success) {
                        permissionsAdapter.submitPermissionsList((permissionsState.permissionsState as PermissionsUiState.Success).permissionsUi.permissions)
                    }
                }
            }
        }
    }

    override fun observeViewModel() {
        super.observeViewModel()
        viewModelPermissions.uiVisibilityEvent.observe(this) {
            if (it is UiVisibilityEvent.DoNotRemindMe) {
                binding.dontAskMeAgain.visibility = it.visibility
            }
        }

        viewModelPermissions.navigationEvent.observe(this) {
            when (it) {
                is NavigationEvent.OpenAlarmActivity -> {
                    openAlarmActivity()
                }

                is NavigationEvent.OpenAppSettings -> {
                    openAppSettings()
                }

                is NavigationEvent.OpenAutoStartActivity -> {
                    openAutoStartActivity(it)
                }

                is NavigationEvent.RequestNotificationPermission -> {
                    requestNotificationPermission()
                }

                else -> {}
            }
        }
    }

    private fun requestNotificationPermission() {
        requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
    }

    override fun onResume() {
        super.onResume()
        viewModelPermissions.checkAllPermissions()
    }

    override fun onPermissionToggled(
        permission: PermissionUI
    ) {
        viewModelPermissions.handlePermissionToggle(permission)
    }

    private fun openAutoStartActivity(event: PermissionsNavigation.OpenAutoStartActivity) {
        startActivity(event.intent)
    }

    private fun openAlarmActivity() {
        startActivity(Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM))
    }

    private fun openAppSettings() {
        startActivity(
            Intent(
                Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.fromParts("package", packageName, null)
            )
        )
    }

    companion object {
        const val SHOW_DO_NOT_REMIND = "SHOW_DO_NOT_REMIND"
    }
}