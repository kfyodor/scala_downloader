// run fileUrl option1=val option2=val
package com.konukhov.downloader

object Server extends App {
  val options = OptionsParser(args)
  new Downloader(options).download
}