package com.hd.clean_arch.ui.permissions

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hd.clean_arch.databinding.ItemPremissionBinding
import com.hd.presentation.permissions.mapper.PermissionUI

class PermissionsAdapter(private val listener: PermissionToggleListener) :
    RecyclerView.Adapter<PermissionsAdapter.PermissionViewHolder>() {

    interface PermissionToggleListener {
        fun onPermissionToggled(
            permissionType: PermissionUI
        )
    }

    private var permissionsList: List<PermissionUI> =
        emptyList()

    fun submitPermissionsList(permissions: List<PermissionUI>) {
        permissionsList = permissions
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PermissionViewHolder {
        val binding =
            ItemPremissionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PermissionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PermissionViewHolder, position: Int) {
        holder.bind(permissionsList[position])
    }

    override fun getItemCount(): Int = permissionsList.size

    inner class PermissionViewHolder(private val binding: ItemPremissionBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(permission: PermissionUI) {
            val context = binding.root.context

            binding.apply {
                txtPermission.text = context.getString(permission.permissionType.displayName())
                txtDescription.text =
                    context.getString(permission.permissionType.displayDescription())
                togglePermission.isChecked = permission.granted
                togglePermission.setOnClickListener {
                    listener.onPermissionToggled(permission)
                }
            }
        }
    }
}