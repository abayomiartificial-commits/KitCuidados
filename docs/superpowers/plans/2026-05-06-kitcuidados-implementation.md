# KitCuidados Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Aplicación Android completa para gestión de salud personal y familiar con módulos de medicamentos, síntomas, citas, diario y contactos de emergencia.

**Architecture:** Clean Architecture con MVVM. UI en Jetpack Compose, datos en Firebase Firestore, notificaciones locales con WorkManager.

**Tech Stack:** Kotlin 1.9, Jetpack Compose, Material 3, Hilt, Firebase Auth/Firestore, Room, WorkManager, Vico Charts

---

## 1. Project Setup

### Task 1: Inicializar Proyecto Android con Gradle Wrapper

**Files:**
- Create: `build.gradle.kts`
- Create: `settings.gradle.kts`
- Create: `gradle/wrapper/gradle-wrapper.properties`
- Create: `gradlew`
- Create: `gradlew.bat`

- [ ] **Step 1: Create basic project files**

Crear estructura básica con Gradle Wrapper para Android.

```kotlin
// build.gradle.kts (root)
plugins {
    id("com.android.application") version "8.2.0" apply false
    id("org.jetbrains.kotlin.android") version "1.9.20" apply false
    id("com.google.dagger.hilt.android") version "2.48.1" apply false
    id("com.google.gms.google-services") version "4.4.0" apply false
}
```

- [ ] **Step 2: Verify Gradle wrapper works**

Run: `./gradlew -v`
Expected: Gradle 8.2+ available

- [ ] **Step 3: Commit**

```bash
git add .
git commit -m "chore: setup Gradle wrapper and project base"
```

---

### Task 2: Configurar Build.gradle de Módulo App

**Files:**
- Create: `app/build.gradle.kts`
- Create: `app/src/main/AndroidManifest.xml`
- Create: `app/src/main/res/values/strings.xml`
- Create: `app/src/main/res/values/themes.xml`
- Create: `app/src/main/res/drawable/ic_launcher_foreground.xml`

- [ ] **Step 1: Create app module build file**

```kotlin
// app/build.gradle.kts
plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.dagger.hilt.android")
    id("com.google.gms.google-services")
    kotlin("kapt")
}

android {
    namespace = "com.kitcuidados.app"
    compileSdk = 34
    defaultConfig {
        applicationId = "com.kitcuidados.app"
        minSdk = 26
        targetSdk = 34
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.5"
    }
}

dependencies {
    // Compose BOM
    val composeBom = platform("androidx.compose:compose-bom:2024.02.00")
    implementation(composeBom)
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-extended")
    
    // Navigation
    implementation("androidx.navigation:navigation-compose:2.7.6")
    implementation("androidx.hilt:hilt-navigation-compose:1.1.0")
    
    // Lifecycle
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.6.2")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.6.2")
    
    // Hilt
    implementation("com.google.dagger:hilt-android:2.48.1")
    kapt("com.google.dagger:hilt-android-compiler:2.48.1")
    
    // Firebase
    implementation(platform("com.google.firebase:firebase-bom:32.7.0"))
    implementation("com.google.firebase:firebase-auth-ktx")
    implementation("com.google.firebase:firebase-firestore-ktx")
    
    // Room
    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    kapt("androidx.room:room-compiler:2.6.1")
    
    // WorkManager
    implementation("androidx.work:work-runtime-ktx:2.9.0")
    implementation("androidx.hilt:hilt-work:1.1.0")
    kapt("androidx.hilt:hilt-compiler:1.1.0")
    
    // Vico Charts
    implementation("com.patrykandpatrick.vico:compose-m3:1.13.1")
    
    // DataStore
    implementation("androidx.datastore:datastore-preferences:1.0.0")
    
    // Debug
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
}

kapt {
    correctErrorTypes = true
}
```

- [ ] **Step 2: Create AndroidManifest.xml**

```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android">
    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission android:name="android.permission.POST_NOTIFICATIONS" />
    <uses-permission android:name="android.permission.SCHEDULE_EXACT_ALARM" />
    <uses-permission android:name="android.permission.CALL_PHONE" />
    <uses-permission android:name="android.permission.RECEIVE_BOOT_COMPLETED" />
    
    <application
        android:name=".KitCuidadosApp"
        android:allowBackup="true"
        android:icon="@mipmap/ic_launcher"
        android:label="@string/app_name"
        android:roundIcon="@mipmap/ic_launcher_round"
        android:supportsRtl="true"
        android:theme="@style/Theme.KitCuidados">
        
        <activity
            android:name=".MainActivity"
            android:exported="true"
            android:theme="@style/Theme.KitCuidados">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>
        
    </application>
</manifest>
```

