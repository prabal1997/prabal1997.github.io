// Live updates registration from firebase
// Live Value Sensors
var aSensor = [];
var aSensorDivider = 1.5;
var aSensorLabel = " Right Arm";
var bSensor = [];
var bSensorDivider = 2.5;
var bSensorLabel = " Left Leg";

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
			if (sensorData["aSensor"] != null) { 
				aSensor = sensorData["aSensor"].split(",");
			}
			if (sensorData["bSensor"] != null) { 
				bSensor = sensorData["bSensor"].split(",");
			}
		}

		console.log("ASensor: " + aSensor);
		console.log("BSensor: " + bSensor);
	});
}

// Start listening on window load
window.addEventListener("load", getData(sensorUpdate));


function sensorUpdate(data) {
	console.log("Window update received");
}




// live update graph
(function($) {  
	(function() {
		if( $('#flotDashRealTime').get(0) ) {
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
					if (aSensor.length > 0) { 
						aSensorData.push(aSensor.shift() / aSensorDivider);
					} else { 
						aSensorData.push(0); //push 0 default value if no sensor data left
					}
				}

				while (bSensorData.length < totalPoints) {
					if (bSensor.length > 0) { 
						bSensorData.push(bSensor.shift() / bSensorDivider);
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

			var flotDashRealTime = $.plot('#flotDashRealTime', getSensorData(), {
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

			function update() {

				flotDashRealTime.setData(getSensorData());

				// Since the axes don't change, we don't need to call plot.setupGrid()
				flotDashRealTime.draw();
				setTimeout(update, ($('html').hasClass( 'mobile-device' ) ? 1000 : 250) );
			}

			update();
		}
	})();
}).apply(this, [jQuery]);