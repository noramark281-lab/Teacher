package com.example

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import com.example.ui.components.AddEditStudentDialog
import com.example.ui.components.BackupRestoreDialog
import com.example.ui.components.EditSchoolInfoDialog
import com.example.ui.components.ExportByGradeAndSubjectDialog
import com.example.ui.components.FactoryResetDialog
import com.example.ui.components.SendClassSheetDialog
import com.example.ui.components.SendStudentReportDialog
import com.example.ui.components.SettingsVariablesDialog
import com.example.ui.screens.ClassSelectionScreen
import com.example.ui.screens.FinalOutcomeScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.SemesterGradeScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.GradeViewModel

class MainActivity : ComponentActivity() {

  private val viewModel: GradeViewModel by viewModels {
    GradeViewModel.provideFactory(this)
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      MyApplicationTheme {
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
          Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
          ) {
            MainAppContent(viewModel = viewModel)
          }
        }
      }
    }
  }
}

@Composable
fun MainAppContent(viewModel: GradeViewModel) {
  val context = LocalContext.current
  val currentScreen by viewModel.currentScreen.collectAsState()
  val schoolInfo by viewModel.schoolInfo.collectAsState()

  val showEditSchoolDialog by viewModel.showEditSchoolDialog.collectAsState()
  val showSettingsDialog by viewModel.showSettingsDialog.collectAsState()
  val showResetDialog by viewModel.showResetDialog.collectAsState()
  val showBackupDialog by viewModel.showBackupDialog.collectAsState()
  val showAddStudentDialog by viewModel.showAddStudentDialog.collectAsState()
  val editingStudent by viewModel.editingStudent.collectAsState()
  val sendingStudent by viewModel.sendingStudent.collectAsState()
  val showSendClassDialog by viewModel.showSendClassDialog.collectAsState()
  val showExportModal by viewModel.showExportModal.collectAsState()
  val exportModalFormat by viewModel.exportModalFormat.collectAsState()
  val allDistinctSubjects by viewModel.allDistinctSubjects.collectAsState()
  val allDatabaseStudents by viewModel.allDatabaseStudents.collectAsState()
  val selectedMonth by viewModel.selectedMonth.collectAsState()
  val selectedSection by viewModel.selectedSection.collectAsState()
  val selectedSubject by viewModel.selectedSubject.collectAsState()
  val currentStudents by viewModel.currentStudents.collectAsState()

  // Collect Toast messages
  LaunchedEffect(Unit) {
    viewModel.userMessage.collect { msg ->
      Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
    }
  }

  val activeSemester = viewModel.getActiveSemester()

  Box(modifier = Modifier.fillMaxSize()) {
    when (currentScreen) {
      0 -> HomeScreen(viewModel = viewModel)
      1 -> ClassSelectionScreen(semester = 1, viewModel = viewModel)
      2 -> ClassSelectionScreen(semester = 2, viewModel = viewModel)
      3 -> SemesterGradeScreen(semester = 1, viewModel = viewModel)
      4 -> SemesterGradeScreen(semester = 2, viewModel = viewModel)
      5 -> FinalOutcomeScreen(viewModel = viewModel)
      else -> HomeScreen(viewModel = viewModel)
    }

    // Dialogs
    if (showEditSchoolDialog) {
      EditSchoolInfoDialog(
        currentInfo = schoolInfo,
        onDismiss = { viewModel.closeEditSchoolDialog() },
        onSave = { updatedInfo ->
          viewModel.updateSchoolInfo(updatedInfo)
        }
      )
    }

    if (showSettingsDialog) {
      SettingsVariablesDialog(
        schoolInfo = schoolInfo,
        onDismiss = { viewModel.closeSettingsDialog() },
        onSave = { updatedInfo ->
          viewModel.updateSchoolInfo(updatedInfo)
        }
      )
    }

    if (showResetDialog) {
      FactoryResetDialog(
        onDismiss = { viewModel.closeResetDialog() },
        onConfirm = {
          viewModel.factoryReset()
        }
      )
    }

    if (showBackupDialog) {
      BackupRestoreDialog(
        onDismiss = { viewModel.closeBackupDialog() },
        onExportBackup = {
          viewModel.exportBackup(context)
        },
        onImportBackup = { json ->
          viewModel.importBackup(json)
        }
      )
    }

    if (showAddStudentDialog) {
      AddEditStudentDialog(
        student = editingStudent,
        schoolInfo = schoolInfo,
        onDismiss = { viewModel.closeAddStudentDialog() },
        onSave = { id, name, att, hw, oral, written, maxAtt, maxHw, maxOral, maxWritten ->
          viewModel.saveStudent(id, name, att, hw, oral, written, maxAtt, maxHw, maxOral, maxWritten)
        }
      )
    }

    if (sendingStudent != null) {
      SendStudentReportDialog(
        student = sendingStudent!!,
        schoolInfo = schoolInfo,
        semester = activeSemester,
        onDismiss = { viewModel.closeSendStudentDialog() },
        onSend = { format, phone, messageText ->
          viewModel.sendSingleStudentReport(context, sendingStudent!!, format, phone, messageText)
        },
        onShareGeneral = { format ->
          if (format == "PDF") {
            viewModel.exportSingleStudentPdf(context, sendingStudent!!)
          } else {
            viewModel.exportSingleStudentExcel(context, sendingStudent!!)
          }
          viewModel.closeSendStudentDialog()
        }
      )
    }

    if (showSendClassDialog) {
      SendClassSheetDialog(
        schoolInfo = schoolInfo,
        semester = activeSemester,
        month = selectedMonth,
        section = selectedSection,
        subject = selectedSubject,
        studentsCount = currentStudents.size,
        onDismiss = { viewModel.closeSendClassDialog() },
        onSend = { format, phone, messageText ->
          viewModel.sendClassSheetReport(context, format, phone, messageText)
        },
        onShareGeneral = { format ->
          if (format == "PDF") {
            viewModel.exportClassPdf(context)
          } else {
            viewModel.exportClassExcel(context)
          }
          viewModel.closeSendClassDialog()
        }
      )
    }

    if (showExportModal) {
      ExportByGradeAndSubjectDialog(
        exportFormat = exportModalFormat,
        schoolInfo = schoolInfo,
        allDistinctSubjects = allDistinctSubjects,
        allStudents = allDatabaseStudents,
        onDismiss = { viewModel.closeExportModal() },
        onExport = { format, gradeLevel, subject, semester, month, section ->
          viewModel.exportCustomGradeSubject(
            context = context,
            format = format,
            gradeLevel = gradeLevel,
            subject = subject,
            semester = semester,
            month = month,
            section = section
          )
        },
        onNavigateToClass = { gradeLevel, semester, subject ->
          viewModel.selectGradeLevelAndOpenSemester(gradeLevel, semester)
          viewModel.setSelectedSubject(subject)
        }
      )
    }
  }
}

