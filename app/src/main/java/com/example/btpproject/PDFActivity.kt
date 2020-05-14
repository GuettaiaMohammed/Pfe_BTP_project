package com.example.btpproject

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ProgressBar
import com.github.barteksc.pdfviewer.PDFView

class PDFActivity : AppCompatActivity() {

    lateinit var doc: PDFView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pdf)

        val p = findViewById<ProgressBar>(R.id.pdfViewProgressBar)

        val d = findViewById<PDFView>(R.id.pdfView)
        d.fromAsset("odoo.pdf").load()
    }
}
