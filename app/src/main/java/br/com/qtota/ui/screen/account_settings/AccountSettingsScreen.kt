package br.com.qtota.ui.screen.account_settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import br.com.qtota.R
import br.com.qtota.ui.components.ImageComponent
import br.com.qtota.ui.components.Toolbar
import br.com.qtota.ui.theme.DefaultColor
import br.com.qtota.ui.theme.DefaultColorDark
import br.com.qtota.ui.theme.ProductTitle
import br.com.qtota.ui.theme.defaultPadding
import com.composables.icons.lucide.Eye
import com.composables.icons.lucide.EyeOff
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.User

@Composable
fun AccountSettingsScreen(navController: NavHostController) {

    val viewModel: AccountSettingsViewModel = hiltViewModel()

    Scaffold(
        topBar = { Toolbar(backButtonEnabled = navController) }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(defaultPadding),
        ) {

            ImageComponent(
                null,
                Lucide.User,
                64.dp,
                Color.White,
                Modifier
                    .background(
                        shape = CircleShape,
                        color = Color.LightGray
                    )
                    .padding(defaultPadding)
                    .align(Alignment.CenterHorizontally)
            )

            Spacer(Modifier.height(defaultPadding))

            AccountSettingsGroup {
                AccountSettingsField(title = stringResource(R.string.name), value = "João da Silva")
                AccountSettingsField(title = stringResource(R.string.email), value = "neresfelip@gmail.com", editable = false)
            }

            ProductTitle("Alterar senha", modifier = Modifier.padding(defaultPadding))

            AccountSettingsGroup {
                AccountSettingsPasswordField("Senha atual", "João da Silva")
                AccountSettingsPasswordField("Nova senha", "João da Silva")
                AccountSettingsPasswordField(
                    stringResource(R.string.confirme_your_password),
                    "João da Silva",
                )
            }

            Button(
                {},
                Modifier
                    .fillMaxWidth()
                    .padding(defaultPadding),
                colors = ButtonDefaults.buttonColors(containerColor = DefaultColor)
            ) {
                Text("Salvar alterações")
            }

            Button(
                {},
                Modifier
                    .fillMaxWidth()
                    .padding(defaultPadding),
                colors = ButtonDefaults.buttonColors(containerColor = DefaultColorDark)
            ) {
                Text("Excluir conta")
            }

        }

    }

}

@Composable
private fun AccountSettingsField(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    editable: Boolean = true,
) {

    var text by remember { mutableStateOf(value) }

    Row(
        modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            title,
            Modifier
                .padding(defaultPadding)
                .weight(0.5f),
            color = Color.DarkGray,
            fontWeight = FontWeight.SemiBold,
            fontSize = 14.sp
        )

        TextField(
            modifier = Modifier.weight(0.5f),
            value = text,
            onValueChange = { text = it },
            textStyle = TextStyle(
                fontSize = 14.sp,
                textAlign = TextAlign.End,
            ),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = if(editable) Color.Unspecified else Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                disabledContainerColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
                focusedTextColor = Color.Gray,
                unfocusedTextColor = Color.Gray
            ),
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Words,
                imeAction = ImeAction.Next
            ),
            singleLine = true,
            readOnly = !editable
        )
    }
}

@Composable
private fun AccountSettingsPasswordField(
    title: String,
    value: String,
    modifier: Modifier = Modifier,
) {

    var text by remember { mutableStateOf(value) }
    var visiblePassword by remember { mutableStateOf(false) }

    Row(
        modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            title,
            Modifier
                .padding(defaultPadding)
                .weight(0.5f),
            color = Color.DarkGray,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
        )

        TextField(
            modifier = Modifier.weight(0.5f),
            value = text,
            onValueChange = { text = it },
            textStyle = TextStyle(
                fontSize = 14.sp,
                textAlign = TextAlign.End,
            ),
            trailingIcon = { IconButton({
                visiblePassword = !visiblePassword
            }) {
                Icon(if(visiblePassword) Lucide.Eye else Lucide.EyeOff, null)
            } },
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = Color.Transparent,
                disabledContainerColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
                focusedTextColor = Color.Gray,
                unfocusedTextColor = Color.Gray
            ),
            singleLine = true,
            visualTransformation = if (visiblePassword) VisualTransformation.None else PasswordVisualTransformation(),
        )
    }
}

@Composable
private fun AccountSettingsGroup(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier
            .padding(8.dp)
            .background(Color.White, RoundedCornerShape(defaultPadding)),
        content = content
    )
}

@Preview(showBackground = true)
@Composable
private fun AccountSettingsScreenPreview() {
    AccountSettingsScreen(rememberNavController())
}