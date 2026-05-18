package com.example.tagpaw.ui.tag

import android.app.Activity
import android.nfc.NdefMessage
import android.nfc.NdefRecord
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.Ndef
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.tagpaw.nfc.NfcUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TagRegisterScreen(
    petId: Long,
    onDone: () -> Unit,
    onBackClick: () -> Unit,
    viewModel: TagRegisterViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val activity = context as Activity
    val nfcAdapter = remember { NfcAdapter.getDefaultAdapter(activity) }

    val pet by viewModel.pet.collectAsState()
    val uiState by viewModel.uiState.collectAsState()

    var lastUid by remember { mutableStateOf<String?>(null) }
    var writeResult by remember { mutableStateOf<String?>(null) }
    var showPinDialog by remember { mutableStateOf(false) }
    var inputPin by remember { mutableStateOf("") }

    LaunchedEffect(petId) {
        viewModel.loadPet(petId)
    }

    LaunchedEffect(uiState) {
        if (uiState is TagRegisterViewModel.RegisterUiState.NeedPinAuth) {
            showPinDialog = true
        }
    }

    DisposableEffect(uiState, pet) {
        val callback = NfcAdapter.ReaderCallback { tag: Tag? ->
            tag ?: return@ReaderCallback
            val uid = NfcUtils.getTagUidHex(tag)
            lastUid = uid
            writeResult = null

            try {
                val ndef = Ndef.get(tag)
                if (ndef != null) {
                    ndef.connect() // 한 번만 연결

                    // 읽기 - 이미 연결된 상태에서
                    val message = ndef.ndefMessage
                    var pinHashOnTag: String? = null

                    if (message != null) {
                        message.records.forEach { record ->
                            when {
                                record.tnf == NdefRecord.TNF_WELL_KNOWN &&
                                        record.type.contentEquals(NdefRecord.RTD_TEXT) -> {
                                    val payload = record.payload
                                    val langLength = payload[0].toInt() and 0x3F
                                    pinHashOnTag = String(
                                        payload, 1 + langLength,
                                        payload.size - 1 - langLength,
                                        Charsets.UTF_8
                                    )
                                }
                            }
                        }
                    }

                    if (pinHashOnTag != null &&
                        uiState !is TagRegisterViewModel.RegisterUiState.AuthSuccess
                    ) {
                        // PIN 있는 태그 → PIN 인증 필요
                        ndef.close()
                        viewModel.onTagDiscovered(pinHashOnTag)
                    } else {
                        // 빈 태그이거나 인증 성공 → 바로 쓰기
                        pet?.let {
                            if (!ndef.isWritable) {
                                ndef.close()
                                writeResult = "쓰기 불가능한 태그입니다."
                                return@ReaderCallback
                            }

                            val formattedPhone = if (it.emergencyPhone.length == 11) {
                                "${it.emergencyPhone.substring(0, 3)}-${it.emergencyPhone.substring(3, 7)}-${it.emergencyPhone.substring(7)}"
                            } else it.emergencyPhone

                            val smsBody = "${it.name}(${it.age}살) / ${it.emergencyNote}"
                            val smsUri = "sms:$formattedPhone?body=$smsBody"
                            val smsRecord = NdefRecord.createUri(smsUri)

                            val pinHash = NfcUtils.hashPin(it.pin)
                            val lang = "en"
                            val langBytes = lang.toByteArray(Charsets.US_ASCII)
                            val pinBytes = pinHash.toByteArray(Charsets.UTF_8)
                            val pinPayload = ByteArray(1 + langBytes.size + pinBytes.size)
                            pinPayload[0] = langBytes.size.toByte()
                            System.arraycopy(langBytes, 0, pinPayload, 1, langBytes.size)
                            System.arraycopy(pinBytes, 0, pinPayload, 1 + langBytes.size, pinBytes.size)
                            val pinRecord = NdefRecord(
                                NdefRecord.TNF_WELL_KNOWN,
                                NdefRecord.RTD_TEXT,
                                ByteArray(0),
                                pinPayload
                            )

                            val ndefMessage = NdefMessage(arrayOf(smsRecord, pinRecord))
                            ndef.writeNdefMessage(ndefMessage)
                            ndef.close()

                            writeResult = "태그 저장 성공!"
                            viewModel.saveTagToPet(petId, uid!!, onDone)
                        }
                    }
                } else {
                    // NDEF 포맷이 안 된 경우
                    pet?.let {
                        val success = NfcUtils.writeNdefRecords(
                            tag = tag,
                            phone = it.emergencyPhone,
                            name = it.name,
                            age = it.age,
                            note = it.emergencyNote,
                            pin = it.pin
                        )
                        if (success) {
                            writeResult = "포맷 및 저장 성공!"
                            viewModel.saveTagToPet(petId, uid!!, onDone)
                        } else {
                            writeResult = "지원되지 않는 태그입니다."
                        }
                    }
                }
            } catch (e: Exception) {
                writeResult = "오류: 다시 태그해주세요."
                android.util.Log.e("TagRegister", "error: ${e.message}", e)
            }
        }
        /*val callback = NfcAdapter.ReaderCallback { tag: Tag? ->
            tag ?: return@ReaderCallback
            val uid = NfcUtils.getTagUidHex(tag)
            lastUid = uid
            writeResult = null

            try {
                val ndef = Ndef.get(tag)
                if (ndef != null) {
                    // connect/close는 readNdefRecords 내부에서 처리
                    val (_, pinHashOnTag) = NfcUtils.readNdefRecords(tag)

                    if (pinHashOnTag != null &&
                        uiState !is TagRegisterViewModel.RegisterUiState.AuthSuccess
                    ) {
                        // PIN 있는 태그 → PIN 인증 필요
                        viewModel.onTagDiscovered(pinHashOnTag)
                    } else {
                        // 빈 태그이거나 인증 성공 → 바로 쓰기
                        pet?.let {
                            val success = NfcUtils.writeNdefRecords(
                                tag = tag,
                                phone = it.emergencyPhone,
                                name = it.name,
                                age = it.age,
                                note = it.emergencyNote,
                                pin = it.pin
                            )
                            if (success) {
                                writeResult = "태그 저장 성공!"
                                viewModel.saveTagToPet(petId, uid!!, onDone)
                            } else {
                                writeResult = "태그 쓰기 실패"
                            }
                        }
                    }
                } else {
                    // NDEF 포맷이 안 된 경우 → 포맷 후 쓰기
                    pet?.let {
                        val success = NfcUtils.writeNdefRecords(
                            tag = tag,
                            phone = it.emergencyPhone,
                            name = it.name,
                            age = it.age,
                            note = it.emergencyNote,
                            pin = it.pin
                        )
                        if (success) {
                            writeResult = "포맷 및 저장 성공!"
                            viewModel.saveTagToPet(petId, uid!!, onDone)
                        } else {
                            writeResult = "지원되지 않는 태그입니다."
                        }
                    }
                }
            } catch (e: Exception) {
                writeResult = "오류: 다시 태그해주세요."
            }
        }*/

        nfcAdapter?.enableReaderMode(
            activity, callback,
            NfcAdapter.FLAG_READER_NFC_A or
                    NfcAdapter.FLAG_READER_NFC_B or
                    NfcAdapter.FLAG_READER_NFC_F or
                    NfcAdapter.FLAG_READER_NFC_V,
            null
        )

        onDispose { nfcAdapter?.disableReaderMode(activity) }
    }

    // PIN 다이얼로그
    if (showPinDialog) {
        AlertDialog(
            onDismissRequest = {
                showPinDialog = false
                inputPin = ""
            },
            title = { Text("이미 사용 중인 태그") },
            text = {
                Column {
                    Text("재사용하려면 기존 PIN 번호를 입력하세요.")
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = inputPin,
                        onValueChange = { input ->
                            val digits = input.filter { it.isDigit() }
                            if (digits.length <= 4) inputPin = digits
                        },
                        label = { Text("PIN 4자리") },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.NumberPassword
                        ),
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.verifyPin(inputPin)
                        showPinDialog = false
                        inputPin = ""
                    },
                    enabled = inputPin.length == 4
                ) {
                    Text("확인")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showPinDialog = false
                    inputPin = ""
                }) {
                    Text("취소")
                }
            }
        )
    }

    Scaffold(
        modifier = Modifier.systemBarsPadding(),
        topBar = {
            TopAppBar(
                title = { Text("NFC 태그 연결") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "뒤로가기")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = if (uiState is TagRegisterViewModel.RegisterUiState.AuthSuccess)
                        MaterialTheme.colorScheme.primaryContainer
                    else
                        MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    val statusText = when {
                        writeResult != null -> writeResult!!
                        uiState is TagRegisterViewModel.RegisterUiState.AuthSuccess ->
                            "✅ 인증 성공! 다시 태그하세요."
                        uiState is TagRegisterViewModel.RegisterUiState.Error ->
                            (uiState as TagRegisterViewModel.RegisterUiState.Error).message
                        else -> "연결할 NFC 태그를 기기 뒷면에 대주세요."
                    }
                    Text(
                        text = statusText,
                        style = MaterialTheme.typography.titleMedium,
                        textAlign = TextAlign.Center
                    )
                    if (lastUid != null) {
                        Spacer(Modifier.height(8.dp))
                        Text("UID: $lastUid", style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            pet?.let {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            "새로 저장될 정보",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(Modifier.height(8.dp))
                        Text("이름: ${it.name}(${it.age}살)")
                        Text("연락처: ${it.emergencyPhone}")
                        Text("메모: ${it.emergencyNote}")
                    }
                }
            }

            if (uiState is TagRegisterViewModel.RegisterUiState.Error) {
                Button(
                    onClick = { viewModel.resetError() },
                    modifier = Modifier.padding(top = 16.dp)
                ) {
                    Text("다시 시도")
                }
            }
        }
    }
}