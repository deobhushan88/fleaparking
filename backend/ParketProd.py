__author__ = 'gbhardwaj'
from bottle import Bottle,route,request,response,get,post,run,ServerAdapter, static_file
import requests
from cherrypy import log
import json
import psycopg2
import datetime
import sys
import uuid
import parketconstants
import dbconnect as connection
import sendEmail as email
import oauth2client.client
from oauth2client.crypt import AppIdentityError
import sendgrid
import pyqrcode
import base64


app = Bottle()

@app.post('/auth')
def auth():
    rtn = {}
    rtn["scopes"] = ["plus.login"]
    rtn["has_refresh_token"] = False
    response.status = 200
    return rtn

@app.post('/auth/callback')
def authCallback():
    serverAuthCode = request.forms.get('serverAuthCode')
    clientId = parketconstants.PARKET_CLIENT_ID
    clientSecret = parketconstants.PARKET_CLIENT_SECRET_KEY
    grantType = "authorization_code"
    # In this flow, there is no need to set redirect url
    payload = {'code' : serverAuthCode, 'client_id' : clientId, 'client_secret' : clientSecret, 'grant_type' : grantType}

    # Make the request for the auth and refresh token
    # In our application, we do not need this and only use the ID Token
    # Consider if you want to remove this step, along with the related if conditions
    r = requests.post('https://www.googleapis.com/oauth2/v3/token', data=payload)

    # Handle error in authenticating with Google
    rtn = {}
    if r.status_code != 200:
        rtn['status'] = 'getting auth token from google failed'
        response.status = 400
        return rtn

    # Handle pass flow
    rtn = {}
    id_token = request.forms.get('idToken')
    if id_token is not None:
        try:
            jwt = oauth2client.client.verify_id_token(id_token, clientId)
            if jwt['aud'] == clientId:
                rtn['status'] = 'authenticated'
                rtn['expiry'] = str(jwt['exp'])
                response.status = 200

                # Everything is perfect, user is authenticated.
                # Now, add the user's email to database using jwt['email'], if does not exist already
                # Use the jwt['sub'] field to uniquely identify the user (guaranteed), maybe use as primary key.
                # This "sub" field is a string containing a large number (can be parsed as a long)
                # TODO
                log("user authenticated: ")
                log(jwt['email'])

            else:
                rtn['status'] = 'invalid clientId in token'
                response.status = 400
        except AppIdentityError:
            rtn['status'] = 'invalid token'
            response.status = 400
    else:
        rtn['status'] = 'token missing'
        response.status = 400
    return rtn


def isAuthenticated(id_token):
    """
    This function returns a dictionary containing following fields:
        Required:
            status : Boolean
        Optional (only if status is True):
            email : String (the user's email)
            sub : String (Google's unique identifier for the user)
    """
    rtn = {}
    clientId = parketconstants.PARKET_CLIENT_ID
    if id_token is not None:
        try:
            jwt = oauth2client.client.verify_id_token(id_token, clientId)
            if jwt['aud'] == clientId:
                rtn['status'] = True
                rtn['email'] = jwt['email']
                rtn['sub'] = jwt['sub']
                return rtn
            else:
                rtn['status'] = False
                return rtn
        except AppIdentityError:
            rtn['status'] = False
            return rtn
    else:
        rtn['status'] = False
        return rtn


