package com.atelier.ims

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import com.atelier.ims.navigation.AtelierNavGraph
import com.atelier.ims.ui.theme.AtelierTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AtelierTheme {
                val imsViewModel: ImsViewModel = viewModel()
                AtelierNavGraph(viewModel = imsViewModel)
            }
        }
    }
}