- [ ] **Step 3: Create resource files**

```xml
<!-- strings.xml -->
<resources>
    <string name="app_name">KitCuidados</string>
</resources>
```

```xml
<!-- themes.xml -->
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <style name="Theme.KitCuidados" parent="android:Theme.Material.Light.NoActionBar" />
</resources>
```

- [ ] **Step 4: Create Application class and MainActivity**

```kotlin
// Application class
package com.kitcuidados.app

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class KitCuidadosApp : Application()
```

```kotlin
// MainActivity
package com.kitcuidados.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.kitcuidados.app.ui.theme.KitCuidadosTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            KitCuidadosTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    KitCuidadosNavHost()
                }
            }
        }
    }
}
```

- [ ] **Step 5: Create theme files**

Crear `app/src/main/java/com/kitcuidados/app/ui/theme/Theme.kt` y colores.

- [ ] **Step 6: Commit**

```bash
git add app/build.gradle.kts app/src/main/
git commit -m "chore: configure app module with dependencies"
```

---

## 2. Autenticación (Firebase Auth)

### Task 3: Módulo de Autenticación

**Files:**
- Create: `app/src/main/java/com/kitcuidados/app/data/repository/AuthRepository.kt`
- Create: `app/src/main/java/com/kitcuidados/app/domain/model/User.kt`
- Create: `app/src/main/java/com/kitcuidados/app/ui/auth/AuthViewModel.kt`
- Create: `app/src/main/java/com/kitcuidados/app/ui/auth/LoginScreen.kt`
- Create: `app/src/main/java/com/kitcuidados/app/ui/auth/RegisterScreen.kt`
- Create: `app/src/main/java/com/kitcuidados/app/di/AuthModule.kt`

- [ ] **Step 1: Create User domain model**

```kotlin
package com.kitcuidados.app.domain.model

data class User(
    val id: String = "",
    val email: String = "",
    val displayName: String = ""
)
```

- [ ] **Step 2: Create AuthRepository**

```kotlin
package com.kitcuidados.app.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.kitcuidados.app.domain.model.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val auth: FirebaseAuth
) {
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: Flow<User?> = _currentUser.asStateFlow()
    
    init {
        auth.addAuthStateListener { firebaseAuth ->
            _currentUser.value = firebaseAuth.currentUser?.toUser()
        }
    }
    
    suspend fun login(email: String, password: String): Result<User> {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            Result.success(result.user?.toUser() ?: throw Exception("User is null"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun register(email: String, password: String, displayName: String): Result<User> {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            result.user?.updateProfile(displayName(displayName))
            Result.success(result.user?.toUser() ?: throw Exception("User is null"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    fun logout() {
        auth.signOut()
    }
    
    fun isLoggedIn(): Boolean = auth.currentUser != null
    
    private fun FirebaseUser.toUser() = User(
        id = uid,
        email = email ?: "",
        displayName = displayName ?: ""
    )
}
```

- [ ] **Step 3: Create AuthViewModel**

```kotlin
package com.kitcuidados.app.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kitcuidados.app.data.repository.AuthRepository
import com.kitcuidados.app.domain.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AuthState(
    val isLoading: Boolean = false,
    val user: User? = null,
    val error: String? = null
)

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {
    
    private val _state = MutableStateFlow(AuthState())
    val state: StateFlow<AuthState> = _state.asStateFlow()
    
    fun login(email: String, password: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            val result = authRepository.login(email, password)
            _state.value = _state.value.copy(
                isLoading = false,
                user = result.getOrNull(),
                error = result.exceptionOrNull()?.message
            )
        }
    }
    
    fun register(email: String, password: String, displayName: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            val result = authRepository.register(email, password, displayName)
            _state.value = _state.value.copy(
                isLoading = false,
                user = result.getOrNull(),
                error = result.exceptionOrNull()?.message
            )
        }
    }
    
    fun clearError() {
        _state.value = _state.value.copy(error = null)
    }
}
```

- [ ] **Step 4: Create LoginScreen**

```kotlin
package com.kitcuidados.app.ui.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun LoginScreen(
    onNavigateToRegister: () -> Unit,
    onLoginSuccess: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    
    LaunchedEffect(state.user) {
        if (state.user != null) onLoginSuccess()
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "KitCuidados",
            style = MaterialTheme.typography.headlineLarge
        )
        Spacer(modifier = Modifier.height(32.dp))
        
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña") },
            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(24.dp))
        
        if (state.error != null) {
            Text(
                text = state.error!!,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
        
        Button(
            onClick = { viewModel.login(email, password) },
            enabled = !state.isLoading && email.isNotBlank() && password.isNotBlank(),
            modifier = Modifier.fillMaxWidth()
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp))
            } else {
                Text("Iniciar Sesión")
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        TextButton(onClick = onNavigateToRegister) {
            Text("¿No tienes cuenta? Regístrate")
        }
    }
}
```

