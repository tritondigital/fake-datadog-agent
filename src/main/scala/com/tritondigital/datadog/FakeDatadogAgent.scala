package com.tritondigital.datadog

import java.io.IOException
import java.net.{DatagramPacket, DatagramSocket, SocketException}
import java.util.{ArrayList, List}

import scala.util.control.Exception.ignoring

class FakeDatadogAgent(port: Int, waitTime: Int = 100) {

  var server: DatagramSocket = _
  var lastMessages: List[String] = new ArrayList()

  def start() {
    resetState()

    server = new DatagramSocket(port)
    server.setReuseAddress(true)
    server.setSoTimeout(500)

    val thread: Thread = new Thread(new Runnable() {
      def run() {
        ignoring(classOf[IOException]) {
          while (!server.isClosed) {
            val receiveData: Array[Byte] = new Array[Byte](1024)
            val receivePacket: DatagramPacket = new DatagramPacket(receiveData, receiveData.length)
            server.receive(receivePacket)
            lastMessages.add(new Predef.String(receivePacket.getData).trim)
          }
        }
      }
    })
    thread.setDaemon(true)
    thread.start()
  }

  def resetState(): Unit = {
    lastMessages.clear()
  }

  def waitForRequest(): Unit = Thread.sleep(waitTime)

  def stop() {
    server.close()
    waitForShutdown()
  }

  def waitForShutdown(): Unit = {
    try {
      new DatagramSocket(port).close()
    }
    catch {
      case e: SocketException =>
        waitForShutdown()
    }
  }
}
