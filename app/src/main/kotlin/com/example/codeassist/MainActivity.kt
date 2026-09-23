package com.example.codeassist

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.example.codeassist.ui.CodeAssistApp
import com.example.codeassist.ui.theme.CodeAssistTheme
import com.example.codeassist.viewmodel.IdeViewModel

class MainActivity : ComponentActivity() {

    private val ideViewModel: IdeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            CodeAssistTheme(darkTheme = true) {
                CodeAssistApp(viewModel = ideViewModel)
            }
        }
    }
}
