package com.example.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "kartban_settings")

enum class AppTheme(val title: String) {
    LIGHT("روشن"),
    DARK("تاریک"),
    SYSTEM("سیستم")
}

data class UserSettings(
    val appTheme: AppTheme = AppTheme.SYSTEM,
    val defaultSortOption: PersonSortOption = PersonSortOption.PINNED_FIRST,
    val defaultGroupFilter: CardGroupFilter = CardGroupFilter.ALL,
    val showNotesInList: Boolean = true,
    val forceDefaultCardTop: Boolean = true
)

class SettingsRepository(private val context: Context) {

    object Keys {
        val APP_THEME = stringPreferencesKey("app_theme")
        val DEFAULT_SORT = stringPreferencesKey("default_sort")
        val DEFAULT_FILTER = stringPreferencesKey("default_filter")
        val SHOW_NOTES = booleanPreferencesKey("show_notes_in_list")
        val FORCE_DEFAULT_TOP = booleanPreferencesKey("force_default_card_top")
    }

    val settingsFlow: Flow<UserSettings> = context.dataStore.data.map { prefs ->
        val themeStr = prefs[Keys.APP_THEME] ?: AppTheme.SYSTEM.name
        val theme = runCatching { AppTheme.valueOf(themeStr) }.getOrDefault(AppTheme.SYSTEM)

        val sortStr = prefs[Keys.DEFAULT_SORT] ?: PersonSortOption.PINNED_FIRST.name
        val sort = runCatching { PersonSortOption.valueOf(sortStr) }.getOrDefault(PersonSortOption.PINNED_FIRST)

        val filterStr = prefs[Keys.DEFAULT_FILTER] ?: CardGroupFilter.ALL.name
        val filter = runCatching { CardGroupFilter.valueOf(filterStr) }.getOrDefault(CardGroupFilter.ALL)

        val showNotes = prefs[Keys.SHOW_NOTES] ?: true
        val forceDefaultTop = prefs[Keys.FORCE_DEFAULT_TOP] ?: true

        UserSettings(
            appTheme = theme,
            defaultSortOption = sort,
            defaultGroupFilter = filter,
            showNotesInList = showNotes,
            forceDefaultCardTop = forceDefaultTop
        )
    }

    suspend fun setAppTheme(theme: AppTheme) {
        context.dataStore.edit { prefs ->
            prefs[Keys.APP_THEME] = theme.name
        }
    }

    suspend fun setDefaultSortOption(sortOption: PersonSortOption) {
        context.dataStore.edit { prefs ->
            prefs[Keys.DEFAULT_SORT] = sortOption.name
        }
    }

    suspend fun setDefaultGroupFilter(filter: CardGroupFilter) {
        context.dataStore.edit { prefs ->
            prefs[Keys.DEFAULT_FILTER] = filter.name
        }
    }

    suspend fun setShowNotesInList(show: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[Keys.SHOW_NOTES] = show
        }
    }

    suspend fun setForceDefaultCardTop(force: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[Keys.FORCE_DEFAULT_TOP] = force
        }
    }
}
