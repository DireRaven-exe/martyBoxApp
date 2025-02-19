package com.jetbrains.kmpapp.ui.components.views.demo

import androidx.compose.runtime.Composable
import com.jetbrains.kmpapp.ui.components.content.CustomSliderWithButtons
import com.jetbrains.kmpapp.utils.DemoUiState
import com.jetbrains.kmpapp.utils.format
import martyboxapp.composeapp.generated.resources.Res
import martyboxapp.composeapp.generated.resources.pitch
import martyboxapp.composeapp.generated.resources.tempo
import martyboxapp.composeapp.generated.resources.volume
import org.jetbrains.compose.resources.stringResource
import kotlin.math.roundToInt

@Composable
fun DemoSlidersView(uiState: DemoUiState) {
    CustomSliderWithButtons(
        label = stringResource(Res.string.volume),
        value = uiState.volume,
        valueRange = 0f..1f,
        format = { "${(it * 100).roundToInt()}%" },
        onValueChange = {
        }
    )

    CustomSliderWithButtons(
        label = stringResource(Res.string.tempo),
        value = uiState.tempo,
        valueRange = 0.5f..1.5f,
        format = { it.format(2) + "x" },
        onValueChange = {
        }
    )

    CustomSliderWithButtons(
        label = stringResource(Res.string.pitch),
        value = uiState.pitch.toFloat(),
        valueRange = -7f..7f,
        steps = 13,
        stepSize = 1f, // Устанавливаем шаг 1, чтобы кнопки работали
        format = { it.roundToInt().toString() },
        onValueChange = {
            val rounded = it.roundToInt().toFloat() // Приводим к целому числу
        }
    )
}