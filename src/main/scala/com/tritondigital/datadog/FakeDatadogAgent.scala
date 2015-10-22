package com.tritondigital.datadog

import java.net.{DatagramPacket, DatagramSocket, SocketException}
import java.util
import java.util.concurrent.{CountDownLatch, TimeUnit, TimeoutException}

class FakeDatadogAgent(port: Int, waitTime: Int = 1000) {
  private val DOGSTATSD_PACKET_SIZE: Int = 1500

  private var server: DatagramSocket = _
  private var messagesEvent: CountDownLatch = _
  private var stopEvent: CountDownLatch = _
  private var expectedMessages = 0

  var lastMessages = new util.ArrayList[String]

  def start(): Unit = {
    resetState()

    server = new DatagramSocket(port)
    stopEvent = new CountDownLatch(1)

    val thread: Thread = new Thread(new Runnable {
      override def run(): Unit = {
        try {
          while (true) {
            readMessages()
          }
        } catch {
          case e: SocketException =>
        } finally {
          stopEvent.countDown()
        }
      }
    })

    thread.setDaemon(true)
    thread.start()
  }

  def readMessages(): Unit = {
    val receiveData = new Array[Byte](DOGSTATSD_PACKET_SIZE)
    val receivePacket = new DatagramPacket(receiveData, receiveData.length)
    server.receive(receivePacket)
    val message = new String(receivePacket.getData)
    message.trim.split("\\n").foreach { item =>
      lastMessages.add(item)
      messagesEvent.countDown()
    }
  }

  def resetState() {
    lastMessages.clear()
    messagesEvent = new CountDownLatch(expectedMessages)
  }

  def expectRequests(count: Int): Unit = {
    expectedMessages = count
    messagesEvent = new CountDownLatch(expectedMessages)
  }

  def waitForRequest(): Unit = {
    if (expectedMessages == 0) {
      Thread.sleep(waitTime)
    } else {
      if (!messagesEvent.await(waitTime, TimeUnit.MILLISECONDS)) {
        throw new TimeoutException("Expected " + expectedMessages + " messages but received " + (expectedMessages - messagesEvent.getCount) + " after " + waitTime + "ms")
      }
    }
  }

  def stop(): Unit = {
    server.close()
    stopEvent.await()
  }
}
