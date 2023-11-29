package com.hd.clean_arch.base

import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.viewbinding.ViewBinding
import javax.inject.Inject

abstract class ViewBindingActivity<VB : ViewBinding> : AppCompatActivity() {

    @Inject
    protected lateinit var mViewModelProvider: ViewModelProvider.Factory

    private var _binding: VB? = null
    abstract val bindingInflater: (LayoutInflater) -> VB

    protected val binding: VB
        get() = requireNotNull(_binding) { "ViewBinding is not initialized" }

    override fun onCreate(savedInstanceState: Bundle?) {
        initializeComponents()
        super.onCreate(savedInstanceState)

        // Guard against multiple initializations
        if (_binding != null) {
            throw IllegalStateException("ViewBinding has already been initialized")
        }

        _binding = bindingInflater.invoke(layoutInflater)
        setContentView(_binding?.root)
        setupClickListeners()
        observeViewModel()
        collectFlows()
        setupUIElements()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    open fun initializeComponents() {}
    open fun setupUIElements() {}
    open fun setupClickListeners() {}
    open fun observeViewModel() {}
    open fun collectFlows() {}
}

