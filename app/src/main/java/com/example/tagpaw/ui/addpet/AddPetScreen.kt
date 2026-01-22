package com.example.tagpaw.ui.addpet

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
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
        onSaveClick = { name, sex, age, phone, note, pin ->
            viewModel.savePet(name, sex, age, phone, note, pin, onPetSaved)
        },
        onBackClick = onBackClick
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPetContent(
    onSaveClick: (String, String, String, String, String, String) -> Unit,
    onBackClick: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var sex by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    var pin by remember { mutableStateOf("") }

    val maxNoteLength = 40

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
        ) {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("이름") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = sex,
                    onValueChange = { sex = it },
                    label = { Text("성별") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
                Spacer(Modifier.width(8.dp))
                OutlinedTextField(
                    value = age,
                    onValueChange = { if (it.all { char -> char.isDigit() }) age = it },
                    label = { Text("나이 (숫자)") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    suffix = { Text("살") },
                    singleLine = true
                )
            }
            Spacer(Modifier.height(8.dp))
            
            // 연락처 필드: 포맷팅 없이 숫자만 입력받음
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
            OutlinedTextField(
                value = note,
                onValueChange = { if (it.length <= maxNoteLength) note = it },
                label = { Text("주의 사항") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                supportingText = {
                    Box(modifier = Modifier.fillMaxWidth()) {
                        Text("NFC 태그 용량상 짧게 작성해주세요.", modifier = Modifier.align(Alignment.CenterStart))
                        Text("${note.length} / $maxNoteLength", modifier = Modifier.align(Alignment.CenterEnd), color = if (note.length >= maxNoteLength) Color.Red else Color.Unspecified)
                    }
                }
            )
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = pin,
                onValueChange = { if (it.length <= 6 && it.all { c -> c.isDigit() }) pin = it },
                label = { Text("PIN (4~6자리 숫자)") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                singleLine = true
            )

            Spacer(Modifier.height(24.dp))

            Button(
                onClick = { onSaveClick(name, sex, age, phone, note, pin) },
                enabled = name.isNotBlank() && pin.length in 4..6,
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
        AddPetContent(onSaveClick = { _, _, _, _, _, _ -> }, onBackClick = {})
    }
}
