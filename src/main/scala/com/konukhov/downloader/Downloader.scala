package com.konukhov.downloader

import scala.language.implicitConversions
import scala.annotation.tailrec
import java.io.{ File, FileOutputStream }
import java.net.URL

import scala.Stream._

// TODO: handle exceptions and stuff
// TODO: use Option[_] type

class Downloader(val options: Map[String, Object]) {
  implicit def strToFile(s: String) = new File(s)

  val fileUrl         = options("fileUrl").asInstanceOf[String]
  val genericFileName = fileUrl.split("/").reverse.head
  val stream          = new URL(fileUrl).openStream

  lazy val fileName   = setFileName(genericFileName, 0)

  def download(): Unit = {
    val tempfile = new FileOutputStream(tempfileName)
    
    Stream.continually(stream.read).takeWhile(_ != -1) foreach { chunk => 
      println(".")
      tempfile.write(chunk.toByte)
    }
    tempfile.close

    tempfileName renameTo fileName
  }


  // TODO: move file name logic to a class
  @tailrec
  private def setFileName(name: String, i: Int): String = {
    if (new File(s"./downloads/$name").exists) {
      val j = i + 1
      val possibleName = possibleFileName(genericFileName, j)
      setFileName(possibleName, j)
    } else {
      s"./downloads/$name"
    }
  }

  private def possibleFileName(name: String, i: Int) = {
    val pattern = """^(.+?)(\.[A-Za-z]+)?$""".r
    (pattern findFirstMatchIn name).map { m => 
      m.group(1) + s" ($i)" + m.group(2) 
    } getOrElse name
  }

  private def tempfileName():String = s"$fileName.download"
}