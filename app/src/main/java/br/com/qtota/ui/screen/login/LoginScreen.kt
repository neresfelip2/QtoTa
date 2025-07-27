package br.com.qtota.ui.screen.login

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import br.com.qtota.R
import br.com.qtota.ui.state_handler.UIState
import br.com.qtota.ui.theme.DefaultColor
import br.com.qtota.ui.theme.DefaultColorDark
import br.com.qtota.ui.theme.ErrorColor
import br.com.qtota.ui.theme.GradientBackground

@Composable
internal fun LoginScreen(navController: NavHostController) {

    val viewModel: LoginViewModel = hiltViewModel()
    val loginState by viewModel.loginState.collectAsState()

    Column(
        Modifier
            .fillMaxSize()
            .background(GradientBackground),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Icon(
            painterResource(R.drawable.qto_ta_logo), null,
            Modifier
                .size(144.dp),
            tint = Color.White,
        )

        Column(
            Modifier
                .padding(vertical = 16.dp)
                .background(Color.White, shape = RoundedCornerShape(24.dp))
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            var newRegister by remember { mutableStateOf(false) }

            if(newRegister) {
                NewRegisterContainer(viewModel) {
                    newRegister = false
                }
            } else {
                LoginContainer(viewModel) {
                    newRegister = true
                }
            }

        }

        TextButton(
            {
                viewModel.setNotFirstAccess()
                viewModel.navigateToNextScreen(navController)
            },
            colors = ButtonDefaults.textButtonColors(contentColor = Color.White),
        ) { Text(stringResource(R.string.continue_without_logging_in)) }

    }

    when(loginState) {
        null -> Unit
        is UIState.Loading -> LoadingDialog()
        is UIState.Error -> ErrorDialog((loginState as UIState.Error).description) {
            viewModel.resetLoginState()
        }
        is UIState.Success -> viewModel.navigateToNextScreen(navController)
    }

}

@Composable
private fun LoginContainer(viewModel: LoginViewModel, newRegister: () -> Unit) {

    val validEmail by viewModel.validEmail.collectAsState()
    val validPassword by viewModel.validPassword.collectAsState()

    EmailField(!validEmail) { viewModel.setEmail(it) }
    PasswordField(isError = !validPassword) { viewModel.setPassword(it) }
    Spacer(Modifier.padding(vertical = 8.dp))
    SubmitButton {
        viewModel.submitLogin()
    }
    Row {
        TextClickable(text = stringResource(R.string.i_forgot_my_password)) {}
        TextClickable(text = stringResource(R.string.new_register), color = DefaultColorDark, onClick = newRegister)
    }
}

@Composable
private fun ColumnScope.NewRegisterContainer(viewModel: LoginViewModel, cancelRegister: () -> Unit) {

    var email by remember { mutableStateOf("") }

    EmailField { email = it }
    PasswordField {}
    PasswordField(stringResource(R.string.confirme_your_password)) {}
    Spacer(Modifier.padding(vertical = 8.dp))
    SubmitButton {}
    TextClickable(
        modifier = Modifier.align(Alignment.End),
        text = stringResource(R.string.cancel),
        color = DefaultColorDark,
        onClick = cancelRegister
    )
}

@Composable
private fun EmailField(isError: Boolean = false, textState: (String) -> Unit) {

    var text by remember { mutableStateOf("") }

    OutlinedTextField(
        value = text,
        onValueChange = {
            text = it
            textState(text)
        },
        label = { Text(stringResource(R.string.email)) },
        placeholder = { Text(stringResource(R.string.type_in_here)) },
        leadingIcon = { Icon(Icons.Outlined.Person, null) },
        shape = CircleShape,
        singleLine = true,
        isError = isError,
    )

    if (isError) {
        Text(
            text = stringResource(R.string.invalid_email),
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(start = 16.dp, top = 4.dp)
        )
    }
}

@Composable
private fun PasswordField(label: String = stringResource(R.string.password), isError: Boolean = false, passwordState: (String) -> Unit) {
    var passwordText by remember { mutableStateOf("") }
    var visible by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = passwordText,
        onValueChange = {
            passwordText = it
            passwordState(it)
        },
        label = { Text(label) },
        placeholder = { Text(stringResource(R.string.type_in_here)) },
        leadingIcon = { Icon(Icons.Outlined.Lock, contentDescription = null) },
        trailingIcon = {
            val image = if (visible) {
                painterResource(R.drawable.outline_remove_red_eye_24)
            } else {
                painterResource(R.drawable.outline_visibility_off_24)
            }

            IconButton(onClick = { visible = !visible }) {
                Icon(painter = image, contentDescription = null)
            }
        },
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = DefaultColor,
            focusedLeadingIconColor = DefaultColor,
            focusedLabelColor = DefaultColor,
            focusedPlaceholderColor = Color.LightGray
        ),
        shape = CircleShape,
        singleLine = true,
        visualTransformation = if (visible) VisualTransformation.None else PasswordVisualTransformation(),
        isError = isError
    )

    if (isError) {
        Text(
            text = stringResource(R.string.password_character_limit_message),
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(start = 16.dp, top = 4.dp)
        )
    }

}

@Composable
private fun TextClickable(modifier: Modifier = Modifier, text: String, color: Color = DefaultColor, onClick: () -> Unit) {
    TextButton(
        onClick,
        modifier = modifier,
        colors = ButtonDefaults.textButtonColors(contentColor = color)
    ) {
        Text(text)
    }
}

@Composable
private fun SubmitButton(submit: () -> Unit) {
    Button(
        submit,
        colors = ButtonDefaults.buttonColors(containerColor = DefaultColor)
    ) {
        Text(stringResource(R.string.submit))
    }
}

@Composable @Preview(showBackground = true)
private fun LoadingDialog() {
    Dialog({}) {
        Surface(
            shape = MaterialTheme.shapes.medium,
            tonalElevation = 8.dp
        ) {
            Column(Modifier.padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally) {
                CircularProgressIndicator()
                Text(stringResource(R.string.logging_in))
            }
        }
    }
}

@Composable
private fun ErrorDialog(errorMessage: String, onDismiss: () -> Unit) {
    Dialog(onDismiss) {
        Surface(
            shape = MaterialTheme.shapes.medium,
            tonalElevation = 8.dp
        ) {
            Column(Modifier.padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Outlined.Warning, null, tint = ErrorColor)
                Text(errorMessage, color = ErrorColor, textAlign = TextAlign.Center)
            }
        }
    }
}

@Preview(showSystemUi = true)
@Composable
private fun LoginScreenPreview() {
    LoginScreen(rememberNavController())
}

@Preview(showBackground = true)
@Composable
private fun NewRegisterContainerPreview() {
    Column(
        Modifier
            .padding(vertical = 16.dp)
            .background(Color.White, shape = RoundedCornerShape(24.dp))
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        NewRegisterContainer(hiltViewModel()) {}
    }
}