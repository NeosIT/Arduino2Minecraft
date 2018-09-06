int ledPin0 = 12;
int ledPin1 = 12;
int ledPin2 = 11;
int ledPin3 = 10;
int ledPin4 = 9;
int ledPin5 = 8;
int ledBlinkPin = 3;
  
int buttonPin1 = 7;
int buttonPin2 = 6;
int buttonPin3 = 5;
  
int soundPin = 13;
  
int potPin = 0;
int potValue = 0;
  
int finalButton = 4;
boolean enableFinal = false;
  
void beep(String level) {
  if(level == "low") {
    tone(soundPin, 500, 50);  
  }
  if(level == "medium") {
    tone(soundPin, 2000, 50);
  }
  if(level == "high") {
    tone(soundPin, 5000, 50);
  }
}
  
void toggleLed(int ledPin) {
  digitalWrite(ledPin, !digitalRead(ledPin));
  sendCommand("buttonToggle" + String(!digitalRead(ledPin)));
}
  
void sendCommand(String command) {
    Serial.println(command);
}
  
void checkPotVal(int pin) {
  int val = map(analogRead(pin), 0, 1023, 0, 24);
  if(abs(val - potValue) &gt; 1){
    //Serial.println(val * 1000);
    String r = String(val * 1000);
    sendCommand("poti:" + r);
    beep("low");
    potValue = val;
    delay(10);
  }
}
  
boolean buttonPressed(int button) {
  if(digitalRead(button)) {
   beep("medium");
    return true;
  }
  return false;
}
  
void setup() {
  // die serielle Kommunikation mit einer Baud-Rate von 9600 initalisieren
  Serial.begin(9600);
  
  pinMode(buttonPin1, INPUT);
  pinMode(buttonPin2, INPUT);
  
  // die digitalen Ports 8 - 12 sollen Strom ausgeben
  pinMode(ledPin1, OUTPUT);
  pinMode(ledPin2, OUTPUT);
  pinMode(ledPin3, OUTPUT);
  pinMode(ledPin4, OUTPUT);
  pinMode(ledPin5, OUTPUT);
  pinMode(ledBlinkPin, OUTPUT);
  
  pinMode(buttonPin3, OUTPUT);
  
  pinMode(soundPin, OUTPUT);
}
  
void loop() {
  
  if(buttonPressed(buttonPin1)) {
    //Serial.write("button1\n");
    sendCommand("button1");
    delay(200);
  }
  
  if(buttonPressed(buttonPin2)) {
    sendCommand("button2");
    delay(200);
  }
  
  if(buttonPressed(buttonPin3)) {
    sendCommand("button3");
    delay(200);
  }
  if(enableFinal) {
    digitalWrite(ledBlinkPin, !digitalRead(ledBlinkPin));
    delay(500);
  } else {
    digitalWrite(ledBlinkPin, LOW);
  }
  if(enableFinal &amp;&amp; buttonPressed(finalButton)) {
    beep("LOW");
    sendCommand("finalButton");
  }
  
  checkPotVal(potPin);
  
  // Prüfe, ob der Host-Rechner einen Befehl gesendet hat
  if (Serial.available()) {
    String input = Serial.readStringUntil('\n');
    if(input == "toggleFinal") {
      enableFinal = !enableFinal;
    }
    if (input == "life 5") {
      digitalWrite(ledPin1, 1);
      digitalWrite(ledPin2, 1);
      digitalWrite(ledPin3, 1);
      digitalWrite(ledPin4, 1);
      digitalWrite(ledPin5, 1);     
    } else if (input == "life 4") {
      digitalWrite(ledPin1, 1);
      digitalWrite(ledPin2, 1);
      digitalWrite(ledPin3, 1);
      digitalWrite(ledPin4, 1);
      digitalWrite(ledPin5, 0);
    } else if (input == "life 3") {
      digitalWrite(ledPin1, 1);
      digitalWrite(ledPin2, 1);
      digitalWrite(ledPin3, 1);
      digitalWrite(ledPin4, 0);
      digitalWrite(ledPin5, 0);
    } else if (input == "life 2") {
      digitalWrite(ledPin1, 1);
      digitalWrite(ledPin2, 1);
      digitalWrite(ledPin3, 0);
      digitalWrite(ledPin4, 0);
      digitalWrite(ledPin5, 0);
    } else if (input == "life 1") {
      digitalWrite(ledPin1, 1);
      digitalWrite(ledPin2, 0);
      digitalWrite(ledPin3, 0);
      digitalWrite(ledPin4, 0);
      digitalWrite(ledPin5, 0);
    } else if (input == "life 0") {
      digitalWrite(ledPin1, 0);
      digitalWrite(ledPin2, 0);
      digitalWrite(ledPin3, 0);
      digitalWrite(ledPin4, 0);
      digitalWrite(ledPin5, 0);
    }
  }
}