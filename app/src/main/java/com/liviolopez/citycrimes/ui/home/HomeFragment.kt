package com.liviolopez.citycrimes.ui.home

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment.findNavController
import com.liviolopez.citycrimes.R
import com.liviolopez.citycrimes.data.local.model.CrimeInfo
import com.liviolopez.citycrimes.databinding.FragmentHomeBinding
import com.liviolopez.citycrimes.ui._components.loading
import com.liviolopez.citycrimes.ui._components.showEmptyMsg
import com.liviolopez.citycrimes.ui._components.showError
import com.liviolopez.citycrimes.ui._components.success
import com.liviolopez.citycrimes.utils.Resource
import com.liviolopez.citycrimes.utils.extensions.setGone
import com.liviolopez.citycrimes.utils.extensions.setOptions
import com.liviolopez.citycrimes.utils.extensions.setVisible
import com.liviolopez.citycrimes.utils.extensions.showSnackBar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch

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
                currentVal = viewModel.filterDate,
                show = { it.date },
                onClick = {
                    viewModel.filterDate = it.date
                    filterCrimes()
                })
        }

        lifecycleScope.launch {
            binding.dmCategory.setOptions(viewModel.categories,
                currentVal = viewModel.filterCategory?.name,
                show = { it.name },
                onClick = {
                    viewModel.filterCategory = it
                    filterCrimes()
                })
        }

        lifecycleScope.launch {
            binding.dmForce.setOptions(viewModel.forces,
                currentVal = viewModel.filterForce?.name,
                show = { it.name },
                onClick = {
                    viewModel.filterForce = it
                    filterCrimes()
                })
        }

        if(viewModel.filterLocation == null){
            viewModel.filterLocation = HomeViewModel.LocationFilter.All
            binding.rbAllLocations.isChecked = true
        }

        binding.rgLocations.setOnCheckedChangeListener { _, checkedId ->
            when(checkedId){
                R.id.rb_all_locations -> {
                    viewModel.filterLocation = HomeViewModel.LocationFilter.All
                }
                R.id.rb_current_location -> {
                    viewModel.filterLocation = HomeViewModel.LocationFilter.CURRENT
                }
            }

            filterCrimes()
        }
    }

    private fun filterCrimes(){
        crimeAdapter.submitList(emptyList())
        viewModel.filterCrimes()
    }

    private fun submitCrimesListAdapter(itemsList: List<CrimeInfo>) {
        crimeAdapter.submitList(itemsList) {
            binding.rvCrimeInfo.scrollToPosition(0)
        }
    }

    private fun setupCrimesAdapter() {
        lifecycleScope.launch {
            viewModel.crimesFiltered.debounce(300).collect { result ->
                when (result.status) {
                    Resource.Status.LOADING -> {
                        if (result.data.isNullOrEmpty()) {
                            binding.standbyView.loading
                        } else {
                            submitCrimesListAdapter(result.data)
                        }
                    }
                    Resource.Status.SUCCESS -> {
                        binding.standbyView.success

                        if (result.data.isNullOrEmpty()) {
                            binding.apply { standbyView showEmptyMsg rvCrimeInfo }
                        } else {
                            binding.rvCrimeInfo.setVisible()
                            submitCrimesListAdapter(result.data)
                        }
                    }
                    Resource.Status.ERROR -> {
                        Log.e(TAG, "Error: ${result.throwable?.localizedMessage}")

                        if (result.data.isNullOrEmpty()) {
                            binding.rvCrimeInfo.setGone()
                            binding.standbyView.showError = getString(
                                R.string.error_msg_param,
                                result.throwable?.localizedMessage
                            )
                        } else {
                            submitCrimesListAdapter(result.data)
                            binding.root.showSnackBar(getString(R.string.error_msg_updating))
                        }
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
