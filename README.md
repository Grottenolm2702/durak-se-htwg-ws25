# Durak

[![CI](https://github.com/Grottenolm2702/durak-se-htwg-ws25/actions/workflows/ci.yml/badge.svg)](https://github.com/Grottenolm2702/durak-se-htwg-ws25/actions/workflows/ci.yml)
[![Coverage Status](https://coveralls.io/repos/github/Grottenolm2702/durak-se-htwg-ws25/badge.svg?branch=main)](https://coveralls.io/github/Grottenolm2702/durak-se-htwg-ws25?branch=main)

## Overview

This project implements the popular Russian card game Durak. The game is designed for 2-6 players and uses a standard deck of 36 cards. Players aim to get rid of all their cards while avoiding becoming the "durak" (fool). The implementation is built with Scala 3.

## Requirements

- JDK 11 or higher
- sbt 1.x

## Installation

```bash
git clone <repository-url>
cd durak_ronny
sbt compile
```

## Usage

Run the game:
```bash
sbt run
```

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
