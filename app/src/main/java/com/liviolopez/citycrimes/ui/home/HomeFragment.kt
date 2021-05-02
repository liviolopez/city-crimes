package com.liviolopez.citycrimes.ui.home

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment.findNavController
import com.liviolopez.citycrimes.R
import com.liviolopez.citycrimes.databinding.FragmentHomeBinding
import com.liviolopez.citycrimes.ui._components.loading
import com.liviolopez.citycrimes.ui._components.showEmptyMsg
import com.liviolopez.citycrimes.ui._components.showError
import com.liviolopez.citycrimes.ui._components.success
import com.liviolopez.citycrimes.utils.Resource
import com.liviolopez.citycrimes.utils.extensions.setGone
import com.liviolopez.citycrimes.utils.extensions.setOptions
import com.liviolopez.citycrimes.utils.extensions.setVisible
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@FlowPreview
@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.fragment_home), CrimeAdapter.OnItemEventListener {
    private val TAG = "HomeFragment"

    private val viewModel: HomeViewModel by activityViewModels()
    private lateinit var binding: FragmentHomeBinding

    private lateinit var crimeAdapter: CrimeAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentHomeBinding.bind(view)

        setupFilterValues()

        crimeAdapter = CrimeAdapter(this)
        binding.rvCrimeInfo.adapter = crimeAdapter

        setupCrimesAdapter()
    }

    private fun setupFilterValues(){
        lifecycleScope.launch {
            binding.dmAvailability.setOptions(viewModel.availabilities,
                show = { it.date },
                currentValIf = { it.date == viewModel.filterDate.value },
                onClick = { viewModel.filterDate.value = it.date }
            )
        }

        lifecycleScope.launch {
            binding.dmCategory.setOptions(viewModel.categories,
                show = { it.name },
                currentValIf = { it.id == viewModel.filterCategory.value },
                onClick = { viewModel.filterCategory.value = it.id }
            )
        }

        lifecycleScope.launch {
            binding.dmForce.setOptions(viewModel.forces,
                show = { it.name },
                currentValIf = { it.id == viewModel.filterForce.value },
                onClick = { viewModel.filterForce.value = it.id }
            )
        }

        viewModel.filterLocation.value = HomeViewModel.LocationFilter.All
        binding.rbAllLocations.isChecked = true

        binding.rgLocations.setOnCheckedChangeListener { _, checkedId ->
            when(checkedId){
                R.id.rb_all_locations -> {
                    viewModel.filterLocation.value = HomeViewModel.LocationFilter.All
                }
                R.id.rb_close_my_location -> {
                    viewModel.filterLocation.value = HomeViewModel.LocationFilter.WAS_CLOSE
                }
            }
        }
    }

    private fun setupCrimesAdapter() {
        lifecycleScope.launch {
            viewModel.crimesFiltered.collect { result ->
                when (result.status) {
                    Resource.Status.LOADING -> {
                        binding.standbyView.loading
                        crimeAdapter.submitList(emptyList())
                    }
                    Resource.Status.SUCCESS -> {
                        binding.standbyView.success

                        if (result.data.isNullOrEmpty()) {
                            binding.apply { standbyView showEmptyMsg rvCrimeInfo }
                        } else {
                            binding.rvCrimeInfo.setVisible()
                            crimeAdapter.submitList(result.data)
                        }
                    }
                    Resource.Status.ERROR -> {
                        Log.e(TAG, "Error: ${result.throwable?.localizedMessage}")

                        binding.rvCrimeInfo.setGone()
                        binding.standbyView.showError = getString(R.string.error_msg_param, result.throwable?.localizedMessage)
                    }
                }
            }
        }
    }

    override fun onClickCrime(persistentId: String, view: View) {
        when(view.id) {
            R.id.root_crime_item -> {
                findNavController(this).navigate(
                    HomeFragmentDirections.actionHomeFragmentToDetailsFragment(persistentId = persistentId)
                )
            }
            R.id.btn_location -> {}
            else -> {}
        }
    }
}
