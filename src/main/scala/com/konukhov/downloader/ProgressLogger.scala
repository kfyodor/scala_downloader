package com.konukhov.downloader

trait Progress {
  def logProgress(fileName: String, downloaded: Int, length: Int) {
    val progress = ((downloaded.toFloat / length) * 100)
    print("\r")
    print(s"-----> Downloading $fileName: " + s"%1.00f".format(progress) + "%")
    if (progress == 100) print("\n")
  }
}