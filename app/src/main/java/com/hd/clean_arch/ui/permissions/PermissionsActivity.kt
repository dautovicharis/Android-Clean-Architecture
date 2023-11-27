package com.hd.clean_arch.ui.permissions

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import androidx.activity.addCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.hd.clean_arch.base.ViewBindingActivity
import com.hd.clean_arch.databinding.ActivityPermissionsBinding
import com.hd.clean_arch.utils.viewModelOf
import com.hd.presentation.permissions.PermissionsNavigation
import com.hd.presentation.permissions.PermissionsUiState
import com.hd.presentation.permissions.PermissionsViewModel
import com.hd.presentation.permissions.mapper.PermissionUI
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
        binding.doNotAskAgain.visibility = intent.getIntExtra(DO_NOT_ASK_VISIBILITY, View.GONE)
    }

    override fun setupClickListeners() {
        super.setupClickListeners()

        binding.btnClose.setOnClickListener {
            setResult(Activity.RESULT_CANCELED)
            finish()
        }

        binding.doNotAskAgain.setOnCheckedChangeListener { _, isChecked ->
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
                viewModelPermissions.permissionsState.collect { permissionsState ->

                    when (permissionsState) {
                        is PermissionsUiState.Success -> {
                            permissionsAdapter.submitPermissionsList((permissionsState).permissionsUi.permissions)
                        }

                        is PermissionsUiState.Loading -> {
                            // Loading TO DO
                        }
                    }
                }
            }
        }
    }

    override fun observeViewModel() {
        super.observeViewModel()
        viewModelPermissions.navigationEvent.observe(this) {
            when (it) {
                is PermissionsNavigation.OpenAlarmActivity -> {
                    openAlarmActivity()
                }

                is PermissionsNavigation.OpenAppSettings -> {
                    openAppSettings()
                }

                is PermissionsNavigation.OpenAutoStartActivity -> {
                    openAutoStartActivity(it)
                }

                is PermissionsNavigation.RequestNotificationPermission -> {
                    requestNotificationPermission()
                }
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
        const val DO_NOT_ASK_VISIBILITY = "DO_NOT_ASK_VISIBILITY"
    }
}