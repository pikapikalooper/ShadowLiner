package org.shadowliner.project

import androidx.compose.animation.animateColor
import androidx.compose.animation.core.EaseInOutCubic
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Share
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.browser.window
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// ============= Theme Constants =============

object AppColors {
    // Water-inspired palette
    val CyanLight = Color(0xFF4FACFE)
    val CyanDark = Color(0xFF00F2FE)
    val TealLight = Color(0xFF43E97B)
    val TealDark = Color(0xFF38F9D7)
    val PurpleLight = Color(0xFF667EEA)
    val PurpleDark = Color(0xFF764BA2)

    // Primary actions
    val Primary = Color(0xFF14B8A6) // Teal-600
    val PrimaryDisabled = Color(0xFF14B8A6)

    // Success states
    val Success = Color(0xFF10B981)
    val SuccessDark = Color(0xFF059669)

    // Error states
    val Error = Color(0xFFEF4444)
    val ErrorDark = Color(0xFFDC2626)

    // Text colors
    val TextPrimary = Color(0xFF0F172A)
    val TextSecondary = Color(0xFF475569)
    val TextTertiary = Color(0xFF64748B)
    val TextLabel = Color(0xFF334155)
    val TextPlaceholder = Color(0xFF94A3B8)

    // Borders
    val BorderFocused = Color(0xFF06B6D4)
    val BorderUnfocused = Color(0xFF94A3B8)

    val BackgroundInput = Color.White
}

object AppDimens {
    val PaddingLarge = 48.dp
    val PaddingMedium = 32.dp
    val PaddingNormal = 24.dp
    val PaddingSmall = 16.dp
    val PaddingTiny = 8.dp
    val PaddingMicro = 4.dp

    val CornerRadiusLarge = 12.dp
    val CornerRadiusMedium = 8.dp

    val ButtonHeight = 52.dp
    val IconSize = 20.dp

    val ElevationDefault = 2.dp
    val ElevationPressed = 4.dp
    val ElevationNone = 0.dp

    val BlurBackground = 30.dp
}

object AppStrings {
    const val APP_TITLE = "ShadowLiner"
    const val APP_SUBTITLE = "Outline VPN Key to Shadowsocks JSON Config Converter"
    const val INPUT_LABEL = "Paste your Outline VPN key here"
    const val INPUT_PLACEHOLDER = "ss://..."
    const val BUTTON_CONVERT = "Convert to JSON"
    const val BUTTON_CONVERTING = "Converting..."
    const val SUCCESS_MESSAGE = "JSON Configuration Generated"
    const val COPIED_MESSAGE = "Copied to clipboard!"
    const val ERROR_PREFIX = "Error: "
    const val ERROR_INVALID_FORMAT = "Invalid key format"
}

object AnimationDurations {
    const val GRADIENT_SHORT = 8000
    const val GRADIENT_MEDIUM = 9000
    const val GRADIENT_LONG = 10000
    const val COPY_FEEDBACK_DELAY = 2000L
    const val CONVERSION_DELAY = 300L
}

// ============= Utility Functions =============

fun decodeBase64(base64: String): String = window.atob(base64)

fun convertOutlineKeyToJson(outlineVpnKey: String): String {
    return try {
        val cleanedKey = outlineVpnKey.removeSuffix("/?outline=1").removePrefix("ss://")
        val splitIndex = cleanedKey.indexOf('@')

        if (splitIndex == -1) {
            throw IllegalArgumentException(AppStrings.ERROR_INVALID_FORMAT)
        }

        val base64Part = cleanedKey.substring(0, splitIndex)
        val decodedPart = decodeBase64(base64Part)
        val (cipher, password) = decodedPart.split(':')

        val hostPort = cleanedKey.substring(splitIndex + 1).split(':')
        val host = hostPort[0]
        val port = hostPort[1].toInt()

        buildJsonConfig(host, port, password, cipher)
    } catch (e: Exception) {
        "${AppStrings.ERROR_PREFIX}${e.message}"
    }
}

private fun buildJsonConfig(host: String, port: Int, password: String, cipher: String): String {
    return """
        {
            "server": "$host",
            "server_port": $port,
            "password": "$password",
            "method": "$cipher",
            "local_address": "127.0.0.1",
            "local_port": 1080,
            "timeout": 300,
            "mode": "tcp_and_udp",
            "fast_open": false
        }
    """.trimIndent()
}

