package com.hisham.fdroidstore

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import com.hisham.fdroidstore.ui.StoreHomeScreen
import com.hisham.fdroidstore.ui.StoreViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: StoreViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    StoreHomeScreen(viewModel = viewModel)
                }
            }
        }
    }
}