- [ ] **Step 5: Create RegisterScreen**

Similar a LoginScreen con campo adicional para nombre.

- [ ] **Step 6: Create AuthModule (DI)**

```kotlin
package com.kitcuidados.app.di

import com.google.firebase.auth.FirebaseAuth
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AuthModule {
    
    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()
}
```

- [ ] **Step 7: Commit**

```bash
git add app/src/main/java/com/kitcuidados/app/data/repository/
git commit -m "feat: add Firebase authentication module"
```

---

## 3. Datos y Repositorios

### Task 4: Modelos de Datos y Room Database

**Files:**
- Create: `app/src/main/java/com/kitcuidados/app/domain/model/Medication.kt`
- Create: `app/src/main/java/com/kitcuidados/app/domain/model/Symptom.kt`
- Create: `app/src/main/java/com/kitcuidados/app/domain/model/Appointment.kt`
- Create: `app/src/main/java/com/kitcuidados/app/domain/model/JournalEntry.kt`
- Create: `app/src/main/java/com/kitcuidados/app/domain/model/EmergencyContact.kt`
- Create: `app/src/main/java/com/kitcuidados/app/data/local/Converters.kt`
- Create: `app/src/main/java/com/kitcuidados/app/data/local/KitCuidadosDatabase.kt`
- Create: `app/src/main/java/com/kitcuidados/app/data/remote/FirestoreService.kt`
- Create: `app/src/main/java/com/kitcuidados/app/data/repository/HealthRepository.kt`

- [ ] **Step 1: Create domain models**

```kotlin
// Medication.kt
data class Medication(
    val id: String = "",
    val name: String = "",
    val dosage: String = "",
    val frequency: String = "",
    val schedules: List<String> = emptyList(),
    val notes: String = "",
    val isActive: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)

// Symptom.kt
data class Symptom(
    val id: String = "",
    val painLevel: Int = 1,
    val location: String = "",
    val notes: String = "",
    val recordedAt: Long = System.currentTimeMillis()
)

// Appointment.kt
data class Appointment(
    val id: String = "",
    val doctorName: String = "",
    val specialty: String = "",
    val dateTime: Long = System.currentTimeMillis(),
    val location: String = "",
    val notes: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

// JournalEntry.kt
data class JournalEntry(
    val id: String = "",
    val mood: String = "",
    val energyLevel: Int = 3,
    val sleepQuality: Int = 3,
    val notes: String = "",
    val recordedAt: Long = System.currentTimeMillis()
)

// EmergencyContact.kt
data class EmergencyContact(
    val id: String = "",
    val name: String = "",
    val phone: String = "",
    val relationship: String = "",
    val isPrimary: Boolean = false
)
```

- [ ] **Step 2: Create Room Database**

