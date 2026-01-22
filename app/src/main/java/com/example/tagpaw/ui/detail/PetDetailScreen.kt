package com.example.tagpaw.ui.detail

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.tagpaw.domain.entities.PetEntity
import com.example.tagpaw.ui.theme.TagPawTheme

@Composable
fun PetDetailScreen(
    petId: Long,
    onEditEmergencyClick: () -> Unit,
    onRegisterTagClick: () -> Unit,
    onBackClick: () -> Unit,
    viewModel: PetDetailViewModel = hiltViewModel()
) {
    LaunchedEffect(petId) {
        viewModel.loadPet(petId)
    }

    val petState = viewModel.pet.collectAsState()
    val pet = petState.value

    var showDeleteDialog by remember { mutableStateOf(false) }

    if (showDeleteDialog && pet != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("반려동물 삭제") },
            text = { Text("'${pet.name}'의 모든 정보를 삭제하시겠습니까? 연결된 NFC 태그 정보는 유지되지만 앱에서는 더 이상 관리할 수 없습니다.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deletePet(pet, onBackClick)
                        showDeleteDialog = false
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("삭제")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("취소")
                }
            }
        )
    }

    if (pet == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        PetDetailContent(
            pet = pet,
            onEditEmergencyClick = onEditEmergencyClick,
            onRegisterTagClick = onRegisterTagClick,
            onBackClick = onBackClick,
            onDeleteClick = { showDeleteDialog = true }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PetDetailContent(
    pet: PetEntity,
    onEditEmergencyClick: () -> Unit,
    onRegisterTagClick: () -> Unit,
    onBackClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    fun formatPhoneNumber(input: String): String {
        val digits = input.filter { it.isDigit() }
        return when {
            digits.length <= 3 -> digits
            digits.length <= 7 -> "${digits.substring(0, 3)}-${digits.substring(3)}"
            digits.length <= 11 -> "${digits.substring(0, 3)}-${digits.substring(3, 7)}-${digits.substring(7)}"
            else -> digits
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(pet.name) },
                navigationIcon = {
                    // IconButton 대신 TextButton을 사용하여 간격 확보
                    TextButton(onClick = onBackClick) { 
                        Text("뒤로") 
                    }
                },
                actions = {
                    // 삭제 버튼도 일관성을 위해 TextButton으로 변경
                    TextButton(onClick = onDeleteClick) {
                        Text("삭제", color = MaterialTheme.colorScheme.error)
                    }
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
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("기본 정보", style = MaterialTheme.typography.headlineSmall)
                    Spacer(Modifier.height(8.dp))
                    Text("성별: ${pet.sex}")
                    Text("나이: ${pet.age}살")
                }
            }

            Spacer(Modifier.height(16.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "비상 정보",
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                    Spacer(Modifier.height(8.dp))
                    Text("비상 연락처: ${formatPhoneNumber(pet.emergencyPhone)}", color = MaterialTheme.colorScheme.onErrorContainer)
                    Text("주의 사항: ${pet.emergencyNote}", color = MaterialTheme.colorScheme.onErrorContainer)
                }
            }

            Spacer(Modifier.height(16.dp))

            Card(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("NFC 태그", style = MaterialTheme.typography.titleMedium)
                        Text(
                            text = if (pet.tagUid != null) "연결됨 (${pet.tagUid})" else "연결된 태그 없음",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                    Button(onClick = onRegisterTagClick) {
                        Text(if (pet.tagUid != null) "태그 갱신" else "태그 등록")
                    }
                }
            }

            Spacer(Modifier.weight(1f))

            Button(
                onClick = onEditEmergencyClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("반려동물 정보 수정")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PetDetailPreview() {
    TagPawTheme {
        PetDetailContent(
            pet = PetEntity(
                id = 1,
                name = "초코",
                sex = "암컷",
                age = "3",
                emergencyPhone = "01012345678",
                emergencyNote = "견과류 알레르기가 있어요.",
                tagUid = "ABC123DEF",
                pin = "1234"
            ),
            onEditEmergencyClick = {},
            onRegisterTagClick = {},
            onBackClick = {},
            onDeleteClick = {}
        )
    }
}
