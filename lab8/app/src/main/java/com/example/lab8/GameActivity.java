package com.example.lab8;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class GameActivity extends AppCompatActivity {

    private Button[] cells = new Button[9];

    private TextView infoText;
    private TextView scoreText;
    private Button nextRoundButton;
    private Button newMatchButton;

    private String[] board = new String[9];

    private String currentPlayer = "X";
    private boolean roundFinished = false;

    private int totalGames;
    private int playedGames = 0;

    private int winsX = 0;
    private int winsO = 0;
    private int draws = 0;

    private double pointsX = 0;
    private double pointsO = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        initViews();

        if (savedInstanceState != null) {
            restoreState(savedInstanceState);
        } else {
            totalGames = getIntent().getIntExtra("totalGames", 5);
            resetBoard();
        }

        updateUI();

        for (int i = 0; i < cells.length; i++) {
            final int index = i;

            cells[i].setOnClickListener(v -> makeMove(index));
        }

        nextRoundButton.setOnClickListener(v -> {
            resetBoard();
            updateUI();
        });

        newMatchButton.setOnClickListener(v -> {
            Intent intent = new Intent(GameActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        });
    }

    private void initViews() {
        infoText = findViewById(R.id.infoText);
        scoreText = findViewById(R.id.scoreText);

        nextRoundButton = findViewById(R.id.nextRoundButton);
        newMatchButton = findViewById(R.id.newMatchButton);

        cells[0] = findViewById(R.id.cell0);
        cells[1] = findViewById(R.id.cell1);
        cells[2] = findViewById(R.id.cell2);
        cells[3] = findViewById(R.id.cell3);
        cells[4] = findViewById(R.id.cell4);
        cells[5] = findViewById(R.id.cell5);
        cells[6] = findViewById(R.id.cell6);
        cells[7] = findViewById(R.id.cell7);
        cells[8] = findViewById(R.id.cell8);
    }

    private void makeMove(int index) {
        if (roundFinished) return;

        if (board[index] != null) return;

        board[index] = currentPlayer;
        cells[index].setText(currentPlayer);

        if (checkWinner(currentPlayer)) {
            finishRound(currentPlayer);
        } else if (isDraw()) {
            finishRound("DRAW");
        } else {
            currentPlayer = currentPlayer.equals("X") ? "O" : "X";
            updateUI();
        }
    }

    private boolean checkWinner(String player) {
        int[][] winLines = {
                {0, 1, 2},
                {3, 4, 5},
                {6, 7, 8},
                {0, 3, 6},
                {1, 4, 7},
                {2, 5, 8},
                {0, 4, 8},
                {2, 4, 6}
        };

        for (int[] line : winLines) {
            if (player.equals(board[line[0]])
                    && player.equals(board[line[1]])
                    && player.equals(board[line[2]])) {
                return true;
            }
        }

        return false;
    }

    private boolean isDraw() {
        for (String field : board) {
            if (field == null) {
                return false;
            }
        }

        return true;
    }

    private void finishRound(String result) {
        roundFinished = true;
        playedGames++;

        if (result.equals("X")) {
            winsX++;
            pointsX += 1;
            infoText.setText("Gracz X wygrał rundę!");
        } else if (result.equals("O")) {
            winsO++;
            pointsO += 1;
            infoText.setText("Gracz O wygrał rundę!");
        } else {
            draws++;
            pointsX += 0.5;
            pointsO += 0.5;
            infoText.setText("Remis!");
        }

        if (playedGames >= totalGames) {
            openSummaryScreen();
        } else {
            nextRoundButton.setVisibility(View.VISIBLE);
        }

        updateUI();
    }

    private void resetBoard() {
        board = new String[9];
        currentPlayer = "X";
        roundFinished = false;

        for (Button cell : cells) {
            cell.setText("");
        }

        nextRoundButton.setVisibility(View.GONE);
    }

    private void updateUI() {
        int remainingGames = totalGames - playedGames;

        if (!roundFinished) {
            infoText.setText("Ruch gracza: " + currentPlayer);
        }

        scoreText.setText(
                "Rozegrane gry: " + playedGames + "\n" +
                        "Pozostałe gry: " + remainingGames + "\n\n" +
                        "Punkty X: " + formatPoints(pointsX) + "\n" +
                        "Punkty O: " + formatPoints(pointsO)
        );
    }

    private String formatPoints(double value) {
        if (value == (int) value) {
            return String.valueOf((int) value);
        }

        return String.valueOf(value);
    }

    private void openSummaryScreen() {
        Intent intent = new Intent(GameActivity.this, SummaryActivity.class);

        intent.putExtra("winsX", winsX);
        intent.putExtra("winsO", winsO);
        intent.putExtra("draws", draws);
        intent.putExtra("pointsX", pointsX);
        intent.putExtra("pointsO", pointsO);
        intent.putExtra("totalGames", totalGames);

        startActivity(intent);
        finish();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putStringArray("board", board);
        outState.putString("currentPlayer", currentPlayer);
        outState.putBoolean("roundFinished", roundFinished);

        outState.putInt("totalGames", totalGames);
        outState.putInt("playedGames", playedGames);

        outState.putInt("winsX", winsX);
        outState.putInt("winsO", winsO);
        outState.putInt("draws", draws);

        outState.putDouble("pointsX", pointsX);
        outState.putDouble("pointsO", pointsO);
    }

    private void restoreState(Bundle savedInstanceState) {
        board = savedInstanceState.getStringArray("board");
        currentPlayer = savedInstanceState.getString("currentPlayer");
        roundFinished = savedInstanceState.getBoolean("roundFinished");

        totalGames = savedInstanceState.getInt("totalGames");
        playedGames = savedInstanceState.getInt("playedGames");

        winsX = savedInstanceState.getInt("winsX");
        winsO = savedInstanceState.getInt("winsO");
        draws = savedInstanceState.getInt("draws");

        pointsX = savedInstanceState.getDouble("pointsX");
        pointsO = savedInstanceState.getDouble("pointsO");

        for (int i = 0; i < board.length; i++) {
            cells[i].setText(board[i] == null ? "" : board[i]);
        }

        nextRoundButton.setVisibility(roundFinished ? View.VISIBLE : View.GONE);
    }
}