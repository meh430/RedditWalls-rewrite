package com.example.redditwalls.fragments


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.redditwalls.datasources.RWApi
import com.example.redditwalls.misc.RadioDialog
import com.example.redditwalls.misc.fromId
import com.example.redditwalls.repositories.SettingsItem
import com.example.redditwalls.repositories.Theme
import com.example.redditwalls.viewmodels.SettingsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingsComposeFragment : Fragment() {

    private val settingsViewModel: SettingsViewModel by viewModels()
    private val darkColors = darkColors(
        primary = Color.Red,
        primaryVariant = Color.Red,
        secondary = Color.Red,
        secondaryVariant = Color.Red
    )
    private val lightColors = lightColors(
        primary = Color.Red,
        primaryVariant = Color.Red,
        secondary = Color.Red,
        secondaryVariant = Color.Red
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            // Dispose of the Composition when the view's LifecycleOwner is destroyed
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            val sidesPadding = 14.dp
            setContent {
                MaterialTheme(if (isSystemInDarkTheme()) darkColors else lightColors) {
                    Column(modifier = Modifier.padding(horizontal = sidesPadding)) {
                        OptionSelector(
                            "Select Theme",
                            settingsViewModel.getTheme(),
                            Theme.values()
                        ) {
                            settingsViewModel.setTheme(it)
                            AppCompatDelegate.setDefaultNightMode(it.mode)
                        }
                        Spacer(modifier = Modifier.height(sidesPadding))
                        OptionSelector(
                            dialogTitle = "Select Default Sort",
                            currentSelection = settingsViewModel.getDefaultSort(),
                            options = RWApi.Sort.values()
                        ) {
                            settingsViewModel.setDefaultSort(it)
                        }
                        Toggles()
                    }

                }
            }
        }
    }

    @Composable
    fun <T : SettingsItem> OptionSelector(
        dialogTitle: String,
        currentSelection: T,
        options: Array<T>,
        onSelect: (T) -> Unit
    ) {
        var selected by rememberSaveable { mutableStateOf(currentSelection) }
        val modifier = Modifier
            .clickable {
                RadioDialog(
                    requireContext(),
                    dialogTitle,
                    options,
                    selected.id
                ).show {
                    options
                        .fromId(it, options[0])
                        .also { o ->
                            selected = o
                            onSelect(o)
                        }
                }
            }
            .fillMaxWidth()
        Column(modifier = modifier) {
            DarkText(dialogTitle, style = MaterialTheme.typography.subtitle1)
            DarkText(
                selected.displayText,
                style = MaterialTheme.typography.caption,
                fontWeight = FontWeight.Light
            )

        }
    }

    @Composable
    fun DarkText(
        text: String,
        modifier: Modifier = Modifier,
        fontSize: TextUnit = TextUnit.Unspecified,
        fontWeight: FontWeight? = null,
        style: TextStyle = LocalTextStyle.current,
        darkTheme: Boolean = isSystemInDarkTheme()
    ) {
        Text(
            text,
            modifier = modifier,
            fontSize = fontSize,
            fontWeight = fontWeight,
            style = style,
            color = getTextColor(darkTheme)
        )
    }

    @Composable
    fun Toggles() {
        TextSwitch()
        TextSwitch()
        TextSwitch()
    }

    @Composable
    fun TextSwitch(
        text: String = "",
        checked: Boolean = true,
        onChange: ((Boolean) -> Unit)? = null
    ) {
        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
            DarkText("This is a setting")
            Switch(checked = true, onCheckedChange = null)
        }
    }

    private fun getTextColor(darkTheme: Boolean) = if (darkTheme) Color.White else Color.Black
}