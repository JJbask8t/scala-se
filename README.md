# StockPilot

[![Scala CI](https://github.com/JJbask8t/scala-se/actions/workflows/scala.yml/badge.svg)](https://github.com/JJbask8t/scala-se/actions/workflows/scala.yml)
[![Coverage Status](https://coveralls.io/repos/github/JJbask8t/scala-se/badge.svg?branch=main)](https://coveralls.io/github/JJbask8t/scala-se?branch=main)

**StockPilot** is a Scala-based application for investment portfolio management and stock tracking. The project demonstrates the application of classic software design patterns and architectural principles.

A key feature of the application is the parallel operation of two interfaces: Graphical (GUI) and Textual (TUI), which are fully synchronized in real-time.

## Features

* **Portfolio Management:** Add and remove stocks.
* **Two Tracking Modes:**
    * *Watchlist:* Quantity = 0.
    * *Real Portfolio:* Quantity > 0.
* **Automatic Analytics:**
    * Fair Value calculation based on EPS.
    * Verdict generation: **BUY** (Undervalued), **SELL** (Overvalued), **HOLD**.
    * Total position value calculation.
* **Interface Synchronization:** Changes in the GUI are instantly reflected in the console and vice versa.
* **Persistence:** Automatic data saving and loading in JSON format (`stock_data.json`).
* **Data Operations:**
    * Price filtering.
    * Sorting (by Ticker, Price Ascending/Descending).
    * Undo last action.
* **Reporting:** CSV report generation for the current portfolio.

## Tech Stack

* **Language:** Scala 3.3.6
* **Build Tool:** sbt 1.11.x
* **GUI:** ScalaSwing
* **Serialization:** Play-JSON
* **Testing:** ScalaTest

## Architecture & Design Patterns

The project is built on the **MVC (Model-View-Controller)** pattern, adhering to **Component-based architecture** principles.

Key Design Patterns used:
* **Observer:** For synchronizing updates between the Model, Controller, and both Views (TUI/GUI).
* **Command:** To encapsulate user actions and implement Undo functionality.
* **Memento:** For saving and restoring repository state (snapshots).
* **Strategy:** For implementing various stock sorting algorithms.
* **Factory Method:** For safely creating stock objects from user input.
* **Decorator:** For adding logging to the repository without modifying its core logic.
* **Dependency Injection:** Wiring dependencies via a central assembly module (`StockModule`).

## Installation & Usage

### Standard sbt Usage
This is a normal sbt project. You can compile code with `sbt compile`, run it with `sbt run`, and `sbt console` will start a Scala 3 REPL.

For more information on the sbt-dotty plugin, see the [scala3-example-project](https://github.com/scala/scala3-example-project/blob/main/README.md).

### Quick Start Guide

1.  **Clone the repository:**
    ```bash
    git clone [https://github.com/JJbask8t/scala-se.git](https://github.com/JJbask8t/scala-se.git)
    cd scala-se/starttemplate
    ```

2.  **Run the application:**
    ```bash
    sbt run
    ```
    Upon launch, a graphical window will open, and a text menu will become available in the terminal.

3.  **Run tests:**
    ```bash
    sbt test
    ```

4.  **Check code coverage:**
    ```bash
    sbt clean coverage test coverageReport
    ```

## User Manual

### Data Input
When adding a stock, the system requests:
* **Ticker:** Stock symbol (e.g., AAPL).
* **P/E:** Price-to-Earnings ratio.
* **EPS:** Earnings Per Share.
* **Price:** Current price.
* **Quantity:** Number of shares (enter 0 to add to Watchlist).

### Verdict Logic
The system automatically analyzes the stock using a simplified Graham formula:
* `Fair Value = EPS * 15`
* **BUY:** If `Price < Fair Value`
* **SELL:** If `Price > Fair Value * 1.5`
* **HOLD:** In all other cases.

---

## 🐳 Docker Usage

You can run StockPilot inside a Docker container. This ensures that the application runs in an isolated environment with all dependencies pre-installed (Java 21, Scala, sbt, GUI libraries).

### 1. Build the Image
First, build the Docker image from the project root directory:

```bash
docker build -t stockpilot:v1 .
```
### 2. Run the Container
The run command depends on your operating system because the application requires access to your graphical display (X11).

#### **macOS (with XQuartz)**
Prerequisites:

Install XQuartz.

Open XQuartz preferences (Cmd + ,) -> Security tab -> Check "Allow connections from network clients".

Restart XQuartz.

Run:
```bash
# Allow local docker connection to X server
xhost +localhost

# Run container
docker run -it --rm -e DISPLAY=host.docker.internal:0 stockpilot:v1
```
#### **Linux**
Linux users can map the X11 socket directly. No additional software is usually required.

Run:
```bash
docker run -it --rm \
    -e DISPLAY=$DISPLAY \
    -v /tmp/.X11-unix:/tmp/.X11-unix \
    stockpilot:v1
```
#### **Windows (WSL2)**
Prerequisites:

Install an X Server for Windows (e.g., VcXsrv / XLaunch)
https://gemini.google.com/app/213740aff72e687c#:~:text=Windows%20(e.g.%2C-,VcXsrv%20/%20XLaunch,-).

Launch XLaunch with the following settings:

- Select "Multiple windows".

- Check "Disable access control".

Run (in WSL2 terminal):

```bash
# Use the internal Docker host display
docker run -it --rm -e DISPLAY=host.docker.internal:0 stockpilot:v1
```

---
*Project developed as part of a Software Engineering course.*