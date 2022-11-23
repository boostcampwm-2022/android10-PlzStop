package com.stop.ui.mission

import android.Manifest
import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.ContextWrapper
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.stop.R
import com.stop.databinding.FragmentMissionBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.InputStreamReader

@AndroidEntryPoint
class MissionFragment : Fragment() , TMapHandler{

    private var _binding: FragmentMissionBinding? = null
    private val binding: FragmentMissionBinding
        get() = _binding!!

    private val viewModel: MissionViewModel by viewModels()

    private lateinit var tMap: TMap

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMissionBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setDataBinding()
        initViewModel()
        initTMap()
        setObserve()
    }

    override fun onDestroyView() {
        _binding = null

        super.onDestroyView()
    }

    override fun alertTMapReady() {
        mimicUserMove()
    }

    private fun mimicUserMove() {
        val lines = readFromAssets()

        CoroutineScope(Dispatchers.IO).launch {
            lines.forEach { line ->
                val (longitude, latitude) = line.split(",")
                tMap.moveLocation(longitude, latitude)
                delay(500)
            }
        }
    }

    private fun readFromAssets(): List<String> {
        val reader =
            BufferedReader(InputStreamReader(requireContext().assets.open(FAKE_USER_FILE_PATH)))
        val lines = arrayListOf<String>()
        var line = reader.readLine()
        while (line != null) {
            lines.add(line)
            line = reader.readLine()
        }
        reader.close()
        return lines
    }

    private fun setDataBinding() {
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
    }

    private fun initTMap() {
        tMap = TMap((requireContext() as ContextWrapper).baseContext, this)
        tMap.init()

        binding.constraintLayoutContainer.addView(tMap.getTMapView())
    }

    private fun initViewModel() {
        viewModel.setDestination(DESTINATION)
        viewModel.countDownWith(LEFT_TIME)
    }

    private fun setObserve() {
        val shortAnimationDuration =
            resources.getInteger(android.R.integer.config_shortAnimTime)

        viewModel.timeIncreased.observe(viewLifecycleOwner) {
            val sign = if (it > 0) {
                PLUS
            } else {
                MINUS
            }
            binding.textViewChangedTime.text =
                resources.getString(R.string.number_format).format(sign, it)
            binding.textViewChangedTime.apply {
                alpha = 0f
                visibility = View.VISIBLE
                animate().alpha(1f)
                    .setDuration(shortAnimationDuration.toLong())
                    .setListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator?) {
                            animate().alpha(0f)
                                .setDuration(shortAnimationDuration.toLong())
                                .setListener(object : AnimatorListenerAdapter() {
                                    override fun onAnimationEnd(animation: Animator?) {
                                        binding.textViewChangedTime.visibility = View.GONE
                                    }
                                })
                        }
                    })
            }
        }
    }

    companion object {
        private const val DESTINATION = "구로3동현대아파트"
        private const val PLUS = "+"
        private const val MINUS = ""
        private const val LEFT_TIME = 60

        private const val FAKE_USER_FILE_PATH = "fake_user_path"
    }
}