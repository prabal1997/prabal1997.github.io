// Live updates registration from firebase
// Live Value Sensors
var aSensor = [];
var aSensorDivider = 1;
var bSensor = [];
var bSensorDivider = 2.5;

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
			var data = [],
				totalPoints = 50;

			function getRandomData() {

				if (data.length > 0)
					data = data.slice(1);

				// Do a random walk
				while (data.length < totalPoints) {

					if (bSensor.length > 0) { 
						data.push(bSensor.shift() / bSensorDivider);
					} else {
						data.push(0);
					}
				}

				// Zip the generated y values with the x values
				var res = [];
				for (var i = 0; i < data.length; ++i) {
					res.push([i, data[i]])
				}

				return res;
			}

			var flotDashRealTime = $.plot('#flotDashRealTime', [getRandomData()], {
				colors: ['#8CC9E8'],
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

				flotDashRealTime.setData([getRandomData()]);

				// Since the axes don't change, we don't need to call plot.setupGrid()
				flotDashRealTime.draw();
				setTimeout(update, ($('html').hasClass( 'mobile-device' ) ? 1000 : 250) );
			}

			update();
		}
	})();
}).apply(this, [jQuery]);