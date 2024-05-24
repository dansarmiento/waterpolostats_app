package com.progressbar.waterpolo_app

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DatabaseHelperTest {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var context: Context

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        dbHelper = DatabaseHelper(context)
        dbHelper.writableDatabase.execSQL("DELETE FROM ${DatabaseHelper.TABLE_GAMES}") // Clear table before each test
    }

    @After
    fun tearDown() {
        dbHelper.close()
    }

    @Test
    fun testAddGame() {
        dbHelper.addGame("2024-05-24", "Opponent Team A", "Level A")
        val cursor = dbHelper.getAllGames()
        cursor.moveToFirst()
        assertEquals("2024-05-24", cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_GAME_DATE)))
        assertEquals("Opponent Team A", cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_OPPONENT_TEAM)))
        cursor.close()
    }

    @Test
    fun testGetAllGames() {
        dbHelper.addGame("2024-05-24", "Opponent Team A", "Level A")
        dbHelper.addGame("2024-05-25", "Opponent Team B", "Level B")
        val cursor = dbHelper.getAllGames()
        assertEquals(2, cursor.count)
        cursor.close()
    }
}
