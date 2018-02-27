#include <SoftwareSerial.h>

#define PIN_RX 12
#define PIN_TX 13
#define PIN_SPEED 3
#define PIN_LEFT_F 1
#define PIN_LEFT_B 0
#define PIN_RIGHT_F 2
#define PIN_RIGHT_B 4
#define PIN_SPEED_T 6
#define PIN_TURRET_F 7
#define PIN_TURRET_B 5
#define PIN_GUN_ELEVATION_F 8
#define PIN_GUN_ELEVATION_B 9
#define PIN_GUN_FIRE_F 10
#define PIN_GUN_FIRE_B 11

#define SPEED 150
#define BUFFER_SIZE 11

#define LEFT_FRONT 'a'
#define RIGHT_FRONT 'b'
#define LEFT_BACK 'c'
#define RIGHT_BACK 'd'
#define RIGHT_STOP 'e'
#define LEFT_STOP 'f'
#define GUN_FIRE 'g'
#define GUN_FIRE_STOP 'h'
#define TURRET_LEFT 'i'
#define TURRET_LEFT_STOP 'l'
#define RIGHT_TURRET 'm'
#define RIGHT_TURRET_STOP 'n'
#define GUN_UP 'o'
#define GUN_UP_STOP 'p'
#define GUN_DOWN 'q'
#define GUN_DOWN_STOP 'r'


SoftwareSerial BTserial(PIN_RX, PIN_TX); // RX | TX
char commandBuffer[BUFFER_SIZE];
int bufferEnd;


void process();

void readMessageFromBt();

char getNextCommand();

void initBuffer();

inline void sendToBt(String message);

inline void stopTurret();

inline void stopGunElevation();


void setup() { 
  //Serial.begin(9600);
  BTserial.begin(9600);
  
  pinMode(PIN_SPEED, OUTPUT);
  pinMode(PIN_LEFT_F, OUTPUT);
  pinMode(PIN_LEFT_B, OUTPUT);
  pinMode(PIN_RIGHT_F, OUTPUT);
  pinMode(PIN_RIGHT_B, OUTPUT);
  pinMode(PIN_SPEED_T, OUTPUT);
  pinMode(PIN_TURRET_F, OUTPUT);
  pinMode(PIN_TURRET_B, OUTPUT);
  pinMode(PIN_GUN_ELEVATION_F, OUTPUT);
  pinMode(PIN_GUN_ELEVATION_B, OUTPUT);
  pinMode(PIN_GUN_FIRE_F, OUTPUT);
  pinMode(PIN_GUN_FIRE_B, OUTPUT);
  
  analogWrite(PIN_SPEED, SPEED);
  digitalWrite(PIN_LEFT_F, LOW);
  digitalWrite(PIN_LEFT_B, LOW);
  digitalWrite(PIN_RIGHT_F, LOW);
  digitalWrite(PIN_RIGHT_B, LOW);
  analogWrite(PIN_SPEED_T, SPEED);
  digitalWrite(PIN_TURRET_F, LOW);
  digitalWrite(PIN_TURRET_B, LOW);
  digitalWrite(PIN_GUN_ELEVATION_F, LOW);
  digitalWrite(PIN_GUN_ELEVATION_B, LOW);
  digitalWrite(PIN_GUN_FIRE_F, LOW);
  digitalWrite(PIN_GUN_FIRE_B, LOW);

  initBuffer();
}
 

void loop(){
  readMessageFromBt();
  process();
}


void process() {
  if(!BTserial.available()) {
    char command = getNextCommand();
          
    if(command == LEFT_FRONT) {
      // left front: a
      digitalWrite(PIN_LEFT_F, HIGH);
      digitalWrite(PIN_LEFT_B, LOW);
    } else if(command == RIGHT_FRONT) {
      // right front: b
      digitalWrite(PIN_RIGHT_F, HIGH);
      digitalWrite(PIN_RIGHT_B, LOW);
    } else if(command == LEFT_BACK) {
      // left back: c
      digitalWrite(PIN_LEFT_F, LOW);
      digitalWrite(PIN_LEFT_B, HIGH);
    } else if(command == RIGHT_BACK) { 
      // right back: d       
      digitalWrite(PIN_RIGHT_F, LOW);
      digitalWrite(PIN_RIGHT_B, HIGH);
    } else if(command == RIGHT_STOP) {
      // right stop: e
      digitalWrite(PIN_RIGHT_F, LOW);
      digitalWrite(PIN_RIGHT_B, LOW);
    } else if(command == LEFT_STOP) {
      // left stop: f
      digitalWrite(PIN_LEFT_F, LOW);
      digitalWrite(PIN_LEFT_B, LOW);         
    } else if(command == GUN_FIRE) {        
      // gun start fire: g
      digitalWrite(PIN_GUN_FIRE_F, HIGH);
      digitalWrite(PIN_GUN_FIRE_B, LOW);
    } else if(command == GUN_FIRE_STOP) {        
      // gun stop fire: h
      digitalWrite(PIN_GUN_FIRE_F, LOW);
      digitalWrite(PIN_GUN_FIRE_B, LOW);
    } else if(command == TURRET_LEFT) {        
      // left turret go: i
      digitalWrite(PIN_TURRET_F, HIGH);
      digitalWrite(PIN_TURRET_B, LOW);
    } else if(command == TURRET_LEFT_STOP) {        
      // left turret stop: l
      stopTurret();
    } else if(command == RIGHT_TURRET) {        
      // right turret go: m
      digitalWrite(PIN_TURRET_F, LOW);
      digitalWrite(PIN_TURRET_B, HIGH);
    } else if(command == RIGHT_TURRET_STOP) {        
      // right turret stop: n
      stopTurret();
    } else if(command == GUN_UP) {        
      // gun up go: o
      digitalWrite(PIN_GUN_ELEVATION_F, HIGH);
      digitalWrite(PIN_GUN_ELEVATION_B, LOW);
    } else if(command == GUN_UP_STOP) {        
      // gun up stop: p
      stopGunElevation();
    } else if(command == GUN_DOWN) {        
      // gun down go: q
      digitalWrite(PIN_GUN_ELEVATION_F, LOW);
      digitalWrite(PIN_GUN_ELEVATION_B, HIGH);
    } else if(command == GUN_DOWN_STOP) {        
      // gun down stop: r
      stopGunElevation();
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


void initBuffer() {
  bufferEnd = 0;
  
  for(int i = 0; i < BUFFER_SIZE; i++) {
    commandBuffer[i] = '\0';
  }
}


void stopTurret() {
  digitalWrite(PIN_TURRET_F, LOW);
  digitalWrite(PIN_TURRET_B, LOW);
}

void stopGunElevation() {
  digitalWrite(PIN_GUN_ELEVATION_F, LOW);
  digitalWrite(PIN_GUN_ELEVATION_B, LOW);
}
