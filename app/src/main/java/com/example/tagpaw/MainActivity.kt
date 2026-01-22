package com.example.tagpaw

import android.content.Intent
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.tagpaw.navigation.TagPawApp
import com.example.tagpaw.nfc.NfcUtils
import com.example.tagpaw.Ui.theme.TagPawTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private var nfcAdapter: NfcAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        nfcAdapter = NfcAdapter.getDefaultAdapter(this)

        enableEdgeToEdge()
        setContent {
            TagPawTheme {
                TagPawApp()
            }
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        // TODO: 나중에 TagRegisterScreen / HomeScreen 쪽과 연결
        val tag: Tag? = intent?.getParcelableExtra(NfcAdapter.EXTRA_TAG)
        val uid = NfcUtils.getTagUidHex(tag)
        val ndefText = NfcUtils.readNdefText(tag)

        // 일단 로그 찍어보는 정도로만 두고,
        // 나중에 ViewModel로 이벤트를 전달해서 처리하자.
        // Log.d("TagPaw", "onNewIntent uid=$uid, text=$ndefText")
    }
}
