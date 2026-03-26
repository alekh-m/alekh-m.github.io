const API_BASE = window.TIC_TAC_TOE_API_BASE || "http://localhost:8080/api/game";

const modeSelect = document.getElementById("mode");
const newGameBtn = document.getElementById("newGameBtn");
const statusEl = document.getElementById("status");
const cells = Array.from(document.querySelectorAll(".cell"));

let gameId = null;
let mode = "computer";
let isBusy = false;
let gameFinished = false;

function setStatus(message) {
  statusEl.textContent = message;
}

function renderBoard(board) {
  cells.forEach((cell, idx) => {
    cell.textContent = board[idx] || "";
  });
}

function updateDisabledState(board) {
  cells.forEach((cell, idx) => {
    const occupied = (board[idx] || "") !== "";
    cell.disabled = isBusy || gameFinished || occupied;
  });
}

function announceState(state) {
  if (state.finished) {
    gameFinished = true;
    if (state.draw) {
      setStatus("It's a draw.");
    } else {
      setStatus(`Winner: ${state.winner}`);
    }
    return;
  }

  if (mode === "computer") {
    setStatus("Your turn (X)");
  } else {
    setStatus(`Player ${state.currentPlayer}'s turn`);
  }
}

async function startNewGame() {
  isBusy = true;
  gameFinished = false;
  mode = modeSelect.value;
  setStatus("Starting game...");
  updateDisabledState(Array(9).fill(""));

  try {
    const response = await fetch(`${API_BASE}/new`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ mode })
    });

    const data = await response.json();
    if (!response.ok) {
      throw new Error(data.error || "Unable to start game.");
    }

    gameId = data.gameId;
    renderBoard(data.board);
    announceState(data);
    updateDisabledState(data.board);
  } catch (err) {
    setStatus(err.message);
  } finally {
    isBusy = false;
  }
}

async function playMove(index) {
  if (!gameId || isBusy || gameFinished) {
    return;
  }

  isBusy = true;
  updateDisabledState(cells.map((cell) => cell.textContent));
  setStatus("Submitting move...");

  try {
    const response = await fetch(`${API_BASE}/move`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ gameId, index })
    });
    const data = await response.json();
    if (!response.ok) {
      throw new Error(data.error || "Move failed.");
    }

    renderBoard(data.board);
    announceState(data);
    updateDisabledState(data.board);
  } catch (err) {
    setStatus(err.message);
  } finally {
    isBusy = false;
    if (!gameFinished) {
      updateDisabledState(cells.map((cell) => cell.textContent));
    }
  }
}

newGameBtn.addEventListener("click", startNewGame);
cells.forEach((cell) => {
  cell.addEventListener("click", () => {
    playMove(Number(cell.dataset.index));
  });
});

startNewGame();
