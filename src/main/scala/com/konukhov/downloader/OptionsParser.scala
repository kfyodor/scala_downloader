// A simple command line options parser
// Test link: https://dl.dropboxusercontent.com/s/o772fwnkexmj9zu/1.png
// https://dl.dropboxusercontent.com/s/tdcncddrxhcrl4n/Lipa%20Kodi%20Ya%20City%20Council%20%28Mississippi%2C%202007%29.zip

package com.konukhov.downloader

import scala.language.implicitConversions

class OptionsParser(val args: List[String]) {
  
  def parse(): Map[String, Object] = {
    args match {
      case x :: xs => parseFileUrl(x) ++ parseOptions(xs)
      case List(x) => parseFileUrl(x)
      case _       => throw new WrongArgs("First arg should be a file url")
    }
  }

  def parseFileUrl(url: String) = Map("fileUrl" -> url)

  def parseOptions(options: List[String]) = {
    options.foldLeft(Map[String, Object]()){ (out, option) =>
      option.split("=") match {
        case Array(k, v) => out ++ Map(k -> v)
        case _           => throw new WrongOptionFormat("Wrong option format")
      }
    }
  }
}

object OptionsParser {
  implicit def argsToList[A](arr: Array[A]) = arr.toList

  def apply(args: Array[String]) = {
    new OptionsParser(args).parse
  }
}

case class WrongArgs(msg: String)         extends Exception
case class WrongOptionFormat(msg: String) extends Exception