package models

import java.util.{Date => JDate}

import play.api.i18n.Lang
import play.api.libs.json.{Json, JsValue}

import com.shorrockin.cascal.serialization.annotations._

object MiscContent {
  abstract class Type(val value: Int) {
    override def toString = value.toString
  }

  case object Introduction extends Type(1)
  case object Announcement extends Type(2)
}

case class MiscContent(val lang: Lang, val mcType: MiscContent.Type, val date: JDate, val title: String, val content: String) {
  def toJson = Json.obj(
    "type" -> mcType.value,
    "lang" -> lang.code,
    "date" -> date.toString,
    "title" -> title,
    "content" -> content
  )
}

case class EnthusiastSession(val lang: Lang, val date: JDate, val title: String, val speaker: String, val content: String) {
  def toJson = Json.obj(
    "date" -> date.toString,
    "lang" -> lang.code,
    "title" -> title,
    "spaker" -> speaker,
    "content" -> content
  )
}

