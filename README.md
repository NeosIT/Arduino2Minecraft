Dieses Minecraft Spigot Server Plugin dient als Schnittstelle zwischen der Server Welt und dem Arduino.
Die Kommunikation erfolgt seriell über das USB-Kabel. Die Befehle werden als String, der mit einem Zeilenumbruch "\n" endet, übertragen.

Das Dokument minecraftInput.pdf skizziert, wie ein Ereignis beim Arduino zum Server übertragen und dort ein Ereignis ausgelöst wird.
Das Dokument minecraftOutput.pdf zeigt, wie Informationen vom Server zum Arduino übertragen werden und dort ein Ereignis auslösen.

Das Plugin liegt unter plugins/Arduino2Minecraft/config.yml eine Konfigurationsdatei an. Diese kann bearbeitet werden.

# Beispiel für Arduino -> Minecraft:

Der Arduino sendet einen Befehl mit einem Linebreak, z. B. könnte der Arduino den Wert "knopf1\n" senden.

In der Konfigurationsdatei muss am Ende die Zeile "arduino_[arduino_wert]: [mein_befehl]" eingefügt werden.
Wenn der Spieler Player1 gekickt werden soll, sobald wenn der Arduino den Wert "knopf1\n" sendet, muss folgendes eingefügt werden:
arduino_knopf1: kick Player1

Die Beispieldateien findest du im Ordner beispiel_knopf1
