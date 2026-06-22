package com.example.personalgymapp.util

import android.content.Context
import android.net.Uri
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.http.FileContent
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.drive.Drive
import com.google.api.services.drive.DriveScopes
import com.google.api.services.drive.model.File
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.FileOutputStream
import java.util.*

class GoogleDriveService(private val context: Context) {

    suspend fun uploadDatabase(account: GoogleSignInAccount, databasePath: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val credential = GoogleAccountCredential.usingOAuth2(
                context, Collections.singleton(DriveScopes.DRIVE_FILE)
            )
            credential.selectedAccount = account.account

            val driveService = Drive.Builder(
                NetHttpTransport(),
                GsonFactory.getDefaultInstance(),
                credential
            ).setApplicationName("Personal Gym App").build()

            val dbFile = java.io.File(databasePath)
            if (!dbFile.exists()) return@withContext false

            val fileMetadata = File()
            fileMetadata.name = "pollux_gym_backup_${System.currentTimeMillis()}.db"
            fileMetadata.parents = listOf("root")

            val mediaContent = FileContent("application/x-sqlite3", dbFile)

            val file = driveService.files().create(fileMetadata, mediaContent)
                .setFields("id")
                .execute()

            return@withContext file.id != null
        } catch (e: Exception) {
            e.printStackTrace()
            return@withContext false
        }
    }
}
