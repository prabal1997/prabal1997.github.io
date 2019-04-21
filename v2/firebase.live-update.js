// Live updates registration from firebase
// Live Value Sensors
var aSensor = [];
var aSensorTempValuesGraph1 = [];
var aSensorTempValuesGraph2 = [];
var aSensorPostureAnalyzer = [];
var aSensorWatchDogTimer = 0;
var aSensorConnected = false;
var aSensorDivider = 2.5;
var aSensorLabel = " Left Leg";
var bSensor = [];
var bSensorTempValuesGraph1 = [];
var bSensorTempValuesGraph2 = [];
var bSensorPostureAnalyzer = [];
var bSensorWatchDogTimer = 0;
var bSensorConnected = false;
var bSensorDivider = 1.5;
var bSensorLabel = " Right Arm";

var oldValuesReceived = false;

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
  // Receive update
  function getData() {	
     var currentSensorValues = firebase.database().ref("Users/0/CurrentValue/SensorValues");
     currentSensorValues.on('value', function(snapshot) {
		// Current sensor data 
		var sensorData = snapshot.val();
		if (sensorData != null){
			if (oldValuesReceived == false) { 
				oldValuesReceived = true;
			} else {
				if (sensorData["bSensor"] != null) { 
					aSensor = sensorData["bSensor"].split(",");
					//create shallow to be used and modified by graphs
					aSensorTempValuesGraph1 = aSensor.slice(0); 
					aSensorTempValuesGraph2 = aSensor.slice(0); 
					aSensorPostureAnalyzer = aSensor.slice(0);
				}
				if (sensorData["aSensor"] != null) { 
					bSensor = sensorData["aSensor"].split(",");
					//create shallow to be used and modified by graphs
					bSensorTempValuesGraph1 = bSensor.slice(0); 
					bSensorTempValuesGraph2 = bSensor.slice(0); 
					bSensorPostureAnalyzer = bSensor.slice(0);
				}
			}
		}
	});
}

// Start listening on window load
window.addEventListener("load", getData(sensorUpdate));


function sensorUpdate(data) {
	console.log("Window update received");
}




