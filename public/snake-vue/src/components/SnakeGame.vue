<template>
  <main id="game-container" aria-label="Juego de la culebrita">
    <h1 class="main-title">Culebrita Clásica</h1>
    <canvas
      ref="canvas"
      id="game-canvas"
      width="320"
      height="480"
      tabindex="0"
      aria-label="Área de juego"
    ></canvas>
    <section id="customization-panel" aria-label="Personalización">
      <div class="custom-row">
        <label>
          <span>Color culebra</span>
          <input
            type="color"
            v-model="snakeColor"
            aria-label="Color de la culebra"
          />
        </label>
        <label>
          <span>Cabeza</span>
          <input
            type="color"
            v-model="snakeHeadColor"
            aria-label="Color de la cabeza"
          />
        </label>
        <label>
          <span>Comida</span>
          <input
            type="color"
            v-model="foodColor"
            aria-label="Color de la comida"
          />
        </label>
      </div>
      <div class="custom-row">
        <label>
          <span>Tema</span>
          <select v-model="bgColor" aria-label="Tema de fondo">
            <option value="#222">Oscuro clásico</option>
            <option value="#fff">Claro</option>
            <option value="#1e1e3f">Nocturno azul</option>
            <option value="#2d2d2d">Gris moderno</option>
          </select>
        </label>
        <label>
          <span>Tamaño inicial</span>
          <select v-model.number="initialSize" aria-label="Tamaño inicial">
            <option :value="3">Normal</option>
            <option :value="5">Largo</option>
            <option :value="1">Corto</option>
          </select>
        </label>
      </div>
    </section>
    <section id="score-board" aria-label="Marcador">
      <span
        >Puntaje: <span id="score">{{ score }}</span></span
      >
      <span
        >Nivel: <span id="level">{{ level }}</span></span
      >
      <button id="restart-btn" @click="restartGame">🔄 Reiniciar</button>
      <button id="show-records" @click="showRecords = true">🏆 Récords</button>
    </section>
    <section
      id="records-panel"
      :class="{ hidden: !showRecords }"
      aria-label="Mejores puntajes"
      v-show="showRecords"
    >
      <h2>Mejores Puntajes</h2>
      <ol id="records-list">
        <li v-if="records.length === 0">No hay récords aún</li>
        <li v-for="(r, i) in records" :key="i">
          Puntaje: {{ r.score }} | Nivel: {{ r.level }} |
          {{ new Date(r.date).toLocaleString() }}
        </li>
      </ol>
      <button id="close-records" @click="closeRecords">Cerrar</button>
    </section>
    <footer>
      <small>&copy; 2025 - Juego estilo Nokia por JhonLaurens</small>
    </footer>
    <!-- Controles táctiles -->
    <nav id="touch-controls" aria-label="Controles táctiles">
      <div class="touch-grid">
        <button aria-label="Arriba" @click="move('UP')" class="touch-btn">
          ⬆️
        </button>
      </div>
      <div class="touch-grid">
        <button aria-label="Izquierda" @click="move('LEFT')" class="touch-btn">
          ⬅️
        </button>
        <button aria-label="Abajo" @click="move('DOWN')" class="touch-btn">
          ⬇️
        </button>
        <button aria-label="Derecha" @click="move('RIGHT')" class="touch-btn">
          ➡️
        </button>
      </div>
    </nav>
  </main>
</template>

<script setup lang="ts">
import { nextTick, onMounted, onUnmounted, ref, watch } from "vue";

const canvas = ref<HTMLCanvasElement | null>(null);
const box = 20;
const width = 320;
const height = 480;
const rows = height / box;
const cols = width / box;

const snakeColor = ref("#50ff6b");
const snakeHeadColor = ref("#bfffbd");
const bgColor = ref("#222");
const foodColor = ref("#3bff4a");
const initialSize = ref(3);

const score = ref(0);
const level = ref(1);
const speed = ref(100);
const running = ref(true);
const direction = ref("RIGHT");
const snake = ref<{ x: number; y: number }[]>([]);
const food = ref({ x: 0, y: 0 });
let gameInterval: ReturnType<typeof setInterval> | null = null;
let directionLocked = false;

