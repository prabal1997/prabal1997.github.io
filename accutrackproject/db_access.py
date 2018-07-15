from flask import Flask, request
from flask_restful import Resource, Api
import json
import sys
import tensorflow as tf
import pyrebase
from firebase import firebase
import os
import scipy.signal as spysig
import datetime
import numpy as npy
from enum import Enum
import timesynth as ts
import pandas as pd
from fbprophet import Prophet


#goals:
'''
1.) make a communication channel (day-by-day history (inluding today), today's history, day-by-day predictions (including today))
2.) make database infrastructure for allowing access to predictions about today and future (based on past data)
3.) generate fake data for a 2 week history for each user, generate fake data 'automatically' everyday to automate this process

TO-DOs:
0.) API for storing DAILY data (starting 12:01 AM to 12:00 AM, 'detailed' summary format) (drops out irrelevant data)
1.) API for accessing DAILY data (starting -Inf to r.n. in 'daily' summary format)
2.) API for accessing TODAY's detailed data (starting 12:01 AM to 12:00 AM, 'detailed' summary format)
3.) API for accessing FUTURE PREDICTIONS with considerations to outliers, and adjustment to NEW ROUTINE (starting today to Inf in 'daily' summary format)
4.) API that generates fake data for past in a given date range ('daily' summary format), today's data ('detailed' summary format)

NOTE: associate each activity level with 'DANGEROUS, LOW, NORMAL, HIGH' level of 'ergonomic efficiency'
curl -H "Content-Type: application/json" -X POST -d  '{"24": 576, "25": 625, "26": 676, "27": 729, "20": 400, "21": 441, "22": 484, "23": 529, "28": 784, "29": 841, "0": 0, "4": 16, "8": 64, "59": 3481, "58": 3364, "55": 3025, "54": 2916, "57": 3249, "56": 3136, "51": 2601, "50": 2500, "53": 2809, "52": 2704, "88": 7744, "89": 7921, "82": 6724, "83": 6889, "80": 6400, "81": 6561, "86": 7396, "87": 7569, "84": 7056, "85": 7225, "3": 9, "7": 49, "39": 1521, "38": 1444, "33": 1089, "32": 1024, "31": 961, "30": 900, "37": 1369, "36": 1296, "35": 1225, "34": 1156, "60": 3600, "61": 3721, "62": 3844, "63": 3969, "64": 4096, "65": 4225, "66": 4356, "67": 4489, "68": 4624, "69": 4761, "2": 4, "6": 36, "99": 9801, "98": 9604, "91": 8281, "90": 8100, "93": 8649, "92": 8464, "95": 9025, "94": 8836, "97": 9409, "96": 9216, "11": 121, "10": 100, "13": 169, "12": 144, "15": 225, "14": 196, "17": 289, "16": 256, "19": 361, "18": 324, "48": 2304, "49": 2401, "46": 2116, "47": 2209, "44": 1936, "45": 2025, "42": 1764, "43": 1849, "40": 1600, "41": 1681, "1": 1, "5": 25, "9": 81, "77": 5929, "76": 5776, "75": 5625, "74": 5476, "73": 5329, "72": 5184, "71": 5041, "70": 4900, "79": 6241, "78": 6084}' http://40.113.192.222:5000/RealtimeUpdate/1

'''
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

#develop API for storing incoming, daily data ('/<int:sensor_id>', JSON file as input);
class RealtimeUpdate(Resource):
    
    #NOTE: we are sending data from the URL, we use 'get', not 'post' method, to do what 'post' typically does
    def get(self, sensor_id, data_array):        

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
#        if ( str(check_trendline_hash) != str(curr_date) ): 
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

            #linearly concatenate all the points    
            day_data_list = (npy.reshape(day_data_list, npy.prod(day_data_list.shape)));   
            day_data_list = npy.log(day_data_list*(npy.exp(1)-1)+1);
            dataFrame = pd.DataFrame(day_data_list);     
            dataFrame['ds'] = [datetime.datetime.today()-datetime.timedelta(days=(MAX_TREND_DAYS-1))+datetime.timedelta(minutes=6*k) for k in range(0, len(day_data_list))];
            print(dataFrame['ds'])
            dataFrame.columns = ['y', 'ds'];
            dataFrame = dataFrame.reindex(columns=['ds', 'y']);
            dataFrame['cap'] = 1.0;
            dataFrame['floor'] = 0.0;

            #predict hourly trends in the data, extract prediction
            fb_model = Prophet();
            fb_model.add_seasonality(name="hourly", period=1, fourier_order=10); 
            fb_model.add_seasonality(name="daily", period=1/24, fourier_order=10); 

            fb_model = fb_model.fit(dataFrame);
            fb_future = fb_model.make_future_dataframe(periods=240);
            fb_future = fb_model.predict(fb_future);

            final_data = npy.array(fb_future['hourly'])[0:240];
            final_data = (npy.exp(final_data)-1)/(npy.exp(1)-1);
            
            N = 10;
            final_data_2 = final_data;
            final_data_2[final_data_2 < 0] = 0;
            final_data_2 =npy.convolve(final_data_2, npy.ones((N,))/N, mode='valid')
            
            #pad the final array to make it 240 elements long
            final_data_3 = npy.zeros(240);
            final_data_3[0:(240-(N-1))] = final_data_2;

                
            #format the output data before writing to the database
            yester_string = (str(yester_value) for yester_value in final_data_3);
            yester_string = "["+(','.join(yester_string))+"]";
            
            firebase.post(trend_data_key+"/", yester_string);

            '''
            yester_key = user_key+"/History/"+str(INT_LIMB_MAP[limb_id])+"/"+str(yester_date)+"/";
            yester_data = firebase.get(yester_key, None);

            yester_index = (npy.array([key for key in yester_data], dtype='int')//minute_window);
            yester_data = ([yester_data[key] for key in yester_data]);
            for index, data in enumerate(yester_data):
                yester_data[index] = [yester_data[index][key] for key in yester_data[index]][0];
            yester_array = npy.zeros(daily_rate);
            yester_array[yester_index] = yester_data;

            yester_array = spysig.medfilt(yester_array, kernel_size=11);
            '''
        
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
                #prev_value = [prev_value[key] for key in prev_value]; 
                #prev_value = prev_value[0];
                prev_value = extract_content(prev_value);
                
            #exponentially average the last value with new one
            prev_value = update_value(output_mean, prev_value);
            
            firebase.post(database_key+str(curr_time), prev_value);
        else:
            #maintain exponential averaging to keep the data 'clean'
            #check_exist = [check_exist[key] for key in check_exist]; 
            #check_exist = check_exist[0];
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
    

