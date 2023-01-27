package com.example.videocuttesttask.presentation.library

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.example.videocuttesttask.R
import com.example.videocuttesttask.databinding.FragmentLibraryBinding
import com.example.videocuttesttask.features.models.ConverterState
import com.example.videocuttesttask.presentation.viewmodels.SharedViewModel
import kotlinx.coroutines.launch

class LibraryFragment : Fragment() {
    private val TAG = "LibraryFragment"
    private val binding: FragmentLibraryBinding by lazy(LazyThreadSafetyMode.NONE) {
        FragmentLibraryBinding.inflate(layoutInflater)
    }
    private val nav: NavController by lazy(LazyThreadSafetyMode.NONE) {
        findNavController()
    }
    private val vm: SharedViewModel by hiltNavGraphViewModels<SharedViewModel>(R.id.navigation)

    private val takeVideo = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) {
        Log.d(TAG, "video = ${it?.path}")

        it?.let { uri ->
            hideSelectButton()
            vm.convert(uri)
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.selectVideo.setOnClickListener {
            takeVideo.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.VideoOnly))
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                vm.convertState.collect { state ->
                    when (state) {
                        is ConverterState.Progress -> {
                            binding.progress.text = state.progress
                        }
                        is ConverterState.Error -> {
                            binding.progress.text = state.error
                            showSelectButton()
                        }
                        is ConverterState.Video -> {
                            val act =
                                LibraryFragmentDirections.actionLibraryFragmentToPlayerFragment(uri = state.uri)
                            nav.navigate(act)
                            showSelectButton()
                            clearState()
                        }
                        else -> {

                        }
                    }
                }
            }
        }
    }

    private fun clearState() {
        vm.clearState()
        binding.progress.text = null
    }

    private fun hideSelectButton() {
        binding.selectVideo.isVisible = false
    }

    private fun showSelectButton() {
        binding.selectVideo.isVisible = true
    }

    override fun onDestroy() {
        super.onDestroy()
        vm.cancelConverter()
    }

    override fun onResume() {
        super.onResume()
        showUiStatusBar()
    }

    private fun showUiStatusBar() {
        val window = requireActivity().window
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window, binding.root).let { controller ->
            controller.show(WindowInsetsCompat.Type.systemBars())
        }
    }

}