```kotlin
package com.kitcuidados.app.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "medications")
data class MedicationEntity(
    @PrimaryKey val id: String,
    val name: String,
    val dosage: String,
    val frequency: String,
    val schedules: String, // JSON string
    val notes: String,
    val isActive: Boolean,
    val createdAt: Long,
    val userId: String
)

@Entity(tableName = "symptoms")
data class SymptomEntity(
    @PrimaryKey val id: String,
    val painLevel: Int,
    val location: String,
    val notes: String,
    val recordedAt: Long,
    val userId: String
)

@Entity(tableName = "appointments")
data class AppointmentEntity(
    @PrimaryKey val id: String,
    val doctorName: String,
    val specialty: String,
    val dateTime: Long,
    val location: String,
    val notes: String,
    val createdAt: Long,
    val userId: String
)

@Entity(tableName = "journal_entries")
data class JournalEntryEntity(
    @PrimaryKey val id: String,
    val mood: String,
    val energyLevel: Int,
    val sleepQuality: Int,
    val notes: String,
    val recordedAt: Long,
    val userId: String
)

@Entity(tableName = "emergency_contacts")
data class EmergencyContactEntity(
    @PrimaryKey val id: String,
    val name: String,
    val phone: String,
    val relationship: String,
    val isPrimary: Boolean,
    val userId: String
)

@Dao
interface MedicationDao {
    @Query("SELECT * FROM medications WHERE userId = :userId")
    fun getMedications(userId: String): Flow<List<MedicationEntity>>
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(medication: MedicationEntity)
    @Delete
    suspend fun delete(medication: MedicationEntity)
}

@Dao
interface SymptomDao {
    @Query("SELECT * FROM symptoms WHERE userId = :userId ORDER BY recordedAt DESC")
    fun getSymptoms(userId: String): Flow<List<SymptomEntity>>
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(symptom: SymptomEntity)
}

@Dao
interface AppointmentDao {
    @Query("SELECT * FROM appointments WHERE userId = :userId ORDER BY dateTime ASC")
    fun getAppointments(userId: String): Flow<List<AppointmentEntity>>
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(appointment: AppointmentEntity)
    @Delete
    suspend fun delete(appointment: AppointmentEntity)
}

@Dao
interface JournalDao {
    @Query("SELECT * FROM journal_entries WHERE userId = :userId ORDER BY recordedAt DESC")
    fun getEntries(userId: String): Flow<List<JournalEntryEntity>>
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entry: JournalEntryEntity)
}

@Dao
interface EmergencyContactDao {
    @Query("SELECT * FROM emergency_contacts WHERE userId = :userId")
    fun getContacts(userId: String): Flow<List<EmergencyContactEntity>>
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(contact: EmergencyContactEntity)
    @Delete
    suspend fun delete(contact: EmergencyContactEntity)
}

@Database(
    entities = [
        MedicationEntity::class,
        SymptomEntity::class,
        AppointmentEntity::class,
        JournalEntryEntity::class,
        EmergencyContactEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class KitCuidadosDatabase : RoomDatabase() {
    abstract fun medicationDao(): MedicationDao
    abstract fun symptomDao(): SymptomDao
    abstract fun appointmentDao(): AppointmentDao
    abstract fun journalDao(): JournalDao
    abstract fun emergencyContactDao(): EmergencyContactDao
}
```

- [ ] **Step 3: Create DatabaseModule**

```kotlin
package com.kitcuidados.app.di

import android.content.Context
import androidx.room.Room
import com.kitcuidados.app.data.local.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): KitCuidadosDatabase {
        return Room.databaseBuilder(
            context,
            KitCuidadosDatabase::class.java,
            "kitcuidados_db"
        ).build()
    }
    
    @Provides
    fun provideMedicationDao(db: KitCuidadosDatabase): MedicationDao = db.medicationDao()
    @Provides
    fun provideSymptomDao(db: KitCuidadosDatabase): SymptomDao = db.symptomDao()
    @Provides
    fun provideAppointmentDao(db: KitCuidadosDatabase): AppointmentDao = db.appointmentDao()
    @Provides
    fun provideJournalDao(db: KitCuidadosDatabase): JournalDao = db.journalDao()
    @Provides
    fun provideEmergencyContactDao(db: KitCuidadosDatabase): EmergencyContactDao = db.emergencyContactDao()
}
```

- [ ] **Step 4: Commit**

```bash
git add app/src/main/java/com/kitcuidados/app/domain/model/
git add app/src/main/java/com/kitcuidados/app/data/local/
git commit -m "feat: add domain models and Room database"
```

---

## 4. Módulos de la UI

### Task 5: Navigation y Dashboard

**Files:**
- Create: `app/src/main/java/com/kitcuidados/app/ui/navigation/NavHost.kt`
- Create: `app/src/main/java/com/kitcuidados/app/ui/navigation/Screen.kt`
- Create: `app/src/main/java/com/kitcuidados/app/ui/home/HomeViewModel.kt`
- Create: `app/src/main/java/com/kitcuidados/app/ui/home/HomeScreen.kt`

- [ ] **Step 1: Create Navigation**

```kotlin
package com.kitcuidados.app.ui.navigation

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
    object Home : Screen("home")
    object Medications : Screen("medications")
    object AddMedication : Screen("add_medication")
    object Symptoms : Screen("symptoms")
    object AddSymptom : Screen("add_symptom")
    object Appointments : Screen("appointments")
    object AddAppointment : Screen("add_appointment")
    object Journal : Screen("journal")
    object AddJournalEntry : Screen("add_journal")
    object EmergencyContacts : Screen("emergency_contacts")
    object AddEmergencyContact : Screen("add_emergency_contact")
    object Charts : Screen("charts")
}
```

