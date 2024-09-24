package com.example.shoppinglist

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ShoppingListScreen()
        }
    }
}

data class ShoppingItem(val name: String, val quantity: String, var isChecked: Boolean = false)

@Composable
@Preview
fun ShoppingListScreen() {
    var itemName by remember { mutableStateOf("") }
    var itemQuantity by remember { mutableStateOf("") }
    var shoppingList by remember { mutableStateOf(listOf<ShoppingItem>()) }
    var errorMessage by remember { mutableStateOf("") }

    val nameFocusRequester = FocusRequester()
    val quantityFocusRequester = FocusRequester()

    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = "Shopping List", fontSize = 24.sp)

        Spacer(modifier = Modifier.height(16.dp))

        InputField(
            value = itemName,
            placeholder = "Enter item name",
            onValueChange = { itemName = it },
            modifier = Modifier.focusRequester(nameFocusRequester),
            onNext = {
                quantityFocusRequester.requestFocus()
            }
        )

        InputField(
            value = itemQuantity,
            placeholder = "Enter quantity",
            onValueChange = { itemQuantity = it },
            modifier = Modifier.focusRequester(quantityFocusRequester),
            onNext = {
                nameFocusRequester.requestFocus()
            }
        )

        if (errorMessage.isNotEmpty()) {
            Text(
                text = errorMessage,
                color = Color.Red,
                modifier = Modifier.padding(8.dp)
            )
        }

        Button(
            onClick = {
                if (itemName.isEmpty() || itemQuantity.isEmpty()) {
                    errorMessage = "Both name and quantity must be filled!"
                } else if (!itemQuantity.all { it.isDigit() }) {
                    errorMessage = "Quantity must be a number!"
                } else {
                    val newItem = ShoppingItem(itemName, itemQuantity)
                    shoppingList = shoppingList + newItem
                    itemName = ""
                    itemQuantity = ""
                    errorMessage = "" // Clear error message
                    nameFocusRequester.requestFocus()
                }
            },
            modifier = Modifier.padding(vertical = 8.dp)
        ) {
            Text("Add to List")
        }

        // Button to remove checked items
        Button(
            onClick = {
                shoppingList = shoppingList.filter { !it.isChecked }
            },
            modifier = Modifier.padding(vertical = 8.dp)
        ) {
            Text("Remove Selected Items")
        }

        LazyColumn {
            itemsIndexed(shoppingList) { index, item ->
                ShoppingListItem(
                    item = item,
                    onCheckedChange = { isChecked ->
                        shoppingList = shoppingList.toMutableList().also { list ->
                            val itemIndex = list.indexOf(item)
                            if (itemIndex != -1) {
                                list[itemIndex] = item.copy(isChecked = isChecked)
                            }
                        }
                    }
                )

                if ((index + 1) % 2 == 0) {
                    Spacer(modifier = Modifier.height(16.dp))
                } else {
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
fun InputField(
    value: String,
    placeholder: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    onNext: () -> Unit
) {
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp),
        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
        keyboardActions = KeyboardActions(onNext = { onNext() }),
        decorationBox = { innerTextField ->
            Box(
                modifier = Modifier.padding(8.dp)
            ) {
                if (value.isEmpty()) {
                    Text(placeholder)
                }
                innerTextField()
            }
        }
    )
}

@Composable
fun ShoppingListItem(item: ShoppingItem, onCheckedChange: (Boolean) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(text = item.name, fontSize = 18.sp)
                Text(text = "Quantity: ${item.quantity}", fontSize = 14.sp)
            }
            Checkbox(
                checked = item.isChecked,
                onCheckedChange = onCheckedChange
            )
        }
    }
}