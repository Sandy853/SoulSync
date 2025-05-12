// ProfileViewModel.kt
package com.example.itworkshopproject.screens.home

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

val Context.dataStore by preferencesDataStore(name = "profile_prefs")

class ProfileViewModel(application: Application) : AndroidViewModel(application) {
    private val context = application.applicationContext

    private val _name = MutableStateFlow("")
    val name: StateFlow<String> = _name

    private val _location = MutableStateFlow("")
    val location: StateFlow<String> = _location

    private val _about = MutableStateFlow("")
    val about: StateFlow<String> = _about

    private val _selectedAvatar = MutableStateFlow(com.example.itworkshopproject.R.drawable.pic1)
    val selectedAvatar: StateFlow<Int> = _selectedAvatar

    private val _avatarUri = MutableStateFlow<Uri?>(null)
    val avatarUri: StateFlow<Uri?> = _avatarUri

    private val NAME_KEY = stringPreferencesKey("name")
    private val LOCATION_KEY = stringPreferencesKey("location")
    private val ABOUT_KEY = stringPreferencesKey("about")
    private val AVATAR_KEY = stringPreferencesKey("avatar")
    private val AVATAR_URI_KEY = stringPreferencesKey("avatar_uri")

    init {
        viewModelScope.launch {
            val prefs = context.dataStore.data.first()
            _name.value = prefs[NAME_KEY] ?: ""
            _location.value = prefs[LOCATION_KEY] ?: ""
            _about.value = prefs[ABOUT_KEY] ?: ""
            _selectedAvatar.value = prefs[AVATAR_KEY]?.toIntOrNull() ?: com.example.itworkshopproject.R.drawable.pic1
            _avatarUri.value = prefs[AVATAR_URI_KEY]?.let { Uri.parse(it) }
        }
    }

    fun updateName(newName: String) {
        _name.value = newName
        viewModelScope.launch {
            context.dataStore.edit { it[NAME_KEY] = newName }
        }
    }

    fun updateLocation(newLocation: String) {
        _location.value = newLocation
        viewModelScope.launch {
            context.dataStore.edit { it[LOCATION_KEY] = newLocation }
        }
    }

    fun updateAbout(newAbout: String) {
        _about.value = newAbout
        viewModelScope.launch {
            context.dataStore.edit { it[ABOUT_KEY] = newAbout }
        }
    }

    fun updateAvatar(newAvatar: Int) {
        _selectedAvatar.value = newAvatar
        _avatarUri.value = null
        viewModelScope.launch {
            context.dataStore.edit {
                it[AVATAR_KEY] = newAvatar.toString()
                it.remove(AVATAR_URI_KEY)
            }
        }
    }

    fun updateAvatarUri(uri: Uri?) {
        _avatarUri.value = uri
        viewModelScope.launch {
            context.dataStore.edit {
                if (uri != null) {
                    it[AVATAR_URI_KEY] = uri.toString()
                    it.remove(AVATAR_KEY)
                }
            }
        }
    }
}