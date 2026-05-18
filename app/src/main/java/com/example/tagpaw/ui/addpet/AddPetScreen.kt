package com.example.tagpaw.ui.addpet

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.tagpaw.ui.theme.TagPawTheme

@Composable
fun AddPetScreen(
    onPetSaved: (Long) -> Unit,
    onBackClick: () -> Unit,
    viewModel: AddPetViewModel = hiltViewModel()
) {
    AddPetContent(
        onSaveClick = { name, age, phone, note, pin ->
            viewModel.savePet(name, age, phone, note, pin, onPetSaved)
        },
        onBackClick = onBackClick
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPetContent(
    onSaveClick: (String, String, String, String, String) -> Unit,
    onBackClick: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    var pin by remember { mutableStateOf("") }

    val maxNoteLength = 30

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("반려동물 등록") },
                navigationIcon = {
                    TextButton(onClick = onBackClick) { Text("취소") }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // 이름 + 나이 한 줄
            Row(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { if (it.length <= 3) name = it },
                    label = { Text("이름") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    supportingText = { Text("최대 3자") }
                )
                Spacer(Modifier.width(8.dp))
                OutlinedTextField(
                    value = age,
                    onValueChange = { input ->
                        val digits = input.filter { it.isDigit() }
                        if (digits.length <= 2) age = digits
                    },
                    label = { Text("나이") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    suffix = { Text("살") },
                    singleLine = true
                )
            }

            Spacer(Modifier.height(8.dp))

            // 연락처
            OutlinedTextField(
                value = phone,
                onValueChange = { input ->
                    val digits = input.filter { it.isDigit() }
                    if (digits.length <= 11) phone = digits
                },
                label = { Text("비상 연락처") },
                placeholder = { Text("01012345678") },
                supportingText = { Text("하이픈(-) 없이 숫자만 입력해주세요.") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                singleLine = true
            )

            Spacer(Modifier.height(8.dp))

            // 메모
            OutlinedTextField(
                value = note,
                onValueChange = { if (it.length <= maxNoteLength) note = it },
                label = { Text("메모") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                supportingText = {
                    Box(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            "NFC 태그 용량상 짧게 작성해주세요.",
                            modifier = Modifier.align(Alignment.CenterStart)
                        )
                        Text(
                            "${note.length} / $maxNoteLength",
                            modifier = Modifier.align(Alignment.CenterEnd),
                            color = if (note.length >= maxNoteLength)
                                Color.Red else Color.Unspecified
                        )
                    }
                }
            )

            Spacer(Modifier.height(8.dp))

            // PIN
            OutlinedTextField(
                value = pin,
                onValueChange = { input ->
                    val digits = input.filter { it.isDigit() }
                    if (digits.length <= 4) pin = digits
                },
                label = { Text("PIN (4자리 숫자)") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                singleLine = true,
                supportingText = { Text("NFC 태그 덮어쓰기 방지용 비밀번호입니다.") }
            )

            Spacer(Modifier.height(24.dp))

            Button(
                onClick = { onSaveClick(name, age, phone, note, pin) },
                enabled = name.isNotBlank() &&
                        age.isNotBlank() &&
                        phone.length == 11 &&
                        pin.length == 4,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("저장 후 태그 등록")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AddPetPreview() {
    TagPawTheme {
        AddPetContent(onSaveClick = { _, _, _, _, _ -> }, onBackClick = {})
    }
}