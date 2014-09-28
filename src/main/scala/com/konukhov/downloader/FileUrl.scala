package com.konukhov.downloader

import java.net.URL

case class FileUrl(url: String) {
  def asUrl: URL       = new URL(url)
  def asString: String = url
}