@app.post('/users/self/parkingspaces')
def addParkingSpace():
    '''
    This function gets executed with HTTP call:

    accepts user supplied details and stores in the back-end database.
    :return: method endpoint returns a dictionary telling about the success or failure of action
    '''
    #Parking space will only be added if user supplies his authentication token with every request
    auth = isAuthenticated(request.json['idToken'])

    # Check if user is authenticated
    if (auth['status'] == True):

        #If user gets authenticated, accept all details from HTTP call and parse them

        parkingspace_label = request.json['parkingSpaceLabel']
        parkingspace_description = request.json['parkingSpaceDescription']
        address_line1 = request.json['addrLine1']
        address_line2 = request.json['addrLine2']
        # addrLine3 = request.forms.get('addrLine3')
        city = request.json['city']
        state = request.json['state']
        country = request.json['country']
        zipcode = int(request.json['zip'])
        disabled_parking_flag = bool(request.json['disabledParkingFlag'])
        parkingspace_photo = request.json['parkingSpacePhoto']
        parkingspace_lat = request.json['parkingSpaceLat']
        parkingspace_long = request.json['parkingSpaceLong']

        '''
        Generating a parking ID, which is a random string from uuid module
        '''
        tempstring  = uuid.uuid4().urn
        parkingspace_id = tempstring[9:]
        '''
        Below is the [qrcode = parkingspace_id + zipcode]
        The concatenation here can be a performance hit when
        there are large number of parking spaces being added.
        this qrcode string will be sent to the user
        '''
        qrcode = (parkingspace_id + str(zipcode)).upper()
        '''insert into database as they are received,
        initialize default variables which are not coming from front-end
        default parking rate until user sets it in another end-point is 0.00
        start_datetime = None [inserted here as default in DB ]
        end_datetime = None [inserted here as default in DB]
        parking_space_availability_flag = False , this will the default value since the user has just added it
        '''

        parking_space_availability_flag = False
        start_datetime = None
        end_datetime = None
        parkingspace_rate = 0.00

        '''
        Geting users private feilds from auth token return dictionary of auth function callback
        sub: returns a unique big integer which can be used as a unique user id for storing user details
        email: returns the user id, corresponding to loggedin session
        phone: currently we are not storing users phone number
        '''
        user_id = auth['sub']
        user_email = auth['email']
        user_phone = None
        is_owner = True
        is_parker = False
        '''
        Create connection with the database and insert all details in the respective relations
        remember %s here used saves us from SQL injections, in case any hacker is in mood to waste his time you know :)
        '''
        try:
            con = connection.get_db_connection()
            #con = psycopg2.connect(database=parketconstants.PARKET_DATABASE_NAME, user=parketconstants.PARKET_DATABASE_USER, password=parketconstants.PARKET_DATABASE_PASSWORD)#constant name required here
            cur = con.cursor()

            '''
            TODO: IF user passes same details again, stopping him from doing so is reuired, we will do it later
            as of now we are handling it from front-end
            '''

            '''
            1. Insert all parking space related details to PARKING_SPACE table
            '''
            insert_detail_data = (parkingspace_id, address_line1, address_line2, city, country, state, zipcode, parkingspace_label, parkingspace_description,parkingspace_photo, disabled_parking_flag, parking_space_availability_flag, start_datetime, end_datetime, parkingspace_rate, qrcode,)

            cur.execute(parketconstants.INSERT_INTO_PARKING_SPACE, insert_detail_data)
            '''
            2. Insert parking space location details to PARKING_SPACE_LOCATION table
            '''
            insert_location_data = (parkingspace_id, parkingspace_lat, parkingspace_long,)
            cur.execute(parketconstants.INSERT_INTO_PARKING_SPACE_LOCATION, insert_location_data)
            '''
            3. Fetch list of all users from user-detail relation so as to check if this user has already any parking space in db
            '''
            cur.execute(parketconstants.GET_ALL_USERS_FROM_USER_DETAILS)
            existing_users_list = cur.fetchall()
            #con.commit() #this will insert when user is a new user

            if (user_id,) not in existing_users_list:
                print user_id+ ' EXISTS'
                insert_user_details_data = (user_email, user_phone, user_id, is_owner, is_parker,)
                cur.execute(parketconstants.INSERT_INTO_USER_DETAILS, insert_user_details_data)
                con.commit()

            insert_parkingspace_owner_data = (parkingspace_id, user_id,)
            cur.execute(parketconstants.INSERT_INTO_PARKING_SPACE_OWNER,insert_parkingspace_owner_data)

            con.commit()
            connection.close_db_connection(con) #close the db connection

            #print cur.fetchone()
            con.commit()
            #print (cur.execute("SELECT * from parketb.parking_space"))
        except: 
            pass #handle connection errors here

        return_dict = {}
        return_dict["parkingSpaceId"] = parkingspace_id

        #Sending Email
        email.sendEmail(auth['email'], qrcode)

        response.status = 201
        return return_dict
    else:
        return_dict = {'status' : 'authentication failure'}
        response.status = 400
        return return_dict

@app.get('/email/qrcodes/<filename:re:.*\.png>')
def getImage(filename):
    '''
    internal function bein used to get image for QR code
    :param filename: gets the filename for the image
    :return: file in a email to the user
    '''
    log("returning " + filename)
    return static_file(filename, root='./email/qrcodes', mimetype='image/png')


