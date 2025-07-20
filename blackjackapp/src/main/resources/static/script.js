let token = null;
let currentGameId = null;

const isLocal = window.location.hostname === "localhost";
const api = isLocal
  ? "http://localhost:8080"
  : "https://s501-blackjack-api.onrender.com";

// 🔐 Lógica de login
function login() {
  const user = document.getElementById("loginUser").value;
  const pass = document.getElementById("loginPass").value;

  fetch(`${api}/auth/login`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ userName: user, password: pass })
  })
    .then(res => {
      if (!res.ok) throw new Error("Credenciales inválidas");
      return res.json();
    })
    .then(data => {
      token = data.data.token;
      document.getElementById("loginPanel").style.display = "none";
      document.getElementById("gamePanel").style.display = "block";
      document.getElementById("loginError").innerText = "";
    })
    .catch(err => {
      document.getElementById("loginError").innerText = err.message;
    });
}

// 🃏 Crear nueva partida
function createGame() {
  fetch(`${api}/game/new`, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
      Authorization: `Bearer ${token}`
    }
  })
    .then(res => res.json())
    .then(data => {
      currentGameId = data.data.id;
      document.getElementById("gameId").value = currentGameId; // Auto-rellena input
      alert("Partida creada con ID: " + currentGameId);
      getGame(); // Mostramos directamente los detalles
    });
}

// 👁️ Ver detalles de partida
function getGame() {
  const id = document.getElementById("gameId").value || currentGameId;
  if (!id) return;

  fetch(`${api}/game/${id}`, {
    headers: { Authorization: `Bearer ${token}` }
  })
    .then(res => res.json())
    .then(data => {
      const game = data.data;

      const playerCards = game.playerHand.cards.map(card => `${card.rank} ${getSuit(card.suit)}`).join(", ");
      const dealerCards = game.dealerHand.cards.map(card => `${card.rank} ${getSuit(card.suit)}`).join(", ");

      document.getElementById("gameDetails").innerText =
        `Jugador: ${game.userName}\n` +
        `Estado: ${game.gameStatus}\n\n` +
        `🧑‍💼 Jugador (${game.playerPoints}): ${playerCards}\n` +
        `🤖 Dealer (${game.dealerPoints}): ${dealerCards}\n\n` +
        `Resultado: ${game.gameResult || "En juego..."}`;
    });
}

// ➕ Pedir carta
function hit() {
  if (!currentGameId) return alert("Primero crea o consulta una partida");
  fetch(`${api}/game/${currentGameId}/hit`, {
    method: "POST",
    headers: { Authorization: `Bearer ${token}` }
  })
    .then(res => res.json())
    .then(getGame);
}

// ✋ Plantarse
function stand() {
  if (!currentGameId) return alert("Primero crea o consulta una partida");
  fetch(`${api}/game/${currentGameId}/stand`, {
    method: "POST",
    headers: { Authorization: `Bearer ${token}` }
  })
    .then(res => res.json())
    .then(getGame);
}

// 📊 Ranking público
function getRanking() {
  fetch(`${api}/player/ranking`, {
    headers: { Authorization: `Bearer ${token}` }
  })
    .then(res => res.json())
    .then(data => {
      const list = document.getElementById("rankingList");
      list.innerHTML = "";
      data.data.forEach(p => {
        const li = document.createElement("li");
        li.innerText = `Pos ${p.position}. ${p.userName} - V: ${p.gamesWon}, D: ${p.gamesLost}, E: ${p.gamesTied}, %V: ${p.winPercentage}`;
        list.appendChild(li);
      });
    });
}

// ♠️♥️♦️♣️ Convertir texto a símbolo
function getSuit(suit) {
  switch (suit) {
    case "HEARTS": return "♥️";
    case "DIAMONDS": return "♦️";
    case "CLUBS": return "♣️";
    case "SPADES": return "♠️";
    default: return suit;
  }
}
