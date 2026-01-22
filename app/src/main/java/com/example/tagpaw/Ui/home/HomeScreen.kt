package com.example.tagpaw.Ui.home

import android.app.Activity
import android.nfc.NfcAdapter
import androidx.compose.foundation.clickable
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.tagpaw.domain.entities.PetEntity
import com.example.tagpaw.nfc.NfcUtils
import com.example.tagpaw.Ui.theme.TagPawTheme

@Composable
fun HomeScreen(
    onAddPetClick: () -> Unit,
    onPetClick: (Long) -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val pets = viewModel.pets.collectAsState()
    val context = LocalContext.current
    val activity = context as? Activity
    val nfcAdapter = remember { activity?.let { NfcAdapter.getDefaultAdapter(it) } }

    LaunchedEffect(Unit) {
        viewModel.navigateToPetDetail.collect { petId ->
            onPetClick(petId)
        }
    }

    DisposableEffect(Unit) {
        val callback = NfcAdapter.ReaderCallback { tag ->
            val uid = NfcUtils.getTagUidHex(tag) ?: return@ReaderCallback
            viewModel.onTagScanned(uid)
        }

        activity?.let {
            nfcAdapter?.enableReaderMode(
                it,
                callback,
                NfcAdapter.FLAG_READER_NFC_A or
                        NfcAdapter.FLAG_READER_NFC_B or
                        NfcAdapter.FLAG_READER_SKIP_NDEF_CHECK,
                null
            )
        }

        onDispose {
            activity?.let { nfcAdapter?.disableReaderMode(it) }
        }
    }

    HomeContent(
        pets = pets.value,
        onAddPetClick = onAddPetClick,
        onPetClick = onPetClick
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeContent(
    pets: List<PetEntity>,
    onAddPetClick: () -> Unit,
    onPetClick: (Long) -> Unit
) {
    Scaffold(
        topBar = { TopAppBar(title = { Text("TagPaw") }) },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddPetClick) {
                Text("+")
            }
        }
    ) { innerPadding ->
        if (pets.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Text("등록된 반려동물이 없습니다.\n+ 버튼으로 반려동물을 등록해 주세요.")
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                items(pets) { pet ->
                    PetListItem(
                        pet = pet,
                        onClick = { onPetClick(pet.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun PetListItem(
    pet: PetEntity,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable(onClick = onClick)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = pet.name, style = MaterialTheme.typography.titleLarge)
            Text(text = "${pet.age}살 · ${pet.sex}", style = MaterialTheme.typography.bodyMedium)
            Spacer(Modifier.height(4.dp))
            if (pet.tagUid != null) {
                AssistChip(onClick = {}, label = { Text("태그 연결됨") })
            } else {
                AssistChip(onClick = {}, label = { Text("태그 미연결") })
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomePreview() {
    TagPawTheme {
        HomeContent(
            pets = listOf(
                PetEntity(id = 1, name = "초코", sex = "암컷", age = "3", tagUid = "ABC123",
                    emergencyPhone = "010-8792-2505", emergencyNote = "귀여움", pin = ""),
                PetEntity(id = 2, name = "쿠키", sex = "수컷", age = "5", tagUid = null,
                    emergencyPhone = "010-8792-2505", emergencyNote = "귀여움", pin = "")
            ),
            onAddPetClick = {},
            onPetClick = {}
        )
    }
}
