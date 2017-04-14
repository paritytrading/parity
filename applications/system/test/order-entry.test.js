'use strict';

const POE = require('parity-poe');
const assert = require('assert');
const soupbintcp = require('soupbintcp');

const PARITY_SYSTEM_ADDRESS = process.env.PARITY_SYSTEM_ADDRESS || 'localhost';
const PARITY_SYSTEM_PORT = Number(process.env.PARITY_SYSTEM_PORT || 4000);

describe('Order entry', function () {
  let a;
  let b;

  beforeEach(function (done) {
    a = createClient(() => {
      b = createClient(done);
    });
  });

  afterEach(function () {
    a.end();
    b.end();
  });

  it('handles unknown instrument', function (done) {
    a.send(format({
      messageType: 'E',
      orderId: '1',
      instrument: 'QUUX',
      side: 'B',
      quantity: 100,
      price: 1500000,
    }));

    a.on('message', (payload) => {
      assert.deepEqual(parse(payload), {
        messageType: 'R',
        orderId: '1',
        reason: 'I',
      });

      done();
    });
  });

  it('handles invalid quantity', function (done) {
    a.send(format({
      messageType: 'E',
      orderId: '1',
      instrument: 'FOO',
      side: 'B',
      quantity: 0,
      price: 1500000,
    }));

    a.on('message', (payload) => {
      assert.deepEqual(parse(payload), {
        messageType: 'R',
        orderId: '1',
        reason: 'Q',
      });

      done();
    });
  });

  it('handles entering an order', function (done) {
    a.send(format({
      messageType: 'E',
      orderId: '1',
      instrument: 'FOO',
      side: 'B',
      quantity: 100,
      price: 1500000,
    }));

    a.on('message', (payload) => {
      assert.deepEqual(parse(payload), {
        messageType: 'A',
        orderId: '1',
        side: 'B',
        instrument: 'FOO',
        quantity: 100,
        price: 1500000,
      });

      done();
    });
  });

  it('handles canceling an order partially', function (done) {
    a.send(format({
      messageType: 'E',
      orderId: '1',
      instrument: 'FOO',
      side: 'B',
      quantity: 100,
      price: 1500000,
    }));

    a.on('message', (payload) => {
      const message = parse(payload);

      if (message.messageType === 'A') {
        a.send(format({
          messageType: 'X',
          orderId: '1',
          quantity: 25,
        }));
      }
      else {
        assert.deepEqual(message, {
          messageType: 'X',
          orderId: '1',
          canceledQuantity: 75,
          reason: 'R',
        });

        done();
      }
    });
  });

  it('handles canceling an order fully', function (done) {
    a.send(format({
      messageType: 'E',
      orderId: '1',
      instrument: 'FOO',
      side: 'B',
      quantity: 100,
      price: 1500000,
    }));

    a.on('message', (payload) => {
      const message = parse(payload);

      if (message.messageType === 'A') {
        a.send(format({
          messageType: 'X',
          orderId: '1',
          quantity: 0,
        }));
      }
      else {
        assert.deepEqual(message, {
          messageType: 'X',
          orderId: '1',
          canceledQuantity: 100,
          reason: 'R',
        });

        done();
      }
    });
  });

  it('handles partial resting order execution', function (done) {
    a.send(format({
      messageType: 'E',
      orderId: '1',
      instrument: 'FOO',
      side: 'B',
      quantity: 100,
      price: 1500000,
    }));

    a.on('message', (payload) => {
      const message = parse(payload);

      if (message.messageType === 'A') {
        b.send(format({
          messageType: 'E',
          orderId: '1',
          instrument: 'FOO',
          side: 'S',
          quantity: 50,
          price: 1500000,
        }));
      }
      else {
        assert.deepEqual(message, {
          messageType: 'E',
          orderId: '1',
          quantity: 50,
          price: 1500000,
          liquidityFlag: 'A',
        });

        done();
      }
    });
  });

  it('handles full resting order execution', function (done) {
    a.send(format({
      messageType: 'E',
      orderId: '1',
      instrument: 'FOO',
      side: 'B',
      quantity: 100,
      price: 1500000,
    }));

    a.on('message', (payload) => {
      const message = parse(payload);

      if (message.messageType === 'A') {
        b.send(format({
          messageType: 'E',
          orderId: '1',
          instrument: 'FOO',
          side: 'S',
          quantity: 150,
          price: 1500000,
        }));
      }
      else {
        assert.deepEqual(message, {
          messageType: 'E',
          orderId: '1',
          quantity: 100,
          price: 1500000,
          liquidityFlag: 'A',
        });

        done();
      }
    });
  });

  it('handles partial incoming order execution', function (done) {
    a.send(format({
      messageType: 'E',
      orderId: '1',
      instrument: 'FOO',
      side: 'B',
      quantity: 50,
      price: 1500000,
    }));

    a.on('message', (payload) => {
      const message = parse(payload);

      if (message.messageType === 'A') {
        b.send(format({
          messageType: 'E',
          orderId: '1',
          instrument: 'FOO',
          side: 'S',
          quantity: 100,
          price: 1500000,
        }));
      }
    });

    b.on('message', (payload) => {
      const message = parse(payload);

      if (message.messageType === 'E') {
        assert.deepEqual(message, {
          messageType: 'E',
          orderId: '1',
          quantity: 50,
          price: 1500000,
          liquidityFlag: 'R',
        });

        done();
      }
    });
  });

  it('handles full incoming order execution', function (done) {
    a.send(format({
      messageType: 'E',
      orderId: '1',
      instrument: 'FOO',
      side: 'B',
      quantity: 150,
      price: 1500000,
    }));

    a.on('message', (payload) => {
      const message = parse(payload);

      if (message.messageType === 'A') {
        b.send(format({
          messageType: 'E',
          orderId: '1',
          instrument: 'FOO',
          side: 'S',
          quantity: 100,
          price: 1500000,
        }));
      }
    });

    b.on('message', (payload) => {
      const message = parse(payload);

      if (message.messageType === 'E') {
        assert.deepEqual(message, {
          messageType: 'E',
          orderId: '1',
          quantity: 100,
          price: 1500000,
          liquidityFlag: 'R',
        });

        done();
      }
    });
  });
});

const format = POE.formatInbound;

function parse(payload) {
  const message = POE.parseOutbound(payload);

  message.orderId = message.orderId.trim();

  if (message.instrument)
    message.instrument = message.instrument.trim();

  delete message.timestamp;
  delete message.orderNumber;
  delete message.matchNumber;

  return message;
}

function createClient(done) {
  const client = soupbintcp.createClient(PARITY_SYSTEM_PORT, PARITY_SYSTEM_ADDRESS, () => {
    client.login({
      username: 'foo',
      password: 'bar',
      requestedSession: '',
      requestedSequenceNumber: 0,
    });
  });

  client.on('accept', (payload) => {
    done();
  });

  return client;
}
