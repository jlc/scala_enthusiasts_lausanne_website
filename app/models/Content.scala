package models

import java.util.{Date => JDate}

import play.api.libs.json.{Json, JsValue}

import com.shorrockin.cascal.serialization.annotations._

abstract class MCType(val value: Int) {
  override def toString = value.toString
}
object MCType {
  case object Introduction extends MCType(1)
  case object Announcement extends MCType(2)
}

case class MiscContent(val mcType: MCType, val date: JDate, val title: String, val content: String) {
  def toJson = Json.obj(
    "type" -> mcType.value,
    "date" -> date.toString,
    "title" -> title,
    "content" -> content
  )
}

case class EnthusiastSession(val date: JDate, val title: String, val speaker: String, val content: String) {
  def toJson = Json.obj(
    "date" -> date.toString,
    "title" -> title,
    "spaker" -> speaker,
    "content" -> content
  )
}

