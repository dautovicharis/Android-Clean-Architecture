package com.hd.presentation.permissions

import android.content.Intent
import android.view.View
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.common.truth.Truth
import com.hd.data.permissions.mapper.toPermissions
import com.hd.data.permissions.model.PermissionDTO
import com.hd.data.permissions.model.PermissionTypeDTO
import com.hd.data.permissions.model.PermissionsDTO
import com.hd.domain.permissions.usecase.PermissionsUseCase
import com.hd.present.permissions.mapper.toUI
import com.hd.presentation.observeOnce
import com.hd.presentation.permissions.model.PermissionTypeUI
import com.hd.presentation.permissions.model.PermissionUI
import com.hd.presentation.permissions.model.event.NavigationEvent
import com.hd.presentation.permissions.model.event.TaskEvent
import com.hd.presentation.permissions.model.event.UiVisibilityEvent
import com.hd.presentation.permissions.model.uistate.PermissionsUiState
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.spyk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule

@ExperimentalCoroutinesApi
class PermissionsViewModelTest {

    private val dispatcher: TestDispatcher = UnconfinedTestDispatcher()

    @get:Rule
    val rule: TestRule = InstantTaskExecutorRule()

    // Mocks
    private val permissionUseCase: PermissionsUseCase = mockk()

