// Receiver setup
// WRL-10532 PIN1=GND PIN2=DATA PIN4=5V PIN8=ANT
// ARDUINO UNO PIN2=DATA PIN13=LED
// Receives plain text message from 434MHz

#include <VirtualWire.h>
#include <SoftwareSerial.h>

SoftwareSerial Bluetooth(5, 6);

//counting variable
int count = 0;

void setup()
{
  //start up the serials
    Serial.begin(9600);  
    Bluetooth.begin(9600);

    //set pintmodes
     pinMode(A1, INPUT);
    pinMode(7, OUTPUT);
    pinMode(13, OUTPUT);
    digitalWrite(7, HIGH);
    
    // Initialise the IO and ISR
    vw_set_ptt_inverted(true); // Required for DR3100
    vw_setup(2000);  // Bits per sec
    vw_set_rx_pin(4);
    vw_rx_start();       // Start the receiver PLL running
}

void loop()
{
 //  Bluetooth.write("asdf");
 ///  Serial.println("asdf");
  // delay(50);
  // const char text[] = "3";
  // Bluetooth.write(text);
   // Serial.println(text);



   //get the data if there is any
    uint8_t buf[VW_MAX_MESSAGE_LEN];
    uint8_t buflen = VW_MAX_MESSAGE_LEN;
    
    String sensorData = "";
    if (vw_get_message(buf, &buflen)) // Non-blocking
    { 
      for (int i = 0; i < buflen; i++)
      {
         sensorData.concat( (char) buf[i] );
      }
   }



   if (sensorData != "") {
    //send out the data for the other sensors
     //Serial.println(sensorData);
     Bluetooth.print(sensorData);
     
      Serial.println(sensorData);
   }
   else if (count > 10) { 
    //send out the data for this sensor
      digitalWrite(13, true);
      count = 0;
      int analogReading = analogRead(A1);
      String analog = "b";
      analog.concat(String(analogReading)); 
      Bluetooth.print(analog);
      //Serial.println(analog);
      digitalWrite(13, false);
   }
   
   count++;
   delay(20);
}


