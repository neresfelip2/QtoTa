package br.com.qtota.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapShader
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Shader
import android.graphics.drawable.BitmapDrawable
import kotlin.math.min
import androidx.core.graphics.createBitmap
import coil.ImageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult

object BitmapUtils {

    suspend fun downloadImageFromUrl(urlImage: String?, context: Context) : Bitmap? {
        val loader = ImageLoader.Builder(context)
            .build()

        val request = ImageRequest.Builder(context)
            .data(urlImage)
            .allowHardware(false)
            .build()

        val result = loader.execute(request) as? SuccessResult
        val bitmap = (result?.drawable as? BitmapDrawable)?.bitmap
        return bitmap
    }

    fun Bitmap.cropToCircle(): Bitmap {
        // determina o tamanho do círculo (o menor lado do bitmap)
        val size = min(width, height)
        // centraliza o recorte
        val x = (width  - size) / 2
        val y = (height - size) / 2

        // corta um quadrado central
        val squared = Bitmap.createBitmap(this, x, y, size, size)

        // bitmap de saída, com canal alpha
        val output = createBitmap(size, size)
        val canvas = Canvas(output)

        // prepara paint com shader do bitmap recortado
        val paint = Paint().apply {
            isAntiAlias = true
            shader = BitmapShader(squared, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
        }

        // desenha um círculo preenchido com o shader
        val r = size / 2f
        canvas.drawCircle(r, r, r, paint)

        return output
    }

}