// ============= Composable Components =============

@Composable
fun AnimatedWaterBackground() {
    val infiniteTransition = rememberInfiniteTransition()

    val color1 by infiniteTransition.animateColor(
        initialValue = AppColors.CyanLight,
        targetValue = AppColors.CyanDark,
        animationSpec = infiniteRepeatable(
            animation = tween(AnimationDurations.GRADIENT_SHORT, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        )
    )

    val color2 by infiniteTransition.animateColor(
        initialValue = AppColors.TealLight,
        targetValue = AppColors.TealDark,
        animationSpec = infiniteRepeatable(
            animation = tween(AnimationDurations.GRADIENT_LONG, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        )
    )

    val color3 by infiniteTransition.animateColor(
        initialValue = AppColors.PurpleLight,
        targetValue = AppColors.PurpleDark,
        animationSpec = infiniteRepeatable(
            animation = tween(AnimationDurations.GRADIENT_MEDIUM, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        color1.copy(alpha = 0.6f),
                        color2.copy(alpha = 0.5f),
                        color3.copy(alpha = 0.4f)
                    ),
                    start = Offset(0f, 0f),
                    end = Offset(1000f, 1000f)
                )
            )
            .blur(AppDimens.BlurBackground)
    )
}

@Composable
fun AppTitle(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = AppStrings.APP_TITLE,
            fontSize = 48.sp,
            fontWeight = FontWeight.Bold,
            color = AppColors.TextPrimary,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = AppDimens.PaddingTiny)
        )

        Text(
            text = AppStrings.APP_SUBTITLE,
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            color = AppColors.TextSecondary,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun InputSection(
    value: String,
    onValueChange: (String) -> Unit,
    isError: Boolean,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = AppStrings.INPUT_LABEL,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = AppColors.TextLabel,
            modifier = Modifier.padding(bottom = AppDimens.PaddingTiny)
        )

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = {
                Text(
                    text = AppStrings.INPUT_PLACEHOLDER,
                    color = AppColors.TextPlaceholder
                )
            },
            modifier = Modifier.fillMaxWidth(),
            isError = isError,
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = AppColors.BorderFocused.copy(alpha = 0.6f),
                unfocusedBorderColor = AppColors.BorderUnfocused.copy(alpha = 0.4f),
                errorBorderColor = AppColors.Error.copy(alpha = 0.6f),
                textColor = AppColors.TextPrimary,
                backgroundColor = AppColors.BackgroundInput.copy(alpha = 0.3f),
                cursorColor = AppColors.BorderFocused
            ),
            shape = RoundedCornerShape(AppDimens.CornerRadiusLarge)
        )
    }
}

@Composable
fun ConvertButton(
    onClick: () -> Unit,
    enabled: Boolean,
    isLoading: Boolean,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(AppDimens.ButtonHeight),
        colors = ButtonDefaults.buttonColors(
            backgroundColor = AppColors.Primary,
            contentColor = Color.White,
            disabledBackgroundColor = AppColors.PrimaryDisabled.copy(alpha = 0.6f),
            disabledContentColor = Color.White.copy(alpha = 0.7f)
        ),
        shape = RoundedCornerShape(AppDimens.CornerRadiusLarge),
        enabled = enabled,
        elevation = ButtonDefaults.elevation(
            defaultElevation = AppDimens.ElevationDefault,
            pressedElevation = AppDimens.ElevationPressed,
            disabledElevation = AppDimens.ElevationNone,
            hoveredElevation = AppDimens.ElevationPressed
        )
    ) {
        Text(
            text = if (isLoading) AppStrings.BUTTON_CONVERTING else AppStrings.BUTTON_CONVERT,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
fun SuccessSection(
    jsonOutput: String,
    isCopied: Boolean,
    onCopyClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        // Success indicator
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = AppDimens.PaddingSmall)
        ) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Success",
                tint = AppColors.Success,
                modifier = Modifier
                    .size(AppDimens.IconSize)
                    .padding(end = 6.dp)
            )
            Text(
                text = AppStrings.SUCCESS_MESSAGE,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = AppColors.SuccessDark
            )
        }

        // Output box with copy button
        Box {
            OutlinedTextField(
                value = jsonOutput,
                onValueChange = {},
                modifier = Modifier.fillMaxWidth(),
                readOnly = true,
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = AppColors.Success.copy(alpha = 0.4f),
                    unfocusedBorderColor = AppColors.Success.copy(alpha = 0.3f),
                    textColor = AppColors.TextPrimary,
                    backgroundColor = AppColors.Success.copy(alpha = 0.05f)
                ),
                shape = RoundedCornerShape(AppDimens.CornerRadiusLarge),
                maxLines = 10
            )

            IconButton(
                onClick = onCopyClick,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(AppDimens.PaddingMicro)
            ) {
                Icon(
                    imageVector = if (isCopied) Icons.Default.Check else Icons.Default.Share,
                    contentDescription = if (isCopied) "Copied!" else "Copy",
                    tint = if (isCopied) AppColors.Success else AppColors.TextTertiary,
                    modifier = Modifier.size(AppDimens.IconSize)
                )
            }
        }

        // Copied feedback
        if (isCopied) {
            Text(
                text = AppStrings.COPIED_MESSAGE,
                fontSize = 12.sp,
                color = AppColors.SuccessDark,
                modifier = Modifier.padding(top = AppDimens.PaddingTiny)
            )
        }
    }
}

