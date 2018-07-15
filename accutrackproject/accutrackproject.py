from flask import Flask, request
from flask_restful import Resource, Api
import json
import sys
import tensorflow as tf
import pyrebase
from firebase import firebase
import os
import datetime
import db_access

'''
#DATA INPUTs (for the web-app)
*Input Time-series: userid/limbid/<time:time_point><int_array:data_packet>


#DATA OUTPUTs (for the web-app)
NOTE: use 'reba' for score calculation (http://ergo-plus.com/wp-content/uploads/REBA.pdf)
*Past daily data (filtered, smoothened, downsampled)
*Today's data (filtered, smoothened, NOT-downsampled)
*Activity Forecast of activity patterns (ignore days with 'outlier'-like activity level)
*Health based on ongoing forecast, and past activity
*Activity 'Intensity' labels (Low, Normal, High, Dangerous)
'''

'''
<script src="https://www.gstatic.com/firebasejs/5.2.0/firebase.js"></script>
<script>
  // Initialize Firebase
    var config = {
                apiKey: "AIzaSyCtJG_Ccxiaai-SflibYt8ilmSV6TnuErg",
                    authDomain: "accutrack-iot.firebaseapp.com",
                        databaseURL: "https://accutrack-iot.firebaseio.com",
                            projectId: "accutrack-iot",
                                storageBucket: "accutrack-iot.appspot.com",
                                    messagingSenderId: "790708014828"
                                      };
      firebase.initializeApp(config);
      </script>
'''

'''
IMPORTANT LINKS:
http://ozgur.github.io/python-firebase/
https://console.firebase.google.com/project/accutrack-iot/database/accutrack-iot/data
https://portal.azure.com/#@eduuwaterloo.onmicrosoft.com/resource/subscriptions/bf80972a-04e9-435d-adfd-23cfe1373d7d/resourceGroups/accutrackApp/providers/Microsoft.Compute/virtualMachines/accutrackIOTvm/overview
https://github.com/ozgur/python-firebase
https://ide.c9.io/prabal1997/weef (Daniel's code for reference)
'''

#define a flask app, and a surrounding api interface
application = Flask(__name__);
api = Api(application);

#connect the appropriate GET, POST methods with relevant data-request/data-delivery format
api.add_resource(db_access.Sensor_read, '/sensor_read/<int:sensor_num>');
api.add_resource(db_access.ReadPrediction, '/ReadPrediction/<int:user_id>/<limb_id>');
api.add_resource(db_access.Data_read, '/data_read/<int:user_id>');
api.add_resource(db_access.FakeData, '/FakeData/<int:user_id>/<int:limb_id>/<int:start_point>/<int:end_point>');
api.add_resource(db_access.RealtimeUpdate, '/RealtimeUpdate/<int:sensor_id>/<data_array>');

#redirect code execution to 'application' object
if __name__ == "__main__":
    application.run(host="0.0.0.0");

