//call the 'init' function on-load of webpage
onload = init;

//already made connection to firebase, download data relevant to current options
window.firebaseRef = firebase.database();

//set the data values to their default values
function init() {

	//set default values of the interface, update interface
	window.firstName = "Daniel";
	window.lastName = "Weisberg";
	window.date = (new Date());
	window.limbName = "Left Hand";
	window.teamName = "Truck Loaders";

	updateVisibleComponents(true);

	//display data on Chart.Js objects; re-format Chart.Js devices

}

//receives a day-object and returns a formatted, string version of it
function giveStringDate(dateObject) {
	var monthNames = ["Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"];
	dateString = dateObject.getDate() + " " + monthNames[dateObject.getMonth()] + " " + dateObject.getFullYear();
	
	return dateString;
}

//receives date-object, outputs the hash-value of the data
//NOTE: we add '1' to month because month indexing starts from '0'
function giveDateHash(dateObject) {
	return dateObject.getDate() + (dateObject.getMonth()+1)*31 + dateObject.getFullYear()*372;
}

//NOTE: if a paramter changes, you NEED to call the following peace of code...
//...through the 'updateVisibleComponents(true)' function call
function updateVisibleComponents(updateChart) {

	//update the chart data, indicator data too
	//NOTE 1: firebase allows you to be aware of changing data too!!!
	if (updateChart) {
		//extract daily data from firebase
		window.firebaseRef.ref("Users").on("value",

			function(snapshot) {
				//modify the request as per the time
				var currDate = giveDateHash(window.date);
				var userIndex = ( (window.firstName == "Daniel") ? "0" : "1" );
				var limbName = window.limbName.replace(/\s/g, '');				

				//check if any data is even available for viewing, update 'cumulative indicator' accordingly
				var dailyData = giveCleanDailyArray(snapshot.val()[userIndex]['History'][limbName][currDate]);				
				var trendData = giveCleanTrendArray(snapshot.val()[userIndex]['TrendLine'][limbName]["TrendData"]);

				var constantScale = 4;
				document.getElementById("totalScoreValue").innerHTML = Math.round(constantScale*giveMainMean(trendData)*100)+"%";
				document.getElementById("currentScoreValue").innerHTML = Math.round(giveMainMean(dailyData)*100)+"%";					

				//display data on the chart
				//update 'indicator' value appropriately

			},

			function(errorObject) {
				console.log("The read failed: " + errorObject.code);
			}

		);

		//extract realtime data from Firebase (NOTE: hide the graph when the date is weird)
	}

	//refresh user name, date, limb name
	console.log(window.scoreVal, window.dalScoreVal);
	document.getElementById("nameInfo").innerHTML = window.lastName + ", " + window.firstName;
	document.getElementById("limbPicker").innerHTML = window.limbName;
	document.getElementById("dayPickerDayName").innerHTML = giveStringDate(window.date);	
	document.getElementById("teamName").innerHTML = window.teamName;	

}

//receives data values paired with random keys, returns array of only data values
function giveCleanDailyArray(inputData) {

	//check for edge case
	if (inputData == undefined) {
		return new Array(240).fill(0);
	}

	//separate into keys and values
	var keys = Object.keys(inputData).map(
		function(key) {
			return Number(key);
		}
	);

	//reformat array structure
	inputData = Object.keys(inputData).map(
		function(key) {
			return inputData[key];
		}
	);

	//extract values as numerical objects
	values = new Array(inputData.length);
	for (index in inputData) {
		
		//remove random key
		loc_values = Object.keys(inputData[index]).map(
			function(key) {
				return Number(inputData[index][key]);
			}
		);

		//store extracted data in the 'values' array
		values[index] = loc_values[0];
	}

	//create a new array with values placed at appropriate locations
	finalValue = new Array(240).fill(0);
	for (index in keys) {
		finalValue[Math.round(keys[index]/6)] = values[index];
	}

	//return pair of keys-values
	return finalValue;
}

//receives trend data, returns it in array form
function giveCleanTrendArray(inputData) {

	//check for edge case
	if (inputData == undefined) {
		return new Array(240).fill(0);
	}

	//remove random key
	var arrayValue = Object.keys(inputData).map(
		function(key) {
			return inputData[key];
		}
	);
	arrayValue = arrayValue[0];

	//convert the string array to an acutal array
	arrayValue = arrayValue.slice(1, arrayValue.length-1).split(',');

	//convert each element of array to a float
	for(index in arrayValue) {
		arrayValue[index] = Number(arrayValue[index]);
	}

	//return the formatted array as output
	return arrayValue;	
}

//calculate mean of non-zero values
function giveMainMean(inputArray) {
	//define a minimum float value below which erryting counts as 'zero'
	var funcDelta = 1e-5;
	
	//find mean of only the values that are considered non-zero
	var valSum = 0;
	var valCounter = 0;
	for (index in inputArray) {
		valSum = valSum + inputArray[index];
		valCounter = valCounter + Number(inputArray[index] > funcDelta);
	}

	//handle the case when everything is 0
	valCounter = ( ( valCounter == 0 ) ? 1 : valCounter );

	//return mean value
	return (valSum/valCounter);
}
