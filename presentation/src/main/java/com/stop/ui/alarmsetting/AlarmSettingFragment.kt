package com.stop.ui.alarmsetting

import android.os.Bundle
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.stop.R
import com.stop.databinding.FragmentAlarmSettingBinding

class AlarmSettingFragment : Fragment() {
    private var _binding: FragmentAlarmSettingBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAlarmSettingBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        buttonClick()
        binding.numberPickerAlarmTime.minValue = 0
        binding.numberPickerAlarmTime.maxValue = 60
    }

    private fun buttonClick() {
        with(binding) {
            textViewRouteContent.setOnClickListener {
                if (textViewTransportContent.visibility == View.VISIBLE) {
                    setTransportViewGone()
                } else {
                    setTransportViewVisible()
                }
            }
        }
    }

    private fun setTransportViewGone() {
        with(binding) {
            TransitionManager.beginDelayedTransition(cardViewRoute, AutoTransition())
            textViewTransportContent.visibility = View.GONE
            textViewRouteContent.setCompoundDrawables(null, null, null, null)
            textViewRouteContent.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_baseline_keyboard_arrow_down_24, 0)
        }
    }

    private fun setTransportViewVisible() {
        with(binding) {
            TransitionManager.beginDelayedTransition(cardViewRoute, AutoTransition())
            textViewTransportContent.visibility = View.VISIBLE
            textViewRouteContent.setCompoundDrawables(null, null, null, null)
            textViewRouteContent.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_baseline_keyboard_arrow_up_24, 0)
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}