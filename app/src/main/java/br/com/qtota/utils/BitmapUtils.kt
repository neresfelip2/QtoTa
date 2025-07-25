package br.com.qtota.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapShader
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Shader
import android.graphics.drawable.BitmapDrawable
import androidx.core.graphics.createBitmap
import coil.ImageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import kotlin.math.max
import kotlin.math.min

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
        // diâmetro do círculo = menor lado do original
        val size = min(width, height)
        val output = createBitmap(size, size)
        val canvas = Canvas(output)

        val radius = size / 2f

        // 1) desenha o fundo branco
        val bgPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE
        }
        canvas.drawCircle(radius, radius, radius, bgPaint)

        // 2) calcula escala para "fit center" (mantém proporção)
        val scale = size.toFloat() / max(width, height).toFloat()
        val dx = (size - width * scale) / 2f
        val dy = (size - height * scale) / 2f

        // 3) prepara shader com o bitmap completo
        val shader = BitmapShader(this, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP).apply {
            val matrix = Matrix().apply {
                setScale(scale, scale)
                postTranslate(dx, dy)
            }
            setLocalMatrix(matrix)
        }

        // 4) paint usando shader anti-alias
        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            this.shader = shader
        }

        // 5) desenha o círculo da imagem por cima do fundo
        canvas.drawCircle(radius, radius, radius, paint)

        return output
    }

}