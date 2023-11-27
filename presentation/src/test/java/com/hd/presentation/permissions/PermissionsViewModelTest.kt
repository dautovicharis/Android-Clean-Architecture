package com.hd.presentation.permissions

import android.content.Intent
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.common.truth.Truth
import com.hd.data.permissions.mapper.toPermissions
import com.hd.data.permissions.model.PermissionDTO
import com.hd.data.permissions.model.PermissionTypeDTO
import com.hd.data.permissions.model.PermissionsDTO
import com.hd.domain.permissions.usecase.PermissionsUseCase
import com.hd.presentation.observeOnce
import com.hd.presentation.permissions.mapper.PermissionTypeUI
import com.hd.presentation.permissions.mapper.PermissionUI
import com.hd.presentation.permissions.mapper.toUI
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
        val result = viewModel.permissionsState.first()

        // Assert
        Truth.assertThat(result).isEqualTo(PermissionsUiState.Loading)
    }

    @Test
    fun `checkAllPermissions whenAllPermissionsGranted should return Granted`() =
        runTest {
            // Arrange
            val permissionsResponseMock = listOf(
                PermissionDTO(PermissionTypeDTO.AUTO_START, granted = true, isOptional = true),
                PermissionDTO(PermissionTypeDTO.ALARM, granted = true, isOptional = false),
                PermissionDTO(PermissionTypeDTO.NOTIFICATIONS, granted = true, isOptional = false)
            )

            val permissionsMock = PermissionsDTO(
                permissions = permissionsResponseMock,
                shouldAskPermission = false,
                allGranted = true
            ).toPermissions()

            val expectedPermissionsUiMock = permissionsMock.toUI().permissions

            coEvery { permissionUseCase.checkPermissions() } returns flowOf(permissionsMock)

            // Act
            viewModel.checkAllPermissions()

            // Assert
            val actualPermissions = viewModel.permissionsState.first().let {
                (it as PermissionsUiState.Success)
            }.permissionsUi

            // Assert permissions list
            Truth.assertThat(actualPermissions.permissions)
                .containsExactlyElementsIn(expectedPermissionsUiMock)
            Truth.assertThat(actualPermissions.shouldAskPermission).isFalse()
            Truth.assertThat(actualPermissions.allGranted).isTrue()
        }

    @Test
    fun `handlePermissionToggle with AUTO_START should call OpenAutoStartActivity`() = runTest {
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
            val expected = PermissionsNavigation.OpenAutoStartActivity(permission.intent!!)
            Truth.assertThat(it).isInstanceOf(expected::class.java)
        }
    }

    @Test
    fun `handlePermissionToggle with ALARM should call OpenAlarmActivity`() = runTest {
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
            val expected = PermissionsNavigation.OpenAlarmActivity
            Truth.assertThat(it).isInstanceOf(expected::class.java)
        }
    }

    @Test
    fun `handlePermissionToggle with NOTIFICATIONS and granted should call OpenAppSettings`() =
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
                val expected = PermissionsNavigation.OpenAppSettings
                Truth.assertThat(it).isInstanceOf(expected::class.java)
            }
        }

    @Test
    fun `handlePermissionToggle with NOTIFICATIONS and !granted should call RequestNotificationPermission`() =
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
                val expected = PermissionsNavigation.RequestNotificationPermission
                Truth.assertThat(it).isInstanceOf(expected::class.java)
            }
        }

    @Test
    fun `doNotAskMeAgain should call permissionsUseCase doNotAskMePermissions`() = runTest {
        // Arrange
        val isChecked = true
        coEvery { permissionUseCase.doNotAskMePermissions(isChecked) } returns Unit

        // Act
        viewModel.doNotAskMeAgain(isChecked)

        // Assert
        coVerify { permissionUseCase.doNotAskMePermissions(isChecked) }
    }
}
