# Durak

[![CI](https://github.com/Grottenolm2702/durak-se-htwg-ws25/actions/workflows/ci.yml/badge.svg)](https://github.com/Grottenolm2702/durak-se-htwg-ws25/actions/workflows/ci.yml)
[![Coverage Status](https://coveralls.io/repos/github/Grottenolm2702/durak-se-htwg-ws25/badge.svg?branch=main)](https://coveralls.io/github/Grottenolm2702/durak-se-htwg-ws25?branch=main)

## Overview

This project implements the popular Russian card game Durak. The game is designed for 2-6 players and uses a standard deck of 36 cards. Players aim to get rid of all their cards while avoiding becoming the "durak" (fool). The implementation is built with Scala 3.

## Requirements

- JDK 21 or higher (required for ScalaFX 23.x)
- sbt 1.x
- For GUI: JavaFX support (included in most JDK distributions)

### Docker (Alternative)
- Docker installed
- For GUI: X11 server (Linux/macOS) or VcXsrv/Xming (Windows)
- See [DOCKER_README.md](DOCKER_README.md) for Docker setup

## Installation

```bash
git clone <repository-url>
cd durak-se-htwg-ws25
sbt compile
```

## Usage

### Native
Run the game (starts both GUI and TUI):
```bash
sbt run
```

### Docker
See [DOCKER_README.md](DOCKER_README.md) for detailed Docker instructions.

Quick start:
```bash
./run-docker.sh -b  # First time: build image
./run-docker.sh     # Start game (choose GUI or TUI)
```

### Testing
Run tests:
```bash
sbt test
```

Start a Scala REPL:
```bash
sbt console
```

## Project Structure

The project follows a standard sbt layout with source code in `src/main/scala` and tests in `src/test/scala`.

## License

This project is licensed under the terms specified in the LICENSE file.