```kotlin
package com.kitcuidados.app.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.kitcuidados.app.ui.auth.LoginScreen
import com.kitcuidados.app.ui.auth.RegisterScreen
import com.kitcuidados.app.ui.home.HomeScreen
import com.kitcuidados.app.ui.medications.MedicationsScreen
import com.kitcuidados.app.ui.medications.AddMedicationScreen
import com.kitcuidados.app.ui.symptoms.SymptomsScreen
import com.kitcuidados.app.ui.symptoms.AddSymptomScreen
import com.kitcuidados.app.ui.appointments.AppointmentsScreen
import com.kitcuidados.app.ui.appointments.AddAppointmentScreen
import com.kitcuidados.app.ui.journal.JournalScreen
import com.kitcuidados.app.ui.journal.AddJournalEntryScreen
import com.kitcuidados.app.ui.emergency.EmergencyContactsScreen
import com.kitcuidados.app.ui.emergency.AddEmergencyContactScreen
import com.kitcuidados.app.ui.charts.ChartsScreen
import com.kitcuidados.app.data.repository.AuthRepository
import com.kitcuidados.app.ui.auth.AuthViewModel
import androidx.hilt.navigation.compose.hiltViewModel

data class BottomNavItem(
    val route: String,
    val icon: ImageVector,
    val label: String
)

val bottomNavItems = listOf(
    BottomNavItem(Screen.Home.route, Icons.Default.Home, "Inicio"),
    BottomNavItem(Screen.Medications.route, Icons.Default.Medication, "Medicamentos"),
    BottomNavItem(Screen.Symptoms.route, Icons.Default.Healing, "Síntomas"),
    BottomNavItem(Screen.Appointments.route, Icons.Default.CalendarMonth, "Citas"),
    BottomNavItem(Screen.Journal.route, Icons.Default.Book, "Diario"),
    BottomNavItem(Screen.EmergencyContacts.route, Icons.Default.Phone, "Emergencia")
)

@Composable
fun KitCuidadosNavHost(
    navController: NavHostController = rememberNavController(),
    authRepository: AuthRepository
) {
    val isLoggedIn = authRepository.isLoggedIn()
    val startDestination = if (isLoggedIn) Screen.Home.route else Screen.Login.route
    
    Scaffold(
        bottomBar = {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentDestination = navBackStackEntry?.destination
            
            if (currentDestination?.route in bottomNavItems.map { it.route }) {
                NavigationBar {
                    bottomNavItems.forEach { item ->
                        NavigationBarItem(
                            icon = { Icon(item.icon, contentDescription = item.label) },
                            label = { Text(item.label) },
                            selected = currentDestination?.hierarchy?.any { it.route == item.route } == true,
                            onClick = {
                                navController.navigate(item.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(Screen.Login.route) {
                LoginScreen(
                    onNavigateToRegister = { navController.navigate(Screen.Register.route) },
                    onLoginSuccess = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    }
                )
            }
            composable(Screen.Register.route) {
                RegisterScreen(
                    onNavigateToLogin = { navController.popBackStack() },
                    onRegisterSuccess = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    }
                )
            }
            composable(Screen.Home.route) {
                HomeScreen(navController = navController)
            }
            composable(Screen.Medications.route) {
                MedicationsScreen(navController = navController)
            }
            composable(Screen.AddMedication.route) {
                AddMedicationScreen(navController = navController)
            }
            composable(Screen.Symptoms.route) {
                SymptomsScreen(navController = navController)
            }
            composable(Screen.AddSymptom.route) {
                AddSymptomScreen(navController = navController)
            }
            composable(Screen.Appointments.route) {
                AppointmentsScreen(navController = navController)
            }
            composable(Screen.AddAppointment.route) {
                AddAppointmentScreen(navController = navController)
            }
            composable(Screen.Journal.route) {
                JournalScreen(navController = navController)
            }
            composable(Screen.AddJournalEntry.route) {
                AddJournalEntryScreen(navController = navController)
            }
            composable(Screen.EmergencyContacts.route) {
                EmergencyContactsScreen(navController = navController)
            }
            composable(Screen.AddEmergencyContact.route) {
                AddEmergencyContactScreen(navController = navController)
            }
            composable(Screen.Charts.route) {
                ChartsScreen(navController = navController)
            }
        }
    }
}
```

- [ ] **Step 2: Create HomeViewModel and HomeScreen**

