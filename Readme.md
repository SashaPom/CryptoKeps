# Trading Dashboard (Java Bot API + React UI)

> **Part of a 3-repository system.**  
> This repository contains the **Java (Spring Boot) trading service** + **React (TypeScript) frontend**.  
> The whole system is intended to be started **together via Docker Compose** from the `infra/` directory (in the infrastructure repo / infra folder of the project).  
>
> ✅ You *can* run services separately for development, but for “everything works end-to-end” you use **docker-compose in `infra/`**.

---

## What this repo contains

- **Backend/** — Spring Boot API (Java) for Bybit trading bots
- **Frontend/** — React + TypeScript UI to manage:
  - risk profiles (via Django service)
  - bots & trades (via Java service)

---

## Architecture (how it fits into the whole system)

There are **3 repos** in the project:

1) **Django Manager Service** (profiles CRUD + server assignment + WS “update” signaling)  
2) **Watcher Service** (one per exchange: OKX/Binance; streams live risk state via WebSocket)  
3) **This repo: Java Bot API + React UI**  
   - Java handles **Bybit trading + trade history**
   - React provides **single UI** for both “Risk Manager” and “Bots”

**In production/normal usage** everything is wired behind a gateway/proxy and started by **docker compose from `infra/`**.

---

## Features

### Java Backend (Spring Boot)
- **Bots list** with KPI stats (PnL, winrate, drawdown, equity curve)
- **Trades list** for a selected bot (trade history table)
- **Webhook endpoint** to receive trading signals (e.g., TradingView)
- **Test signal endpoint** to simulate a signal from UI (debug tool)
- Storage:
  - accounts (bots)
  - trade history

### React Frontend (Vite + TS + Tailwind)
- **Risk Manager page**
  - CRUD profiles via Django API
  - Start/Stop profiles
  - Live WS stream from watcher services (OKX/Binance)
- **Bots page**
  - List bots (Java API)
  - Show bot metrics
  - Show trades by period
  - Send “test signal” to bot

---

## Backend (Java) overview

### API Controllers

#### `BotController`
- `GET /bots`
  - query params:
    - `q` — search by name/key
    - `status` — `RUNNING` / `PAUSED`
    - `period` — `24h` (default), `7d`, `30d`
  - returns list of `BotCardDto`

#### `TradeController`
- `GET /bots/{botId}/trades?period=...`
  - returns list of `TradeRowDto`

#### `TestSignalController`
- `POST /bots/{botId}/test-signal`
  - body: `TestSignalRequest`
  - returns `{ ok: true }`

#### `WebhookController` (Bybit signals)
- `POST /api/v1/webhook/signal`
  - body: `WebhookSignalDto`
  - triggers bot logic via `WebhookService`

---

## Trading logic (high level)

### `WebhookServiceImpl` strategy
- Receives signal: `accountId`, `symbol`, `side`, `entryPrice`, `ATR`, etc.
- Risk sizing:
  - risk is fixed to **0.5%** of total balance
  - `quantity = (totalBalance * 0.005) / ATR`
- On **BUY (Long)**:
  - ignores signal if position already open
  - opens long with MARKET order
  - sets:
    - `stop = entry - ATR`
    - `take = entry + ATR * 100`
- On **SELL (Close)**:
  - closes open position (if exists)
  - updates trade history with calculated PnL

---

## Frontend (React) overview

### Pages

#### `RiskManager.tsx`
- Loads profiles from Django:
  - `GET /api/py/dj/tw/api/watcher/profiles/?all=1`
- Opens WebSockets (via gateway):
  - `ws://<host>/ws/okx/`
  - `ws://<host>/ws/binance/`
- Subscribes by sending IDs: `"1.4.5"`
- Receives streaming rows and updates UI live
- Start/Stop triggers:
  - Django API calls (`/profile/start`, `/profile/stop`)
  - then sends `"update"` into the WS so watcher reloads profiles immediately

#### `Bots.tsx`
- Loads bots from Java:
  - `GET /api/java/bots?...`
- Loads trades:
  - `GET /api/java/bots/{id}/trades?period=...`
- Sends test signal:
  - `POST /api/java/bots/{id}/test-signal`

---

## API routing (through gateway)

UI talks to different backends via prefixes:

- **Django (profiles)**: `/api/py/dj/tw/api/...`
- **Java (bots/trades)**: `/api/java/...`
- **WebSockets**:
  - `/ws/okx/` → OKX watcher
  - `/ws/binance/` → Binance watcher

So frontend uses `location.host` and doesn’t hardcode internal service ports.

---

## Run the whole system (recommended)

✅ **Main way to run** — from `infra/` (docker-compose).

Example (adjust paths to your infra repo):

```bash
cd infra
docker compose up --build
```
