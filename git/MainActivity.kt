package com.example.phoneuser

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.phoneuser.ui.theme.PhoneUserTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
        PhoneUserTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    FactorizationScreen()
                }
            }
        }
    }
}

@Composable
fun FactorizationScreen() {
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    var numberInput by rememberSaveable { mutableStateOf("") }
    var errorMessage by rememberSaveable { mutableStateOf<String?>(null) }
    var resultText by rememberSaveable { mutableStateOf("") }

    val scrollState = rememberScrollState()
    val contentModifier = Modifier
        .fillMaxSize()
        .padding(16.dp)
        .verticalScroll(scrollState)

    if (isLandscape) {
        Row(
            modifier = contentModifier,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            InputPanel(
                modifier = Modifier
                    .weight(1f)
                    .align(Alignment.Top),
                numberInput = numberInput,
                onNumberChange = {
                    numberInput = it
                    errorMessage = null
                },
                onFactorizeClick = {
                    val (error, result) = handleFactorization(numberInput)
                    errorMessage = error
                    resultText = result
                },
                errorMessage = errorMessage
            )

            ResultPanel(
                modifier = Modifier
                    .weight(1f)
                    .align(Alignment.Top),
                resultText = resultText
            )
        }
    } else {
        Column(
            modifier = contentModifier,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            InputPanel(
                modifier = Modifier.fillMaxWidth(),
                numberInput = numberInput,
                onNumberChange = {
                    numberInput = it
                    errorMessage = null
                },
                onFactorizeClick = {
                    val factorizationResult = PrimeFactorizer.formatFactorization(numberInput)
                    errorMessage = factorizationResult.errorMessage
                    resultText = factorizationResult.resultText
                },
                errorMessage = errorMessage
            )

            ResultPanel(
                modifier = Modifier.fillMaxWidth(),
                resultText = resultText
            )
        }
    }
}

@Composable
fun InputPanel(
    modifier: Modifier = Modifier,
    numberInput: String,
    onNumberChange: (String) -> Unit,
    onFactorizeClick: () -> Unit,
    errorMessage: String?
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Разложение числа на простые множители",
            style = MaterialTheme.typography.titleMedium
        )

        OutlinedTextField(
            value = numberInput,
            onValueChange = onNumberChange,
            label = { Text("Введите целое число ≥ 2") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            isError = errorMessage != null,
            modifier = Modifier.fillMaxWidth()
        )

        if (errorMessage != null) {
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

        Button(
            onClick = onFactorizeClick,
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("Разложить")
        }
    }
}

@Composable
fun ResultPanel(
    modifier: Modifier = Modifier,
    resultText: String
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Результат",
            style = MaterialTheme.typography.titleMedium
        )
        if (resultText.isNotBlank()) {
            Text(
                text = resultText,
                style = MaterialTheme.typography.bodyMedium
            )
        } else {
            Text(
                text = "Пока ничего не посчитано",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

fun handleFactorization(input: String): Pair<String?, String> {
    if (input.isBlank()) {
        return "Введите число" to ""
    }

    val number: Long = input.toLongOrNull()
        ?: return "Некорректный формат числа" to ""

    if (number < 2) {
        return "Разложение определено только для чисел ≥ 2" to ""
    }

    val factors = factorizeNumber(number)
    if (factors.isEmpty()) {
        return null to "Не удалось разложить число"
    }

    val factorsString = factors.joinToString(" × ")
    val result = "$number = $factorsString"
    return null to result
}

fun factorizeNumber(n: Long): List<Long> {
    var num = kotlin.math.abs(n)
    val factors = mutableListOf<Long>()

    var divisor = 2L
    while (divisor * divisor <= num) {
        while (num % divisor == 0L) {
            factors.add(divisor)
            num /= divisor
        }
        divisor++
    }
    if (num > 1) {
        factors.add(num)
    }
    return factors
}

