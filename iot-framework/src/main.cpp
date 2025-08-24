#include <WiFi.h>
#include <FirebaseClient.h>
#include <ArduinoJson.h>
#include <OneWire.h>
#include <DallasTemperature.h>
#include <WiFiClientSecure.h>

// Pin definitions based on schematic
#define TEMP_SENSOR_PIN 4
#define TURBIDITY_SENSOR_PIN 34
#define PH_SENSOR_PIN 35
#define STATUS_LED_PIN 2

// Sensor setup
OneWire oneWire(TEMP_SENSOR_PIN);
DallasTemperature temperatureSensor(&oneWire);

// WiFi credentials (use environment variables in production)
const char* WIFI_SSID = "YOUR_WIFI_SSID";
const char* WIFI_PASSWORD = "YOUR_WIFI_PASSWORD";

// Firebase configuration
const char* FIREBASE_PROJECT_ID = "your-project-id";
const char* FIREBASE_API_KEY = "your-api-key";
const char* FIREBASE_CLIENT_EMAIL = "your-client-email@project.iam.gserviceaccount.com";
const char FIREBASE_PRIVATE_KEY[] PROGMEM = "-----BEGIN PRIVATE KEY-----\n...\n-----END PRIVATE KEY-----\n";

FirebaseApp app;
ServiceAuth sa_auth(FIREBASE_CLIENT_EMAIL, FIREBASE_PROJECT_ID, FIREBASE_PRIVATE_KEY);
FirebaseClient client;

// Sensor thresholds
const float TURBIDITY_THRESHOLD = 100.0; // NTU
const float PH_MIN = 6.5;
const float PH_MAX = 8.5;

struct SensorReading {
    float temperature;
    float turbidity;
    float ph;
    unsigned long timestamp;
};

void setup() {
    Serial.begin(115200);
    pinMode(STATUS_LED_PIN, OUTPUT);
    
    // Initialize sensors
    temperatureSensor.begin();
    
    // Connect to WiFi
    connectToWiFi();
    
    // Initialize Firebase
    initializeFirebase();
    
    Serial.println("TankSensr system initialized");
    digitalWrite(STATUS_LED_PIN, HIGH);
}

void loop() {
    SensorReading reading = takeSensorReadings();
    
    if (sendDataToFirebase(reading)) {
        checkThresholds(reading);
        Serial.println("Data sent successfully");
    } else {
        Serial.println("Failed to send data");
        digitalWrite(STATUS_LED_PIN, LOW);
        delay(1000);
        digitalWrite(STATUS_LED_PIN, HIGH);
    }
    
    delay(10000); // 10-second interval
}

void connectToWiFi() {
    WiFi.begin(WIFI_SSID, WIFI_PASSWORD);
    Serial.print("Connecting to WiFi");
    
    while (WiFi.status() != WL_CONNECTED) {
        delay(500);
        Serial.print(".");
    }
    
    Serial.println();
    Serial.print("Connected! IP address: ");
    Serial.println(WiFi.localIP());
}

void initializeFirebase() {
    Serial.printf("Firebase Client v%s\n", FIREBASE_CLIENT_VERSION);
    
    app.setCallback(firebaseAuthHandler);
    
    initializeApp(client, app, getAuth(sa_auth));
    
    Serial.println("Firebase initialized");
}

SensorReading takeSensorReadings() {
    SensorReading reading;
    
    // Read temperature
    temperatureSensor.requestTemperatures();
    reading.temperature = temperatureSensor.getTempCByIndex(0);
    
    // Read turbidity (0-4.5V analog, convert to NTU)
    int turbidityRaw = analogRead(TURBIDITY_SENSOR_PIN);
    float turbidityVoltage = turbidityRaw * (3.3 / 4095.0);
    reading.turbidity = map(turbidityVoltage * 100, 0, 450, 3000, 0); // Convert to NTU
    
    // Read pH (0-3.3V analog, convert to pH 0-14)
    int phRaw = analogRead(PH_SENSOR_PIN);
    float phVoltage = phRaw * (3.3 / 4095.0);
    reading.ph = (phVoltage * 14.0) / 3.3; // Linear conversion
    
    reading.timestamp = millis();
    
    return reading;
}

bool sendDataToFirebase(const SensorReading& reading) {
    // Create Firestore document
    Values::Value temperatureValue, turbidityValue, phValue, timestampValue;
    temperatureValue.set<Values::DoubleValue>(reading.temperature);
    turbidityValue.set<Values::DoubleValue>(reading.turbidity);
    phValue.set<Values::DoubleValue>(reading.ph);
    timestampValue.set<Values::IntegerValue>(reading.timestamp);
    
    Document<Values::Value> doc;
    doc.add("temperature", temperatureValue);
    doc.add("turbidity", turbidityValue);
    doc.add("ph", phValue);
    doc.add("timestamp", timestampValue);
    
    Firestore::Parent parent(FIREBASE_PROJECT_ID);
    String collectionId = "readings";
    String documentId = String(reading.timestamp);
    
    String response = Firestore::Documents::createDocument(client, parent, collectionId, documentId, DocumentMask(), doc);
    
    return response.length() > 0 && !response.startsWith("{");
}

void checkThresholds(const SensorReading& reading) {
    bool alertTriggered = false;
    String alertMessage = "";
    
    if (reading.turbidity > TURBIDITY_THRESHOLD) {
        alertMessage = "High turbidity detected: " + String(reading.turbidity) + " NTU";
        alertTriggered = true;
    }
    
    if (reading.ph < PH_MIN || reading.ph > PH_MAX) {
        if (alertTriggered) alertMessage += " | ";
        alertMessage += "pH out of range: " + String(reading.ph);
        alertTriggered = true;
    }
    
    if (alertTriggered) {
        sendAlert(alertMessage, reading.timestamp);
    }
}

void sendAlert(const String& message, unsigned long timestamp) {
    // Create Firestore document for alert
    Values::Value messageValue, timestampValue, typeValue;
    messageValue.set<Values::StringValue>(message);
    timestampValue.set<Values::IntegerValue>(timestamp);
    typeValue.set<Values::StringValue>("threshold_exceeded");
    
    Document<Values::Value> alertDoc;
    alertDoc.add("message", messageValue);
    alertDoc.add("timestamp", timestampValue);
    alertDoc.add("type", typeValue);
    
    Firestore::Parent parent(FIREBASE_PROJECT_ID);
    String collectionId = "alerts";
    String documentId = String(timestamp);
    
    String response = Firestore::Documents::createDocument(client, parent, collectionId, documentId, DocumentMask(), alertDoc);
    
    Serial.println("Alert sent: " + message);
}

void firebaseAuthHandler(AsyncResult &aResult) {
    // Handle Firebase authentication events
    if (app.isAuthenticated()) {
        Serial.println("Firebase authenticated");
    } else {
        Serial.println("Firebase authentication failed");
        Serial.println(aResult.error().message());
    }
}