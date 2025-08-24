#!/bin/bash

echo "Setting up TankSensr development environment..."

# Check prerequisites
command -v node >/dev/null 2>&1 || { echo "Node.js is required but not installed. Aborting." >&2; exit 1; }
command -v npm >/dev/null 2>&1 || { echo "npm is required but not installed. Aborting." >&2; exit 1; }

# Install Firebase CLI
npm install -g firebase-tools

# Install PlatformIO CLI
pip install -U platformio

# Setup Firebase backend
echo "Setting up Firebase backend..."
cd firebase-backend
npm install
cd ..

# Setup Android dependencies
echo "Setting up Android project..."
cd android-app
chmod +x gradlew
./gradlew dependencies
cd ..

# Setup IoT firmware dependencies
echo "Setting up IoT firmware..."
cd iot-firmware
pio lib install
cd ..

echo "Development environment setup complete!"
echo "Next steps:"
echo "1. Configure Firebase project ID in firebase-backend/.firebaserc"
echo "2. Update WiFi credentials in iot-firmware/src/main.cpp"
echo "3. Add google-services.json to android-app/app/"