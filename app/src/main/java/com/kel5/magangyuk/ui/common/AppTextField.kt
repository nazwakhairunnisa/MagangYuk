package com.kel5.magangyuk.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.kel5.magangyuk.ui.theme.ActionRed
import com.kel5.magangyuk.ui.theme.MagangYukTheme
import com.kel5.magangyuk.ui.theme.Navy
import com.kel5.magangyuk.ui.theme.StatusGrayText
import com.kel5.magangyuk.ui.theme.White

private val FieldBorder = Color(0xFFC9C9C9)

// Field dengan label di atas, pesan error merah di bawah, dan (opsional) ikon mata untuk password.
// error = null -> tampilan normal. error = "Wajib diisi" -> border merah + teks merah.
@Composable
fun AppTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    error: String? = null,
    isPassword: Boolean = false,
    keyboardType: KeyboardType = KeyboardType.Text,
    imeAction: ImeAction = ImeAction.Next,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    singleLine: Boolean = true,          // false untuk field banyak baris
    minLines: Int = 1,                   // tinggi minimal saat multiline
    leadingIcon: ImageVector? = null     // BARU: ikon di dalam field (telepon, email, dst)
) {
    // State lokal khusus tampilan: apakah password sedang terlihat.
    // Disimpan di sini (bukan di ViewModel) karena tidak penting untuk logika bisnis.
    var passwordVisible by remember { mutableStateOf(false) }

    // Dipakai untuk tahu field sedang fokus atau tidak (border jadi navy saat fokus)
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()

    val shape = RoundedCornerShape(16.dp)
    val borderColor = when {
        error != null -> ActionRed
        isFocused -> Navy
        else -> FieldBorder
    }

    Column(modifier = modifier) {
        Text(
            text = label,
            style = MaterialTheme.typography.titleSmall,
            color = Navy
        )

        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .padding(top = 8.dp)
                .fillMaxWidth(),
            singleLine = singleLine,
            minLines = minLines,
            interactionSource = interactionSource,
            textStyle = MaterialTheme.typography.bodyLarge.copy(color = Navy),
            cursorBrush = SolidColor(Navy),
            // Password ditampilkan sebagai titik-titik kalau belum diminta terlihat
            visualTransformation = if (isPassword && !passwordVisible)
                PasswordVisualTransformation() else VisualTransformation.None,
            keyboardOptions = KeyboardOptions(
                keyboardType = if (isPassword) KeyboardType.Password else keyboardType,
                imeAction = imeAction
            ),
            keyboardActions = keyboardActions,
            // decorationBox = "bingkai" di sekeliling area ketik. Di sinilah kita gambar
            // latar putih, border, placeholder, dan ikon mata.
            decorationBox = { innerTextField ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 48.dp)
                        .background(White, shape)
                        .border(1.dp, borderColor, shape)
                        .padding(
                            start = 16.dp,
                            end = if (isPassword) 4.dp else 16.dp,
                            top = if (singleLine) 0.dp else 12.dp,
                            bottom = if (singleLine) 0.dp else 12.dp
                        ),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    if (leadingIcon != null) {
                        Icon(
                            imageVector = leadingIcon,
                            contentDescription = null,
                            tint = StatusGrayText,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(Modifier.width(12.dp))
                    }
                    Box(modifier = Modifier.weight(1f)) {
                        if (value.isEmpty()) {
                            Text(
                                text = placeholder,
                                style = MaterialTheme.typography.bodyLarge,
                                color = StatusGrayText,
                                maxLines = 1
                            )
                        }
                        innerTextField()   // area ketik asli
                    }
                    if (isPassword) {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                imageVector = if (passwordVisible) Icons.Filled.Visibility
                                else Icons.Filled.VisibilityOff,
                                contentDescription = if (passwordVisible)
                                    "Sembunyikan password" else "Tampilkan password",
                                tint = StatusGrayText
                            )
                        }
                    }
                }
            }
        )

        if (error != null) {
            Text(
                text = error,
                modifier = Modifier.padding(top = 4.dp),
                style = MaterialTheme.typography.bodySmall,
                color = ActionRed
            )
        }
    }
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true, backgroundColor = 0xFFF6F3EC)
@Composable
private fun AppTextFieldPreview() {
    MagangYukTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            AppTextField("", {}, label = "NIM", placeholder = "231402045")
            AppTextField("abc", {}, label = "Password", isPassword = true)
            AppTextField("", {}, label = "NIM", error = "Wajib diisi")
        }
    }
}