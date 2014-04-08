package com.konukhov.downloader

import scala.annotation.tailrec
import java.io.File

// TODO: url-escaped names

class FileNameBuilder(url: String) {
  private val genericFileName = url.split("/").reverse.head

  lazy val fileName     = setFileName(genericFileName, 0)
  lazy val tempfileName = setTempfileName()

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

  private def setTempfileName():String = s"$fileName.download"
}

object FileNameBuilder {
  def fromUrlString(url: String) = new FileNameBuilder(url)
}