@app.post('/users/self/parkingspaces/<parkingSpaceId>')
def editParkingSpace(parkingSpaceId):
    '''
    This end-point makes a particular parking space enable or disable.
    :param parkingSpaceId: the id against which action needs to be performed
    :return: action status
    '''
    auth = isAuthenticated(request.json['idToken'])
    if (auth['status'] == True):
        parkingspace_id = parkingSpaceId
        action = request.json['action']
        parking_space_availability_flag = request.json['parkingSpaceAvailabilityFlag']
        start_datetime=request.json['startDateTime']
        end_datetime=request.json['endDateTime']
        parking_space_rate = float(request.json['parkingSpaceRate'])

        con = connection.get_db_connection()
        cur = con.cursor()
        #Query to update parking space availability flag
        if(action=='parkingSpaceEnable'):
            update_parking_space_enable = (start_datetime, end_datetime, parking_space_rate, parkingspace_id,)
            cur.execute(parketconstants.UPDATE_PARKING_SPACE_ENABLE,update_parking_space_enable)

        elif(action=='parkingSpaceDisable'):
            parking_id_data = (parkingspace_id,)
            cur.execute(parketconstants.UPDATE_PARKING_SPACE_DISABLE_AVAILABILITY_FLAG,parking_id_data)

        #Query to change parking time and date
        elif(action=='parkingSpaceTimeChange'):
            date_time_data = (start_datetime,end_datetime,parkingspace_id,)
            print cur.execute(parketconstants.UPDATE_PARKING_SPACE_DATETIME, date_time_data)

        #Query to change to change parking space rate
        elif(action=='parkingSpaceRateChange'):
            parking_space_id_data = (parking_space_rate,parkingspace_id,)
            print cur.execute(parketconstants.UPDATE_PARKING_SPACE_RATE,parking_space_id_data )

        con.commit()
        connection.close_db_connection(con) # close the connection once done

        return_dict = {}
        return_dict["status"] = request.json['action'] + " DONE"
        response.status = 200
        return return_dict
    else:
        return_dict = {'status' : 'authentication failure'}
        response.status = 400
        return return_dict


@app.post('/users/self/queryparkingspaces')
def queryParkingSpace():
    '''
    This method will help in returning all parking spaces of one particular user
    :return:list of dictionaies  of all details of all parking spaces owned by one user
    '''
    auth = isAuthenticated(request.json["idToken"])
    if (auth['status'] == True):
        #user_id=request.json['userid']
        user_id = auth['sub']
        #printing the requested values
        con = connection.get_db_connection()
        cur = con.cursor()
        #Query to update parking space availability flag
        user_id_data = (user_id,)
        cur.execute(parketconstants.GET_ALL_PARKING_SPACE_DETAILS_FOR_USER, user_id_data)

        parking_spaces = [] #list of all parking spaces
        parking_space_details_data =cur.fetchall()
        number_of_spaces = 0
        '''
        Suppose the user has more than 1 space so we will return a list of dictionaries containing details of every parking space
        below for loop can be a performance hit to us if user has got more than 10 spaces, but we wont allow any user to have more than 5 anyways
        '''

        for parkingspace_id, addrline1, addrline2, city, country, state, zipcode, parking_space_label, parking_space_availability_flag , latitude, longitude, disabledparkingflag, parking_space_rate, startdatetime, enddatetime, parking_space_photo_urls, parking_space_description in parking_space_details_data:
            aparking_space = {}
            aparking_space['parkingSpaceId'] = parkingspace_id
            aparking_space['parkingSpaceAddress'] = addrline1 +' '+  addrline2+' ' + city+' ' + state+' ' + str(zipcode)+' ' + country+'.'
            aparking_space['parkingSpaceLat'] = latitude
            aparking_space['parkingSpaceLong'] = longitude
            aparking_space['parkingSpaceLabel'] = parking_space_label
            aparking_space['parkingSpaceAvailabilityFlag'] = parking_space_availability_flag
            aparking_space['disabledParkingFlag'] = disabledparkingflag
            aparking_space['parkingSpaceRate'] = float(parking_space_rate)
            aparking_space['parkingSpacePhoto'] = parking_space_photo_urls
            aparking_space['parkingSpaceDescription'] = parking_space_description
            number_of_spaces += 1

            parking_spaces.append(aparking_space)

        connection.close_db_connection(con) # close the connection once done
        return_dict = {}
        return_dict["count"] = number_of_spaces
        return_dict["parkingSpaces"]=parking_spaces
        response.status = 200
        return return_dict
    else:
        return_dict = {'status' : 'authentication failure'}
        response.status = 400
        return return_dict