```kotlin
// HomeViewModel
package com.kitcuidados.app.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kitcuidados.app.data.repository.HealthRepository
import com.kitcuidados.app.domain.model.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

data class HomeState(
    val isLoading: Boolean = false,
    val upcomingMedications: List<Medication> = emptyList(),
    val upcomingAppointments: List<Appointment> = emptyList(),
    val recentSymptoms: List<Symptom> = emptyList(),
    val recentJournal: List<JournalEntry> = emptyList()
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val healthRepository: HealthRepository
) : ViewModel() {
    
    private val _state = MutableStateFlow(HomeState())
    val state: StateFlow<HomeState> = _state.asStateFlow()
    
    fun loadDashboard(userId: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            
            combine(
                healthRepository.getActiveMedications(userId),
                healthRepository.getUpcomingAppointments(userId),
                healthRepository.getRecentSymptoms(userId),
                healthRepository.getRecentJournalEntries(userId)
            ) { meds, appts, symptoms, journal ->
                HomeState(
                    isLoading = false,
                    upcomingMedications = meds.take(3),
                    upcomingAppointments = appts.take(3),
                    recentSymptoms = symptoms.take(1),
                    recentJournal = journal.take(1)
                )
            }.collect { newState ->
                _state.value = newState
            }
        }
    }
}
```

```kotlin
// HomeScreen
package com.kitcuidados.app.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.kitcuidados.app.ui.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("KitCuidados") },
                actions = {
                    IconButton(onClick = { navController.navigate(Screen.Charts.route) }) {
                        Icon(Icons.Default.BarChart, contentDescription = "Gráficos")
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Today's Summary
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Resumen del Día",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Tienes ${state.upcomingMedications.size} medicamentos pendientes",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
            
            // Upcoming Medications
            item {
                Text(
                    text = "Próximos Medicamentos",
                    style = MaterialTheme.typography.titleMedium
                )
            }
            
            if (state.upcomingMedications.isEmpty()) {
                item {
                    Text(
                        text = "No hay medicamentos programados",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                items(state.upcomingMedications.size) { index ->
                    val med = state.upcomingMedications[index]
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Medication,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(med.name, style = MaterialTheme.typography.bodyLarge)
                                Text(
                                    "${med.dosage} - ${med.schedules.firstOrNull() ?: ""}",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }
                }
            }
            
            // Upcoming Appointments
            item {
                Text(
                    text = "Próximas Citas",
                    style = MaterialTheme.typography.titleMedium
                )
            }
            
            if (state.upcomingAppointments.isEmpty()) {
                item {
                    Text(
                        text = "No hay citas programadas",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                items(state.upcomingAppointments.size) { index ->
                    val appt = state.upcomingAppointments[index]
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.CalendarMonth,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.secondary
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(appt.doctorName, style = MaterialTheme.typography.bodyLarge)
                                Text(
                                    appt.specialty,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }
                }
            }
            
            // Quick Actions
            item {
                Text(
                    text = "Acciones Rápidas",
                    style = MaterialTheme.typography.titleMedium
                )
            }
            
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = { navController.navigate(Screen.AddSymptom.route) },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Healing, contentDescription = null)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Síntoma")
                    }
                    OutlinedButton(
                        onClick = { navController.navigate(Screen.AddJournalEntry.route) },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Book, contentDescription = null)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Nota")
                    }
                }
            }
        }
    }
}
```

- [ ] **Step 3: Commit**

```bash
git add app/src/main/java/com/kitcuidados/app/ui/navigation/
git commit -m "feat: add navigation and home screen"
```

---

### Task 6: Módulo Medicamentos

**Files:**
- Create: `app/src/main/java/com/kitcuidados/app/ui/medications/MedicationsViewModel.kt`
- Create: `app/src/main/java/com/kitcuidados/app/ui/medications/MedicationsScreen.kt`
- Create: `app/src/main/java/com/kitcuidados/app/ui/medications/AddMedicationViewModel.kt`
- Create: `app/src/main/java/com/kitcuidados/app/ui/medications/AddMedicationScreen.kt`

- [ ] **Step 1: Create MedicationsViewModel**

```kotlin
package com.kitcuidados.app.ui.medications

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kitcuidados.app.data.repository.HealthRepository
import com.kitcuidados.app.domain.model.Medication
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class MedicationsState(
    val isLoading: Boolean = false,
    val medications: List<Medication> = emptyList(),
    val error: String? = null
)

@HiltViewModel
class MedicationsViewModel @Inject constructor(
    private val healthRepository: HealthRepository
) : ViewModel() {
    
    private val _state = MutableStateFlow(MedicationsState())
    val state: StateFlow<MedicationsState> = _state.asStateFlow()
    
    fun loadMedications(userId: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            healthRepository.getMedications(userId).collect { medications ->
                _state.value = _state.value.copy(
                    isLoading = false,
                    medications = medications
                )
            }
        }
    }
    
    fun deleteMedication(medication: Medication, userId: String) {
        viewModelScope.launch {
            healthRepository.deleteMedication(medication, userId)
        }
    }
}
```

- [ ] **Step 2: Create MedicationsScreen**

