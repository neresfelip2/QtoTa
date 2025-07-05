package br.com.qtota.utils

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okio.BufferedSink
import okio.source
import java.io.IOException

object Utils {

    /**
     * Cria um MultipartBody.Part a partir de um Uri
     */
    fun uriToMultipart(
        context: Context,
        uri: Uri,
        fieldName: String = "flyer"
    ): MultipartBody.Part {
        // Tenta obter o nome do arquivo
        val contentResolver = context.contentResolver
        val fileName = contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (cursor.moveToFirst() && nameIndex != -1) cursor.getString(nameIndex) else "upload_file"
        } ?: "upload_file"

        // Determina MIME type
        val mimeType = contentResolver.getType(uri) ?: "application/octet-stream"

        // Cria RequestBody customizado para ler o InputStream
        val requestBody = object : RequestBody() {
            override fun contentType() = mimeType.toMediaTypeOrNull()

            override fun writeTo(sink: BufferedSink) {
                contentResolver.openInputStream(uri)?.use { inputStream ->
                    sink.writeAll(inputStream.source())
                } ?: throw IOException("Não foi possível abrir o InputStream do Uri")
            }
        }

        return MultipartBody.Part.createFormData(fieldName, fileName, requestBody)
    }

}
