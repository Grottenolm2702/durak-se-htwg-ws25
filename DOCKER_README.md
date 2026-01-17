# Docker Setup für Durak Game

## Voraussetzungen

### Linux
- Docker installiert
- X11 Server läuft (normalerweise vorhanden)
- xhost installiert: `sudo pacman -S xorg-xhost` (Arch) oder `sudo apt install x11-xserver-utils` (Debian/Ubuntu)

### Windows
- Docker Desktop installiert
- X Server (z.B. VcXsrv, Xming oder X410)
- VcXsrv Config: "Disable access control" aktivieren

## Schnellstart

### Linux/macOS
```bash
# Erstes Mal oder nach Code-Änderungen: Image bauen
./run-docker.sh -b

# Danach nur starten (viel schneller)
./run-docker.sh
```

### Windows
```powershell
# Erstes Mal oder nach Code-Änderungen: Image bauen
.\run-docker.bat -b

# Danach nur starten
.\run-docker.bat
```

Wähle dann: **1** für GUI oder **2** für TUI.

## Manuelle Ausführung

### Image bauen
```bash
docker build -t durak:v1 .
```

### Mit GUI starten (Linux)
```bash
xhost +local:docker
docker run -it --rm -e DISPLAY=$DISPLAY -v /tmp/.X11-unix:/tmp/.X11-unix --network host durak:v1
```

### Mit GUI starten (Windows)
```powershell
# X Server muss laufen
docker run -it --rm -e DISPLAY=host.docker.internal:0 durak:v1
```

### Nur TUI (alle Plattformen)
```bash
docker run -it --rm durak:v1
