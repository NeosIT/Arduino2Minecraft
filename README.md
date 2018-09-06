# Minecraft Arduino

Dieses Minecraft Spigot Server Plugin dient als Schnittstelle zwischen der Server Welt und dem Arduino.
Die Kommunikation erfolgt seriell über das USB-Kabel. Die Befehle werden als String, der mit einem Zeilenumbruch "\n" endet, übertragen.

Das Dokument minecraftInput.pdf skizziert, wie ein Ereignis beim Arduino zum Server übertragen und dort ein Ereignis ausgelöst wird.
Das Dokument minecraftOutput.pdf zeigt, wie Informationen vom Server zum Arduino übertragen werden und dort ein Ereignis auslösen.

Das Plugin liegt unter plugins/Arduino2Minecraft/config.yml eine Konfigurationsdatei an. Diese kann bearbeitet werden.

## Hinweis für Linux Nutzer

Damit die Anwendung vernünftig unter Linux läuft und die USB Schnittstellen erkennen kann ist es notwendig folgende Parameter beim Start des Servers mitzugeben:

```bash
-Dgnu.io.rxtx.SerialPorts=/dev/ttyACM1
```

Hier gilt es `/dev/ttyACM1` mit dem jeweils richtigen Port zu ersetzten. Diesen kann man beispielsweise in der Arduino IDE auslesen.

Eine kompletter Befehl um den Server dann zu starten sieht wie folgt aus:

```bash
java -Dgnu.io.rxtx.SerialPorts=/dev/ttyACM1 -jar spigot-1.9.4-R0.1-SNAPSHOT.jar
```

### Hinweis für Fedora

Aus Sicherheitsgründen ist es unter Fedora nicht möglich den Server ohne Adminberichtigung zu starten. Grund dafür ist die RXTX Bibliothek welche versucht eine Lock-Datei im "alten" Stil zu erzeugen was nicht mehr funktioniert (siehe [Issue](https://github.com/openhab/openhab1-addons/issues/3257)).

Folgende Möglichkeiten ergeben sich jetzt:

1. Server als Admin starten (via `sudo`)
2. Das Verzeichnis `/var/lock` manipulieren

   ```bash
   sudo chown root:lock /var/lock
   sudo chmod g+w /var/lock
   ```

## Beispiel für Arduino -> Minecraft

Der Arduino sendet einen Befehl mit einem Linebreak, z. B. könnte der Arduino den Wert *knopf1\n* senden.

In der Konfigurationsdatei muss am Ende die Zeile *arduino_[arduino_wert]: [mein_befehl]* eingefügt werden.
Wenn der Spieler Player1 gekickt werden soll, sobald der Arduino den Wert *knopf1\n* sendet, muss Folgendes eingefügt werden:

    arduino_knopf1: kick Player1

Die Beispieldateien findest Du im Ordner */beispiel_knopf1*
