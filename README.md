# 🚌 Ticket to Ride: London (Java Desktop Game)

A digital version of the *Ticket to Ride: London* board game, built from scratch in **Java Swing**. Players collect coloured cards, claim bus routes across a map of London and race to complete secret destination tickets, all on one computer in pass-and-play style.

> Unofficial fan implementation for learning purposes. *Ticket to Ride* is a trademark of its respective owners.

---

## ✨ Features

- **Full game loop**: lobby, player colour selection, turns, end-game detection and final scoring
- **Pass-and-play multiplayer**: a hand-off screen hides each player's cards between turns
- **Route claiming** with colour-specific and any-colour routes, plus **double-route rules**
- **Destination tickets**: draw up to three, choose which to keep, and get scored on completed and failed tickets
- **Longest-path bonus**, computed with graph search over each player's claimed routes
- **Interactive map UI**: clickable board, card hand bar, score panel, toast notifications and an in-game rules guide
- **Data-driven board**: cities, routes and settings are loaded from a JSON config

---

## 🏗️ Design

The codebase (60+ classes) is organised around classic OOP design patterns so new rules or maps can be added without touching the core:

| Pattern | Where it's used |
|---|---|
| **Strategy** | Scoring (`RouteScoreStrategy`, `TicketScoreStrategy`, `LongestPathStrategy`) and route-claim rules (`AnyColourStrategy`, `SpecificColourStrategy`) |
| **Command** | Player moves (`ClaimRouteAction`, `DrawTrainCardAction`, `DrawDestinationTicketAction`) with an action history |
| **Observer** | `GameStateListener` keeps the UI in sync with the game state |
| **Variant / config** | `GameVariant` and `MapConfiguration` make the London edition one pluggable variant |

```
src/
├── actions/    player moves (Command pattern)
├── game/       game controller, turns, scoring
├── model/      board, cities, routes, cards, decks, players
├── strategy/   scoring and claiming rules (Strategy pattern)
├── variant/    London edition rules and map config
└── ui/         Swing screens, panels and dialogs
```

UML class, sequence and domain diagrams are in `docs/` and the sprint deliverable folders.

---

## 🚀 Run it

**Requires Java 17+.**

```bash
java -jar TicketToRideLondon.jar
```
Or just double-click `TicketToRideLondon.jar`.

### Build from source (JDK 17+)
- **Windows:** double-click `build.bat`
- **macOS / Linux:**
  ```bash
  chmod +x build.sh
  ./build.sh      # or: make
  ```

There are no third-party dependencies. The JSON parser is bundled in the source.

---

## 👥 Team & my role

Built by a team of three using an agile, sprint-based process. **I was the top contributor (83 of 189 commits).** My work included:
- Designing the **scoring system** with the Strategy pattern (routes, tickets, longest path, London scoring)
- Implementing **destination ticket** and **double-route** rules
- Refactoring scoring onto a reusable **map graph** utility
- **Build scripts** for Windows/macOS/Linux and the bundled **JSON parser**
- UI polish across the score panel, dialogs and game menu
