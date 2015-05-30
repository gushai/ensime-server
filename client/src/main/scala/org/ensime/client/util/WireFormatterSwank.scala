package org.ensime.client.util

import org.ensime.server.protocol.RpcRequest
import org.ensime.server.protocol._
import org.ensime.server.protocol.swank.SwankProtocolRequest.RpcRequestEnvelopeFormat
import java.io.File
import org.ensime.model._

class WireFormatterSwank extends WireFormatter {

  private val protocolLabelSwankRpc = ":swank-rpc"

  def toWireFormat(request: RpcRequest, callId: Int): String = {
    val msg = setRequestEnvelope(protocolLabelSwankRpc, callId, getMessageCore(request))
    msg
  }

  private def setRequestEnvelope(label: String, callId: Int, msgCore: String): String = {
    s"""($label $msgCore $callId)"""
  }

  private def getMessageCore(request: RpcRequest): String = {

    request match {
      case ConnectionInfoReq => {
        s"""(swank:connection-info)"""
      }
      case FormatSourceReq(files) => {
        val f = files.map { (f: File) => s""""$f"""" }.mkString(" ")
        s"""(swank:format-source ($f))"""
      }
      case InspectPackageByPathReq(path) => {
        s"""(swank:inspect-package-by-path "$path")"""
      }
      case InspectTypeAtPointReq(file, range) => {
        val fileStr = file.getAbsolutePath
        val from = range.from
        val to = range.to
        if (from != to) {
          s"""(swank:inspect-type-at-point "$fileStr" ($from $to))"""
        } else {
          s"""(swank:inspect-type-at-point "$fileStr" $from)"""
        }
      }
      case InitProjectReq => {
        s"""(swank:init-project)"""
      }
      case TypecheckAllReq => {
        s"""(swank:typecheck-all)"""
      }
      case TypecheckFileReq(file) => {
        s"""(swank:typecheck-file "$file")"""
      }
      case TypecheckFilesReq(files) => {
        val f = files.map { (f: File) => s""""$f"""" }.mkString(" ")
        s"""(swank:typecheck-files ($f))"""
      }
      case TypeAtPointReq(file, range) => {
        val fileStr = file.getAbsolutePath
        val from = range.from
        val to = range.to
        if (from != to) {
          s"""(swank:type-at-point "$fileStr" ($from $to))"""
        } else {
          s"""(swank:type-at-point "$fileStr" $from)"""
        }

      }
      case ShutdownServerReq => {
        s"""(swank:shutdown-server)"""
      }
      case SymbolDesignationsReq(filename, start, end, requestedTypes) => {
        val types = requestedTypes.map { (s: SourceSymbol) => mapSourceSymbolMap(s) }.mkString(" ")
        s"""(swank:symbol-designations "$filename" $start $end (""" + types + """))"""
      }
      case UsesOfSymbolAtPointReq(file, point) => {
        s"""(swank:uses-of-symbol-at-point "$file" $point)"""
      }
      case _ => throw (new Exception("Unknown Request Type supplied"))
    }
  }

  private def mapSourceSymbolMap(sourceSymbol: SourceSymbol): String = {
    sourceSymbol match {
      case ObjectSymbol => "object"
      case ClassSymbol => "class"
      case TraitSymbol => "trait"
      case PackageSymbol => "package"
      case ConstructorSymbol => "constructor"
      case ImportedNameSymbol => "importedName"
      case TypeParamSymbol => "typeParam"
      case ParamSymbol => "param"
      case VarFieldSymbol => "varField"
      case ValFieldSymbol => "valField"
      case OperatorFieldSymbol => "operator"
      case VarSymbol => "var"
      case ValSymbol => "val"
      case FunctionCallSymbol => "functionCall"
      case _ => throw new Exception("Unknown SourceSymbol!")
    }
  }

}