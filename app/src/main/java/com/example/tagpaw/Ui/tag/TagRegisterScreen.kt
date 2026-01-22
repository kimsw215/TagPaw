package com.example.tagpaw.Ui.tag

import android.app.Activity
import android.nfc.NfcAdapter
import android.nfc.Tag
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.tagpaw.nfc.NfcUtils
import com.example.tagpaw.nfc.NfcUtils.buildEmergencyText
import com.example.tagpaw.Ui.theme.TagPawTheme

@Composable
fun TagRegisterScreen(
    petId: Long,
    onDone: () -> Unit,
    onBackClick: () -> Unit,
    viewModel: TagRegisterViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val activity = context as? Activity
    val nfcAdapter = remember { activity?.let { NfcAdapter.getDefaultAdapter(it) } }

    val petState = viewModel.pet.collectAsState()
    val pet = petState.value

    var writeResult by remember { mutableStateOf<String?>(null) }
    var lastUid by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(petId) {
        viewModel.loadPet(petId)
    }

    DisposableEffect(pet) {
        val callback = NfcAdapter.ReaderCallback { tag: Tag? ->
            val currentPet = pet ?: return@ReaderCallback
            val uid = NfcUtils.getTagUidHex(tag)
            if (uid != null) {
                val emergencyText = buildEmergencyText(
                    phone = currentPet.emergencyPhone,
                    note = currentPet.emergencyNote,
                    name = currentPet.name,
                    sex = currentPet.sex,
                    age = currentPet.age
                )
                // 태그 쓰기 시도
                val success = NfcUtils.writeNdefText(tag, emergencyText)

                if (success) {
                    lastUid = uid
                    writeResult = "태그 쓰기 성공!"
                    viewModel.saveTagToPet(petId = petId, uid = uid, onSaved = onDone)
                } else {
                    writeResult = "태그 쓰기 실패 (태그가 잠겨있거나 지원되지 않는 형식입니다)"
                }
            } else {
                writeResult = "태그 UID를 읽을 수 없습니다"
            }
        }

        if (pet != null) {
            activity?.let {
                nfcAdapter?.enableReaderMode(
                    it,
                    callback,
                    // FLAG_READER_SKIP_NDEF_CHECK를 제거하여 NDEF 인식이 가능하도록 수정
                    NfcAdapter.FLAG_READER_NFC_A or
                            NfcAdapter.FLAG_READER_NFC_B or
                            NfcAdapter.FLAG_READER_NFC_F or
                            NfcAdapter.FLAG_READER_NFC_V,
                    null
                )
            }
        }

        onDispose {
            activity?.let { nfcAdapter?.disableReaderMode(it) }
        }
    }

    TagRegisterContent(
        petName = pet?.name ?: "",
        lastUid = lastUid,
        writeResult = writeResult,
        onBackClick = onBackClick
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TagRegisterContent(
    petName: String,
    lastUid: String?,
    writeResult: String?,
    onBackClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("NFC 태그 등록") },
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
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "'$petName'의 정보를",
                style = MaterialTheme.typography.headlineSmall
            )
            Text(
                text = "NFC 태그에 저장합니다.",
                style = MaterialTheme.typography.headlineSmall
            )
            
            Spacer(Modifier.height(32.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        "스마트폰 뒷면에 태그를 가까이 대주세요.",
                        style = MaterialTheme.typography.titleMedium
                    )
                    
                    if (lastUid != null) {
                        Spacer(Modifier.height(16.dp))
                        Text("인식된 태그: $lastUid", style = MaterialTheme.typography.bodyMedium)
                    }
                    
                    if (writeResult != null) {
                        Spacer(Modifier.height(8.dp))
                        Text(
                            writeResult,
                            color = if (writeResult.contains("성공")) 
                                MaterialTheme.colorScheme.primary 
                            else 
                                MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }

            Spacer(Modifier.height(32.dp))
            
            Text(
                "※ 금속 재질의 폰 케이스는 인식이 안 될 수 있습니다.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TagRegisterPreview() {
    TagPawTheme {
        TagRegisterContent(
            petName = "초코",
            lastUid = "FE1234ABCD",
            writeResult = "태그를 인식해주세요",
            onBackClick = {}
        )
    }
}
