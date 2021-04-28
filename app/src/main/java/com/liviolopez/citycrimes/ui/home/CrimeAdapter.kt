package com.liviolopez.citycrimes.ui.home

import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.liviolopez.citycrimes.R
import com.liviolopez.citycrimes.data.local.model.CrimeInfo
import com.liviolopez.citycrimes.databinding.CrimeItemBinding
import com.liviolopez.citycrimes.ui._components.BindingViewHolder
import com.liviolopez.citycrimes.ui._components.typed
import com.liviolopez.citycrimes.ui._components.viewHolderFrom
import kotlinx.coroutines.*

class CrimeAdapter(
    private val onCrimeEventListener: OnItemEventListener,
) : ListAdapter<CrimeInfo, BindingViewHolder<*>>(ItemComparator()) {

    interface OnItemEventListener {
        fun onClickCrime(persistentId: String, view: View)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BindingViewHolder<*> {
        return parent.viewHolderFrom(CrimeItemBinding::inflate)
    }

    override fun onBindViewHolder(holder: BindingViewHolder<*>, position: Int) {
        val item = getItem(position)
        holder.typed<CrimeItemBinding>().bind(item)
    }

    private fun onClick(crimeId: String, vararg views: View) {
        views.forEach { view ->
            view.setOnClickListener { onCrimeEventListener.onClickCrime(crimeId, view) }
        }
    }

    class ItemComparator : DiffUtil.ItemCallback<CrimeInfo>() {
        override fun areItemsTheSame(oldItem: CrimeInfo, newItem: CrimeInfo) = oldItem.crime.id == newItem.crime.id

        override fun areContentsTheSame(oldItem: CrimeInfo, newItem: CrimeInfo) = oldItem == newItem
    }

    @FlowPreview
    private fun BindingViewHolder<CrimeItemBinding>.bind(crimeInfo: CrimeInfo) {
        binding.apply {
            tvCategory.text = crimeInfo.category.name
            tvMonth.text = crimeInfo.crime.month
            tvContext.text = crimeInfo.crime.context

            if(crimeInfo.crime.locationLatitude == null || crimeInfo.crime.locationLongitude == null){
                btnLocation.setImageDrawable(ContextCompat.getDrawable(root.context, R.drawable.ic_no_location))
            } else {
                btnLocation.setImageDrawable(ContextCompat.getDrawable(root.context, R.drawable.ic_location))
                onClick(crimeInfo.crime.persistentId, btnLocation)
            }

            onClick(crimeInfo.crime.persistentId, root)
        }
    }
}