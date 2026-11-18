package com.kel5.magangyuk.ui.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kel5.magangyuk.R
import com.kel5.magangyuk.ui.theme.*

@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "Cari posisi atau perusahaan...",
    onSearch: (() -> Unit)? = null,        // dipanggil saat tombol cari di keyboard ditekan
    onFilterClick: (() -> Unit)? = null    // null = ikon filter disembunyikan
) {
    TextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier.fillMaxWidth(),
        singleLine = true,
        placeholder = {
            Text(
                text = placeholder,
                style = MaterialTheme.typography.bodyMedium,
                color = StatusGrayText
            )
        },
        leadingIcon = {
            Icon(Icons.Default.Search, contentDescription = null, tint = StatusGrayText)
        },
        // trailingIcon = ikon di dalam bar, sisi kanan. Hanya tampil jika onFilterClick diisi.
        trailingIcon = if (onFilterClick != null) {
            {
                IconButton(onClick = onFilterClick) {
                    Icon(
                        painter = painterResource(R.drawable.ic_filter_list),
                        contentDescription = "Filter",
                        tint = StatusGrayText
                    )
                }
            }
        } else null,
        shape = RoundedCornerShape(50),   // pill bulat penuh sesuai Figma
        // Isi putih, garis bawah/garis tepi bawaan TextField dihilangkan
        colors = TextFieldDefaults.colors(
            focusedContainerColor = White,
            unfocusedContainerColor = White,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            cursorColor = Navy,
            focusedTextColor = Navy,
            unfocusedTextColor = Navy
        ),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(onSearch = { onSearch?.invoke() })
    )
}

@Preview(showBackground = true, backgroundColor = 0xFFF6F3EC)
@Composable
private fun SearchBarPreview() {
    MagangYukTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            SearchBar(query = "", onQueryChange = {}, onFilterClick = {})   // dengan ikon filter
            SearchBar(query = "Android", onQueryChange = {})                // tanpa ikon filter
        }
    }
}