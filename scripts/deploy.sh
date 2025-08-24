#!/bin/bash

echo "Deploying TankSensr system..."

# Deploy Firebase backend
echo "Deploying Firebase functions..."
cd firebase-backend
firebase deploy --only functions,firestore:rules
cd ..

# Build and deploy IoT firmware
echo "Building IoT firmware..."
cd iot-firmware
pio run
echo "Flash firmware manually to ESP32 device"
cd ..

# Build Android APK
echo "Building Android APK..."
cd android-app
./gradlew assembleRelease
cd ..

echo "Deployment complete!"
echo "APK location: android-app/app/build/outputs/apk/release/"