const showRecords = ref(false);
const records = ref<{ score: number; level: number; date: string }[]>([]);

function closeRecords() {
  showRecords.value = false;
}

function randomFood(): { x: number; y: number } {
  let newFood: { x: number; y: number } = { x: 0, y: 0 };
  while (true) {
    newFood = {
      x: Math.floor(Math.random() * cols),
      y: Math.floor(Math.random() * rows),
    };
    if (
      !snake.value.some(
        (segment) => segment.x === newFood.x && segment.y === newFood.y
      )
    ) {
      return newFood;
    }
  }
}

function initGame() {
  snake.value = [];
  for (let i = 0; i < initialSize.value; i++) {
    snake.value.push({ x: Math.floor(cols / 2) - i, y: Math.floor(rows / 2) });
  }
  direction.value = "RIGHT";
  food.value = randomFood();
  score.value = 0;
  level.value = 1;
  speed.value = 100;
  running.value = true;
  if (gameInterval) clearInterval(gameInterval);
  gameInterval = setInterval(gameLoop, speed.value);
}

function update() {
  let head = { ...snake.value[0] };
  switch (direction.value) {
    case "LEFT":
      head.x--;
      break;
    case "UP":
      head.y--;
      break;
    case "RIGHT":
      head.x++;
      break;
    case "DOWN":
      head.y++;
      break;
  }
  // Colisión con bordes
  if (head.x < 0 || head.x >= cols || head.y < 0 || head.y >= rows) {
    running.value = false;
    saveScore();
    return;
  }
  // Colisión con sí mismo
  if (
    snake.value.some((segment) => segment.x === head.x && segment.y === head.y)
  ) {
    running.value = false;
    saveScore();
    return;
  }
  // Comer comida
  if (head.x === food.value.x && head.y === food.value.y) {
    snake.value.unshift(head);
    food.value = randomFood();
    score.value += 10;
    if (score.value % 50 === 0) {
      level.value++;
      speed.value = Math.max(40, speed.value - 10);
      if (gameInterval) clearInterval(gameInterval);
      gameInterval = setInterval(gameLoop, speed.value);
    }
  } else {
    snake.value.unshift(head);
    snake.value.pop();
  }
}

function draw() {
  const ctx = canvas.value!.getContext("2d")!;
  ctx.fillStyle = bgColor.value;
  ctx.fillRect(0, 0, width, height);
  // Comida
  ctx.fillStyle = foodColor.value;
  ctx.fillRect(food.value.x * box, food.value.y * box, box, box);
  // Culebra
  for (let i = 0; i < snake.value.length; i++) {
    ctx.fillStyle = i === 0 ? snakeHeadColor.value : snakeColor.value;
    ctx.fillRect(snake.value[i].x * box, snake.value[i].y * box, box, box);
    ctx.strokeStyle = "#111";
    ctx.strokeRect(snake.value[i].x * box, snake.value[i].y * box, box, box);
  }
  if (!running.value) drawGameOver(ctx);
}

function drawGameOver(ctx: CanvasRenderingContext2D) {
  ctx.fillStyle = "rgba(20,20,20,0.82)";
  ctx.fillRect(0, height / 2 - 60, width, 120);
  ctx.fillStyle = "#fff";
  ctx.font = "bold 32px monospace";
  ctx.textAlign = "center";
  ctx.fillText("¡GAME OVER!", width / 2, height / 2 - 10);
  ctx.font = "16px monospace";
  ctx.fillText(
    `Puntaje: ${score.value}  Nivel: ${level.value}`,
    width / 2,
    height / 2 + 20
  );
  ctx.font = "14px monospace";
  ctx.fillText(
    "Presiona 'Reiniciar' para jugar de nuevo",
    width / 2,
    height / 2 + 45
  );
}

function gameLoop() {
  update();
  draw();
  directionLocked = false;
  if (!running.value && gameInterval) {
    clearInterval(gameInterval);
    gameInterval = null;
  }
}

