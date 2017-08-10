#!/usr/bin/env python

#
# This is a script for tracking the portfolios of market participants. It
# consumes the tab-separated values (TSV) output format of Parity Trade
# Reporter from the standard input and produces TSV on the standard output.
#
# Run the script as follows:
#
#     java -jar parity-reporter.jar -t etc/example-udp.conf | \
#             python scripts/portfolio.py
#
#

import collections
import csv
import sys


class User(object):

    def __init__(self):
        self.cash = 0.0
        self.inventory = {}


def record(timestamp, username):
    user = users[username]
    cash = '{}'.format(user.cash)
    inventory = ['{}:{}'.format(instrument, quantity) for instrument, quantity
            in user.inventory.iteritems()]
    print '\t'.join([timestamp, username, cash, ','.join(sorted(inventory))])


users = collections.defaultdict(User)

print '\t'.join(['Timestamp', 'Username', 'Cash', 'Inventory'])

for row in csv.DictReader(iter(sys.stdin.readline, None), delimiter='\t'):
    timestamp = row['Timestamp']
    instrument = row['Instrument']
    price = float(row['Price'])
    quantity = float(row['Quantity'])
    seller = users[row['Seller']]
    buyer = users[row['Buyer']]
    seller.cash += price * quantity
    buyer.cash -= price * quantity
    seller.inventory[instrument] = seller.inventory.get(instrument, 0.0) - quantity
    buyer.inventory[instrument] = buyer.inventory.get(instrument, 0.0) + quantity
    record(timestamp, row['Seller'])
    record(timestamp, row['Buyer'])
