package br.com.qtota.ui

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.core.content.FileProvider
import androidx.core.graphics.createBitmap
import androidx.hilt.navigation.compose.hiltViewModel
import br.com.qtota.R
import br.com.qtota.ui.components.ErrorComponent
import br.com.qtota.ui.components.LoadingComponent
import br.com.qtota.ui.screen.home.FlyerState
import br.com.qtota.ui.screen.home.HomeViewModel
import br.com.qtota.ui.theme.DefaultColor
import coil.compose.AsyncImage
import java.io.File

@Composable
fun SendFlyerDialog(viewModel: HomeViewModel, dismiss: () -> Unit) {

    var selectedUri by remember { mutableStateOf<UriSource?>(null) }

    val context = LocalContext.current

    var photoUri: Uri? = null
    val takePictureLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
        onResult = { success ->
            if(success) {
                selectedUri = UriSource(photoUri!!, true)
            }
        }
    )

    val pickFileLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
        onResult = { uri: Uri? ->
            uri?.let {
                context.contentResolver.takePersistableUriPermission(
                    it, Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
                selectedUri = UriSource(it, false)
            }
        }
    )

    Dialog(dismiss) {
        Surface(
            shape = MaterialTheme.shapes.medium,
            tonalElevation = 8.dp
        ) {

            Column(Modifier.padding(16.dp)) {

                val sendingState by viewModel.sendingFlyerState.collectAsState()

                when(sendingState) {
                    FlyerState.Error -> ErrorComponent("Algo de errado não está certo")
                    FlyerState.Sending -> LoadingComponent()
                    null -> if (selectedUri == null) {
                        InitContainer(
                            onClickFromCamera = {
                                photoUri = createImageFileUri(context)
                                takePictureLauncher.launch(photoUri!!)
                            },
                            onClickFromFileSystem = {
                                openFilePicker(pickFileLauncher)
                            }
                        )
                    } else {

                        val context = LocalContext.current

                        SelectedContainer(
                            selectedUri = selectedUri!!.uri,
                            onEditButtonClick = {
                                if(selectedUri!!.isFromCamera) {
                                    photoUri = createImageFileUri(context)
                                    takePictureLauncher.launch(photoUri!!)
                                } else {
                                    openFilePicker(pickFileLauncher)
                                }
                            },
                            onSendButtonClick = {
                                viewModel.sendFlyer(selectedUri!!.uri, context, dismiss)
                            }
                        )

                        TextButton(dismiss, Modifier.align(Alignment.End)) {
                            Text(
                                "Cancelar",
                                color = DefaultColor,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 17.sp
                            )
                        }

                    }

                }

            }

        }
    }

}

data class UriSource (
    val uri: Uri,
    val isFromCamera: Boolean
)

@Composable
private fun InitContainer(onClickFromCamera: () -> Unit, onClickFromFileSystem: () -> Unit) {

    Text(
        "Envie um encarte para comparar os preços e ajudarmos você a encontrar as melhores ofertas na sua região!",
        Modifier.padding(8.dp),
        fontWeight = FontWeight.Bold,
    )
    Row(Modifier.padding(8.dp)) {
        DialogButton(
            "Tirar foto do encarte\n",
            R.drawable.outline_add_a_photo_24,
            Modifier.weight(1f),
            onClick = onClickFromCamera
        )
        Column(Modifier.weight(1f)) {
            DialogButton(
                "Enviar do sistema de arquivos",
                R.drawable.outline_add_notes_24,
                onClick = onClickFromFileSystem
            )
            Text(
                "Formatos permitidos: JPG/JPEG, PNG ou PDF",
                fontSize = 12.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun DialogButton(
    title: String,
    iconResource: Int,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Column(
        modifier
            .padding(8.dp)
            .clickable { onClick() },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            painter = painterResource(iconResource),
            contentDescription = null,
            tint = Color.DarkGray,
            modifier = Modifier.size(64.dp)
        )
        Text(
            title,
            textAlign = TextAlign.Center,
            maxLines = 2,
            color = Color.DarkGray
        )
    }
}

@Composable
private fun ColumnScope.SelectedContainer(selectedUri: Uri, onEditButtonClick: () -> Unit, onSendButtonClick: () -> Unit) {

    val context = LocalContext.current

    if(selectedUri.isPdf(context)) {
        val pdfBitmaps = rememberPdfBitmaps(context, selectedUri)

        if(pdfBitmaps == null) {
            Text("PDF Inválido", Modifier.padding(16.dp).align(Alignment.CenterHorizontally))
            Buttons(onEditButtonClick, null)
        } else {
            LazyColumn(Modifier.weight(1f, false)) {
                items(pdfBitmaps) { bmp ->
                    Image(
                        bitmap = bmp.asImageBitmap(),
                        contentDescription = "Página do PDF",
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.White),
                        contentScale = ContentScale.Crop
                    )
                }
            }
            Buttons(onEditButtonClick, onSendButtonClick)
        }
    } else {
        AsyncImage(
            model = selectedUri,
            contentDescription = "Encarte escolhido",
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f, false)
                .clip(MaterialTheme.shapes.small),
            contentScale = ContentScale.Inside
        )
        Buttons(onEditButtonClick, onSendButtonClick)
    }

}

@Composable
private fun Buttons(onEditButtonClick: () -> Unit, onSendButtonClick: (() -> Unit)?) {
    Row {
        Button(
            onSendButtonClick ?: {},
            Modifier
                .padding(16.dp)
                .weight(1f),
            enabled = onSendButtonClick != null,
            colors = ButtonDefaults.buttonColors(
                containerColor = DefaultColor
            )
        ) {
            Text("Enviar encarte")
        }
        OutlinedButton(
            onEditButtonClick,
            Modifier
                .padding(16.dp)
                .fillMaxWidth()
                .weight(1f),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = DefaultColor
            ),
            border = BorderStroke(1.dp, DefaultColor),
        ) {
            Text("Alterar")
        }
    }
}

private fun openFilePicker(pickFileLauncher: ManagedActivityResultLauncher<Array<String>, Uri?>) {
    pickFileLauncher.launch(
        arrayOf(
            "image/jpeg",
            "image/png",
            "application/pdf"
        )
    )
}

private fun createImageFileUri(context: Context): Uri {
    val file = File(context.cacheDir, "flyer_${System.currentTimeMillis()}.jpg")
    return FileProvider.getUriForFile(
        context,
        "${context.packageName}.provider",
        file
    )
}

private fun rememberPdfBitmaps(context: Context, uri: Uri): List<Bitmap>? {
    val resolver = context.contentResolver
    try {
        val pfd = resolver.openFileDescriptor(uri, "r") ?: return null
        PdfRenderer(pfd).use { renderer ->
            val pages = mutableListOf<Bitmap>()
            for (i in 0 until renderer.pageCount) {
                renderer.openPage(i).use { page ->
                    val width = page.width
                    val height = page.height
                    val bmp = createBitmap(width, height)
                    page.render(bmp, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                    pages += bmp
                }
            }
            pfd.close()
            return pages
        }
    } catch (_: Exception) {
        return null
    }
}

private fun Uri.isPdf(context: Context): Boolean {
    return context.contentResolver.getType(this) == "application/pdf"
}

@Composable @Preview(showBackground = true)
private fun SendFlyerDialogPreview() {
    SendFlyerDialog(hiltViewModel()) { }
}