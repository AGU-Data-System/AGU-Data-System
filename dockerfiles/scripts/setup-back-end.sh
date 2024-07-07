#!/bin/sh

APP_HOME=/usr/app
SCRIPTS_DIR=$APP_HOME/scripts
FLAG_FILE=$APP_HOME/flags/script_ran.flag

# Function to check if the application is ready
check_app_ready() {
  curl -s http://localhost:8080/api/dnos | grep '"size"'
}

# Start the main application
java -jar $APP_HOME/$ARTIFACT_NAME &

# Wait until the application is up and ready
until check_app_ready; do
  echo "Waiting for the application to be ready..."
  sleep 5
done

# Run the script if the flag file does not exist
if [ ! -f $FLAG_FILE ]; then
  java -jar $SCRIPTS_DIR/script.jar
  touch $FLAG_FILE
fi

# Keep the container running
wait
