package com.konukhov.downloader

import java.io.{ File, FileOutputStream, InputStream }
import java.net.URL
import scala.language.implicitConversions

// TODO: handle exceptions and stuff
// TODO: use Option[_] type

class Downloader(val options: Map[String, Object]) {
  implicit def strToFile(s: String) = new File(s)
  case class Chunk(length: Int, bytes: Array[Byte])

  var downloadedBytes = 0
  val fileUrl = options("fileUrl").asInstanceOf[String]

  private[this] 
  val nameBuilder = FileNameBuilder.fromUrlString(fileUrl)

  val fileName     = nameBuilder.fileName
  val tempfileName = nameBuilder.tempfileName
  val tempfile     = new FileOutputStream(tempfileName)

  def byteStream(input: InputStream): Stream[Chunk] = {
    val bytes  = Array.fill[Byte](1024)(0)  
    val length = input.read(bytes)
    Chunk(length, bytes) #:: byteStream(input)
  }

  def readBytes(input: InputStream)(func: Chunk => Unit) {
    byteStream(input) takeWhile { chunk => chunk.length > 0 } foreach func
  }

  // Move to log class or trait
  def logProgress(chunk: Chunk, length: Int) {
    downloadedBytes += chunk.length
    print("\r")
    print(s"Downloading $fileName: " + s"%1.00f".format((downloadedBytes.toFloat / length) * 100) + "%")
  }

  def download() = {
    val conn   = new URL(fileUrl).openConnection
    val length = conn.getContentLength
    val in     = conn.getInputStream
    
    try {
      readBytes(in) { chunk => 
        logProgress(chunk, length)
        tempfile write chunk.bytes 
      }
      tempfileName renameTo fileName
    } finally {
      in.close
      tempfile.close
    }
  }
}