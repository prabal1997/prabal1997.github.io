// Transmitter setup
// WRL-10534 PIN1=GND PIN2=DATA PIN3=5V PIN4=ANT
// ARDUINO UNO PIN12=DATA PIN13=LED
// Sends plain text message to 434MHz

  #include <VirtualWire.h>

void setup()
{
    Serial.begin(9600);    // Debugging only
    Serial.println("setup");

    // Initialise the IO and ISR
    vw_set_ptt_inverted(true); // Required for DR3100
    vw_setup(2000);  // Bits per sec
    pinMode(3, OUTPUT);
    pinMode(A2, INPUT);
    digitalWrite(3, HIGH);
}

void loop()
{
    
    int analogReading = analogRead(A2);
    String analog = "a";
    analog.concat(String(analogReading));
    Serial.println(analog);
    
    //char *msg = "a";
    char msg[60];
    analog.toCharArray(msg, 6); 
    
    digitalWrite(13, true); // Flash a light to show transmitting
    vw_send((uint8_t *)msg, strlen(msg));
    vw_wait_tx(); // Wait until the whole message is gone
    digitalWrite(13, false);

    delay(100);
}
