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