// live update graphs
(function($) { 
	
	// live update of sensor angles
	(function() {
		if( $('#sensorValuesDashRealTime').get(0) ) {
			var aSensorData = [],
				bSensorData = [],
				totalPoints = 50;

			function getSensorData() {
				if (aSensorData.length > 0) 
					aSensorData = aSensorData.slice(1);

				if (bSensorData.length > 0)
					bSensorData = bSensorData.slice(1);


				// Add new data
				while (aSensorData.length < totalPoints) { 
					if (aSensorTempValuesGraph1.length > 0) { 
						aSensorData.push(aSensorTempValuesGraph1.shift() / aSensorDivider);
					} else { 
						aSensorData.push(0); //push 0 default value if no sensor data left
					}
				}

				while (bSensorData.length < totalPoints) {
					if (bSensorTempValuesGraph1.length > 0) { 
						bSensorData.push(bSensorTempValuesGraph1.shift() / bSensorDivider);
					} else {
						bSensorData.push(0); //push 0 default value if no sensor data left
					}
				}

				// Zip the generated y values with the x values
				var aSensorFormattedData = [],
					bSensorFormattedData = [];

				for (var i = 0; i < aSensorData.length; ++i) { 
					aSensorFormattedData.push([i, aSensorData[i]])
				}	

				for (var i = 0; i < bSensorData.length; ++i) {
					bSensorFormattedData.push([i, bSensorData[i]])
				}

				plotStructure =  [
					{
						data: aSensorFormattedData,
						label: aSensorLabel,
						color: "#734ba9"
					},
					{
						data: bSensorFormattedData,
						label: bSensorLabel,
						color: "#2baab1"
					}
				];

				return plotStructure;
			}

			var sensorValuesDashRealTime = $.plot('#sensorValuesDashRealTime', getSensorData(), {
				colors: ['#8CC9E8'],
				label: "asdf",
				series: {
					lines: {
						show: true,
						fill: true,
						lineWidth: 1,
						fillColor: {
							colors: [{
								opacity: 0.45
							}, {
								opacity: 0.45
							}]
						}
					},
					points: {
						show: false
					},
					shadowSize: 0
				},
				grid: {
					borderColor: 'rgba(0,0,0,0.1)',
					borderWidth: 1,
					labelMargin: 15,
					backgroundColor: 'transparent'
				},
				yaxis: {
					min: 0,
					max: 100,
					color: 'rgba(0,0,0,0.1)'
				},
				xaxis: {
					show: false
				}
			});

			function updateLiveSensorDash() {

				sensorValuesDashRealTime.setData(getSensorData());

				// Since the axes don't change, we don't need to call plot.setupGrid()
				sensorValuesDashRealTime.draw();
				setTimeout(updateLiveSensorDash, ($('html').hasClass( 'mobile-device' ) ? 1000 : 250) );
			}

			updateLiveSensorDash();
		}
	})();




	// live update of activity/fatigue rating
	(function() {
		if( $('#activityDashRealTime').get(0) ) {
			var aSensorData = [],
				bSensorData = [],
				totalPoints = 50;

			function getSensorData() {
				if (aSensorData.length > 0) 
					aSensorData = aSensorData.slice(1);

				if (bSensorData.length > 0)
					bSensorData = bSensorData.slice(1);


				// Add new data
				while (aSensorData.length < totalPoints) { 
					if (aSensorTempValuesGraph2.length > 0) { 
						aSensorData.push(aSensorTempValuesGraph2.shift() / aSensorDivider);
					} else { 
						aSensorData.push(0); //push 0 default value if no sensor data left
					}
				}

				while (bSensorData.length < totalPoints) {
					if (bSensorTempValuesGraph2.length > 0) { 
						bSensorData.push(bSensorTempValuesGraph2.shift() / bSensorDivider);
					} else {
						bSensorData.push(0); //push 0 default value if no sensor data left
					}
				}

				// Zip the generated y values with the x values
				var aSensorFormattedData = [],
					bSensorFormattedData = [];

				for (var i = 0; i < aSensorData.length; ++i) { 
					aSensorFormattedData.push([i, aSensorData[i]])
				}	

				for (var i = 0; i < bSensorData.length; ++i) {
					bSensorFormattedData.push([i, bSensorData[i]])
				}

				plotStructure =  [
					{
						data: aSensorFormattedData,
						label: aSensorLabel,
						color: "#734ba9"
					},
					{
						data: bSensorFormattedData,
						label: bSensorLabel,
						color: "#2baab1"
					}
				];

				return plotStructure;
			}

			var sensorValuesDashRealTime = $.plot('#activityDashRealTime', getSensorData(), {
				colors: ['#8CC9E8'],
				label: "asdf",
				series: {
					lines: {
						show: true,
						fill: true,
						lineWidth: 1,
						fillColor: {
							colors: [{
								opacity: 0.45
							}, {
								opacity: 0.45
							}]
						}
					},
					points: {
						show: false
					},
					shadowSize: 0
				},
				grid: {
					borderColor: 'rgba(0,0,0,0.1)',
					borderWidth: 1,
					labelMargin: 15,
					backgroundColor: 'transparent'
				},
				yaxis: {
					min: 0,
					max: 100,
					color: 'rgba(0,0,0,0.1)'
				},
				xaxis: {
					show: false
				}
			});

			function updateLiveSensorDash() {

				sensorValuesDashRealTime.setData(getSensorData());

				// Since the axes don't change, we don't need to call plot.setupGrid()
				sensorValuesDashRealTime.draw();
				setTimeout(updateLiveSensorDash, ($('html').hasClass( 'mobile-device' ) ? 1000 : 250) );
			}

			updateLiveSensorDash();
		}
	})();



	// update the posture image - will make this more legit a bit later.
	function updatePostureImage() {

		if (aSensorPostureAnalyzer.length > 0) { 
			document.getElementById("livePostureImage").src="assets/images/poses/squat.png";
			aSensorPostureAnalyzer.shift();
		} else {
			document.getElementById("livePostureImage").src="assets/images/poses/unknown.png";
		}

		setTimeout(updatePostureImage, ($('html').hasClass( 'mobile-device' ) ? 1000 : 250) );
	}

	updatePostureImage();


	// watchdog timer will determine when a sensor has been disconnected because there are no new data received
	function updateWatchDogTimer() {
		// update the watchdog timers
		if (aSensorTempValuesGraph1.length == 0) { 
			aSensorWatchDogTimer -= 1;

			// show the notification
			if (aSensorConnected == true & aSensorWatchDogTimer <= 0) { 
				new PNotify({
					title: 'Prabals Left Leg',
					text: 'Prabals left leg sensor is disconnected. You will no longer receive live updates.',
					type: 'warning'
				});
				aSensorConnected = false;
			}
		} else {
			aSensorWatchDogTimer = 5;

			if (aSensorConnected == false) { 
				new PNotify({
					title: 'Prabals Left Leg',
					text: 'Prabals left leg sensor is connected. You will start receiving live updates.',
					type: 'success'
				});
				aSensorConnected = true;
			}
		}

		if (bSensorTempValuesGraph1.length == 0) { 
			bSensorWatchDogTimer -= 1;

			
			// show the notification
			if (bSensorConnected == true & bSensorWatchDogTimer <= 0) { 
				new PNotify({
					title: 'Prabals Right Arm',
					text: 'Prabals right arm sensor is disconnected. You will no longer receive live updates.',
					type: 'warning'
				});
				bSensorConnected = false;
			}
		} else {
			

			bSensorWatchDogTimer = 5;

			if (bSensorConnected == false) { 
				new PNotify({
					title: 'Prabals Right Arm',
					text: 'Prabals right arm sensor is connected. You will start receiving live updates.',
					type: 'success'
				});
				bSensorConnected = true;
			}
		}

		// update the ui to show the status of the sensor
		if (bSensorConnected == false) { 
			document.getElementById('right-arm-color1').className = "summary-icon bg-secondary";
			document.getElementById('right-arm-color2').className = "panel panel-featured-left panel-featured-secondary";
			document.getElementById('right-arm-status').innerHTML = "Inactive";
		} else {
			document.getElementById('right-arm-color1').className = "summary-icon bg-tertiary";
			document.getElementById('right-arm-color2').className = "panel panel-featured-left panel-featured-tertiary";
			document.getElementById('right-arm-status').innerHTML = "Active";
		}

		if (aSensorConnected == false) { 
			document.getElementById('left-leg-color1').className = "summary-icon bg-secondary";
			document.getElementById('left-leg-color2').className = "panel panel-featured-left panel-featured-secondary";
			document.getElementById('left-leg-status').innerHTML = "Inactive";
		} else { 
			document.getElementById('left-leg-color1').className = "summary-icon bg-tertiary";
			document.getElementById('left-leg-color2').className = "panel panel-featured-left panel-featured-tertiary";
			document.getElementById('left-leg-status').innerHTML = "Active";
		}

		setTimeout(updateWatchDogTimer, ($('html').hasClass( 'mobile-device' ) ? 1000 : 500) );
	}

	updateWatchDogTimer();

}).apply(this, [jQuery]);