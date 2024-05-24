package com.progressbar.waterpolo_app

import android.content.Intent
import android.database.Cursor
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity

class GamesActivity : AppCompatActivity() {
    private lateinit var db: DatabaseHelper
    private lateinit var gamesListView: ListView
    private lateinit var gameDate: EditText
    private lateinit var opponentTeam: EditText
    private lateinit var opponentTeamLevel: EditText
    private lateinit var addGameButton: Button
    private lateinit var assignCapsButton: Button
    private var selectedGameId: Long = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_games)

        db = DatabaseHelper(this)
        gamesListView = findViewById(R.id.gamesListView)
        gameDate = findViewById(R.id.gameDate)
        opponentTeam = findViewById(R.id.opponentTeam)
        opponentTeamLevel = findViewById(R.id.opponentTeamLevel)
        addGameButton = findViewById(R.id.addGameButton)
        assignCapsButton = findViewById(R.id.assignCapsButton)

        addGameButton.setOnClickListener {
            val date = gameDate.text.toString()
            val team = opponentTeam.text.toString()
            val level = opponentTeamLevel.text.toString()
            db.addGame(date, team, level)
            loadGames()
        }

        gamesListView.setOnItemClickListener { _, _, position, id ->
            selectedGameId = id
            assignCapsButton.isEnabled = true // Enable the button when a game is selected
        }

        assignCapsButton.setOnClickListener {
            val intent = Intent(this, AssignCapsActivity::class.java)
            intent.putExtra("GAME_ID", selectedGameId)
            startActivity(intent)
        }

        loadGames()
    }

    private fun loadGames() {
        val cursor: Cursor = db.getAllGames()
        val gamesList = ArrayList<String>()
        while (cursor.moveToNext()) {
            val gameDate = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_GAME_DATE))
            val opponentTeam = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_OPPONENT_TEAM))
            gamesList.add("$gameDate: VUWPC vs. $opponentTeam")
        }
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, gamesList)
        gamesListView.adapter = adapter
        cursor.close()
    }
}
