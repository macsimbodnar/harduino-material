#include <SoftwareSerial.h>

#define SPEED 150
#define BUFFER_SIZE 11

const int pin_rX = 12;
const int pin_tX = 13;
const int pin_speed = 3;
const int pin_leftF = 0;
const int pin_leftB = 1;
const int pin_rightF = 4;
const int pin_rightB = 2;
const int pin_speedT = 6;
const int pin_turretF = 7;
const int pin_turretB = 5;
const int pin_gunElevationF = 8;
const int pin_gunElevationB = 9;
const int pin_gunFireF = 10;
const int pin_gunFireB = 11;

SoftwareSerial BTserial(pin_rX, pin_tX); // RX | TX

char commandBuffer[BUFFER_SIZE];
int bufferEnd;

void setup() { 
  //Serial.begin(9600);
  BTserial.begin(9600);
  
  pinMode(pin_speed, OUTPUT);
  pinMode(pin_leftF, OUTPUT);
  pinMode(pin_leftB, OUTPUT);
  pinMode(pin_rightF, OUTPUT);
  pinMode(pin_rightB, OUTPUT);
  pinMode(pin_speedT, OUTPUT);
  pinMode(pin_turretF, OUTPUT);
  pinMode(pin_turretB, OUTPUT);
  pinMode(pin_gunElevationF, OUTPUT);
  pinMode(pin_gunElevationB, OUTPUT);
  pinMode(pin_gunFireF, OUTPUT);
  pinMode(pin_gunFireB, OUTPUT);
  
  analogWrite(pin_speed, SPEED);
  digitalWrite(pin_leftF, LOW);
  digitalWrite(pin_leftB, LOW);
  digitalWrite(pin_rightF, LOW);
  digitalWrite(pin_rightB, LOW);
  analogWrite(pin_speedT, SPEED);
  digitalWrite(pin_turretF, LOW);
  digitalWrite(pin_turretB, LOW);
  digitalWrite(pin_gunElevationF, LOW);
  digitalWrite(pin_gunElevationB, LOW);
  digitalWrite(pin_gunFireF, LOW);
  digitalWrite(pin_gunFireB, LOW);

  bufferEnd = 0;
  for(int i = 0; i < BUFFER_SIZE; i++) {
    commandBuffer[i] = '\0';
  }
}
 
void loop(){
  readMessageFromBt();
  execActionBt();
}

void execActionBt() {
  if(!BTserial.available()) {
    char command = getNextCommand();
          
    if(command == 'a') {
      // left front: a
      digitalWrite(pin_leftF, HIGH);
      digitalWrite(pin_leftB, LOW);
    } else if(command == 'b') {
      // right front: b
      digitalWrite(pin_rightF, HIGH);
      digitalWrite(pin_rightB, LOW);
    } else if(command == 'c') {
      // left back: c
      digitalWrite(pin_leftF, LOW);
      digitalWrite(pin_leftB, HIGH);
    } else if(command == 'd') { 
      // right back: d       
      digitalWrite(pin_rightF, LOW);
      digitalWrite(pin_rightB, HIGH);
    } else if(command == 'e') {
      // right stop: e
      digitalWrite(pin_rightF, LOW);
      digitalWrite(pin_rightB, LOW);
    } else if(command == 'f') {
      // left stop: f
      digitalWrite(pin_leftF, LOW);
      digitalWrite(pin_leftB, LOW);         
    } else if(command == 'g') {        
      // gun start fire: g
      digitalWrite(pin_gunFireF, HIGH);
      digitalWrite(pin_gunFireB, LOW);
    } else if(command == 'h') {        
      // gun stop fire: h
      digitalWrite(pin_gunFireF, LOW);
      digitalWrite(pin_gunFireB, LOW);
    } else if(command == 'i') {        
      // left turret go: i
      digitalWrite(pin_turretF, HIGH);
      digitalWrite(pin_turretB, LOW);
    } else if(command == 'l') {        
      // left turret stop: l
      digitalWrite(pin_turretF, LOW);
      digitalWrite(pin_turretB, LOW);
    } else if(command == 'm') {        
      // right turret go: m
      digitalWrite(pin_turretF, LOW);
      digitalWrite(pin_turretB, HIGH);
    } else if(command == 'n') {        
      // right turret stop: n
      digitalWrite(pin_turretF, LOW);
      digitalWrite(pin_turretB, LOW);
    } else if(command == 'o') {        
      // gun up go: o
      digitalWrite(pin_gunElevationF, HIGH);
      digitalWrite(pin_gunElevationB, LOW);
    } else if(command == 'p') {        
      // gun up stop: p
      digitalWrite(pin_gunElevationF, LOW);
      digitalWrite(pin_gunElevationB, LOW);
    } else if(command == 'q') {        
      // gun down go: q
      digitalWrite(pin_gunElevationF, LOW);
      digitalWrite(pin_gunElevationB, HIGH);
    } else if(command == 'r') {        
      // gun down stop: r
      digitalWrite(pin_gunElevationF, LOW);
      digitalWrite(pin_gunElevationB, LOW);
    }   
  }  
}

void readMessageFromBt(){
  while(BTserial.available()){
    char c = char(BTserial.read());
    commandBuffer[bufferEnd] = c;
    if(bufferEnd < BUFFER_SIZE) {
      bufferEnd++;
    }
  }
}

char getNextCommand() {
  char c;
  
  if(bufferEnd > 0) {
    c = commandBuffer[0];
    for(int i = 0; i < bufferEnd; i++) {
      commandBuffer[i] = commandBuffer[i + 1];
    }
    bufferEnd--;
  } else {
    c = '\0';
  }
  
  return c;
}

void sendToBt(String message) {
  BTserial.print("~" + message);
}

