package com.liviolopez.citycrimes.ui.details

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.liviolopez.citycrimes.R
import com.liviolopez.citycrimes.databinding.FragmentDetailsBinding
import com.liviolopez.citycrimes.utils.Resource
import com.liviolopez.citycrimes.utils._log
import com.liviolopez.citycrimes.utils.extensions.showSnackBar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class DetailsFragment : Fragment(R.layout.fragment_details) {
    private val args: DetailsFragmentArgs by navArgs()

    private val viewModel: DetailsViewModel by activityViewModels()
    private lateinit var binding: FragmentDetailsBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentDetailsBinding.bind(view)

        args.persistentId._log()

        lifecycleScope.launchWhenResumed {
            viewModel.crime(args.persistentId).collect {
                it.first().let {
                    binding.apply {
                        tvCategory.text = it.category.name
                        tvDate.text = it.crime.month
                    }
                }
            }
        }

        lifecycleScope.launchWhenResumed {
            viewModel.crimeDetails(args.persistentId).collect { result ->
                when (result.status) {
                    Resource.Status.LOADING -> {}
                    Resource.Status.SUCCESS -> {
                        result.data?.let { details ->
                            binding.apply {
                                tvLatitude.text = details.crime.location?.latitude
                                tvLongitude.text = details.crime.location?.longitude
                            }
                        }
                    }
                    Resource.Status.ERROR -> {
                        binding.root.showSnackBar(requireContext().getString(R.string.error_msg))
                    }
                }
            }
        }
    }
}