@app.post('/search')
def search():
    '''
    This end point is fired every 10 secs on the default home screen, it returns list of available spaces in range
    :return: the list of spaces available in the user supplied end point
    '''

    auth = isAuthenticated(request.json['idToken'])
    # if authenticated, prepare a return dictionary
    return_dict={}

    if (auth['status'] == True):
        action = request.json['action']
        userLat = request.json['userLat']
        userLong = request.json['userLong']
        queryStartDateTime =request.json['queryStartDateTime']
        queryStopDateTime =request.json['queryStopDateTime']
        # range has been changed to radius as it is a keyword
        radius =request.json['range']

        # get a connection to database
        con = connection.get_db_connection()

        cur_range_match = con.cursor()  #this cursor holds the result for spaces which occur in the users range
        cur_bookings_match=con.cursor() #to get currently booked spaces
        cur_final_available_ids=con.cursor() #to populate final spaces

        nearby_space_data = (userLat,userLong,radius,queryStartDateTime ,queryStopDateTime,)
        cur_range_match.execute(parketconstants.GET_ALL_NEARBY_SPACES_IN_RANGE,nearby_space_data)

        # create a empty array which will contain details of all spaces to be returned
        parkingSpaces = []

        spaces_in_range =cur_range_match.fetchall()
        number_of_spaces =0

        # getting a list of all parking ids in the nearby range , so that we can fetch booking details only against those ids
        parkingspace_ids=[]

        # Getting all ids and storing them in a list
        for parkingspace_id, addrline1, addrline2, city, country, state, zipcode,latitude, longitude, disabledparkingflag, parking_space_rate, startdatetime, enddatetime, parking_space_photo_urls, parking_space_description, qrcode in spaces_in_range:
            parkingspace_ids.append(parkingspace_id) # some good way instead of for loop

        if not parkingspace_ids==[]: #this checks if there are atleast some parkingspace id availble in that range
            availble_spacesarray=[] # this cursor conatains ids of only not booked parking spaces
            for parkingspace_ids_val in parkingspace_ids:
                # check for all parkingspace id against any prior bookings in bookings table
                booked_spaces_data = (queryStartDateTime,queryStopDateTime,parkingspace_ids_val,)
                cur_bookings_match.execute(parketconstants.GET_ALL_NOT_BOOKED_SPACES,booked_spaces_data)
                # Parking spaces are being checked against bookings table

                if not ((True,) in cur_bookings_match.fetchall()):# doesnt have any bookings for this id
                    #Space is free
                    availble_spacesarray.append(parkingspace_ids_val)
                else:
                    print "Spaces is already booked and does not apprear in search result"

            if (availble_spacesarray):
                # we found some space unbooked in the range,so now we will get all details of those parking space from database
                for parking_space_id_available in availble_spacesarray:
                    parking_space_id_data = (parking_space_id_available,)
                    cur_final_available_ids.execute(parketconstants.GET_DETAILS_OF_ONE_PARKING_SPACE, parking_space_id_data)
                    final_parking_spaces_available = cur_final_available_ids.fetchall()
                    for parkingspace_id, addrline1, addrline2, city, country, state, zipcode,latitude, longitude, disabledparkingflag, parking_space_rate, startdatetime, enddatetime, parking_space_photo_urls, parking_space_description, qrcode in final_parking_spaces_available:
                        aparking_space = {}
                        parkingSpaceAddress = addrline1 +' '+  addrline2+' ' + city+' ' + state+' ' + str(zipcode)+' ' + country
                        aparking_space['parkingSpaceId'] = parkingspace_id
                        aparking_space['parkingSpaceAddress']=parkingSpaceAddress
                        aparking_space['parkingSpaceLat']=latitude
                        aparking_space['parkingSpaceLong']=longitude
                        aparking_space['disabledParkingFlag'] = disabledparkingflag
                        aparking_space['parkingSpaceRate']=float(parking_space_rate)
                        stdatime = str(queryStartDateTime)
                        endatime = str(queryStopDateTime)
                        stdatime=stdatime.replace(" ","T")
                        endatime=endatime.replace(" ","T")
                        aparking_space['startDateTime']=stdatime
                        aparking_space['endDateTime'] =endatime
                        aparking_space['parkingSpacePhoto']=parking_space_photo_urls
                        aparking_space['parkingSpaceDescription']=parking_space_description
                        aparking_space['qrCode']=qrcode
                        number_of_spaces+=1
                        parkingSpaces.append(aparking_space)
                        connection.close_db_connection(con)#close the connections as soon as you are done

            else:
                #we found alll spaces in the range already booked
                return_dict["count"]=0
                return_dict["parkingSpaces"]=[]
        else:
            # No parking spaces found in giving range
            # preoare your return dict for no spaces
            return_dict["count"]=0
            return_dict["parkingSpaces"]= []

        response.status = 200
        return_dict["count"]=number_of_spaces
        return_dict["parkingSpaces"] = parkingSpaces
        return return_dict

    else:# if id token issues are there
        return_dict = {'status':'authentication failure'}
        response.status = 400
        return return_dict

