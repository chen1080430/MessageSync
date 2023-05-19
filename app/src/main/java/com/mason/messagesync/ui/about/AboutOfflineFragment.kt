package com.mason.messagesync.ui.about

import android.os.Bundle
import android.text.Html
import android.view.*
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.mason.messagesync.R
import com.mason.messagesync.databinding.FragmentAboutOfflineBinding

/**
 * A simple [Fragment] subclass.
 * Use the [AboutOfflineFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AboutOfflineFragment : Fragment() {

    private lateinit var _binding: FragmentAboutOfflineBinding
    private val binding get() = _binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAboutOfflineBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val htmlString = getString(R.string.privacy_police_content_1)
        val spannedText = Html.fromHtml(htmlString, Html.FROM_HTML_MODE_LEGACY)
        binding.textViewPrivacyPolicy.text = spannedText

        val htmlString2 = getString(R.string.privacy_police_content_2)
        val spannedText2 = Html.fromHtml(htmlString2, Html.FROM_HTML_MODE_LEGACY)

        binding.textViewPrivacyPolicy2.text = spannedText2
    }

/*
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.about_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.about_item -> {
                val action = AboutOfflineFragmentDirections.goAboutOnline()
                findNavController().navigate(action)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
*/
}