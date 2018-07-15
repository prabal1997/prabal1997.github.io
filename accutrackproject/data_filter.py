from flask import Flask, request
from flask_restful import Resource, Api
import json
import sys
import tensorflow as tf
import pyrebase
from firebase import firebase
import os
import datetime