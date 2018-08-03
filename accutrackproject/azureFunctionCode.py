import os
import json
import sys
from firebase import firebase
import scipy.signal as spysig
import datetime
import numpy as npy

'''
ALL THE CODE TO PROCESS THE REQUEST STRING GOES HERE
'''

#receive the request input tags
env = os.environ;
req_url = env['REQ_HEADERS_X-ORIGINAL-URL'];
urlparts = req_url.split('?')[1];

urlparts = urlparts[urlparts.find('sensor_id'):].split('&');
sensor_id = int(urlparts[0].split('=')[1]); 
data_array = urlparts[1].split('=')[1];

#postreqdata = json.loads(open(os.environ['req']).read())
response = open(os.environ['res'], 'w');
response.write(sys.version + " " + str(sensor_id)+" "+str(data_array));

#create a mapping from sensor ID to user-id and limb-id
SENSOR_USER_MAP = {0:0, 1:1};
SENSOR_LIMB_MAP = {0:3, 1:0};

INT_LIMB_MAP = { 0:'LeftHand', 1:'RightHand', 2:'LeftLeg', 3:'RightLeg'};
LIMB_INT_MAP = {'LeftHand':0, 'RightHand':1, 'LeftLeg':2, 'RightLeg':3};

#additional constant declarations for semantic ease
SECONDS_IN_DAY = 24*3600;
EPS = npy.finfo(npy.float32).eps;

#NOTE: the idea is that fatigue is significant with large extension, insignificant with small extension
MAX_TREND_DAYS = 7;
CURR_VAL_WEIGHT = 0.1;
SMALL_VAL_WEIGHT = 0.01;
THRESH_LOW_ACTIVITY = 15;
NORM_CONSTANT = 250.0;

#establish a connection with firebase database
firebase = firebase.FirebaseApplication("https://accutrack-iot.firebaseio.com", None);

#support functions for use by communication protocols
def update_value(curr_input, prev_input):
    output_val = abs((1-CURR_VAL_WEIGHT)*prev_input + CURR_VAL_WEIGHT*curr_input);
    return output_val;

def extract_content(input_data):
    input_data = [input_data[key] for key in input_data];
    return input_data[0];

#NOTE: we are sending data from the URL, we use 'get', not 'post' method, to do what 'post' typically does
def get_data(sensor_id, data_array):        

    #calculate current time, drop packet if it's too close to next day; identify user and limb being used
    sample_rate, minute_window = 10, 6;
    daily_rate = 24*60//minute_window;

    curr_time_orig = datetime.datetime.now();
    curr_time = int(curr_time_orig.hour)*60;
    curr_time = int(curr_time + (npy.floor(curr_time_orig.minute/(1.0*minute_window)+EPS)*minute_window+EPS) + EPS);
    curr_date = int(curr_time_orig.year)*372+int(curr_time_orig.month)*31+int(curr_time_orig.day);
    
    if (SECONDS_IN_DAY-curr_time < sample_rate*minute_window+EPS):
        #return an error message indicating change-of-day
        return {'STATUS': 'ERROR'};
    
    #identify user_id, limb_id based on the sensor_id that's sending data
    user_id, limb_id = SENSOR_USER_MAP[sensor_id], SENSOR_LIMB_MAP[sensor_id];    
    
    #convert incoming data to numpy array, find indices & time of occurence of 'significant' points only
    print("DATA: " + str(data_array));
    input_array = npy.array(npy.matrix('['+data_array+']'));
    if (input_array.shape[0]==0):
        input_array = numpy.array([0.0]);
        
    #see if the array has any significant values, if not simply use the non-significant values
    clean_array = input_array[input_array > THRESH_LOW_ACTIVITY]/NORM_CONSTANT; 
    if (clean_array.shape[0] == 0):
        clean_array = input_array;
        
    #push 'significant' data to firebase in 'daily' summary format, and post a SINGLE value to 'CurrentValue'
    user_key = "/Users/"+str(user_id);
    database_key = user_key+"/History/"+str(INT_LIMB_MAP[limb_id])+"/"+str(curr_date)+"/";
    
    #check if the trendline has been updated
    trend_key_format = user_key + "/TrendLine/" + str(INT_LIMB_MAP[limb_id]); 
    trend_date_key = trend_key_format + "/" + "LastUpdateDate";
    trend_data_key = trend_key_format + "/" + "TrendData";
    check_trendline_hash = firebase.get(trend_date_key, None);
    
    print("CONTENT PULLED: " + str(check_trendline_hash), " ORIG: " + str(curr_date));
    if (check_trendline_hash is not None):
        check_trendline_hash = extract_content(check_trendline_hash);
    
    print("BOOL: " + str(str(check_trendline_hash) != str(curr_date)));
