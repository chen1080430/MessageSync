package com.mason.messagesync.ui.home

import android.content.Context.MODE_PRIVATE
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.RadioGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceManager
import com.mason.messagesync.R
import com.mason.messagesync.databinding.FragmentHomeBinding
import com.mason.messagesync.util.LogUtil
import com.mason.messagesync.util.SharePreferenceUtil

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

//        homeViewModel.testCounter.observe(viewLifecycleOwner) {
//            updateTestCounter(it)
//        }

        binding.buttonTelegramSend.setOnClickListener {

            when (binding.radioButtonGroup.checkedRadioButtonId) {
                binding.radioButtonCheckStatus.id -> {
                    homeViewModel.checkToken()
                }

                binding.radioButtonSendMessage.id -> {
                    binding.editTextMessage.text?.let { message ->
                        message.isNotEmpty().takeIf { it }?.let {
                            homeViewModel.sendMessage(message.toString())
                        }
                    }
                }
            }
        }

        return binding.root
    }

    private fun saveToken(telegramToken: Editable) {
        SharePreferenceUtil.getTelegramToken(requireContext())?.let { oldToken ->
            if (oldToken != telegramToken.toString()) {
                SharePreferenceUtil.putTelegramToken(requireContext(), telegramToken.toString())
            }
        } ?: run {
            SharePreferenceUtil.putTelegramToken(requireContext(), telegramToken.toString())
        }
    }

    fun init() {
        setHasOptionsMenu(true)
//        readHttpCounter()
//        updateToken()
    }

    override fun onResume() {
        super.onResume()
//        updateToken()
    }

    private fun updateToken() {
        PreferenceManager.getDefaultSharedPreferences(requireContext()).apply {
            var token = this.getString(getString(R.string.telegram_token_key), "")
            var charID = this.getString(getString(R.string.telegram_chat_id_key), "")
            LogUtil.d(TAG, "init: charID: $charID, token: $token")
            if (!token.isNullOrEmpty() && !charID.isNullOrEmpty()) {
                homeViewModel.setTelegramTokenAndID(token, charID)
            }
        }
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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.notification_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.setting_item -> {
                findNavController().navigate(R.id.action_go_message_setting)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val TAG = "HomeFragment"
    }
}