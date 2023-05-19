package com.mason.messagesync.ui.about

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.mason.messagesync.databinding.FragmentAboutBinding

class AboutFragment : Fragment() {

    private lateinit var webView: WebView
    private var _binding: FragmentAboutBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val notificationsViewModel =
            ViewModelProvider(this).get(AboutViewModel::class.java)

        _binding = FragmentAboutBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textNotifications
        notificationsViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }

        webView = binding.webView

//        webView.loadUrl("https://github.com/chen1080430/gitbook/blob/master/privacy-notice.md")
        webView.loadUrl("https://masons-organization-1.gitbook.io/untitled/privacy-notice")
        webView.settings.javaScriptEnabled = false
        webView.settings.builtInZoomControls = true
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}