#    if ( str(check_trendline_hash) != str(curr_date) ): 
    if ( int(check_trendline_hash) < int(curr_date) ): 
        print("MANGO!");
        print("COMP: " + str(check_trendline_hash), "COMP1: " + str(curr_date));
        
        #delete out-dated copy of the trend-line
        firebase.delete(trend_key_format, "LastUpdateDate");
        firebase.delete(trend_key_format, "TrendData");
        
        #update the trend-line's new date and data
        firebase.post(trend_date_key+"/", str(curr_date));
        
        #check today's date, calculate yesterday's date hash
        yester_date = datetime.date.today()-datetime.timedelta(days=1);
        yester_date = yester_date.year*372+yester_date.month*31+yester_date.day;

        #access all the samples from yesterday's data, filter them
        daylist_key = user_key+"/History/"+str(INT_LIMB_MAP[limb_id])+"/";
        history_data = firebase.get(daylist_key, None);
        
        daylist_days = list(history_data.keys());
        trend_day_count = npy.min(npy.array([MAX_TREND_DAYS, len(daylist_days)]));
        daylist_days = daylist_days[(-trend_day_count):];
        
        history_data = [history_data[key] for key in daylist_days];
        history_data = [ (list(value.keys()), list(value.values())) for value in history_data];
        
        history_data_keys = [npy.array([int(val) for val in value[0]]).astype(int) for value in history_data];
        history_data_values = [npy.array([ list(element.values())[0] for element in value[1] ]) for value in history_data];

        day_time_list = npy.arange(0, 24*60, 6);
        day_data_list = npy.zeros([len(history_data_keys), 24*10]);
        for key in range(0, len(history_data_keys)):
            day_data_list[key, history_data_keys[key]//6] = history_data_values[key];

        #replace fb-prophet prediction with simple filtering
        N = 7;
        final_data_3 = npy.median(day_data_list, axis=0);
        final_data_3 = npy.convolve(final_data_3, npy.ones((N,))/N, mode='valid');
        final_data_3 = spysig.medfilt(final_data_3, kernel_size=N);
            
        #format the output data before writing to the database
        yester_string = (str(yester_value) for yester_value in final_data_3);
        yester_string = "["+(','.join(yester_string))+"]";
        
        firebase.post(trend_data_key+"/", yester_string);
    
    #convert incoming data to REBA-like format. Lower the value, worse the ergonomic form of the user
    #NOTE: roughly based on info provided on https://ergo-plus.com/wp-content/uploads/REBA.pdf
    output_mean = npy.mean(clean_array);
    if ("hand" in str.lower(INT_LIMB_MAP[limb_id])):
        output_mean = npy.sin(npy.abs((output_mean*npy.pi)-npy.pi/2));
    else:
        output_mean = npy.cos((npy.pi-output_mean*npy.pi)/2);
    
    
    #check if the key already exists or not, and re-calculate the value of ongoing 6-min chunk
    check_exist = firebase.get(database_key+str(curr_time), None);
    if (check_exist is None):
        #check if the last sensor value is available
        prev_value = firebase.get(user_key+"/CurrentValue/"+str(INT_LIMB_MAP[limb_id]), None);
        if (prev_value is None):
            prev_value = output_mean;
        else:
            prev_value = extract_content(prev_value);
            
        #exponentially average the last value with new one
        prev_value = update_value(output_mean, prev_value);
        
        firebase.post(database_key+str(curr_time), prev_value);
    else:
        #maintain exponential averaging to keep the data 'clean'
        check_exist = extract_content(check_exist);
        
        check_exist = update_value(output_mean, check_exist);

        firebase.delete(database_key, str(curr_time));
        firebase.post(database_key+str(curr_time), check_exist);
        
    #post the 'real-time' view into a single database field
    firebase.delete(user_key+"/CurrentValue/", str(INT_LIMB_MAP[limb_id]));
    
    round_factor = 1000.0;
    firebase.post(user_key+"/CurrentValue/"+str(INT_LIMB_MAP[limb_id])+"/", npy.round(output_mean*round_factor)/round_factor);
    
    #return a status indicator informing user of successful attempt
    return ({'STATUS': 'ATTEMPT SUCCESSFUL'});

#call the appropriate python function
get_data(sensor_id, data_array);

response.close();
