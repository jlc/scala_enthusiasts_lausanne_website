package models

import java.util.{Date => JDate, UUID}

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

case class MiscContent(val lang: Lang, val mcType: MiscContent.Type, val date: JDate, val title: String, val content: String)

case class EnthusiastSession(val uuid: UUID, val lang: Lang, val date: JDate, val title: String, val speaker: String, val content: String)

/*
 * Writes to Json
 */
object ContentWrites {
  import play.api.libs.json._

  implicit object MiscContentWrites extends Writes[MiscContent] {
    def writes(o: MiscContent): JsObject =
      Json.obj(
        "type" -> o.mcType.value,
        "lang" -> o.lang.code,
        "date" -> o.date.toString,
        "title" -> o.title,
        "content" -> o.content
      )
  }

  implicit object EnthusiastSessionWrites extends Writes[EnthusiastSession] {
    def writes(o: EnthusiastSession): JsObject =
      Json.obj(
        "uuid" -> o.uuid.toString,
        "date" -> o.date.getTime.toString,
        "lang" -> o.lang.code,
        "title" -> o.title,
        "speaker" -> o.speaker,
        "content" -> o.content
      )
  }
}

