package com.mason.messagesync.ui.message

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresPermission
import androidx.core.app.ActivityCompat
import androidx.core.content.PermissionChecker.PermissionResult
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.mason.messagesync.BuildConfig
import com.mason.messagesync.model.Sms
import com.mason.messagesync.databinding.FragmentMessageBinding
import com.mason.messagesync.model.MessageViewModel
import com.mason.messagesync.model.MessageViewModelFactory
import com.mason.messagesync.model.SmsAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.security.Permission

class MessageFragment : Fragment() {

    private lateinit var smsAdapter: SmsAdapter
    private var _binding: FragmentMessageBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
//    private val smsList: MutableList<Sms> = mutableListOf()

//    private val messageViewModel: MessageViewModel by viewModels {
//        MessageViewModelFactory(requireActivity().application)
//    }

    private val messageViewModel by activityViewModels<MessageViewModel> {
        MessageViewModelFactory(requireActivity().application)
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentMessageBinding.inflate(inflater, container, false)
        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = messageViewModel
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        smsAdapter = SmsAdapter()
        binding.recyclerViewMessage.adapter = smsAdapter
        binding.recyclerViewMessage.layoutManager = LinearLayoutManager(requireContext())

        checkPermissionAndLoadSMS()

        messageViewModel.smsListLiveData.observe(viewLifecycleOwner) {
            smsAdapter.submitList(it)
        }

        binding.buttonReloadMessage.setOnClickListener {
            checkPermissionAndLoadSMS()
        }
    }

    private fun checkPermissionAndLoadSMS() {
        takeIf { grantSmsPermission() }?.let {
            messageViewModel.loadSMS()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    /*

    //    @RequiresPermission(Manifest.permission.READ_SMS)
        suspend fun readAllSms() {
            val cursor = requireActivity().contentResolver.query(
                Uri.parse("content://sms/"),
                null,
                null,
                null,
                null
            )
            cursor?.let {
                smsList.removeAll(smsList)
                while (it.moveToNext()) {
                    val id = it.getLong(it.getColumnIndexOrThrow("_id"))
                    val address = it.getString(it.getColumnIndexOrThrow("address")).toString()
                    val body = it.getString(it.getColumnIndexOrThrow("body")).toString()
                    val date = it.getString(it.getColumnIndexOrThrow("date")).toString()
                    val type = it.getInt(it.getColumnIndexOrThrow("type"))
                    val sms = Sms(id, address, body, date, type)
                    smsList.add(sms)
                }
                it.close()

                withContext(Dispatchers.Main) {
                    smsAdapter.submitList(smsList)
                    binding.progressBarMessage.visibility = View.GONE
                    Log.d(Companion.TAG, "XXXXX> readAllSms: smsAdapter.itemCound = ${smsAdapter.itemCount}")
                }
            }
        }
    */

    fun checkPermission(permission: String) = ActivityCompat.checkSelfPermission(
        requireActivity(),
        permission
    )

    private fun grantSmsPermission(): Boolean {
        if (BuildConfig.DEBUG) return false
//         TODO Release: check google privacy policy
        var checkReadSMSSelfPermission = checkPermission(Manifest.permission.READ_SMS)
        var checkReceiveSMSSelfPermission = checkPermission(Manifest.permission.RECEIVE_SMS)
        if (checkReadSMSSelfPermission != PackageManager.PERMISSION_GRANTED ||
            checkReceiveSMSSelfPermission != PackageManager.PERMISSION_GRANTED
        ) {
            // request permission
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.READ_SMS, Manifest.permission.RECEIVE_SMS),
                0
            )
        }
        return checkPermission(Manifest.permission.READ_SMS) == android.content.pm.PackageManager.PERMISSION_GRANTED

        /*
                val smsPermission = android.Manifest.permission.READ_SMS
                val grant = requireActivity().checkCallingOrSelfPermission(smsPermission)
                if (grant != android.content.pm.PackageManager.PERMISSION_GRANTED) {
                    // request permission
                    requestPermissions(arrayOf(smsPermission), 0)
                }
                return grant == android.content.pm.PackageManager.PERMISSION_GRANTED
        //        return true
         */
    }

    companion object {
        private const val TAG = "MessageFragment"
    }


}