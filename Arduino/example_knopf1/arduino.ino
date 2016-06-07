void setup() {
  // Intialisiere die Verbindung mit dem Minecraft Server
  Serial.begin(9600);
}

void loop() {
  // Warte 60 Sekunden
  delay(60000);
  
  // Sende den Text knopf1 mit einem Zeilenumbruch zum Server
  Serial.write("knopf1\n");
}
