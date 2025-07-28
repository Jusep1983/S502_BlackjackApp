//let token = null;
//let currentGameId = null;
//
//const isLocal = window.location.hostname === "localhost";
//const api = isLocal
//  ? "http://localhost:8080"
//  : "https://s501-blackjack-api.onrender.com";
//
//// Al cargar la página, ocultamos los botones de acción
//document.addEventListener("DOMContentLoaded", () => {
//  document.getElementById("actionButtons").style.display = "none";
//});
//
//// 🔐 Login del usuario
//function login() {
//  const user = document.getElementById("loginUser").value;
//  const pass = document.getElementById("loginPass").value;
//
//  fetch(`${api}/auth/login`, {
//    method: "POST",
//    headers: { "Content-Type": "application/json" },
//    body: JSON.stringify({ userName: user, password: pass })
//  })
//    .then(res => {
//      if (!res.ok) throw new Error("Credenciales inválidas");
//      return res.json();
//    })
//    .then(data => {
//      token = data.data.token;
//      document.getElementById("loginPanel").style.display = "none";
//      document.getElementById("gamePanel").style.display = "block";
//      document.getElementById("loginError").innerText = "";
//    })
//    .catch(err => {
//      document.getElementById("loginError").innerText = err.message;
//    });
//}
//
//// ➕ Crear partida
//function createGame() {
//  fetch(`${api}/game/new`, {
//    method: "POST",
//    headers: {
//      "Content-Type": "application/json",
//      Authorization: `Bearer ${token}`
//    }
//  })
//    .then(res => res.json())
//    .then(data => {
//      currentGameId = data.data.id;
//      document.getElementById("gameId").value = currentGameId;
//      // alert("Partida creada con ID: " + currentGameId);
//      getGame(); // mostrar detalles directamente
//    });
//}
//
//// 👁️ Ver detalles
//function getGame() {
//  const id = document.getElementById("gameId").value || currentGameId;
//  if (!id) {
//    document.getElementById("actionButtons").style.display = "none";
//    return;
//  }
//
//  fetch(`${api}/game/${id}`, {
//    headers: { Authorization: `Bearer ${token}` }
//  })
//    .then(res => res.json())
//    .then(data => {
//      const game = data.data;
//
//      const playerCards = game.playerHand.cards.map(card => `${card.rank} ${getSuit(card.suit)}`).join(", ");
//      const dealerCards = game.dealerHand.cards.map(card => `${card.rank} ${getSuit(card.suit)}`).join(", ");
//
//      document.getElementById("gameDetails").innerText =
//        `Jugador: ${game.userName}\n` +
//        `Estado: ${game.gameStatus}\n\n` +
//        `🧑‍💼 Jugador (${game.playerPoints}): ${playerCards}\n` +
//        `🤖 Dealer (${game.dealerPoints}): ${dealerCards}\n\n` +
//        `Resultado: ${game.gameResult || "En juego..."}`;
//
//      // 🔄 Mostrar/ocultar acciones según estado
//      if (game.gameStatus === "NEW" || game.gameStatus === "IN_PROGRESS") {
//        document.getElementById("actionButtons").style.display = "block";
//      } else {
//        document.getElementById("actionButtons").style.display = "none";
//      }
//    });
//}
//
//// 🃏 Pedir carta
//function hit() {
//  if (!currentGameId) return alert("Primero crea o consulta una partida");
//  fetch(`${api}/game/${currentGameId}/hit`, {
//    method: "POST",
//    headers: { Authorization: `Bearer ${token}` }
//  })
//    .then(res => res.json())
//    .then(getGame);
//}
//
//// ✋ Plantarse
//function stand() {
//  if (!currentGameId) return alert("Primero crea o consulta una partida");
//  fetch(`${api}/game/${currentGameId}/stand`, {
//    method: "POST",
//    headers: { Authorization: `Bearer ${token}` }
//  })
//    .then(res => res.json())
//    .then(getGame);
//}
//
//// 🏆 Ranking
//function getRanking() {
//  fetch(`${api}/player/ranking`, {
//    headers: { Authorization: `Bearer ${token}` }
//  })
//    .then(res => res.json())
//    .then(data => {
//      const list = document.getElementById("rankingList");
//      list.innerHTML = "";
//      data.data.forEach(p => {
//        const li = document.createElement("li");
//        li.innerText = `Pos ${p.position}. ${p.userName} - V: ${p.gamesWon}, D: ${p.gamesLost}, E: ${p.gamesTied}, %V: ${p.winPercentage}`;
//        list.appendChild(li);
//      });
//    });
//}
//
//// ♠️♥️♦️♣️ Símbolos de palos
//function getSuit(suit) {
//  switch (suit) {
//    case "HEARTS": return "♥️";
//    case "DIAMONDS": return "♦️";
//    case "CLUBS": return "♣️";
//    case "SPADES": return "♠️";
//    default: return suit;
//  }
//}
//function getMyProfile() {
//  fetch(`${api}/player/me`, {
//    headers: {
//      "Authorization": "Bearer " + token
//    }
//  })
//    .then(res => res.json())
//    .then(data => {
//      console.log("✅ API response from /player/me:", data);
//
////    if (data.success !== false) {
////      alert("❌ Error: " + data.message);
////      return;
////    }
//
//      const player = data.data;
//
//      document.getElementById("playerProfile").innerHTML = `
//        <p><strong>User:</strong> ${player.userName}</p>
//        <p><strong>Alias:</strong> ${player.alias}</p>
//        <p><strong>Games Played:</strong> ${player.gamesPlayed}</p>
//        <p><strong>Wins:</strong> ${player.gamesWon}</p>
//        <p><strong>Losses:</strong> ${player.gamesLost}</p>
//        <p><strong>Ties:</strong> ${player.gamesTied}</p>
//      `;
//
//      const tableBody = document.querySelector("#myGamesTable tbody");
//      tableBody.innerHTML = "";
//
//      player.games.forEach(game => {
//        const row = document.createElement("tr");
//        row.innerHTML = `
//          <td>${game.number}</td>
//          <td>${game.id}</td>
//          <td>${game.status}</td>
//          <td>${game.result}</td>
//          <td>${new Date(game.createdAt).toLocaleString()}</td>
//        `;
//        tableBody.appendChild(row);
//      });
//    })
//    .catch(err => {
//      console.error("💥 Fetch failed:", err);
//      alert("Could not load profile.");
//    });
//}
//
