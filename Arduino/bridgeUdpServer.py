#!/usr/bin/python

#import sys
import socket

#sys.path.insert(0, '/usr/lib/python2.7/bridge/')
 
#from bridgeclient import BridgeClient as bridgeclient
from pyfirmata import Arduino, util
from time import sleep
 
#value = bridgeclient()
board = Arduino('/dev/ttyATH0', baudrate=115200)
pin9 = board.get_pin('d:9:s')
pin10 = board.get_pin('d:10:s')
 
UDP_IP = "192.168.240.1"
UDP_PORT = 5005
 
sock = socket.socket(socket.AF_INET, # Internet
                     socket.SOCK_DGRAM) # UDP
sock.bind((UDP_IP, UDP_PORT))

pin9.write(40)
sleep(0.5)
pin9.write(140)
sleep(0.5)
pin9.write(90)
sleep(0.5)

#print 'server is running...'

while True:
  data, addr = sock.recvfrom(512) # buffer size is 512 bytes
  data = data.split(':')
#  print "steering:", data[0]
#  print "gas:", data[1]
  pin9.write(int(data[0]))
  pin10.write(int(data[1]))
#  board.digital[13].write(data)
#  value.put(data[0],data[1])
#  value.put("s",data)

