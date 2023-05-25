package com.mason.messagesync.ui.home

import android.content.Context.MODE_PRIVATE
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.mason.messagesync.R
import com.mason.messagesync.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private val homeViewModel by activityViewModels<HomeViewModel>()
    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = homeViewModel
            homeFragment = this@HomeFragment
        }

        init()

        homeViewModel.testCounter.observe(viewLifecycleOwner) {
            updateTestCounter(it)
        }

        binding.buttonTelegramSend.setOnClickListener {
            binding.editTextSendTelegram.text?.let { text ->

                text.isNotEmpty().takeIf { it }?.let {
                    Log.d(Companion.TAG, "XXXXX> onCreateView: text: $text")
//                    homeViewModel.sendMessage(text.toString())
                    when (binding.radioButtonGroup.checkedRadioButtonId) {
                        binding.radioButtonCheckStatus.id -> homeViewModel.checkToken()
                        binding.radioButtonSendMessage.id -> homeViewModel.sendMessage(text.toString())
                    }
//                    homeViewModel.checkToken()
//                    homeViewModel.sendMessageByOkhttp3(text.toString())
//                    homeViewModel.sendLineByOkhttp3(text.toString())
                }
            }
        }

        return binding.root
    }

    fun init() {
        readHttpCounter()
    }

    fun onRadioButtonClicked(group: RadioGroup, checkedId: Int) {
        when (group.checkedRadioButtonId) {
            binding.radioButtonCheckStatus.id -> {
                binding.buttonTelegramSend.text = getString(R.string.check_token)
            }

            binding.radioButtonSendMessage.id -> {
                binding.buttonTelegramSend.text = getString(R.string.send_message)
            }
        }

    }

    private fun readHttpCounter() {
        homeViewModel._testCounter.value =
            requireActivity().getSharedPreferences("test_counter", MODE_PRIVATE)
                .getInt("http_counter", 0)
    }

    fun updateTestCounter(counter: Int) {
        requireActivity().getSharedPreferences("test_counter", MODE_PRIVATE).edit()
            .putInt("http_counter", counter).apply()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val TAG = "HomeFragment"
    }
}