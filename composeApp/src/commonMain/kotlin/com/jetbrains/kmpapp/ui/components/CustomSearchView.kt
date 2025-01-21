package com.jetbrains.kmpapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.jetbrains.kmpapp.ui.theme.LocalCustomColorsPalette
import martyboxapp.composeapp.generated.resources.Res
import martyboxapp.composeapp.generated.resources.search
import org.jetbrains.compose.resources.stringResource

@Composable
fun CustomSearchView(
    search: String,
    modifier: Modifier = Modifier,
    onValueChange: (String) -> Unit,
    onClearSearchQuery: () -> Unit
) {

    val isKeyboardVisible = remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusRequester = FocusRequester()//remember { FocusRequester() }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(LocalCustomColorsPalette.current.containerSecondary)

    ) {
        TextField(
            value = search,
            textStyle = MaterialTheme.typography.bodyLarge,
            onValueChange = onValueChange,
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = LocalCustomColorsPalette.current.containerSecondary,
                focusedContainerColor = LocalCustomColorsPalette.current.containerSecondary,
                unfocusedIndicatorColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                cursorColor = Color(0XFF888D91),
                unfocusedTextColor = LocalCustomColorsPalette.current.secondaryText,
                focusedTextColor = LocalCustomColorsPalette.current.primaryText,
                focusedLeadingIconColor = LocalCustomColorsPalette.current.secondaryIcon,
                unfocusedLeadingIconColor = LocalCustomColorsPalette.current.secondaryIcon,
                focusedTrailingIconColor = LocalCustomColorsPalette.current.secondaryIcon,
                unfocusedTrailingIconColor = LocalCustomColorsPalette.current.secondaryIcon,
            ),
            leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = null) },
            trailingIcon = {
                if (search.isNotEmpty()) {
                    IconButton(
                        onClick = {
                            onClearSearchQuery()
                            keyboardController?.hide()
                            isKeyboardVisible.value = false
                            focusManager.clearFocus()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = null
                        )
                    }
                }
            },
            placeholder = {
                Text(
                    text = stringResource(Res.string.search),
                    color = LocalCustomColorsPalette.current.secondaryText,
                    style = MaterialTheme.typography.bodyLarge
                )
            },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester)
                .clickable {
                    focusRequester.requestFocus()
                    keyboardController?.show()
                    isKeyboardVisible.value = true
                }
                .onFocusChanged { it -> isKeyboardVisible.value = it.isFocused },
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Search,
                keyboardType = KeyboardType.Text
            ),
            keyboardActions = KeyboardActions(
                onSearch = {
                    keyboardController?.hide()
                    isKeyboardVisible.value = false
                    focusManager.clearFocus()
                }
            )

        )
        if (!isKeyboardVisible.value) {
            focusManager.clearFocus()
        }
    }
}
