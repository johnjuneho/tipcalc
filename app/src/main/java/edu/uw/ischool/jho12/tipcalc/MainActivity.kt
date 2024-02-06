package edu.uw.ischool.jho12.tipcalc

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import edu.uw.ischool.jho12.tipcalc.ui.theme.TipcalcTheme
import java.text.NumberFormat
import java.util.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TipcalcTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    TipCalculator()
                }
            }
        }
    }
}

@Composable
fun TipCalculator() {
    var rawInput by remember { mutableStateOf("") } // Raw input from the user, representing the amount
    var tipDisplay by remember { mutableStateOf("") } // To display the calculated tip amount
    val context = LocalContext.current // Access to the local context for Toast

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            value = rawInput,
            onValueChange = { newValue ->
                // Only allow input that is a valid number with up to two decimal places
                if (newValue.matches(Regex("^\\d*\\.?\\d{0,2}$"))) {
                    rawInput = newValue
                } else if (newValue.endsWith(".") && !rawInput.contains(".")) {
                    rawInput = newValue // Allow adding a period for decimals
                } else if (newValue.contains(".") && newValue.substringAfter(".").length <= 2) {
                    rawInput = newValue // Correctly handle typing after the decimal
                }
            },
            label = { Text("Amount") },
            singleLine = true,
            leadingIcon = { Text("$") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                // Calculate the tip when the button is clicked
                val amount = rawInput.toDoubleOrNull() ?: 0.0
                val tip = calculateTip(amount)
                tipDisplay = formatAsCurrency(tip)
                // Optionally, show the tip as a Toast as well
                Toast.makeText(context, "Tip: $tipDisplay", Toast.LENGTH_LONG).show()
            },
            enabled = rawInput.isNotEmpty() && rawInput.toDoubleOrNull() ?: 0.0 > 0,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("Calculate Tip")
        }

        // Display the calculated tip amount directly in the UI
        if (tipDisplay.isNotEmpty()) {
            Text(text = "Tip Amount: $tipDisplay", modifier = Modifier.align(Alignment.CenterHorizontally))
        }
    }
}

fun calculateTip(amount: Double, tipPercentage: Double = 0.15): Double = amount * tipPercentage

fun formatAsCurrency(amount: Double): String = NumberFormat.getCurrencyInstance(Locale.US).format(amount)

fun formatAsCurrency(text: String): String {
    return if (text.isNotEmpty()) {
        val amount = text.toDoubleOrNull() ?: 0.0
        NumberFormat.getCurrencyInstance(Locale.US).format(amount)
    } else {
        "$0.00"
    }
}
