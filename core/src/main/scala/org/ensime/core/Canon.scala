package org.ensime.core

import java.io.File
import shapeless._
import pimpathon.file._

/**
 * Goes through sealed families and gets the canonical path of `File`
 * instances. Not to be confused with "the other" Cannon ;-)
 */
object Canon extends Poly1 {
  // people extend File, so we have to handle subtypes
  implicit def caseFile[F <: File] = at[F](f => f.canon)
}

object Canonised {
  def apply[T](t: T)(implicit everywhere: Everywhere[Canon.type, T]) = everywhere(t)
}