function move(dir: string) {
  if (!running.value || directionLocked) return;
  const opposites: Record<string, string> = {
    LEFT: "RIGHT",
    RIGHT: "LEFT",
    UP: "DOWN",
    DOWN: "UP",
  };
  if (dir !== opposites[direction.value]) {
    direction.value = dir;
    directionLocked = true;
  }
}

function handleKey(e: KeyboardEvent) {
  if (!running.value) return;
  switch (e.key) {
    case "ArrowLeft":
    case "a":
    case "A":
      move("LEFT");
      break;
    case "ArrowUp":
    case "w":
    case "W":
      move("UP");
      break;
    case "ArrowRight":
    case "d":
    case "D":
      move("RIGHT");
      break;
    case "ArrowDown":
    case "s":
    case "S":
      move("DOWN");
      break;
  }
}

function restartGame() {
  initGame();
  draw();
}

function saveScore() {
  try {
    const local: { score: number; level: number; date: string }[] = JSON.parse(
      localStorage.getItem("snakeRecords") || "[]"
    );
    local.push({
      score: score.value,
      level: level.value,
      date: new Date().toISOString(),
    });
    local.sort((a, b) => b.score - a.score);
    localStorage.setItem("snakeRecords", JSON.stringify(local.slice(0, 10)));
    records.value = local.slice(0, 10);
  } catch {
    records.value = [];
  }
}

function loadRecords() {
  try {
    records.value = JSON.parse(
      localStorage.getItem("snakeRecords") || "[]"
    ).slice(0, 10);
  } catch {
    records.value = [];
  }
}

onMounted(() => {
  loadRecords();
  initGame();
  draw();
  window.addEventListener("keydown", handleKey);
  nextTick(() => canvas.value?.focus());
});

onUnmounted(() => {
  if (gameInterval) clearInterval(gameInterval);
  window.removeEventListener("keydown", handleKey);
});

watch([snakeColor, snakeHeadColor, bgColor, foodColor, initialSize], () => {
  nextTick(() => draw());
});
</script>

<style scoped>
.main-title {
  font-size: 2.2rem;
  margin-bottom: 0.5em;
  letter-spacing: 1px;
}
.custom-row {
  display: flex;
  gap: 1.2em;
  margin-bottom: 0.5em;
  flex-wrap: wrap;
  justify-content: center;
}
.custom-row label {
  display: flex;
  flex-direction: column;
  align-items: center;
  font-size: 0.98em;
  color: #bfffcf;
  gap: 0.2em;
}
#customization-panel {
  margin-bottom: 0.7em;
}
#score-board {
  margin-bottom: 0.7em;
}
#score-board button {
  font-size: 1em;
  padding: 4px 14px;
  margin-left: 8px;
}
#records-panel {
  margin-bottom: 0.7em;
}
#records-list li {
  font-size: 1em;
  margin-bottom: 2px;
}
/* Mejor distribución de controles táctiles */
#touch-controls {
  margin-top: 1.2em;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 0.2em;
}
.touch-grid {
  display: flex;
  flex-direction: row;
  justify-content: center;
  gap: 0.2em;
}
.touch-btn {
  font-size: 1.6rem;
  min-width: 44px;
  min-height: 44px;
  padding: 0;
  margin: 0 2px;
}
@media (max-width: 500px) {
  .main-title {
    font-size: 1.1rem;
  }
  .custom-row label {
    font-size: 0.9em;
  }
  #score-board button {
    font-size: 0.95em;
    padding: 4px 8px;
  }
  #game-container {
    padding: 8px 2vw;
    width: 99vw;
    min-width: unset;
    max-width: 99vw;
  }
  #game-canvas {
    width: 98vw;
    height: auto;
    max-width: 99vw;
  }
  #records-panel {
    max-width: 98vw;
  }
  #touch-controls {
    gap: 0.1em;
  }
  .touch-btn {
    min-width: 38px;
    min-height: 38px;
    font-size: 1.2rem;
  }
}
</style>
