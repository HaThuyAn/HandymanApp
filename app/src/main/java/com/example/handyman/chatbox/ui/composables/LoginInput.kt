package com.example.handyman.chatbox.ui.composables

import androidx.compose.foundation.layout.*
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp


@Composable
fun LoginInput(
    email: String,
    onEmailChange: (String) -> Unit,
    password: String,
    onPasswordChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
    ) {
        Text(
            text = "Email",
            color = Color(0xff121c2d),
            lineHeight = 1.43.em,
            style = TextStyle(
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            ),
            modifier = Modifier.padding(vertical = 5.dp)
        )

        OutlinedTextField(
            value = email,
            onValueChange = onEmailChange,
            singleLine = true,
            maxLines = 1,
            label = {
                Text(
                    text = "email@example.com",
                    color = Color(0xff8891aa),
                    lineHeight = 1.43.em,
                    style = TextStyle(
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                )
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(18.dp))

        Text(
            text = "Password",
            color = Color(0xff121c2d),
            lineHeight = 1.43.em,
            style = TextStyle(
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            ),
            modifier = Modifier.padding(vertical = 5.dp)
        )

        OutlinedTextField(
            value = password,
            onValueChange = onPasswordChange,
            singleLine = true,
            maxLines = 1,
            label = {
                Text(
                    text = "password",
                    color = Color(0xff8891aa),
                    lineHeight = 1.43.em,
                    style = TextStyle(
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                )
            },
            modifier = Modifier.fillMaxWidth()
        )
    }

}

@Preview(widthDp = 428, heightDp = 926)
@Composable
private fun UserLoginPreview() {
}
