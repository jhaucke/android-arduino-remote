#!/usr/bin/python

import sys
import socket

sys.path.insert(0, '/usr/lib/python2.7/bridge/')

from bridgeclient import BridgeClient as bridgeclient

value = bridgeclient()

print 'server is running...'

UDP_IP = "192.168.240.1"
UDP_PORT = 5005

sock = socket.socket(socket.AF_INET, # Internet
                     socket.SOCK_DGRAM) # UDP
sock.bind((UDP_IP, UDP_PORT))

while True:
  data, addr = sock.recvfrom(1024) # buffer size is 1024 bytes
  data = data.split(':')
  value.put(data[0],data[1])