@app.post('/users/self/bookings')
def bookParkingSpace():
    '''
    This method will book a parking space for the user for the given timings
    :return: returns the booking id to the user in return dict
    '''
    auth = isAuthenticated(request.json['idToken'])
    if (auth['status'] == True):
        '''
        insert a row in bookings table with auto generated bookingId
        '''
        # get a connection to database
        con = connection.get_db_connection()
        cur = con.cursor()

        # get values from request
        parkingspace_id = request.json['parkingSpaceId']
        booking_start_datetime = request.json['bookingStartDateTime']
        booking_end_datetime = request.json['bookingEndDateTime']
        user_id = auth['sub']

        # we are generating a booking id for this user
        currentTime = datetime.datetime.now().isoformat()
        booking_id_generated = "B"+currentTime

        # below is the timestamp at which booking happened
        booking_at_datetime = currentTime

        # Make the bookings
        insert_booking_data = (booking_id_generated,user_id,parkingspace_id,booking_at_datetime,booking_start_datetime,booking_end_datetime,)
        cur.execute(parketconstants.INSERT_INTO_BOOKINGS,insert_booking_data)
        con.commit()

        # finsih booking, close the connection
        connection.close_db_connection(con)
        return_dict = {}
        return_dict["bookingId"] = booking_id_generated
        response.status = 201
        return return_dict
    else:
        return_dict = {'status' : 'authentication failure'}
        response.status = 400
        return return_dict

@app.post('/users/self/bookings/<bookingId>')
def editBooking(bookingId):
    '''
    This method handles user checked In state and user checked out state, sends email with appropriate action
    action : "checkIn" OR "checkOut"
    :param bookingId
    :return: "action done"
    '''

    auth = isAuthenticated(request.json['idToken'])
    if (auth['status'] == True):
        user_action = request.json['action']

        # get the email id of user who checked in, to say that user with this email id has parked in your space
        parker_emailid = auth['email']

        # get connection to database
        con = connection.get_db_connection()
        cur = con.cursor()

        # get parking spaec id first from booking id

        id_data = (bookingId,)
        cur.execute(parketconstants.GET_PARKINGSPACE_ID_FOR_THIS_BOOKING_ID,id_data)
        aparking_space_id = cur.fetchone()[0]
        print aparking_space_id

        # get owner email id
        id_data = (aparking_space_id,)
        cur.execute(parketconstants.GET_OWNER_EMAIL,id_data)
        owner_email = cur.fetchone()[0]
        print owner_email

        if user_action=='checkIn':
            # send email to owner that someone has checked in his space
            subject_of_email = "A Parket user just checked in to your parking space"
            email.sendEmailForCheckInOut(owner_email, subject_of_email)

        elif user_action=='checkOut':
            # send email to owner that someone has checked out of his space
            subject_of_email = "A Parket user just checked out of your parking space"
            email.sendEmailForCheckInOut(owner_email, subject_of_email)

        return_dict = {}
        return_dict["status"] = request.json['action'] + " DONE"
        response.status = 200
        return return_dict
    else:
        return_dict = {'status':'authentication failure'}
        response.status = 400
        return return_dict


# Run the bottle web service on CherryPy server with HTTPS
run(app, host='0.0.0.0', port='443', server='cherrypy', certfile="cert.pem", keyfile="privkey.pem", certchain="certchain.pem")
