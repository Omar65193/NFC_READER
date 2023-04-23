package com.example.nfc_app

import android.app.PendingIntent
import android.content.Intent
import android.content.IntentFilter
import android.nfc.NdefMessage
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.Ndef
import android.nfc.tech.NfcA
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.io.IOException
import java.io.UnsupportedEncodingException
import java.nio.charset.Charset
import kotlin.experimental.and

class MainActivity : AppCompatActivity() {
    private var nfcAdapter: NfcAdapter? = null
    private var pendingIntent: PendingIntent? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val foco_on = findViewById<ImageView>(R.id.foco_on)
        val foco_off = findViewById<ImageView>(R.id.foco_off)
        foco_on.visibility = View.INVISIBLE
        foco_off.visibility = View.VISIBLE
        // Obtener el adaptador NFC
        nfcAdapter = NfcAdapter.getDefaultAdapter(this)


        // Crear un PendingIntent para la actividad actual
        pendingIntent = PendingIntent.getActivity(
            this, 0, Intent(
                this,
                javaClass
            ).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0
        )
    }

    override fun onResume() {
        super.onResume()

        // Habilitar la detección de etiquetas NFC
        val intentFilter = IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED)
        val filters = arrayOf(intentFilter)
        nfcAdapter?.enableForegroundDispatch(this, pendingIntent, filters, null)
    }

    override fun onPause() {
        super.onPause()

        // Deshabilitar la detección de etiquetas NFC
        nfcAdapter?.disableForegroundDispatch(this)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)

        // Leer los datos de la etiqueta NFC
        if (NfcAdapter.ACTION_TAG_DISCOVERED == intent.action) {
            val tag = intent.getParcelableExtra<Tag>(NfcAdapter.EXTRA_TAG)

            // Leer el contenido de la etiqueta
            val ndef = Ndef.get(tag)
            val ndefMessage = ndef.cachedNdefMessage
            val records = ndefMessage.records
            val record = records[0]
            val payload = record.payload
            val textEncoding = if ((payload[0].toInt() and 128) == 0) "UTF-8" else "UTF-16"
            val languageCodeLength = payload[0] and 63.toByte()
            val text = String(payload, languageCodeLength + 1, payload.size - languageCodeLength - 1, Charset.forName(textEncoding))

            // Mostrar el contenido de la etiqueta en una vista de texto y prender o apagar el foco
            val tagInfoLabel = findViewById<TextView>(R.id.tag_info_label)
            val foco_on = findViewById<ImageView>(R.id.foco_on)
            val foco_off = findViewById<ImageView>(R.id.foco_off)
            if(text.equals("Encender",ignoreCase = true)){
                foco_on.visibility = View.VISIBLE
                foco_off.visibility = View.INVISIBLE
            }else{
                foco_on.visibility = View.INVISIBLE
                foco_off.visibility = View.VISIBLE
            }
            tagInfoLabel.text = text
        }
    }
}
