package com.konukhov.downloader

import java.net.{ MalformedURLException }

class Connection(val fileUrl: FileUrl) {
  var _inputStreamInitialized: Boolean = false

  lazy val connection = 
    try   fileUrl.asUrl.openConnection
    catch {
      case urle: MalformedURLException => throw new WrongFileUrl("Wrong file url.")
      case e:    Throwable => throw e
    }

  lazy val inputStream = {
    _inputStreamInitialized = true
    connection.getInputStream
  }

  def contentLength = connection.getContentLength

  def close() = if (_inputStreamInitialized) inputStream.close
}

object Connection {
  def apply(fileUrl: FileUrl) = new Connection(fileUrl)
}