    // SUT
    private lateinit var viewModel: PermissionsViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
        viewModel = PermissionsViewModel(permissionUseCase)
        viewModel = spyk(viewModel)
    }

    @Test
    fun `Init permissionsState should return Loading`() = runTest {
        // Act
        val result = viewModel.permissionsFlow.first()

        // Assert
        Truth.assertThat(result.permissionsState).isEqualTo(PermissionsUiState.Loading)
    }

    // Tests Feature: Permissions
    @Test
    fun `checkAllPermissions whenAllPermissionsGranted should return ScheduleNotifications`() =
        runTest {
            // Arrange
            val permissionsResponseMock = listOf(
                PermissionDTO(PermissionTypeDTO.AUTO_START, granted = true),
                PermissionDTO(PermissionTypeDTO.ALARM, granted = true),
                PermissionDTO(PermissionTypeDTO.NOTIFICATIONS, granted = true)
            )

            val permissionsMock = PermissionsDTO(
                permissions = permissionsResponseMock,
                shouldAskPermission = false,
                allGranted = true
            ).toPermissions()

            val expectedPermissionsUiMock = permissionsMock.toUI().permissions

            coEvery { permissionUseCase.checkPermissions() } returns flowOf(permissionsMock)
            coEvery { viewModel.permissionsActivityDismissed } returns flowOf(true)

            // Act
            viewModel.checkAllPermissions()

            // Assert
            val actualPermissions = viewModel.permissionsFlow.first().permissionsState.let {
                (it as PermissionsUiState.Success)
            }.permissionsUi

            // Assert permissions list
            Truth.assertThat(actualPermissions.permissions)
                .containsExactlyElementsIn(expectedPermissionsUiMock)
            Truth.assertThat(actualPermissions.shouldAskPermission).isFalse()

            viewModel.navigationEvent.observeOnce {
                val expected = TaskEvent.ScheduleNotifications
                Truth.assertThat(it).isInstanceOf(expected::class.java)
            }
        }

    @Test
    fun `checkAllPermissions whenPermissionsAreNotGranted and not dismissed should return ShowPermissionsActivity`() =
        runTest {
            // Arrange
            val permissionsResponseMock = listOf(
                PermissionDTO(PermissionTypeDTO.AUTO_START, granted = false),
                PermissionDTO(PermissionTypeDTO.ALARM, granted = false),
                PermissionDTO(PermissionTypeDTO.NOTIFICATIONS, granted = false)
            )

            val permissionsMock = PermissionsDTO(
                permissions = permissionsResponseMock,
                shouldAskPermission = true,
                allGranted = false
            ).toPermissions()

            val expectedPermissions = permissionsMock.toUI().permissions

            coEvery { permissionUseCase.checkPermissions() } returns flowOf(permissionsMock)
            coEvery { viewModel.permissionsActivityDismissed } returns flowOf(false)

            // Act
            viewModel.checkAllPermissions()

            // Assert
            val actualPermissions = viewModel.permissionsFlow.first().permissionsState.let {
                (it as PermissionsUiState.Success)
            }.permissionsUi

            Truth.assertThat(actualPermissions.permissions)
                .containsExactlyElementsIn(expectedPermissions)
            Truth.assertThat(actualPermissions.allGranted).isFalse()
            Truth.assertThat(actualPermissions.shouldAskPermission).isTrue()

            viewModel.navigationEvent.observeOnce {
                val expected = NavigationEvent.ShowPermissionsActivity
                Truth.assertThat(it).isInstanceOf(expected::class.java)
            }
        }


    @Test
    fun `checkAllPermissions whenPermissionsAreNotGranted and dismissed and shouldNotAskPermission should return MissingPermissionWarning`() =
        runTest {
            // Arrange
            val permissionsResponseMock = listOf(
                PermissionDTO(PermissionTypeDTO.AUTO_START, granted = false),
                PermissionDTO(PermissionTypeDTO.ALARM, granted = false),
                PermissionDTO(PermissionTypeDTO.NOTIFICATIONS, granted = false)
            )

            val permissionsMock = PermissionsDTO(
                permissions = permissionsResponseMock,
                shouldAskPermission = false,
                allGranted = false
            ).toPermissions()

            val expectedPermissions = permissionsMock.toUI().permissions

            coEvery { permissionUseCase.checkPermissions() } returns flowOf(permissionsMock)
            coEvery { viewModel.permissionsActivityDismissed } returns flowOf(true)

            // Act
            viewModel.checkAllPermissions()

            // Assert
            val actualPermissions = viewModel.permissionsFlow.first().permissionsState.let {
                (it as PermissionsUiState.Success)
            }.permissionsUi

            Truth.assertThat(actualPermissions.permissions)
                .containsExactlyElementsIn(expectedPermissions)
            Truth.assertThat(actualPermissions.allGranted).isFalse()
            Truth.assertThat(actualPermissions.shouldAskPermission).isFalse()

            viewModel.uiVisibilityEvent.observeOnce {
                val expected = UiVisibilityEvent.MissingPermissionWarning(View.VISIBLE)
                Truth.assertThat(it).isInstanceOf(expected::class.java)
                Truth.assertThat(it.visibility).isEqualTo(View.VISIBLE)
            }
        }


    @Test
    fun `handlePermissionToggle with AUTO_START should return OpenAutoStartActivity`() = runTest {
        // Arrange
        val intent = Intent()
        val permission = PermissionUI(
            permissionType = PermissionTypeUI.AUTO_START,
            intent = intent,
            granted = false
        )

        coEvery { permissionUseCase.toggleAutoStartPermission() } returns Unit

        // Act
        viewModel.handlePermissionToggle(permission)

        // Assert
        viewModel.navigationEvent.observeOnce {
            val expected = NavigationEvent.OpenAutoStartActivity(permission.intent!!)
            Truth.assertThat(it).isInstanceOf(expected::class.java)
        }
    }

    @Test
    fun `handlePermissionToggle with ALARM return shouldOpenAlarmActivity`() = runTest {
        // Arrange
        val permission = PermissionUI(
            permissionType = PermissionTypeUI.ALARM,
            intent = null,
            granted = true
        )

        // Act
        viewModel.handlePermissionToggle(permission)

        // Assert
        viewModel.navigationEvent.observeOnce {
            val expected = NavigationEvent.OpenAlarmActivity
            Truth.assertThat(it).isInstanceOf(expected::class.java)
        }
    }

    @Test
    fun `handlePermissionToggle with NOTIFICATIONS and granted should return OpenAppSettings`() =
        runTest {
            // Arrange
            val permission = PermissionUI(
                permissionType = PermissionTypeUI.NOTIFICATIONS,
                intent = null,
                granted = true
            )

            // Act
            viewModel.handlePermissionToggle(permission)

            // Assert
            viewModel.navigationEvent.observeOnce {
                val expected = NavigationEvent.OpenAppSettings
                Truth.assertThat(it).isInstanceOf(expected::class.java)
            }
        }

    @Test
    fun `handlePermissionToggle with NOTIFICATIONS and not granted should return RequestNotificationPermission`() =
        runTest {
            // Arrange
            val permission = PermissionUI(
                permissionType = PermissionTypeUI.NOTIFICATIONS,
                intent = null,
                granted = false
            )

            // Act
            viewModel.handlePermissionToggle(permission)

            // Assert
            viewModel.navigationEvent.observeOnce {
                val expected = NavigationEvent.RequestNotificationPermission
                Truth.assertThat(it).isInstanceOf(expected::class.java)
            }
        }

    // Tests Feature: Do not ask me again
    @Test
    fun `showDoNotRemindMeAgain should return DoNotRemindMe`() = runTest {
        // Act
        viewModel.showDoNotRemindMeAgain()

        // Assert
        viewModel.uiVisibilityEvent.observeOnce { event ->
            Truth.assertThat(event).isInstanceOf(UiVisibilityEvent.DoNotRemindMe::class.java)
            Truth.assertThat(event.visibility).isEqualTo(View.VISIBLE)
        }
    }

    @Test
    fun `doNotAskMeAgain should call permissionsUseCase doNotAskMeAgain`() = runTest {
        // Arrange
        val isChecked = true
        coEvery { permissionUseCase.doNotAskMePermissions(isChecked) } returns Unit

        // Act
        viewModel.doNotAskMeAgain(isChecked)

        // Assert
        coVerify { permissionUseCase.doNotAskMePermissions(isChecked) }
    }


    // Tests Feature: Dismiss
    @Test
    fun `dismissPermissionsActivity should call permissionsActivityDismissed`() = runTest {
        // Act
        viewModel.dismiss()

        // Assert
        val result = viewModel.permissionsActivityDismissed.first()
        Truth.assertThat(result).isTrue()
    }
}
