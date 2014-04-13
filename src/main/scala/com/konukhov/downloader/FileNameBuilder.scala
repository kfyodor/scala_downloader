package com.konukhov.downloader

import java.io.File
import java.net.URLDecoder
import scala.annotation.tailrec

case class NameWithCounter(name: String, counter: Int = 0) {
  def next = NameWithCounter(name, counter + 1)

  override def toString = counter match {
    case 0 => name
    case _ => asStringWithCounter
  }

  private[this] def asStringWithCounter = {
    val namePattern = """^(.+?)(\.[A-Za-z]+)?$""".r
    val findName    = namePattern findFirstMatchIn name

    findName map { m => 
      m.group(1) + s" ($counter)" + m.group(2)
    } getOrElse name
  }
}


class FileNameBuilder(url: String) {

  private[this] lazy val genericFileName = {
    NameWithCounter(URLDecoder.decode(url.split("/").last, "UTF-8"))
  }

  lazy val fileName     = setFileName(genericFileName)
  lazy val tempfileName = setTempfileName

  def fileExists(name: NameWithCounter): Boolean = {
    new File(s"./downloads/$name").exists
  }

  @tailrec 
  private[this] def setFileName(name: NameWithCounter): String = {
    if (fileExists(name)) { 
      setFileName(name.next) 
    } else {
      s"./downloads/$name"
    }
  }

  private[this] def setTempfileName = s"$fileName.download"

}

object FileNameBuilder {
  def fromUrlString(url: String) = new FileNameBuilder(url)
}