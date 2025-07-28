package br.com.qtota.ui.screen.auth

import android.widget.Toast
import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
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
import br.com.qtota.ui.navigation.AppRoute
import br.com.qtota.ui.state_handler.UIState
import br.com.qtota.ui.theme.DefaultColor
import br.com.qtota.ui.theme.DefaultColorDark
import br.com.qtota.ui.theme.ErrorColor
import br.com.qtota.ui.theme.GradientBackground
import com.composables.icons.lucide.AtSign
import com.composables.icons.lucide.Eye
import com.composables.icons.lucide.EyeOff
import com.composables.icons.lucide.Lock
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.TriangleAlert
import com.composables.icons.lucide.User

@Composable
internal fun AuthScreen(navController: NavHostController) {

    val viewModel: AuthViewModel = hiltViewModel()

    var newRegister by remember { mutableStateOf(false) }

    BackHandler(newRegister) { newRegister = false }

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

            if(newRegister) {
                NewRegisterContainer(navController) {
                    newRegister = false
                }
            } else {
                LoginContainer(navController) {
                    newRegister = true
                }
            }

        }

        TextButton(
            {
                viewModel.setNotFirstAccess()
                navigateToNextScreen(navController)
            },
            colors = ButtonDefaults.textButtonColors(contentColor = Color.White),
        ) { Text(stringResource(R.string.continue_without_logging_in)) }

    }

}

@Composable
private fun LoginContainer(navController: NavHostController, newRegister: () -> Unit) {

    val viewModel: LoginViewModel = hiltViewModel()

    val email by viewModel.emailState.collectAsState()
    val validEmail by viewModel.validEmail.collectAsState()

    val password by viewModel.passwordState.collectAsState()
    val validPassword by viewModel.validPassword.collectAsState()

    val loginState by viewModel.loginState.collectAsState()

    EmailField(email,!validEmail, viewModel::setEmail)
    PasswordField(
        password,
        error = stringResource(R.string.password_character_limit_message).takeUnless { validPassword },
        onValueChange = viewModel::setPassword
    )
    Spacer(Modifier.padding(vertical = 8.dp))
    SubmitButton(viewModel::submitLogin)
    Row {
        TextClickable(text = stringResource(R.string.i_forgot_my_password)) {}
        TextClickable(text = stringResource(R.string.new_register), color = DefaultColorDark, onClick = newRegister)
    }

    when(loginState) {
        null -> Unit
        is UIState.Loading -> LoadingDialog()
        is UIState.Error -> ErrorDialog((loginState as UIState.Error).description) {
            viewModel.resetLoginState()
        }
        is UIState.Success -> navigateToNextScreen(navController)
    }

}

@Composable
private fun ColumnScope.NewRegisterContainer(navController: NavHostController, cancelRegister: () -> Unit) {

    val viewModel: RegisterViewModel = hiltViewModel()

    val name by viewModel.nameState.collectAsState()
    val validName by viewModel.validName.collectAsState()

    val email by viewModel.emailState.collectAsState()
    val validEmail by viewModel.validEmail.collectAsState()

    val password by viewModel.passwordState.collectAsState()
    val validPassword by viewModel.validPassword.collectAsState()

    val confirmPassword by viewModel.confirmPasswordState.collectAsState()
    val validConfirmPassword by viewModel.validConfirmPassword.collectAsState()

    val registerState by viewModel.registerState.collectAsState()

    NameField(name, !validName, viewModel::setName)
    EmailField(email, !validEmail, viewModel::setEmail)
    PasswordField(
        password,
        error = stringResource(R.string.password_character_limit_message).takeUnless { validPassword },
        onValueChange = viewModel::setPassword
    )
    PasswordField(
        confirmPassword,
        stringResource(R.string.confirme_your_password),
        stringResource(R.string.passwords_dont_match).takeUnless { validConfirmPassword },
        viewModel::setConfirmPassword
    )
    Spacer(Modifier.padding(vertical = 8.dp))
    SubmitButton(viewModel::submitRegister)
    TextClickable(
        modifier = Modifier.align(Alignment.End),
        text = stringResource(R.string.cancel),
        color = DefaultColorDark,
        onClick = cancelRegister
    )

    when(registerState) {
        null -> Unit
        is UIState.Loading -> LoadingDialog()
        is UIState.Error -> ErrorDialog((registerState as UIState.Error).description) {
            viewModel.resetRegisterState()
        }
        is UIState.Success -> {
            navigateToNextScreen(navController)
            Toast.makeText(LocalContext.current, "Usuário cadastrado com sucesso", Toast.LENGTH_LONG).show()
        }
    }

}

@Composable
private fun NameField(text: String, isError: Boolean = false, onValueChange: (String) -> Unit) {
    OutlinedTextField(
        value = text,
        onValueChange = onValueChange,
        label = { Text(stringResource(R.string.name)) },
        placeholder = { Text(stringResource(R.string.type_in_here)) },
        leadingIcon = { Icon(Lucide.User, null) },
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = DefaultColor,
            focusedLeadingIconColor = DefaultColor,
            focusedLabelColor = DefaultColor,
            focusedPlaceholderColor = Color.LightGray
        ),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Next
        ),
        shape = CircleShape,
        singleLine = true,
        isError = isError,
    )
}

@Composable
private fun EmailField(text: String, isError: Boolean = false, onValueChange: (String) -> Unit) {
    OutlinedTextField(
        value = text,
        onValueChange = onValueChange,
        label = { Text(stringResource(R.string.email)) },
        placeholder = { Text(stringResource(R.string.type_in_here)) },
        leadingIcon = { Icon(Lucide.AtSign, null) },
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = DefaultColor,
            focusedLeadingIconColor = DefaultColor,
            focusedLabelColor = DefaultColor,
            focusedPlaceholderColor = Color.LightGray
        ),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Email,
            imeAction = ImeAction.Next
        ),
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
private fun PasswordField(text: String, label: String = stringResource(R.string.password), error: String? = null, onValueChange: (String) -> Unit) {

    var passwordVisible by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = text,
        onValueChange = onValueChange,
        label = { Text(label) },
        placeholder = { Text(stringResource(R.string.type_in_here)) },
        leadingIcon = { Icon(Lucide.Lock, contentDescription = null) },
        trailingIcon = {
            val image = if (passwordVisible) {
                Lucide.Eye
            } else {
                Lucide.EyeOff
            }

            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                Icon(image, contentDescription = null)
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
        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        isError = error != null
    )

    if (error != null) {
        Text(
            text = error,
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
                Icon(Lucide.TriangleAlert, null, tint = ErrorColor)
                Text(errorMessage, color = ErrorColor, textAlign = TextAlign.Center)
            }
        }
    }
}

internal fun navigateToNextScreen(navController: NavHostController) {
    if (navController.previousBackStackEntry == null) {
        navController.navigate(AppRoute.Main.route) {
            popUpTo(navController.graph.startDestinationId) { inclusive = true }
            launchSingleTop = true
        }
    } else {
        navController.popBackStack()
    }
}

@Preview(showSystemUi = true)
@Composable
private fun LoginScreenPreview() {
    AuthScreen(rememberNavController())
}