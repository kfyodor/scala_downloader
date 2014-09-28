package com.konukhov.downloader

import java.io.{ File, InputStream }

class Downloader(val options: Map[String, Object]) extends Runnable {
  val fileUrl  = options("fileUrl").asInstanceOf[FileUrl]
  val nameBuilder = FileNameBuilder.fromUrlString(fileUrl.asString)
  val fileName = nameBuilder.fileName
  val conn     = Connection(fileUrl)
  val tempfile = Tempfile(nameBuilder.tempfileName)

  def byteStream(input: InputStream): Stream[Chunk] = {
    val bytes  = Array.fill[Byte](1024)(0)  
    val length = input.read(bytes)
    Chunk(length, bytes) #:: byteStream(input)
  }

  def readBytes(func: (Int, Chunk) => Int) {
    byteStream(conn.inputStream)
      .takeWhile { chunk => chunk.length > 0 }
      .foldLeft(0) { (downloaded, chunk) =>
        func(downloaded, chunk)
      }
  }

  def writeChunk(downloaded: Int, chunk: Chunk): Int = {
    tempfile write chunk.bytes 
    downloaded + chunk.length
  }

  def run {
    try {
      readBytes(writeChunk)
      tempfile.swap(fileName)
    } finally {
      conn.close
      tempfile.close
    }
  }
}

class DownloaderWithProgress(options: Map[String, Object]) extends Downloader(options) with Progress {
  override def writeChunk(downloaded: Int, chunk: Chunk) = {
    logProgress(fileName, downloaded + chunk.length, conn.contentLength)
    super.writeChunk(downloaded, chunk)
  }
}

object Downloader {
  def apply(options: Map[String, Object]): Downloader = {
    new DownloaderWithProgress(options)
  }
}