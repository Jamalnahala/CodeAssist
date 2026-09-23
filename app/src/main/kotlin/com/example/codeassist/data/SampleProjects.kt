package com.example.codeassist.data

import com.example.codeassist.model.CodeFile
import com.example.codeassist.model.Project
import com.example.codeassist.model.ProjectType

object SampleProjects {

    val composeAppProject = Project(
        id = "proj_compose_starter",
        name = "MyComposeApp",
        packageName = "com.example.mycomposeapp",
        type = ProjectType.ANDROID_COMPOSE,
        minSdk = 26,
        targetSdk = 36,
        rootFiles = listOf(
            CodeFile(
                id = "f_app_manifest",
                name = "AndroidManifest.xml",
                path = "app/src/main/AndroidManifest.xml",
                content = """<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android">
    <application
        android:allowBackup="true"
        android:icon="@mipmap/ic_launcher"
        android:label="MyComposeApp"
        android:theme="@style/Theme.Material3.DayNight.NoActionBar">
        <activity
            android:name=".MainActivity"
            android:exported="true">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>
    </application>
</manifest>"""
            ),
            CodeFile(
                id = "f_main_activity",
                name = "MainActivity.kt",
                path = "app/src/main/kotlin/com/example/mycomposeapp/MainActivity.kt",
                content = """package com.example.mycomposeapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    CounterScreen()
                }
            }
        }
    }
}

@Composable
fun CounterScreen() {
    var count by remember { mutableStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Welcome to CodeAssist!",
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Button clicks: ${'$'}count",
            style = MaterialTheme.typography.bodyLarge
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = { count++ }
        ) {
            Text("Increment Counter")
        }
    }
}"""
            ),
            CodeFile(
                id = "f_build_gradle",
                name = "build.gradle.kts",
                path = "app/build.gradle.kts",
                content = """plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.example.mycomposeapp"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.mycomposeapp"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.0.0"
    }

    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.material3)
}"""
            ),
            CodeFile(
                id = "f_readme",
                name = "README.md",
                path = "README.md",
                content = """# MyComposeApp

Built on-device using CodeAssist Mobile IDE!

## Features
- Jetpack Compose M3 UI
- Clean architecture
- On-device compilation & APK install
"""
            )
        )
    )

    val game2048Project = Project(
        id = "proj_2048_game",
        name = "Game2048",
        packageName = "com.example.game2048",
        type = ProjectType.ANDROID_COMPOSE,
        minSdk = 26,
        targetSdk = 36,
        rootFiles = listOf(
            CodeFile(
                id = "g_main",
                name = "GameActivity.kt",
                path = "app/src/main/kotlin/com/example/game2048/GameActivity.kt",
                content = """package com.example.game2048

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun GameBoard() {
    var score by remember { mutableStateOf(256) }
    val board = remember {
        mutableStateListOf(
            listOf(2, 4, 8, 16),
            listOf(32, 64, 128, 256),
            listOf(0, 2, 4, 8),
            listOf(0, 0, 2, 2)
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("2048", style = MaterialTheme.typography.headlineLarge)
            Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
                Text(
                    text = "SCORE: ${'$'}score",
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
        // 4x4 Grid
        Column(
            modifier = Modifier
                .background(Color(0xFFBBADA0), RoundedCornerShape(8.dp))
                .padding(8.dp)
        ) {
            for (row in board) {
                Row {
                    for (cell in row) {
                        Box(
                            modifier = Modifier
                                .size(70.dp)
                                .padding(4.dp)
                                .background(if (cell == 0) Color(0xFFCDC1B4) else Color(0xFFEDC22E), RoundedCornerShape(4.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            if (cell > 0) {
                                Text(
                                    text = cell.toString(),
                                    style = MaterialTheme.typography.titleLarge,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}"""
            ),
            CodeFile(
                id = "g_build",
                name = "build.gradle.kts",
                path = "app/build.gradle.kts",
                content = """plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
}"""
            )
        )
    )

    val calculatorProject = Project(
        id = "proj_calculator",
        name = "QuickCalculator",
        packageName = "com.example.calculator",
        type = ProjectType.ANDROID_VIEWS,
        minSdk = 26,
        targetSdk = 36,
        rootFiles = listOf(
            CodeFile(
                id = "calc_activity",
                name = "MainActivity.java",
                path = "app/src/main/java/com/example/calculator/MainActivity.java",
                content = """package com.example.calculator;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private TextView display;
    private double currentResult = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        display = findViewById(R.id.txt_display);
    }
}"""
            ),
            CodeFile(
                id = "calc_layout",
                name = "activity_main.xml",
                path = "app/src/main/res/layout/activity_main.xml",
                content = """<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical"
    android:padding="16dp"
    android:background="#1E1E1E">

    <TextView
        android:id="@+id/txt_display"
        android:layout_width="match_parent"
        android:layout_height="120dp"
        android:gravity="bottom|end"
        android:text="1,024"
        android:textColor="#FFFFFF"
        android:textSize="48sp" />

    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:orientation="horizontal"
        android:layout_marginTop="24dp">
        <Button
            android:id="@+id/btn_c"
            android:layout_width="0dp"
            android:layout_height="64dp"
            android:layout_weight="1"
            android:text="C" />
        <Button
            android:id="@+id/btn_div"
            android:layout_width="0dp"
            android:layout_height="64dp"
            android:layout_weight="1"
            android:text="/" />
        <Button
            android:id="@+id/btn_mul"
            android:layout_width="0dp"
            android:layout_height="64dp"
            android:layout_weight="1"
            android:text="*" />
        <Button
            android:id="@+id/btn_sub"
            android:layout_width="0dp"
            android:layout_height="64dp"
            android:layout_weight="1"
            android:text="-" />
    </LinearLayout>
</LinearLayout>"""
            )
        )
    )

    val all = listOf(composeAppProject, game2048Project, calculatorProject)
}
