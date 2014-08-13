#include <Bridge.h>
#include <Servo.h>

Servo gas;
char gasValue[3];
int gasInt;
Servo steering;
char steeringValue[3];
int steeringInt;

void setup() {
  Serial.begin(9600);
  gas.attach(9);  // attaches the servo on pin 9 to the servo object 
  steering.attach(10);
  Bridge.begin();
}

void loop() {
  Bridge.get("g",gasValue,3);
  gasInt = atoi(gasValue);
  gasInt = map(gasInt, 0, 180, 10, 170);
  gas.write(gasInt);
  Serial.println(gasInt);
  
  Bridge.get("s",steeringValue,3);
  steeringInt = atoi(steeringValue);
  steeringInt = map(steeringInt, 0, 180, 10, 170);
  steering.write(steeringInt);
  Serial.println(steeringInt);
  
  delay(15);
}
