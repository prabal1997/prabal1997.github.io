// Receiver setup
// WRL-10532 PIN1=GND PIN2=DATA PIN4=5V PIN8=ANT
// ARDUINO UNO PIN2=DATA PIN13=LED
// Receives plain text message from 434MHz

#include <VirtualWire.h>


void setup()
{
    pinMode(13,OUTPUT);
    digitalWrite(13,LOW);
  


    Serial.begin(9600);  // Debugging only
    Serial.println("setup");

    // Initialise the IO and ISR
    vw_set_ptt_inverted(true); // Required for DR3100
    vw_setup(2000);  // Bits per sec
    vw_set_rx_pin(4);
    vw_rx_start();       // Start the receiver PLL running
}

void loop()
{
    uint8_t buf[VW_MAX_MESSAGE_LEN];
    uint8_t buflen = VW_MAX_MESSAGE_LEN;

    if (vw_get_message(buf, &buflen)) // Non-blocking
    {
  
      Serial.print("Got: ");
      
      for (int i = 0; i < buflen; i++)
      {
         Serial.print((char) buf[i]);
      }
      Serial.println("");
   }
}


