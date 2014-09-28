package com.konukhov.downloader


import java.io.{ FileOutputStream, File }
import scala.language.implicitConversions

class Tempfile(val name: String) {
  implicit def strToFile(s: String) = new File(s)

  var _tempfileInitted = false

  lazy val file = {
    _tempfileInitted = true
    new FileOutputStream(name)
  }

  def write(bytes: Array[Byte]) = file.write(bytes)

  def close() = if (_tempfileInitted) file.close

  def swap(newName: String) = name.renameTo(newName)

}

object Tempfile {
  def apply(name: String) = new Tempfile(name)
}