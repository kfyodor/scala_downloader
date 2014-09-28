package com.konukhov.downloader

import org.scalatest._
import org.scalamock.scalatest.MockFactory

class FileNameBuilderSpec extends FunSpec
                             with BeforeAndAfter
                             with MockFactory {

  class FileNameBuilderWithFile(url: String) extends FileNameBuilder(url) {
    var fileChecked = false

    override def fileExists(name: NameWithCounter) = {
      // OMG this is so ugly!
      if (!fileChecked) { 
        fileChecked = true 
        true
      } else { 
        false
      }
    }
  }

  class FileNameBuilderWithoutFile(url: String) extends FileNameBuilder(url) {
    override def fileExists(name: NameWithCounter) = false
  }
  
  describe("If the file does not exist") {
    val obj1 = new FileNameBuilderWithoutFile("http://testurl.com/1.png")

    it("should have file name '1.png'") {
      assert(obj1.fileName == "./downloads/1.png")
    }

    it("should have tempfile name '1.png.download'") {
      assert(obj1.tempfileName == "./downloads/1.png.download")
    }
  }

  describe("If the file already exists") {
    val obj2 = new FileNameBuilderWithFile("http://testurl.com/1.png")

    it("should have file name '1 (1).png'") {
      assert(obj2.fileName == "./downloads/1 (1).png", "Got " + obj2.fileName)
    }

    it("should have tempfile name '1 (1).png.download'") {
      assert(obj2.tempfileName == "./downloads/1 (1).png.download", "Got " + obj2.fileName)
    }
  }

  describe("Name with counter class") {
    val name = new NameWithCounter("1.png")

    it("counter is 0") {
      assert(name.counter == 0)
    }

    it("string is the same if counter is 0") {
      assert(name.toString == "1.png")
    }

    it("next increases counter by 1") {
      assert(name.next.counter == 1)
    }

    describe("counter > 0") {

      it("counter is 1") {
        assert(name.next.toString == "1 (1).png")
      }

      it("counter is 2") {
        assert(name.next.next.toString == "1 (2).png")
      }

    }
  }
}