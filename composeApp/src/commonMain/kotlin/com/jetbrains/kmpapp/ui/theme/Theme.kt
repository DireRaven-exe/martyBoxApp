package com.jetbrains.kmpapp.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

private val DarkColorPalette = darkColorScheme(
    primary = darkPrimaryColor,
    secondary = darkPrimaryColor,
    surface = darkSurface,
    onSurface = darkTextPrimary,
    background = darkSurface
)

private val LightColorPalette = lightColorScheme(
    primary = darkPrimaryColor,
    secondary = darkPrimaryColor,
    surface = contentColor,
    onSurface = darkSurface,
    background = contentColor
)

val CustomDarkColors = CustomColorsPalette(
    primaryBackground = Color(0xFF141718),
    primaryText = Color(0xFFF2F4F5),

    containerSecondary = Color(0xff232627),
    secondaryText = Color(0xffabacb8),

    selectedText = Color(0xff141718),
    tintColor = Color(0xFF485866),


    primaryIcon = Color(0xffc0c0c0),
    secondaryIcon = Color(0xffabacb8),
    selectedIcon = Color(0xfffd6246),
    unselectedIcon = Color(0xff454545),

    primaryButtonTextColor = Color(0xFF5EB4F6),
    secondaryButtonTextColor = Color(0xFF7993AA),

    buttonContainer = Color(0xff54c575),
    buttonContent = Color(0xffe5f4ea),

    cardInfoBackground = Color(0xff232627),
    cardCurrentSongBackground = Color(0xff932714),
    cardInfoTextPrimary = Color(0xff111111),

    checkBoxContainerNotSelected = Color(0xffeefdf7),
    checkBoxContainerSelected = Color(0xff252525),

    checkBoxContentNotSelected = Color(0xFF000000),
    checkBoxContentSelected = Color(0xffeefdf7),

    notificationBackground = Color(0xa6ff6347),
    notificationContent = Color(0xffeefdf7),
    defaultButtonContainer = Color(0xff454545),
    defaultButtonContent = Color(0xfffafbfb)
)

val CustomLightColors = CustomColorsPalette(
    primaryBackground = Color(0xfff5f6f8),
    primaryText = Color(0xff050505),

    containerSecondary = Color(0xfffafbfb),
    secondaryText = Color(0xffabacb8),

    selectedText = Color(0xfffafbfb),
    tintColor = Color(0xFF485866),

    primaryIcon = Color(0xff333333),
    secondaryIcon = Color(0xffc0c0c0),
    selectedIcon = Color(0xfffd6246),
    unselectedIcon = Color(0xffabacb8),

    primaryButtonTextColor = Color(0xFF5EB4F6),
    secondaryButtonTextColor = Color(0xFF7993AA),

    buttonContainer = Color(0xff54c575),
    buttonContent = Color(0xffe5f4ea),

    defaultButtonContainer = Color(0xffffffff),
    defaultButtonContent = Color(0xff101010),

    cardInfoBackground = Color(0xfff5f6f8),
    cardCurrentSongBackground = Color(0xff932714),
    cardInfoTextPrimary = Color(0xff111111),

    checkBoxContainerNotSelected = Color(0xFF000000),
    checkBoxContainerSelected = Color(0xff484848),

    checkBoxContentNotSelected = Color(0xffeefdf7),
    checkBoxContentSelected = Color(0xffd8d3d3),

    notificationBackground = Color(0x9e333333),
    notificationContent = Color(0xffeefdf7)
)

data class CustomColorsPalette(
    val primaryBackground: Color = Color.Unspecified,
    val primaryText: Color = Color.Unspecified,

    val containerSecondary: Color = Color.Unspecified,
    val secondaryText: Color = Color.Unspecified,

    val selectedText: Color = Color.Unspecified,
    val tintColor: Color = Color.Unspecified,

    val primaryIcon: Color = Color.Unspecified,
    val secondaryIcon: Color = Color.Unspecified,
    val selectedIcon: Color = Color.Unspecified,
    val unselectedIcon: Color = Color.Unspecified,

    val primaryButtonTextColor: Color = Color.Unspecified,
    val secondaryButtonTextColor: Color = Color.Unspecified,

    val buttonContainer: Color = Color.Unspecified,
    val buttonContent: Color = Color.Unspecified,

    val cardInfoBackground: Color = Color.Unspecified,
    val cardCurrentSongBackground: Color = Color.Unspecified,
    val cardInfoTextPrimary: Color = Color.Unspecified,

    val checkBoxContainerNotSelected: Color = Color.Unspecified,
    val checkBoxContainerSelected: Color = Color.Unspecified,

    val checkBoxContentNotSelected: Color = Color.Unspecified,
    val checkBoxContentSelected: Color = Color.Unspecified,

    val notificationBackground: Color = Color.Unspecified,
    val notificationContent: Color = Color.Unspecified,
    val defaultButtonContainer: Color,
    val defaultButtonContent: Color
)

val LocalCustomColorsPalette = staticCompositionLocalOf { CustomColorsPalette(
    defaultButtonContainer = Color(0xfffafbfb),
    defaultButtonContent = Color(0xff101010)
) }

@Composable
fun MartinBoxAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colors = if (!darkTheme) {
        LightColorPalette
    } else {
        DarkColorPalette
    }

    val customColorsPalette = if (!darkTheme) {
        CustomLightColors
    } else {
        CustomDarkColors
    }

    val typography = androidx.compose.material3.Typography(
        displayLarge = header1,
        headlineLarge = header2,
        headlineMedium = header3,
        displayMedium = header4,
        headlineSmall = header5,
        displaySmall = header6
    )

//    val view = LocalView.current
//    if (!view.isInEditMode) {
//        SideEffect {
//            val window = (view.context as Activity).window
//            window.statusBarColor = colorScheme.background.toArgb()
//            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
//        }
//    }
    CompositionLocalProvider(
        LocalCustomColorsPalette provides customColorsPalette,
    ) {
        MaterialTheme(
            colorScheme = colors,
            typography = typography,
            content = content
        )
    }
}