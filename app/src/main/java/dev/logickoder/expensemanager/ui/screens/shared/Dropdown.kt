package dev.logickoder.expensemanager.ui.screens.shared

import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuBoxScope
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> DropdownField(
    suggested: T,
    suggestions: List<T>,
    modifier: Modifier = Modifier,
    onSuggestionSelected: ((T) -> Unit),
    dropdownField: @Composable ExposedDropdownMenuBoxScope.(String, Boolean) -> Unit = { suggestion, _ ->
        OutlinedTextField(
            // The `menuAnchor` modifier must be passed to the text field for correctness.
            modifier = Modifier.menuAnchor(),
            value = suggestion,
            onValueChange = { }
        )
    }
) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(
        modifier = modifier,
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        content = {
            dropdownField(suggested.toString(), expanded)
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                content = {
                    suggestions.forEach { suggestion ->
                        DropdownMenuItem(
                            onClick = {
                                expanded = false
                                onSuggestionSelected(suggestion)
                            },
                            text = {
                                Text(text = suggestion.toString())
                            }
                        )
                    }
                }
            )
        }
    )
}