```kotlin
package com.kitcuidados.app.ui.medications

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.kitcuidados.app.domain.model.Medication
import com.kitcuidados.app.ui.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedicationsScreen(
    navController: NavController,
    viewModel: MedicationsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Medicamentos") }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate(Screen.AddMedication.route) }
            ) {
                Icon(Icons.Default.Add, contentDescription = "Agregar")
            }
        }
    ) { paddingValues ->
        if (state.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (state.medications.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Default.Medication,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        "No hay medicamentos",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    TextButton(onClick = { navController.navigate(Screen.AddMedication.route) }) {
                        Text("Agregar tu primer medicamento")
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(state.medications) { medication ->
                    MedicationCard(
                        medication = medication,
                        onDelete = { viewModel.deleteMedication(medication, "currentUser") }
                    )
                }
            }
        }
    }
}

@Composable
fun MedicationCard(
    medication: Medication,
    onDelete: () -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.Medication,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(40.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    medication.name,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    medication.dosage,
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    medication.frequency,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            IconButton(onClick = onDelete) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Eliminar",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}
```

- [ ] **Step 3: Create AddMedicationViewModel and Screen**

Similar estructura para agregar medicamentos con campos: nombre, dosis, frecuencia, horarios.

- [ ] **Step 4: Commit**

```bash
git add app/src/main/java/com/kitcuidados/app/ui/medications/
git commit -m "feat: add medications module"
```

---

### Task 7: Módulo Síntomas

**Files:**
- Create: `app/src/main/java/com/kitcuidados/app/ui/symptoms/SymptomsViewModel.kt`
- Create: `app/src/main/java/com/kitcuidados/app/ui/symptoms/SymptomsScreen.kt`
- Create: `app/src/main/java/com/kitcuidados/app/ui/symptoms/AddSymptomViewModel.kt`
- Create: `app/src/main/java/com/kitcuidados/app/ui/symptoms/AddSymptomScreen.kt`

- [ ] **Step 1: Create SymptomsScreen con registro de dolor 1-10**

Incluye slider para nivel de dolor, selector de ubicación, campo de notas.

- [ ] **Step 2: Commit**

```bash
git add app/src/main/java/com/kitcuidados/app/ui/symptoms/
git commit -m "feat: add symptoms module"
```

---

### Task 8: Módulo Citas

**Files:**
- Create: `app/src/main/java/com/kitcuidados/app/ui/appointments/AppointmentsViewModel.kt`
- Create: `app/src/main/java/com/kitcuidados/app/ui/appointments/AppointmentsScreen.kt`
- Create: `app/src/main/java/com/kitcuidados/app/ui/appointments/AddAppointmentViewModel.kt`
- Create: `app/src/main/java/com/kitcuidados/app/ui/appointments/AddAppointmentScreen.kt`

- [ ] **Step 1: Create AppointmentsScreen con calendario**

Incluye fecha, hora, doctor, especialidad, lugar, notas.

- [ ] **Step 2: Commit**

```bash
git add app/src/main/java/com/kitcuidados/app/ui/appointments/
git commit -m "feat: add appointments module"
```

---

### Task 9: Módulo Diario

**Files:**
- Create: `app/src/main/java/com/kitcuidados/app/ui/journal/JournalViewModel.kt`
- Create: `app/src/main/java/com/kitcuidados/app/ui/journal/JournalScreen.kt`
- Create: `app/src/main/java/com/kitcuidados/app/ui/journal/AddJournalEntryViewModel.kt`
- Create: `app/src/main/java/com/kitcuidados/app/ui/journal/AddJournalEntryScreen.kt`

- [ ] **Step 1: Create JournalScreen**

Incluye selector de estado emocional (emoji), nivel de energía (1-5), calidad de sueño (1-5), notas.

- [ ] **Step 2: Commit**

```bash
git add app/src/main/java/com/kitcuidados/app/ui/journal/
git commit -m "feat: add journal module"
```

---

### Task 10: Módulo Contactos de Emergencia

**Files:**
- Create: `app/src/main/java/com/kitcuidados/app/ui/emergency/EmergencyContactsViewModel.kt`
- Create: `app/src/main/java/com/kitcuidados/app/ui/emergency/EmergencyContactsScreen.kt`
- Create: `app/src/main/java/com/kitcuidados/app/ui/emergency/AddEmergencyContactViewModel.kt`
- Create: `app/src/main/java/com/kitcuidados/app/ui/emergency/AddEmergencyContactScreen.kt`

- [ ] **Step 1: Create EmergencyContactsScreen**

Lista de contactos con llamada directa con un tap. Campos: nombre, teléfono, relación.