@Composable
fun ErrorMessage(
    message: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = message.removePrefix(AppStrings.ERROR_PREFIX),
        color = AppColors.ErrorDark,
        fontWeight = FontWeight.SemiBold,
        fontSize = 14.sp,
        textAlign = TextAlign.Center,
        modifier = modifier
            .background(
                color = AppColors.Error.copy(alpha = 0.1f),
                shape = RoundedCornerShape(AppDimens.CornerRadiusMedium)
            )
            .padding(horizontal = AppDimens.PaddingSmall, vertical = AppDimens.PaddingSmall)
    )
}

// ============= Main App =============

@Composable
fun App() {
    var outlineKey by remember { mutableStateOf("") }
    var jsonOutput by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var isCopied by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(isLoading) {
        if (isLoading) {
            delay(AnimationDurations.CONVERSION_DELAY)
            jsonOutput = convertOutlineKeyToJson(outlineKey)
            errorMessage = if (jsonOutput.startsWith(AppStrings.ERROR_PREFIX)) jsonOutput else null
            isLoading = false
        }
    }

    MaterialTheme {
        Box(modifier = Modifier.fillMaxSize()) {
            AnimatedWaterBackground()

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(AppDimens.PaddingMedium),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                AppTitle(
                    modifier = Modifier.padding(bottom = AppDimens.PaddingLarge)
                )

                InputSection(
                    value = outlineKey,
                    onValueChange = {
                        outlineKey = it
                        errorMessage = null
                    },
                    isError = errorMessage != null,
                    modifier = Modifier
                        .fillMaxWidth(0.7f)
                        .padding(bottom = AppDimens.PaddingNormal)
                )

                ConvertButton(
                    onClick = {
                        if (!isLoading && outlineKey.isNotEmpty()) {
                            isLoading = true
                        }
                    },
                    enabled = !isLoading && outlineKey.isNotEmpty(),
                    isLoading = isLoading,
                    modifier = Modifier.fillMaxWidth(0.7f)
                )

                if (jsonOutput.isNotEmpty() && !jsonOutput.startsWith(AppStrings.ERROR_PREFIX)) {
                    SuccessSection(
                        jsonOutput = jsonOutput,
                        isCopied = isCopied,
                        onCopyClick = {
                            window.navigator.clipboard.writeText(jsonOutput)
                            isCopied = true
                            coroutineScope.launch {
                                delay(AnimationDurations.COPY_FEEDBACK_DELAY)
                                isCopied = false
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth(0.7f)
                            .padding(top = AppDimens.PaddingMedium)
                    )
                }

                errorMessage?.let { error ->
                    if (error.startsWith(AppStrings.ERROR_PREFIX)) {
                        ErrorMessage(
                            message = error,
                            modifier = Modifier.padding(top = AppDimens.PaddingNormal)
                        )
                    }
                }
            }
        }
    }
}
