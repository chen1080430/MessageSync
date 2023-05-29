package com.mason.messagesync.ui

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import com.mason.messagesync.R

class MessageSettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
    }
}