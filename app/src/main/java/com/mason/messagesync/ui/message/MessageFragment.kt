package com.mason.messagesync.ui.message

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresPermission
import androidx.core.app.ActivityCompat
import androidx.core.content.PermissionChecker.PermissionResult
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.mason.messagesync.model.Sms
import com.mason.messagesync.databinding.FragmentMessageBinding
import com.mason.messagesync.model.SmsAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.security.Permission

class MessageFragment : Fragment() {

    private lateinit var smsAdapter: SmsAdapter
    private var _binding: FragmentMessageBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private val smsList: MutableList<Sms> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val dashboardViewModel =
            ViewModelProvider(this).get(MessageViewModel::class.java)

        _binding = FragmentMessageBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        smsAdapter = SmsAdapter()
        binding.recyclerViewMessage.adapter = smsAdapter
        binding.recyclerViewMessage.layoutManager = LinearLayoutManager(requireContext())

        if (grantSmsPermission()) {
            lifecycleScope.launch{
                readAllSms()
            }
        }


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

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
                Log.d(Companion.TAG, "XXXXX> readAllSms: smsAdapter.itemCound = ${smsAdapter.itemCount}")
            }
        }
    }

    fun checkPermission(permission: String) = ActivityCompat.checkSelfPermission(
        requireActivity(),
        permission
    )
    private fun grantSmsPermission(): Boolean {
        // check sms permission
                var checkSelfPermission = checkPermission(Manifest.permission.READ_SMS)
                if (checkSelfPermission != PackageManager.PERMISSION_GRANTED) {
                    // request permission
                    ActivityCompat.requestPermissions(
                        requireActivity(),
                        arrayOf(Manifest.permission.READ_SMS),
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