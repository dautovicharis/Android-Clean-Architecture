package com.hd.presentation.main

import android.view.View
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.common.truth.Truth
import com.hd.data.permissions.mapper.toPermissions
import com.hd.data.permissions.model.PermissionDTO
import com.hd.data.permissions.model.PermissionTypeDTO
import com.hd.data.permissions.model.PermissionsDTO
import com.hd.domain.permissions.usecase.PermissionsUseCase
import com.hd.presentation.R
import com.hd.presentation.observeOnce
import io.mockk.coEvery
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
class MainViewModelTest {

    private val dispatcher: TestDispatcher = UnconfinedTestDispatcher()

    @get:Rule
    val rule: TestRule = InstantTaskExecutorRule()

    // Mocks
    private val permissionUseCase: PermissionsUseCase = mockk()

    // SUT
    private lateinit var viewModel: MainViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
        viewModel = MainViewModel(permissionUseCase)
        viewModel = spyk(viewModel)
    }

    // Tests Feature: Permissions
    @Test
    fun `Init permissionsFlow should return MissingPermissions`() = runTest {
        // Act
        val result = viewModel.permissionsState.first()

        // Assert
        Truth.assertThat(result).isEqualTo(
            MainUIState.MissingPermissions(
                text = R.string.permissions_missing,
                icon = R.drawable.ic_warning
            )
        )
    }

    @Test
    fun `checkAllPermissions when allGranted should return Granted`() =
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

            coEvery { permissionUseCase.checkPermissions() } returns flowOf(permissionsMock)
            coEvery { viewModel.permissionsDismissed } returns true

            // Act
            viewModel.checkAllPermissions()

            // Assert
            val actualPermissions = viewModel.permissionsState.first()
            Truth.assertThat(actualPermissions).isInstanceOf(MainUIState.Granted::class.java)
        }

    @Test
    fun `checkAllPermissions when shouldAskPermission && !dismissed should return AskUser`() =
        runTest {
            // Arrange
            val permissionsResponseMock = listOf(
                PermissionDTO(PermissionTypeDTO.AUTO_START, granted = false, isOptional = true),
                PermissionDTO(PermissionTypeDTO.ALARM, granted = false, isOptional = false),
                PermissionDTO(PermissionTypeDTO.NOTIFICATIONS, granted = false, isOptional = false)
            )

            val permissionsMock = PermissionsDTO(
                permissions = permissionsResponseMock,
                shouldAskPermission = true,
                allGranted = false
            ).toPermissions()

            coEvery { permissionUseCase.checkPermissions() } returns flowOf(permissionsMock)
            coEvery { viewModel.permissionsDismissed } returns false

            // Act
            viewModel.checkAllPermissions()

            // Assert
            val actualPermissions = viewModel.permissionsState.first()

            Truth.assertThat(actualPermissions).isInstanceOf(MainUIState.AskUser::class.java)

            viewModel.navigationEvent.observeOnce {
                val expectedVisibility = View.VISIBLE
                val expected = MainNavigation.ShowPermissionsActivity(expectedVisibility)

                val navigationEvent = it as MainNavigation.ShowPermissionsActivity
                Truth.assertThat(navigationEvent).isInstanceOf(expected::class.java)
                Truth.assertThat((navigationEvent).doNotAskVisibility).isEqualTo(expectedVisibility)
            }
        }

    @Test
    fun `checkAllPermissions when !allGranted and dismissed and !shouldAskPermission should return MissingPermissions`() =
        runTest {
            // Arrange
            val permissionsResponseMock = listOf(
                PermissionDTO(PermissionTypeDTO.AUTO_START, granted = false, isOptional = true),
                PermissionDTO(PermissionTypeDTO.ALARM, granted = false, isOptional = false),
                PermissionDTO(PermissionTypeDTO.NOTIFICATIONS, granted = true, isOptional = false)
            )

            val permissionsMock = PermissionsDTO(
                permissions = permissionsResponseMock,
                shouldAskPermission = false,
                allGranted = false
            ).toPermissions()

            coEvery { permissionUseCase.checkPermissions() } returns flowOf(permissionsMock)
            coEvery { viewModel.permissionsDismissed } returns true

            // Act
            viewModel.checkAllPermissions()

            // Assert
            val actualPermissions = viewModel.permissionsState.first()
            Truth.assertThat(actualPermissions)
                .isInstanceOf(MainUIState.MissingPermissions::class.java)
        }


    // Tests Feature: Dismiss
    @Test
    fun `dismiss should call permissionsActivityDismissed`() = runTest {
        // Act
        viewModel.dismissPermissions()

        // Assert
        val result = viewModel.permissionsDismissed
        Truth.assertThat(result).isTrue()
    }

    // Tests Feature: openPermissionActivity
    @Test
    fun `openPermissionActivity(Visible) should call ShowPermissionsActivity`() = runTest {
        // Act
        val visibility = View.VISIBLE
        viewModel.openPermissionActivity(visibility)

        // Assert
        viewModel.navigationEvent.observeOnce {
            val expected = MainNavigation.ShowPermissionsActivity(visibility)

            val navigationEvent = it as MainNavigation.ShowPermissionsActivity
            Truth.assertThat(navigationEvent).isInstanceOf(expected::class.java)
            Truth.assertThat((navigationEvent).doNotAskVisibility).isEqualTo(visibility)
        }
    }

    @Test
    fun `openPermissionActivity(Gone) should call ShowPermissionsActivity`() = runTest {
        // Act
        val visibility = View.GONE
        viewModel.openPermissionActivity(visibility)

        // Assert
        viewModel.navigationEvent.observeOnce {
            val expected = MainNavigation.ShowPermissionsActivity(visibility)

            val navigationEvent = it as MainNavigation.ShowPermissionsActivity
            Truth.assertThat(navigationEvent).isInstanceOf(expected::class.java)
            Truth.assertThat((navigationEvent).doNotAskVisibility).isEqualTo(visibility)
        }
    }
}