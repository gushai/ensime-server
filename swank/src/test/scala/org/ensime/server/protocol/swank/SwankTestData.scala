package org.ensime.server.protocol.swank

import java.io.File

import org.ensime.api._

import pimpathon.file._

object SwankTestData extends EnsimeTestData {

  val typeInfoStr = """(:arrow-type nil :name "type1" :type-id 7 :decl-as method :full-name "FOO.type1" :type-args nil :members nil :pos nil :outer-type-id 8)"""

  val typeInspectInfoStr = s"""(:type $typeInfoStr :companion-id 1 :interfaces ((:type """ + typeInfoStr + """ :via-view "DEF")) :info-type typeInspect)"""

  val callCompletionInfoStr = """(:result-type """ + typeInfoStr + """ :param-sections ((:params (("ABC" """ + typeInfoStr + """)) :is-implicit nil)))"""

  val symbolDesignationsStr = s"""(:file $symFile :syms ((object 7 9) (trait 11 22)))"""

  val symbolInfoStr = """(:name "name" :local-name "localName" :decl-pos nil :type """ + typeInfoStr + """ :is-callable nil :owner-type-id 2)"""

  val implicitInfosStr = s"""((:type conversion :start 5 :end 6 :fun $symbolInfoStr) (:type param :start 7 :end 8 :fun $symbolInfoStr :params ($symbolInfoStr $symbolInfoStr) :fun-is-implicit t))"""

  val rangePos1Str = s"""(:file "$batchSourceFile" :offset 75 :start 70 :end 90)"""

  val rangePos2Str = s"""(:file "$batchSourceFile" :offset 85 :start 80 :end 100)"""

  val packageInfoStr = """(:info-type package :name "name" :full-name "fullName" :members nil)"""

  val completionInfoStr = """(:name "name" :type-sig (((("abc" "def") ("hij" "lmn"))) "ABC") :type-id 88 :is-callable nil :relevance 90 :to-insert "BAZ")"""

  val completionInfo2Str = """(:name "name2" :type-sig (((("abc" "def"))) "ABC") :type-id 90 :is-callable t :relevance 91 :to-insert nil)"""

  val completionInfoListStr = "(" + completionInfoStr + " " + completionInfo2Str + ")"

  val refactorFailureStr = """(:procedure-id 7 :reason "message" :status failure)"""

  val breakpointListStr = s"""(:active ((:file "$file1" :line 57)) :pending ((:file "$file1" :line 59)))"""

  val debugBacktraceStr = s"""(:frames ((:index 7 :locals ((:index 3 :name "name1" :summary "summary1" :type-name "type1") (:index 4 :name "name2" :summary "summary2" :type-name "type2")) :num-args 4 :class-name "class1" :method-name "method1" :pc-location (:file "$file1" :line 57) :this-object-id "7")) :thread-id "17" :thread-name "thread1")"""

  val undoResultStr = """(:id 7 :touched-files ($file3_str $file4_str))"""

  val replConfigStr = """(:classpath ($file1))"""

  val importSuggestionsStr = s"""(((:type method :name "abc" :local-name "a" :decl-as method :pos (:type line :file $abd :line 10) :owner-name "ownerStr") (:type type :name "abc" :local-name "a" :decl-as trait :pos (:type line :file $abd :line 10))))"""

  val symbolSearchResultsStr = s"""((:type method :name "abc" :local-name "a" :decl-as method :pos (:type line :file $abd :line 10) :owner-name "ownerStr") (:type type :name "abc" :local-name "a" :decl-as trait :pos (:type line :file $abd :line 10)))"""

  val completionInfoCListStr = s"""(:prefix "fooBar" :completions ($completionInfoStr))"""

  val refactorRenameEffectStr = s"""(:procedure-id 7 :refactor-type rename :changes ((:type edit :file $file3 :from 5 :to 7 :text "aaa")) :status success)"""

  val fileRangeStr = """(:file "/abc" :start 7 :end 9)"""

  val debugLocObjectRefStr = """(:type reference :object-id "57")"""

  val debugNullValueStr = """(:val-type null :type-name "typeNameStr")"""

  val debugArrayInstValueStr = """(:val-type arr :length 3 :type-name "typeName" :element-type-name "elementType" :object-id "5")"""

  val debugPrimitiveValueStr = """(:val-type prim :summary "summaryStr" :type-name "typeNameStr")"""

  val debugClassFieldStr = """(:index 19 :name "nameStr" :type-name "typeNameStr" :summary "summaryStr")"""

  val debugStringValueStr = s"""(:val-type str :summary "summaryStr" :fields ($debugClassFieldStr) :type-name "typeNameStr" :object-id "6")"""

  val note1Str = """(:file "file1" :msg "note1" :severity error :beg 23 :end 33 :line 19 :col 8)"""
  val note2Str = """( :file "file1" :msg "note2" :severity warn :beg 23 :end 33 :line 19 :col 8)"""

  val noteListStr = "(:is-full t :notes (" + note1Str + " " + note2Str + "))"

  val entityInfoStr = """(:arrow-type t :name "Arrow1" :type-id 8 :result-type (:arrow-type nil :name "type1" :type-id 7 :decl-as method :full-name "FOO.type1" :type-args nil :members nil :pos nil :outer-type-id 8) :param-sections ((:params (("ABC" (:arrow-type nil :name "type1" :type-id 7 :decl-as method :full-name "FOO.type1" :type-args nil :members nil :pos nil :outer-type-id 8))) :is-implicit nil)))"""
}
