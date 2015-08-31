package com.tritondigital.datadog

import java.io.IOException
import java.net.InetSocketAddress
import java.nio.ByteBuffer
import java.nio.channels.{DatagramChannel, SelectionKey, Selector}
import java.util

import scala.collection.JavaConversions._
import scala.util.control.Exception.ignoring

class FakeDatadogAgent(port: Int, waitTime: Int = 100) {

  private var server: DatagramChannel = _
  var lastMessages: util.List[String] = new util.ArrayList()

  def start(): Unit = {
    resetState()
    server = DatagramChannel.open
    server.socket.bind(new InetSocketAddress(port))
    server.configureBlocking(false)
    val selector: Selector = Selector.open
    server.register(selector, SelectionKey.OP_READ)
    val thread: Thread = new Thread(new Runnable {
      override def run(): Unit = {
        var stop = false
        while (!stop) {
          if (selector.select() <= 0) {
            val keyIterator: util.Iterator[SelectionKey] = selector.selectedKeys().iterator()
            while (keyIterator.hasNext) {
              val key: SelectionKey = keyIterator.next()
              keyIterator.remove()
              if (key.isReadable) {
                read(key.channel().asInstanceOf[DatagramChannel])
              }
            }
            stop = true
          }
        }
      }
    })
    thread.setDaemon(true)
    thread.start()
  }

  private def read(channel: DatagramChannel) {
    val packet: ByteBuffer = ByteBuffer.allocate(1024)
    packet.clear
    try {
      channel.receive(packet)
      val message: String = new String(packet.array).trim
      val split: Array[String] = message.split("\\s")
      lastMessages.addAll(split.toSeq)
    }
    catch {
      case e: IOException =>  e.printStackTrace()
    }
  }

  def resetState() {
    lastMessages.clear()
  }

  def waitForRequest(): Unit = {
    ignoring(classOf[InterruptedException]) {
      Thread.sleep(waitTime)
    }
  }

  def stop(): Unit = {
    server.close()
  }
}
