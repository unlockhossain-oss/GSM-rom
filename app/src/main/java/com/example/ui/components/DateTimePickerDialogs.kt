package com.example.ui.components

import android.app.TimePickerDialog
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppDatePickerDialog(
    initialDateMillis: Long = System.currentTimeMillis(),
    onDateSelected: (String, Long) -> Unit,
    onDismiss: () -> Unit
) {
    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = initialDateMillis)

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    val selectedMillis = datePickerState.selectedDateMillis ?: initialDateMillis
                    // Format to "15 August 2026"
                    val formatted = SimpleDateFormat("dd MMMM yyyy", Locale.ENGLISH).format(Date(selectedMillis))
                    onDateSelected(formatted, selectedMillis)
                }
            ) {
                Text("Select Date")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    ) {
        DatePicker(state = datePickerState)
    }
}

fun showAppTimePicker(
    context: android.content.Context,
    initialHour: Int = 17,
    initialMinute: Int = 0,
    onTimeSelected: (String, Int, Int) -> Unit
) {
    val dialog = TimePickerDialog(
        context,
        { _, hourOfDay, minute ->
            val cal = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, hourOfDay)
                set(Calendar.MINUTE, minute)
            }
            val formatted = SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(cal.time)
            onTimeSelected(formatted, hourOfDay, minute)
        },
        initialHour,
        initialMinute,
        false // 12-hour format with AM/PM
    )
    dialog.show()
}
