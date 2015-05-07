__author__ = 'gbhardwaj'
import parketconstants
import sendgrid
import pyqrcode

#this utility handles all email send and receive part for parket backend

def sendEmail(recipient, qr_code):
    '''
    :param recipient:
    :param qr_code:
    :return: None
    '''
    # Create image from qr_code
    # TODO: Remember to do toUpper() or equivalent of python on the qr_code string wherever it is generated
    qr = pyqrcode.create(qr_code)
    filename = "./email/qrcodes/" + recipient + "-" + qr_code + ".png"
    qr.png(filename, scale=10)
    imagefilename = recipient + "-" + qr_code + ".png"

    # Create email and send it
    sg = sendgrid.SendGridClient(parketconstants.SENDGRID_CLIENT_ID, parketconstants.SENDGRID_CLIENT_PASSWORD)
    message = sendgrid.Mail()
    message.add_to(recipient)
    message.set_subject(parketconstants.SEND_EMAIL_WITH_SUBJECT)
    html = parketconstants.SEND_HTML_PART_1 + parketconstants.SEND_LOGO_FILE_NAME + parketconstants.SEND_HTML_PART_2 + imagefilename + parketconstants.SEND_HTML_PART_3
    message.set_html(html)
    message.set_from(parketconstants.SEND_MESSAGE_FROM_SENDER_ID)
    status, msg = sg.send(message)

def sendEmailForCheckInOut(recipient, subject):
    '''
    :param recipient:
    :return: None
    '''

    # Create email and send it
    sg = sendgrid.SendGridClient(parketconstants.SENDGRID_CLIENT_ID, parketconstants.SENDGRID_CLIENT_PASSWORD)
    message = sendgrid.Mail()
    message.add_to(recipient)
    message.set_subject(subject)
    #parker_name
    html = '<html><body></body></html>'
    message.set_html(html)
    message.set_from(parketconstants.SEND_MESSAGE_FROM_SENDER_ID)
    status, msg = sg.send(message)
