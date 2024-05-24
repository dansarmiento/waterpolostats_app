package com.progressbar.waterpolo_app

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.io.FileOutputStream
import java.io.IOException

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    init {
        val dbExist = checkDatabase(context)
        if (!dbExist) {
            this.readableDatabase
            copyDatabase(context)
        }
    }

    override fun onCreate(db: SQLiteDatabase) {
        // No need to create the table as we are copying the pre-existing database
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Handle database upgrade if needed
    }

    private fun checkDatabase(context: Context): Boolean {
        val dbFile = context.getDatabasePath(DATABASE_NAME)
        return dbFile.exists()
    }

    private fun copyDatabase(context: Context) {
        try {
            val inputStream = context.assets.open(DATABASE_NAME)
            val outFileName = context.getDatabasePath(DATABASE_NAME).path
            val outputStream = FileOutputStream(outFileName)

            val buffer = ByteArray(1024)
            var length: Int
            while (inputStream.read(buffer).also { length = it } > 0) {
                outputStream.write(buffer, 0, length)
            }

            outputStream.flush()
            outputStream.close()
            inputStream.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun addGame(gameDate: String, opponentTeam: String, opponentTeamLevel: String) {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_GAME_DATE, gameDate)
            put(COLUMN_OPPONENT_TEAM, opponentTeam)
            put(COLUMN_OPPONENT_TEAM_LEVEL, opponentTeamLevel)
        }
        db.insert(TABLE_GAMES, null, values)
        db.close()
    }

    fun getAllGames(): Cursor {
        val db = this.readableDatabase
        return db.rawQuery("SELECT * FROM $TABLE_GAMES", null)
    }

    companion object {
        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "waterpolostats.db"
        const val TABLE_GAMES = "games"
        const val COLUMN_ID = "id"
        const val COLUMN_GAME_DATE = "game_date"
        const val COLUMN_OPPONENT_TEAM = "opponent_team"
        const val COLUMN_OPPONENT_TEAM_LEVEL = "opponent_team_level"
    }
}