- [ ] **Step 2: Commit**

```bash
git add app/src/main/java/com/kitcuidados/app/ui/emergency/
git commit -m "feat: add emergency contacts module"
```

---

### Task 11: Módulo Gráficos

**Files:**
- Create: `app/src/main/java/com/kitcuidados/app/ui/charts/ChartsViewModel.kt`
- Create: `app/src/main/java/com/kitcuidados/app/ui/charts/ChartsScreen.kt`

- [ ] **Step 1: Create ChartsScreen con Vico**

Gráfico de línea para evolución de síntomas. Gráfico de barras para adherencia a medicamentos.

```kotlin
package com.kitcuidados.app.ui.charts

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.patrykandpatrick.vico.compose.axis.horizontal.rememberBottomAxis
import com.patrykandpatrick.vico.compose.axis.vertical.rememberStartAxis
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.line.lineChart
import com.patrykandpatrick.vico.core.entry.entryModelOf

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChartsScreen(
    navController: NavController,
    viewModel: ChartsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Gráficos") })
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Symptom Evolution Chart
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "Evolución de Síntomas",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    if (state.symptomData.isNotEmpty()) {
                        Chart(
                            chart = lineChart(),
                            model = entryModelOf(*state.symptomData.toTypedArray()),
                            startAxis = rememberStartAxis(),
                            bottomAxis = rememberBottomAxis(),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                        )
                    } else {
                        Text("No hay datos suficientes")
                    }
                }
            }
            
            // Medication Adherence Chart
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "Adherencia a Medicamentos",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Text("Tomados: ${state.takenCount}")
                    Text("Omitidos: ${state.skippedCount}")
                    Text("Pendientes: ${state.pendingCount}")
                }
            }
        }
    }
}
```

- [ ] **Step 2: Commit**

```bash
git add app/src/main/java/com/kitcuidados/app/ui/charts/
git commit -m "feat: add charts module"
```

---

## 5. Notificaciones

### Task 12: WorkManager para Recordatorios

**Files:**
- Create: `app/src/main/java/com/kitcuidados/app/workers/MedicationReminderWorker.kt`
- Create: `app/src/main/java/com/kitcuidados/app/workers/AppointmentReminderWorker.kt`

- [ ] **Step 1: Create MedicationReminderWorker**

```kotlin
package com.kitcuidados.app.workers

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.kitcuidados.app.R
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class MedicationReminderWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {
    
    override suspend fun doWork(): Result {
        val medicationName = inputData.getString(KEY_MEDICATION_NAME) ?: return Result.failure()
        val dosage = inputData.getString(KEY_DOSAGE) ?: ""
        
        showNotification(medicationName, dosage)
        return Result.success()
    }
    
    private fun showNotification(name: String, dosage: String) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Recordatorios de Medicamentos",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Notificaciones para tomar medicamentos"
        }
        notificationManager.createNotificationChannel(channel)
        
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("Hora de tomar $name")
            .setContentText(dosage)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()
        
        notificationManager.notify(name.hashCode(), notification)
    }
    
    companion object {
        const val CHANNEL_ID = "medication_reminders"
        const val KEY_MEDICATION_NAME = "medication_name"
        const val KEY_DOSAGE = "dosage"
    }
}
```

- [ ] **Step 2: Commit**

```bash
git add app/src/main/java/com/kitcuidados/app/workers/
git commit -m "feat: add WorkManager notification system"
```

---

## 6. Firebase Config

### Task 13: google-services.json

**Files:**
- Create: `app/google-services.json` (placeholder - user needs to download from Firebase Console)

- [ ] **Step 1: Create placeholder instructions**

El usuario necesita:
1. Ir a Firebase Console
2. Crear proyecto "KitCuidados"
3. Habilitar Authentication y Firestore
4. Descargar `google-services.json`
5. Colocarlo en `app/google-services.json`

- [ ] **Step 2: Commit**

```bash
git add .
git commit -m "chore: add google-services.json placeholder"
```

---

## Summary

El plan cubre:

1. ✓ Project Setup con Gradle
2. ✓ Autenticación Firebase
3. ✓ Modelos de datos y Room
4. ✓ Navigation y Dashboard
5. ✓ Módulo Medicamentos
6. ✓ Módulo Síntomas
7. ✓ Módulo Citas
8. ✓ Módulo Diario
9. ✓ Módulo Contactos de Emergencia
10. ✓ Módulo Gráficos
11. ✓ Notificaciones WorkManager
12. ✓ Firebase Config

**Total de tareas: 13 tareas principales**

Cada tarea está diseñada para completarse de forma independiente y ser mergeable.