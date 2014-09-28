package com.konukhov.downloader

object Server extends App {
  val options = OptionsParser(args)
  val downloader = Downloader(options)

  downloader.run
}
