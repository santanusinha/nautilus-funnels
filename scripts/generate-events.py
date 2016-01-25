#!/usr/bin/env python
import sys
import argparse
import requests
import json
import uuid
import time
import random
from datetime import timedelta, date

#activityTemplate = [[ "Home"], ["Mini"]]
#activityTemplate = [[ "Home"], ["Mini", "Sedan", "Prime"], ["RideNow", "RideLater"], ["Confirm", "Cancel"]]
activityTemplate = [[ "Home", "Search", "Notification"], ["BrowsePage", "SearchResults", "Product"], ["Product", "Home"], ["Checkout", "Home"]]

categoryNames = ["Electronics", "Media", "Fashion"]
cityNames = ["Kolkata", "Bangalore", "Delhi", "Chennai", "Mumbai"]

def simulateUser():
    session = dict()
    session['sessionId'] = str(uuid.uuid4())
    session['sessionStartTime'] = long(time.time() * 1000)
    sessionAttributes = dict()
    sessionAttributes['category'] = random.choice(categoryNames)
    session['attributes'] = sessionAttributes
    numSteps = random.randint(1,len(activityTemplate))
    #numSteps = 4
    activities = []
    city = random.choice(cityNames)
    for i in range(0, numSteps):
        activity = dict()
        activity['timestamp'] = long(time.time() * 1000)
        activity['state'] = random.choice(activityTemplate[i])
        activityAttributes = dict()
        activityAttributes['city'] = city
        activity['attributes'] = activityAttributes
        activities.append(activity)
    session['activities'] = activities
    print json.dumps(session)
    #print numSteps
    r = requests.post(url="http://" + args.server + "/v1/activities/test", data=json.dumps(session), headers={'Content-type': 'application/json'})
    if r.status_code == requests.codes.ok:
        print "Saved"
    else:
        print "Error running query: " + str(r)
        

def createDocument(argv):
    opts,


parser = argparse.ArgumentParser(description='Send synthetic events to nautilus for testing')
parser.add_argument('--count', type=int, metavar='N', action='store',
                   default=10,
                   help='the number of events to be sent (default: 10)')
parser.add_argument('--server', metavar='host:port', action='store',
                   help='foxtrot server host:port',
                   required=True)
args=parser.parse_args()

for i in range(0,args.count):
    simulateUser()

#for i in range(0,args.count):
#    foxtrotEvent=dict()
#    foxtrotEvent['id'] = str(uuid.uuid4())
#    foxtrotEvent['timestamp'] = long(time.time() * 1000)
#    data=dict()
#    data['eventType'] = random.choice(eventTypes)
#    foxtrotEvent['data']=data
#    r = requests.post(url="http://" + args.server + "/foxtrot/v1/document/testapp", data=json.dumps(foxtrotEvent), headers={'Content-type': 'application/json'})
#    if r.status_code == requests.codes.created:
#        print "Save"
#    else:
#        print "Error running query: " + str(r)
#


