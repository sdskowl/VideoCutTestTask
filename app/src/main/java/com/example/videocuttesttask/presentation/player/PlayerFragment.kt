package com.example.videocuttesttask.presentation.player

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.Fragment
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.navigation.fragment.navArgs
import com.example.videocuttesttask.R
import com.example.videocuttesttask.databinding.FragmentPlayerBinding
import com.example.videocuttesttask.presentation.viewmodels.SharedViewModel

class PlayerFragment : Fragment() {
    private val binding: FragmentPlayerBinding by lazy(LazyThreadSafetyMode.NONE) {
        FragmentPlayerBinding.inflate(layoutInflater)
    }
    private val vm: SharedViewModel by hiltNavGraphViewModels<SharedViewModel>(R.id.navigation)
    private val args: PlayerFragmentArgs by navArgs<PlayerFragmentArgs>()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return binding.root
    }


    private fun releasePlayer() {
        vm.release()
        binding.videoView.player = null
    }

    private fun pausePlayer() {
        vm.pause()
        binding.videoView.player = null
    }

    private fun initializePlayer() {
        binding.videoView.player = vm.player()
        vm.play(args.uri)
    }

    override fun onStart() {
        super.onStart()
        if (Build.VERSION.SDK_INT > 23) {
            initializePlayer()
        }
    }

    override fun onResume() {
        super.onResume()
        hideSystemUi()
        if (Build.VERSION.SDK_INT <= 23) {
            initializePlayer()
        }
    }

    override fun onStop() {
        super.onStop()
        if (Build.VERSION.SDK_INT > 23) {
            pausePlayer()
        }
    }

    override fun onPause() {
        super.onPause()
        if (Build.VERSION.SDK_INT <= 23) {
            pausePlayer()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        releasePlayer()
    }

    @SuppressLint("InlinedApi")
    private fun hideSystemUi() {
        val window = requireActivity().window
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window, binding.videoView).let { controller ->
            controller.hide(WindowInsetsCompat.Type.systemBars())
            controller.systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }
}