#develop API for providing history after receiving user_id, limb_id    
class ReadPrediction(Resource):
    
    #NOTE: here 'user_id' is received as a STRING
    def get(self, user_id, limb_id):
        
        #check today's date, calculate yesterday's date hash
        yester_date = datetime.date.today()-datetime.timedelta(days=1);
        yester_date = yester_date.year*372+yester_date.month*31+yester_date.day;
        
        #access all the samples from yesterday's data, filter them
        user_key = "/Users/"+str(user_id);
        database_key = user_key+"/History/"+limb_id+"/"+str(yester_date)+"/";
        yester_data = firebase.get(database_key, None);
        
        yester_index = (npy.array([key for key in yester_data], dtype='int')//6);
        yester_data = ([yester_data[key] for key in yester_data]);
        for index, data in enumerate(yester_data):
            yester_data[index] = [yester_data[index][key] for key in yester_data[index]][0];
        yester_array = npy.zeros(240);
        yester_array[yester_index] = yester_data;

        yester_array = spysig.medfilt(yester_array, kernel_size=11);
        
        #format the output data before returning
        yester_string = (str(yester_value) for yester_value in yester_array);
        yester_string = "["+(','.join(yester_string))+"]";
        
        return yester_string;
        
        
        '''
        #calculate today's hash, check what the input date hash is
        curr_time_orig = datetime.datetime.now();
        curr_date = int(curr_time_orig.year)*372+int(curr_time_orig.month)*31+int(curr_time_orig.day);
        input_date_hash = npy.min(curr_date, input_date_hash);
        
        #find the earliest available date in the database
        
        #read starting from 'k' days before to yesterday (dropping outliers)
        
        
        
        #prepare data base key for data-access
        user_key = "/Users/"+str(user_id);
        database_key = user_key+"/History/"+str(INT_LIMB_MAP[limb_id])+"/"+str(input_date_hash);
        
        #pull data from firebase database
        input_data = firebase.get(database_key, None);
        input_data = input_data.items();
        print(input_data);
        '''
        
        return input_data;

class GivePrediction(Resource):
    
    def get(self):
        
        return "MANGO!";    
    
class RandomData(Resource):
    
    def get(self):
        
        return "MANGO!";

#develop mechanism(s) for GETting and POSTing data to firebase
class Data_read(Resource):
    def get(self, user_id):
        result = firebase.get('/Users', str(user_id));
        return {'result':result};
        
class Sensor_read(Resource):
    def get(self, sensor_num):
        return {'Description':'Sensor data parser, filter, and storage tool.'};

    def post(self, sensor_num):
        some_json = request.get_json();
        return ({'you sent':some_json}, 201);

#develop API for generating fake data for accutrack
class FakeData(Resource):
    def detailed_fake_data(self, time_range, major_hours=[10, 11, 12, 13, 14, 19, 20], activity_amount=[1, 0.5, 1, 0.5, 1, 0.5, 1]):
        '''
        DETAILED_FAKE_DATE: receives a range of hours [start, end] and generates DETAILED fake data for the duration of it.
        e.g. time_range=[0, 24] will generate entire days worth of fake data, 'major_hours' tells how many hours have activity, 'activity_amount' (b/w 0 and 1) tells how MUCH activity thoughout the hour
        NOTE: level of activity is ALWAYS between 0 and 1, and represents the ANGLE OF EXTENSTION
        NOTE: acitivity_amount is the PROPORTION OF SECONDS in which 'activity' was detected 
        '''
        
        '''
        #convert input data to numpy arrays
        time_range, major_hours, activity_amount = npy.array(time_range), npy.array(major_hours), npy.array(activity_amount);
        
        #mark the seconds that are expected to show any activity
        second_array = npy.zeros([24*3600, -1]);
        return second_array;
        '''
        return "MANGO!";
        
    def daily_from_detailed_format(input_array):
        '''
        #convert input array to numpy format (if that's not already the case)
        input_array = npy.asarray(input_array);   
        
        #calculate the level of activity in EVERY 10 seconds by downsampling array
        second_window, sample_rate = 10, 10;
        
        input_array = npy.reshape(input_array, [-1, second_window*sample_rate]);
        input_array = npy.mean(input_array, axis=1);
        
        #return the downsampled array
        return input_array;
        '''
        return "MANGO!";
        
        
    def get(self, user_id, limb_id, start_point, end_point):
        '''
        GET: receives a range of days and adds fake data to them.
        e.g. -5 to 0 means fake data needs to be generated from 5 days ago to r.n. for mentioned user-id and limb-id
        '''
        
        #access the database to generate days worth of fake data
        return {'UserID':user_id, 'LimbID':limb_id, 'StartDate':start_point, 'EndDate':end_point};
        
        
