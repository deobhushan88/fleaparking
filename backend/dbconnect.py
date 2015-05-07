__author__ = 'gbhardwaj'
import parketconstants
import psycopg2 as connector
#these functions provides utility functions for opening and closing connections

def get_db_connection():
    try:
        return connector.connect(database=parketconstants.PARKET_DATABASE_NAME, user=parketconstants.PARKET_DATABASE_USER, password=parketconstants.PARKET_DATABASE_PASSWORD)
    except connector.Error as e:
        print e.pgerror


def close_db_connection(con):
    print "connection to db closed"
    return con.close


