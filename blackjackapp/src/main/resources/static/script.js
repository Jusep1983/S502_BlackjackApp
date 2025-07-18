let currentGameId = null;
const isLocal = window.location.hostname === "localhost";
const api = isLocal
  ? "http://localhost:8080"
  : "https://s501-blackjack-api.onrender.com";
//const api = "http://localhost:8080";
//const api = "https://s501-blackjack-api.onrender.com";
function createPlayer() {
  const name = document.getElementById("playerName").value;
  fetch(`${api}/player/new`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ name })
  }).then(res => res.json()).then(console.log);
}

function createGame() {
  const name = document.getElementById("gamePlayerName").value;
  fetch(`${api}/game/new`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ playerName: name })
  }).then(res => res.json()).then(data => {
    currentGameId = data.data.id;
    alert("Game created with ID: " + currentGameId);
  });
}

function getGame() {
  const id = document.getElementById("gameId").value || currentGameId;
  fetch(`${api}/game/${id}`)
    .then(res => res.json())
    .then(data => {
      const game = data.data;
      document.getElementById("gameDetails").innerText =
        `Jugador: ${game.playerName}\n` +
        `Estado: ${game.gameStatus}\n` +
        `Puntos jugador: ${game.playerPoints}\n` +
        `Puntos dealer: ${game.dealerPoints} (${game.dealerHand.cards.map(c => c.rank.symbol).join(", ")})`;
    });
}

function hit() {
  if (!currentGameId) return alert("Primero crea o consulta una partida");
  fetch(`${api}/game/${currentGameId}/hit`, { method: "POST" })
    .then(res => res.json()).then(getGame);
}

function stand() {
  if (!currentGameId) return alert("Primero crea o consulta una partida");
  fetch(`${api}/game/${currentGameId}/stand`, { method: "POST" })
    .then(res => res.json()).then(getGame);
}

function getRanking() {
  fetch(`${api}/player/ranking`)
    .then(res => res.json())
    .then(players => {
      const list = document.getElementById("rankingList");
      list.innerHTML = "";
      players.forEach(p => {
        const li = document.createElement("li");
        li.innerText = `Pos ${p.position}. ${p.name} - V: ${p.gamesWon}, D: ${p.gamesLost}, E: ${p.gamesTied}, %V: ${p.winPercentage}`;
        list.appendChild(li);
      });
    });
}

//function getRanking() {
//  fetch(`${api}/player/ranking`)
//    .then(res => res.json())
//    .then(response => {
//      const players = response.data; // accedemos al array dentro de data
//      const list = document.getElementById("rankingList");
//      list.innerHTML = "";
//
//      players.forEach(p => {
//        const li = document.createElement("li");
//        li.innerText = `${p.position}. ${p.name} - Victorias: ${p.gamesWon}, Derrotas: ${p.gamesLost}, Empates: ${p.gamesTied}, % Victorias: ${p.winPercentage}`;
//        list.appendChild(li);
//      });
//    })
//    .catch(err => console.error("Error cargando ranking:", err));
//}