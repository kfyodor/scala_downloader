package com.konukhov.downloader

trait Progress {
  def logProgress(fileName: String, downloaded: Int, length: Int) {
    print("\r")
    print(s"Downloading $fileName: " + s"%1.00f".format((downloaded.toFloat / length) * 100) + "%")
  }
}