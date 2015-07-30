package org.ensime.core

import java.io.File
import org.scalatest._
import shapeless._
import pimpathon.file._

import org.ensime.api._

// this test is mostly showing what Canon can do, we're testing
// shapeless more than our specific Poly1.
class CanonSpec extends FlatSpec with Matchers {

  val file = new File(".")
  val canon = file.canon
  assert(file != canon)

  "Canon" should "canon File" in {
    Canonised(file) shouldBe canon
  }

  it should "canon List of Files" in {
    Canonised(List(file)) shouldBe List(canon)
  }

  class MyFile(name: String) extends File(name)

  it should "canon subtypes of File" in {
    val mine = new MyFile(".")
    val myCanon = mine.canon
    assert(mine != myCanon)
    Canonised(mine) shouldBe myCanon
  }

  it should "canon an RpcRequest" in {
    val request = TypeAtPointReq(file, OffsetRange(100)): RpcRequest
    val expected = TypeAtPointReq(canon, OffsetRange(100))
    Canonised(request) shouldBe expected
  }

  it should "canon an EnsimeServerMessage" in {
    val response = Breakpoint(file, 13): RpcResponse
    val expected = Breakpoint(canon, 13)
    Canonised(response) shouldBe expected
  }

}
