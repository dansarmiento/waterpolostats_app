from flask import Flask, g, jsonify, request, render_template, redirect, url_for
from flask_cors import CORS
import sqlite3
import datetime

app = Flask(__name__)
CORS(app)

DATABASE = 'waterpolostats.db'

def get_db():
    db = getattr(g, '_database', None)
    if db is None:
        db = g._database = sqlite3.connect(DATABASE)
    return db

@app.teardown_appcontext
def close_connection(exception):
    db = getattr(g, '_database', None)
    if db is not None:
        db.close()

@app.route('/')
def index():
    return render_template('index.html')

@app.route('/players', methods=['GET', 'POST'])
def players():
    db = get_db()
    cursor = db.cursor()

    if request.method == 'POST':
        first_name = request.form['first_name']
        last_name = request.form['last_name']
        cursor.execute("INSERT INTO players (first_name, last_name, hs_team_id, club_team_id) VALUES (?, ?, ?, ?)",
                       (first_name, last_name, 161, 239))
        db.commit()
        return redirect(url_for('players'))

    cursor.execute("SELECT first_name, last_name FROM players WHERE club_team_id = 239")
    players = cursor.fetchall()
    return render_template('players.html', players=players)

@app.route('/games', methods=['GET', 'POST'])
def games():
    db = get_db()
    cursor = db.cursor()

    if request.method == 'POST':
        date = request.form['date']
        opponent_team = request.form['opponent_team']
        opponent_team_level = request.form['opponent_team_level']
        cursor.execute("INSERT INTO games (game_date, my_team_id, opponent_team, opponent_team_level) VALUES (?, ?, ?, ?)",
                       (date, 239, opponent_team, opponent_team_level))
        db.commit()
        game_id = cursor.lastrowid
        return redirect(url_for('cap_assignment', game_id=game_id))

    return render_template('games.html', current_date=datetime.date.today().strftime('%Y-%m-%d'))

@app.route('/cap_assignment/<int:game_id>', methods=['GET', 'POST'])
def cap_assignment(game_id):
    db = get_db()
    cursor = db.cursor()

    cursor.execute("SELECT game_date, opponent_team FROM games WHERE game_id = ?", (game_id,))
    game = cursor.fetchone()
    game_date, opponent_team = game

    if request.method == 'POST':
        player_cap_assignments = request.form.to_dict()
        for player_id, cap_id in player_cap_assignments.items():
            cursor.execute("INSERT INTO cap_assignment (game_id, player_id, cap_id) VALUES (?, ?, ?)",
                           (game_id, player_id, cap_id))
        db.commit()
        return redirect(url_for('game_stats', game_id=game_id))

    cursor.execute("SELECT player_id, first_name, last_name FROM players WHERE club_team_id = 239")
    players = cursor.fetchall()

    # Ensure cap_number is included in the available_caps query
    cursor.execute("""
        SELECT caps.cap_id, caps.cap_number 
        FROM caps 
        WHERE caps.cap_id NOT IN (SELECT cap_id FROM cap_assignment WHERE game_id = ?)
    """, (game_id,))
    available_caps = cursor.fetchall()

    return render_template('cap_assignment.html', game_id=game_id, game_date=game_date,
                           opponent_team=opponent_team, players=players, available_caps=available_caps)


@app.route('/game_stats/<int:game_id>', methods=['GET', 'POST'])
def game_stats(game_id):
    db = get_db()
    cursor = db.cursor()

    cursor.execute("SELECT game_date, opponent_team FROM games WHERE game_id = ?", (game_id,))
    game = cursor.fetchone()
    game_date, opponent_team = game

    cursor.execute("SELECT cap_assignment.cap_id, players.last_name FROM cap_assignment JOIN players ON cap_assignment.player_id = players.player_id WHERE cap_assignment.game_id = ?", (game_id,))
    players_in_game = cursor.fetchall()

    cursor.execute("SELECT game_state_id, game_state_desc FROM game_state")
    game_states = cursor.fetchall()

    cursor.execute("SELECT action_id, action_desc FROM actions")
    actions = cursor.fetchall()

    cursor.execute("SELECT result_id, result_desc, result_type FROM results")
    results = cursor.fetchall()

    selected_game_state = None
    field_players = []

    if request.method == 'POST':
        game_state_id = request.form.get('game_state_id')
        cap_number = request.form.get('cap_number')
        action_id = request.form.get('action_id')
        result_id = request.form.get('result_id')
        field_players = request.form.getlist('field_players')

        if game_state_id and cap_number and action_id and result_id:
            cursor.execute("INSERT INTO game_stats (game_id, game_state_id, cap_number, action_id, result_id) VALUES (?, ?, ?, ?, ?)",
                           (game_id, game_state_id, cap_number, action_id, result_id))
            db.commit()

        selected_game_state = game_state_id

    return render_template('game_stats.html', game_id=game_id, game_date=game_date, opponent_team=opponent_team,
                           players_in_game=players_in_game, game_states=game_states, actions=actions, results=results,
                           selected_game_state=selected_game_state, field_players=field_players)



@app.route('/results/<int:action_id>', methods=['GET'])
def get_results(action_id):
    db = get_db()
    cursor = db.cursor()

    cursor.execute("SELECT result_id, result_desc FROM results WHERE result_type = (SELECT result_type FROM actions WHERE action_id = ?)", (action_id,))
    results = cursor.fetchall()

    results_data = [{'id': result[0], 'desc': result[1]} for result in results]
    return jsonify(results_data)


if __name__ == '__main__':
    app.run(debug=True)
