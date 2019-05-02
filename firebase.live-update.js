// Live updates registration from firebase
// Live Value Sensors
var aSensor = [];
var aSensorTempValuesGraph1 = [];
var aSensorTempValuesGraph2 = [];
var aSensorPostureAnalyzer = [];
var aSensorWatchDogTimer = 0;
var aSensorConnected = false;
var aSensorDivider = 1.5; // from 2.5
var aSensorLabel = " Left Leg";
var bSensor = [];

var bSensorTempValuesGraph1 = [];
var bSensorTempValuesGraph2 = [];
var bSensorPostureAnalyzer = [];
var bSensorWatchDogTimer = 0;
var bSensorConnected = false;
var bSensorDivider = 0.90; // from 1.5
var bSensorLabel = " Right Arm";

var oldValuesReceived = false;

// set constants for smoothening
const currWeightActivity = 0.1
const currWeightSensor = 0.9

// set constants for posture analyzer
const aMoveThresh = 45
const bMoveThresh = 45

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

			// NOTE: this values are already normalized to b/w 0 and 100
			var aSmoothVal = 0,
				bSmoothVal = 0;

			function getSensorData() {
				if (aSensorData.length > 0) 
					aSensorData = aSensorData.slice(1);

				if (bSensorData.length > 0)
					bSensorData = bSensorData.slice(1);

				// Add new data, smoothen it
				while (aSensorData.length < totalPoints) { 
					var new_val = 0;
					if (aSensorTempValuesGraph1.length > 0) { 
						new_val = aSensorTempValuesGraph1.shift() / aSensorDivider;
					}
					aSensorData.push(new_val);
				}

				while (bSensorData.length < totalPoints) {
					var new_val = 0;
					if (bSensorTempValuesGraph1.length > 0) { 
						new_val = bSensorTempValuesGraph1.shift() / bSensorDivider;
					}
					bSensorData.push(new_val)
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
		if (document.getElementById("livePostureImage") != null) {
			document.getElementById("livePostureImage").src="assets/images/poses/squat.png"
			if( $('#activityDashRealTime').get(0) ) {
			var aSensorData = [],
				bSensorData = [],
				totalPoints = 50;

			// NOTE: this values are already normalized to b/w 0 and 100
			var aSmoothVal = 0,
				bSmoothVal = 0;

			function getSensorData() {
				if (aSensorData.length > 0) 
					aSensorData = aSensorData.slice(1);

				if (bSensorData.length > 0)
					bSensorData = bSensorData.slice(1);

				// Add new data, smoothen it, calculate mean
				var aTotalMean = 0.0;
				var aCounter = 0;
				while (aSensorData.length < totalPoints) { 
					// calculate new samples
					var new_val = 0;
					if (aSensorPostureAnalyzer.length > 0) { 
						new_val = aSensorPostureAnalyzer.shift() / aSensorDivider;
					}
					aSmoothVal = currWeightActivity * new_val + (1 - currWeightActivity) * aSmoothVal;
					aSensorData.push(aSmoothVal);

					// update mean (using raw, normalized value)
					if (new_val != 0) {
						aTotalMean += new_val;
						aCounter += 1;
					}
				}
				if (aCounter > 0) {
					aTotalMean = aTotalMean / aCounter;
				}

				var bTotalMean = 0.0;
				var bCounter = 0;
				while (bSensorData.length < totalPoints) {
					// calculate new samples
					var new_val = 0;
					if (bSensorPostureAnalyzer.length > 0) { 
						new_val = bSensorPostureAnalyzer.shift() / bSensorDivider;
					}
					bSmoothVal = currWeightActivity * new_val + (1 - currWeightActivity) * bSmoothVal;
					bSensorData.push(bSmoothVal)

					// update mean (using raw, normalized value)
					if (new_val != 0) {
						bTotalMean += new_val;
						bCounter += 1;
					}
				}
				if (bCounter > 0) {
					bTotalMean = bTotalMean / bCounter;
				}

				// use mean of activity levels to determine posture
				if ( (aTotalMean > aMoveThresh) && (bTotalMean > bMoveThresh) ) {
					// squatting, arms open
					document.getElementById("livePostureImage").src="assets/images/poses/poseab.png";
				}
				else if ( (aTotalMean > aMoveThresh) && (bTotalMean < bMoveThresh) ) {
					// squatting, arms closed
					document.getElementById("livePostureImage").src="assets/images/poses/posea_.png";
				}
				else if ( (aTotalMean < aMoveThresh) && (bTotalMean > bMoveThresh) ) {
					// standing, arms open
					document.getElementById("livePostureImage").src="assets/images/poses/pose_b.png";
				}
				else {
					// standing, arms closed
					document.getElementById("livePostureImage").src="assets/images/poses/pose__.png";

					// check if the sensors are stopped
					if ( (aTotalMean == 0) && (bTotalMean == 0) ) {
						document.getElementById("livePostureImage").src="assets/images/poses/unknown.png";
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
		}
	})();

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
			if (document.getElementById('right-arm-color1-a') != null) {
				document.getElementById('right-arm-color1-a').className = "summary-icon-2 bg-secondary";
			} else {
				document.getElementById('right-arm-color1').className = "summary-icon bg-secondary";
			}
			document.getElementById('right-arm-color2').className = "panel panel-featured-left panel-featured-secondary";
			document.getElementById('right-arm-status').innerHTML = "Inactive";
		} else {
			if (document.getElementById('right-arm-color1-a') != null) {
				document.getElementById('right-arm-color1-a').className = "summary-icon-2 bg-tertiary";
			} else {
				document.getElementById('right-arm-color1').className = "summary-icon bg-tertiary";
			}
			document.getElementById('right-arm-color2').className = "panel panel-featured-left panel-featured-tertiary";
			document.getElementById('right-arm-status').innerHTML = "Active";
		}

		if (aSensorConnected == false) { 
			if (document.getElementById('left-leg-color1-a') != null) {
				document.getElementById('left-leg-color1-a').className = "summary-icon-2 bg-secondary";
			} else {
				document.getElementById('left-leg-color1').className = "summary-icon bg-secondary";
			}
			document.getElementById('left-leg-color2').className = "panel panel-featured-left panel-featured-secondary";
			document.getElementById('left-leg-status').innerHTML = "Inactive";
		} else { 
			if (document.getElementById('left-leg-color1-a') != null) {
			document.getElementById('left-leg-color1-a').className = "summary-icon-2 bg-tertiary";
			} else {
				document.getElementById('left-leg-color1').className = "summary-icon bg-tertiary";
			}
			document.getElementById('left-leg-color2').className = "panel panel-featured-left panel-featured-tertiary";
			document.getElementById('left-leg-status').innerHTML = "Active";
		}

		setTimeout(updateWatchDogTimer, ($('html').hasClass( 'mobile-device' ) ? 1000 : 500) );
	}

	updateWatchDogTimer();

}).apply(this, [jQuery]);