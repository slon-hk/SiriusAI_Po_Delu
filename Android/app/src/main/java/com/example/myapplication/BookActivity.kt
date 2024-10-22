package com.example.myapplication

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.os.Bundle
import android.os.ParcelFileDescriptor
import android.provider.OpenableColumns
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.ItemsAdapter
import java.io.File
import java.io.FileOutputStream

class BookActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var itemsAdapter: ItemsAdapter
    private lateinit var addFileBtn: Button
    private lateinit var headerText: TextView
    private val items = mutableListOf<Item>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book)

        recyclerView = findViewById(R.id.booksList)
        addFileBtn = findViewById(R.id.addFileBtn)
        headerText = findViewById(R.id.headerText)

        // Установка GridLayoutManager с двумя колонками
        itemsAdapter = ItemsAdapter(items, this)
        val gridLayoutManager = GridLayoutManager(this, 2) // 2 колонки
        recyclerView.layoutManager = gridLayoutManager
        recyclerView.adapter = itemsAdapter

        // Установка обработчика нажатия кнопки
        addFileBtn.setOnClickListener {
            openFileChooser()
        }
    }

    private fun openFileChooser() {
        // Открытие меню выбора файла
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "*/*" // Можете изменить тип файла, если нужно
        }
        fileChooserLauncher.launch(intent)
    }

    // Регистрация для получения результата выбора файла
    private val fileChooserLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.let { data ->
                    data.data?.let { uri ->
                        addBook(uri)
                    }
                }
            }
        }

    private fun getPdfThumbnail(uri: Uri): Bitmap? {
        return try {
            val parcelFileDescriptor = contentResolver.openFileDescriptor(uri, "r")
            val pdfRenderer = PdfRenderer(parcelFileDescriptor!!)

            if (pdfRenderer.pageCount > 0) {
                val page = pdfRenderer.openPage(0) // Открываем первую страницу
                val bitmap = Bitmap.createBitmap(page.width, page.height, Bitmap.Config.ARGB_8888)

                // Рендерим страницу в битмап
                page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                page.close()
                pdfRenderer.close()

                // Возвращаем битмап
                bitmap
            } else {
                pdfRenderer.close()
                null
            }
        } catch (e: Exception) {
            Log.e("BookActivity", "Ошибка при извлечении обложки: ${e.message}")
            null
        }
    }

    private fun addBook(uri: Uri) {
        // Получение имени файла и добавление в список
        val fileName = getFileName(uri)
        val thumbnail = getPdfThumbnail(uri)

        if (thumbnail != null) {
            val item = Item(fileName ?: "Unknown", thumbnail)
            items.add(item)
            itemsAdapter.notifyItemInserted(items.size - 1)

            // Изменение текста заголовка, если добавлен хотя бы один элемент
            if (items.size == 1) {
                headerText.visibility = View.VISIBLE
                headerText.text = "Последние открытые книги"
            }
        } else {
            // Обработка случая, когда обложка не может быть извлечена
            Log.e("BookActivity", "Не удалось извлечь обложку для файла: $fileName")
        }
    }

    private fun getFileName(uri: Uri): String? {
        var name: String? = null
        contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (cursor.moveToFirst()) {
                name = cursor.getString(nameIndex)
            }
        }
        return name
    }
}