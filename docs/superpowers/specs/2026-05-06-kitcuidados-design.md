# KitCuidados - Specification Document

## 1. Project Overview

**Project Name:** KitCuidados
**Type:** Android Mobile Application (Full Stack)
**Core Functionality:** Aplicación móvil para autogestión de salud y seguimiento familiar de medicamentos, síntomas, citas médicas, diario de bienestar y contactos de emergencia.

## 2. Technology Stack

### Frontend (Android)
- **Language:** Kotlin 1.9+
- **UI Framework:** Jetpack Compose (Material 3)
- **Architecture:** MVVM + Clean Architecture
- **DI:** Hilt
- **Navigation:** Compose Navigation
- **Charts:** Vico (Compose-native)
- **Local Storage:** Room (cache offline)
- **State Management:** StateFlow + ViewModel

### Backend
- **Platform:** Firebase
- **Authentication:** Firebase Auth (email/password)
- **Database:** Cloud Firestore
- **Notifications:** Firebase Cloud Messaging + WorkManager
- **Hosting:** Firebase Hosting (opcional para web)

## 3. User Features

### 3.1 Autenticación
- Login con email/password
- Registro de nuevo usuario
- Logout
- Recuperación de contraseña

### 3.2 Dashboard (Inicio)
- Resumen del día actual
- Próximos medicamentos (próximas 24h)
- Citas cercanas
- Indicador de estado de bienestar general
- quick actions: registrar síntoma rápido, agregar nota

### 3.3 Módulo Medicamentos
- Agregar/editar/eliminar medicamentos
- Campos: nombre, dosis, frecuencia, horarios, notas
- Sistema de recordatorios programables
- Historial de tomas (tomado/no tomado/omitido)
- Vista de adherencia semanal

### 3.4 Módulo Síntomas
- Registro diario de síntomas
- Escala de dolor 1-10
- Ubicación del dolor (opciones predefinidas)
- Notas adicionales
- Historial con timeline

### 3.5 Módulo Citas
- Calendario de citas médicas
- Agregar/editar/eliminar citas
- Campos: fecha, hora, doctor, especialidad, lugar, notas
- Recordatorio previo (1 día antes)
- Historial pasado

### 3.6 Módulo Diario de Bienestar
- Entradas diarias de bienestar
- Campos: estado emocional (emoji), nivel de energía, calidad de sueño, notas
- Vista cronológica invertida

### 3.7 Módulo Contactos de Emergencia
- Lista de contactos importantes
- Campos: nombre, teléfono, relación (médico, familiar, hospital)
- Llamada directa con un tap
- Información visible sin login (opcional)

### 3.8 Módulo Gráficos
- Gráfico de evolución de síntomas (línea temporal)
- Gráfico de adherencia a medicamentos
- Filtros por rango de fecha

## 4. Data Model (Firestore)

### Users Collection
```
users/{userId}
  - email: string
  - createdAt: timestamp
  - displayName: string (optional)
```

### Medications Collection
```
users/{userId}/medications/{medicationId}
  - name: string
  - dosage: string
  - frequency: string
  - schedules: array of times
  - notes: string
  - isActive: boolean
  - createdAt: timestamp
```

### MedicationLogs Collection
```
users/{userId}/medicationLogs/{logId}
  - medicationId: string
  - scheduledTime: timestamp
  - status: "taken" | "skipped" | "missed"
  - loggedAt: timestamp
```

### Symptoms Collection
```
users/{userId}/symptoms/{symptomId}
  - painLevel: number (1-10)
  - location: string
  - notes: string
  - recordedAt: timestamp
```

### Appointments Collection
```
users/{userId}/appointments/{appointmentId}
  - doctorName: string
  - specialty: string
  - dateTime: timestamp
  - location: string
  - notes: string
  - createdAt: timestamp
```

### Journal Entries Collection
```
users/{userId}/journal/{entryId}
  - mood: string (emoji)
  - energyLevel: number (1-5)
  - sleepQuality: number (1-5)
  - notes: string
  - recordedAt: timestamp
```

### Emergency Contacts Collection
```
users/{userId}/emergencyContacts/{contactId}
  - name: string
  - phone: string
  - relationship: string
  - isPrimary: boolean
```

## 5. Screen Flow

```
Splash → Login/Register → Dashboard
                              ↓
    ┌─────────┬──────────┬──────────┐
    │  Meds   │ Symptoms │ Calendar │
    │  (Tab)  │  (Tab)   │  (Tab)   │
    └─────────┴──────────┴──────────┘
              ↓
    ┌─────────┬──────────┐
    │ Journal │ Contacts │
    │  (Tab)  │  (Tab)   │
    └─────────┴──────────┘
```

Bottom Navigation con 6 tabs:
1. Inicio (Dashboard)
2. Medicamentos
3. Síntomas
4. Citas
5. Diario
6. Contactos

## 6. Notifications

- **Recordatorios de medicamentos:** WorkManager programa notificaciones locales
- **Recordatorios de citas:** Notificación 1 día antes
- **Frecuencia configurable por medicamento**

## 7. Offline Support

- Room cache para datos frecuentemente accedidos
- Sincronización automática cuando hay conexión
- UI muestra estado de sync

## 8. UI/UX Guidelines

- **Design System:** Material Design 3
- **Theme:** Colores suaves, orientador a bienestar (verdes, azules, blancos)
- **Tipografía:** Roboto (default Material)
- **Iconografía:** Material Icons
- **Modo oscuro:** Soportado via Material 3

## 9. Success Criteria

- [ ] Usuario puede registrarse y hacer login
- [ ] Usuario puede agregar y gestionar medicamentos
- [ ] Usuario recibe recordatorios de medicamentos
- [ ] Usuario puede registrar síntomas diarios
- [ ] Usuario puede agendar citas médicas
- [ ] Usuario puede escribir entradas de diario
- [ ] Usuario tiene contactos de emergencia accesibles
- [ ] Usuario puede ver gráficos de evolución
- [ ] App funciona offline con sync automático