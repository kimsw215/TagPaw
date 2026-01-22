package com.example.tagpaw.ui.emergency

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
import com.example.tagpaw.domain.entities.PetEntity
import com.example.tagpaw.ui.theme.TagPawTheme

@Composable
fun EmergencyEditScreen(
    petId: Long,
    onDone: () -> Unit,
    onBackClick: () -> Unit,
    viewModel: EmergencyEditViewModel = hiltViewModel()
) {
    LaunchedEffect(petId) {
        viewModel.loadPet(petId)
    }

    val pet = viewModel.pet.collectAsState().value

    if (pet == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        EmergencyEditContent(
            pet = pet,
            onUpdateClick = { name, sex, age, phone, note, pin ->
                viewModel.updatePet(name, sex, age, phone, note, pin, onDone)
            },
            onBackClick = onBackClick
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmergencyEditContent(
    pet: PetEntity,
    onUpdateClick: (String, String, String, String, String, String) -> Unit,
    onBackClick: () -> Unit
) {
    var name by remember { mutableStateOf(pet.name) }
    var sex by remember { mutableStateOf(pet.sex) }
    var age by remember { mutableStateOf(pet.age) }
    var phone by remember { mutableStateOf(pet.emergencyPhone.filter { it.isDigit() }) }
    var note by remember { mutableStateOf(pet.emergencyNote) }
    var pin by remember { mutableStateOf(pet.pin) }

    val maxNoteLength = 40

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("반려동물 정보 수정") },
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
            Text("기본 정보", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))
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
                    label = { Text("나이 (숫자만)") }, 
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    suffix = { Text("살") },
                    singleLine = true
                )
            }
            
            Spacer(Modifier.height(24.dp))
            HorizontalDivider()
            Spacer(Modifier.height(24.dp))

            Text("비상 정보", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))
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

            Spacer(Modifier.height(32.dp))

            Button(
                onClick = { onUpdateClick(name, sex, age, phone, note, pin) },
                enabled = name.isNotBlank() && pin.length in 4..6,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("정보 수정 및 태그 등록으로 이동")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun EmergencyEditPreview() {
    TagPawTheme {
        EmergencyEditContent(
            pet = PetEntity(id = 1, name = "초코", sex = "암컷", age = "3", pin = "1234",
                emergencyPhone = "01012345678", emergencyNote = "치즈"),
            onUpdateClick = { _, _, _, _, _, _ -> },
            onBackClick = {}
        )
    }
}
